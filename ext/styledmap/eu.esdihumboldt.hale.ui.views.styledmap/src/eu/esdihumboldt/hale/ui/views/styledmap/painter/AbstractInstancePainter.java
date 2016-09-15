/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.views.styledmap.painter;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.jdesktop.swingx.image.FastBlurFilter;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.PixelConverter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import de.fhg.igd.mapviewer.AbstractTileOverlayPainter;
import de.fhg.igd.mapviewer.Refresher;
import de.fhg.igd.mapviewer.geom.BoundingBox;
import de.fhg.igd.mapviewer.geom.Point3D;
import de.fhg.igd.mapviewer.marker.Marker;
import de.fhg.igd.mapviewer.waypoints.GenericWaypoint;
import de.fhg.igd.mapviewer.waypoints.GenericWaypointPainter;
import de.fhg.igd.mapviewer.waypoints.MarkerWaypointRenderer;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.convert.ConversionUtil;
import eu.esdihumboldt.hale.common.instance.helper.PropertyResolver;
import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.impl.PseudoInstanceReference;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.common.service.style.StyleService;
import eu.esdihumboldt.hale.ui.common.service.style.StyleServiceListener;
import eu.esdihumboldt.hale.ui.geometry.DefaultGeometryUtil;
import eu.esdihumboldt.hale.ui.geometry.service.GeometrySchemaServiceListener;
import eu.esdihumboldt.hale.ui.selection.InstanceSelection;
import eu.esdihumboldt.hale.ui.selection.impl.DefaultInstanceSelection;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.service.instance.InstanceServiceListener;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;
import eu.esdihumboldt.hale.ui.util.io.ThreadProgressMonitor;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.Clip;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.ClipPainter;
import eu.esdihumboldt.hale.ui.views.styledmap.util.CRSConverter;
import eu.esdihumboldt.hale.ui.views.styledmap.util.CRSDecode;

/**
 * Abstract instance painter implementation based on an {@link InstanceService}.
 * 
 * @author Simon Templer
 */
public abstract class AbstractInstancePainter
		extends GenericWaypointPainter<InstanceReference, InstanceWaypoint>
		implements InstanceServiceListener, ISelectionListener, ClipPainter {

	private static final ALogger log = ALoggerFactory.getLogger(AbstractInstancePainter.class);

	private static final double BUFFER_VALUE = 0.0125;

	/**
	 * The list of default paths searched in an instance for an instance name.
	 * Search is done in the given order.
	 */
	private static final String[] DEFAULT_NAME_PATHS = new String[] { "name", "id", "fid" };

	private final InstanceService instanceService;

	private final DataSet dataSet;

	private CoordinateReferenceSystem waypointCRS;

	private Clip clip;

	private final StyleServiceListener styleListener;

	private final GeometrySchemaServiceListener geometryListener;

	private Set<InstanceReference> lastSelected = new HashSet<InstanceReference>();

	/**
	 * Create an instance painter.
	 * 
	 * @param instanceService the instance service
	 * @param dataSet the data set
	 */
	public AbstractInstancePainter(InstanceService instanceService, DataSet dataSet) {
		super(new MarkerWaypointRenderer<InstanceWaypoint>(), 4); // four worker
																	// threads
		this.instanceService = instanceService;
		this.dataSet = dataSet;

		styleListener = new StyleServiceListener() {

			@Override
			public void stylesRemoved(StyleService styleService) {
				styleRefresh();
			}

			@Override
			public void stylesAdded(StyleService styleService) {
				styleRefresh();
			}

			@Override
			public void styleSettingsChanged(StyleService styleService) {
				styleRefresh();
			}

			@Override
			public void backgroundChanged(StyleService styleService, RGB background) {
				// ignore, background not supported
			}
		};

		geometryListener = new GeometrySchemaServiceListener() {

			@Override
			public void defaultGeometryChanged(TypeDefinition type) {
				SchemaService ss = PlatformUI.getWorkbench().getService(SchemaService.class);
				SchemaSpaceID spaceID;
				switch (getDataSet()) {
				case TRANSFORMED:
					spaceID = SchemaSpaceID.TARGET;
					break;
				case SOURCE:
					spaceID = SchemaSpaceID.SOURCE;
					break;
				default:
					throw new IllegalStateException("Illegal data set");
				}
				SchemaSpace schemas = ss.getSchemas(spaceID);
				if (schemas.getType(type.getName()) != null) {
					// TODO only update way-points that are affected XXX save
					// type def in way-point?
					// XXX for now: recreate all
					update(null);
				}
			}
		};
	}

	/**
	 * Refresh with style update.
	 */
	protected void styleRefresh() {
		for (InstanceWaypoint wp : iterateWaypoints()) {
			Marker<? super InstanceWaypoint> marker = wp.getMarker();
			if (marker instanceof StyledInstanceMarker) {
				((StyledInstanceMarker) marker).resetStyle();
			}
		}
		refreshAll();
	}

	/**
	 * @see InstanceServiceListener#datasetChanged(DataSet)
	 */
	@Override
	public void datasetChanged(DataSet type) {
		if (type == dataSet) {
			update(null);
		}
	}

	/**
	 * Get the CRS for use in way-point bounding boxes.
	 * 
	 * @return the way-point CRS
	 */
	public CoordinateReferenceSystem getWaypointCRS() {
		if (waypointCRS == null) {
			try {
				waypointCRS = CRSDecode.getCRS(GenericWaypoint.COMMON_EPSG);
			} catch (Throwable e) {
				throw new IllegalStateException("Could not decode way-point CRS", e);
			}
		}

		return waypointCRS;
	}

	/**
	 * Do a complete update of the way-points. Existing way-points are
	 * discarded.
	 * 
	 * @param selection the current selection
	 */
	public void update(ISelection selection) {
		clearWaypoints();

		// XXX only mappable type instances for source?!
		InstanceCollection instances = instanceService.getInstances(dataSet);

		if (selection != null) {
			lastSelected = collectReferences(selection);
		}

		if (instances.isEmpty()) {
			return;
		}

		final AtomicBoolean updateFinished = new AtomicBoolean(false);
		IRunnableWithProgress op = new IRunnableWithProgress() {

			@Override
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				String taskName;
				switch (getDataSet()) {
				case SOURCE:
					taskName = "Update source data in map";
					break;
				case TRANSFORMED:
				default:
					taskName = "Update transformed data in map";
					break;
				}
				monitor.beginTask(taskName, IProgressMonitor.UNKNOWN);

				// add way-points for instances
				InstanceCollection instances = instanceService.getInstances(dataSet);
				ResourceIterator<Instance> it = instances.iterator();
				try {
					while (it.hasNext()) {
						Instance instance = it.next();

						InstanceWaypoint wp = createWaypoint(instance, instanceService);

						if (wp != null) {
							if (lastSelected.contains(wp.getValue())) {
								wp.setSelected(true, null); // refresh can be
															// ignored because
															// it's done for
															// addWaypoint
							}
							addWaypoint(wp, null); // no refresher, as
													// refreshAll is executed
						}
					}
				} finally {
					it.close();
					monitor.done();
					updateFinished.set(true);
				}
			}
		};

		try {
			ThreadProgressMonitor.runWithProgressDialog(op, false);
		} catch (Throwable e) {
			log.error("Error running painter update", e);
		}

		HaleUI.waitFor(updateFinished);
		refreshAll();
	}

	/**
	 * Create a way-point for an instance
	 * 
	 * @param instance the instance
	 * @param instanceService the instance service
	 * @return the created way-point or <code>null</code> if
	 */
	protected InstanceWaypoint createWaypoint(Instance instance, InstanceService instanceService) {
		// retrieve instance reference
		InstanceReference ref = instanceService.getReference(instance);// ,
																		// getDataSet());

		BoundingBox bb = null;
		List<GeometryProperty<?>> geometries = new ArrayList<GeometryProperty<?>>(
				DefaultGeometryUtil.getDefaultGeometries(instance));
		ListIterator<GeometryProperty<?>> it = geometries.listIterator();
		while (it.hasNext()) {
			GeometryProperty<?> prop = it.next();

			// check if geometry is valid for display in map
			CoordinateReferenceSystem crs = (prop.getCRSDefinition() == null) ? (null)
					: (prop.getCRSDefinition().getCRS());

			if (crs == null) {
				// no CRS, can't display in map

				// remove from list
				it.remove();
			}
			else {
				Geometry geometry = prop.getGeometry();

				// determine geometry bounding box
				BoundingBox geometryBB = getBoundingBox(geometry);

				if (geometryBB == null) {
					// no valid bounding box for geometry
					it.remove();
				}
				else {
					try {
						// get converter to way-point CRS
						CRSConverter conv = CRSConverter.getConverter(crs, getWaypointCRS());

						// convert BB to way-point SRS
						geometryBB = conv.convert(geometryBB);

						// add to instance bounding box
						if (bb == null) {
							bb = new BoundingBox(geometryBB);
						}
						else {
							bb.add(geometryBB);
						}
					} catch (Exception e) {
						log.error("Error converting instance bounding box to waypoint bounding box",
								e);
						// ignore geometry
						it.remove();
					}
				}
			}
		}

		if (bb == null || geometries.isEmpty()) {
			// don't create way-point w/o geometries
			return null;
		}

		// use bounding box center as GEO position
		Point3D center = bb.getCenter();
		GeoPosition pos = new GeoPosition(center.getX(), center.getY(),
				GenericWaypoint.COMMON_EPSG);

		// buffer bounding box if x or y dimension empty
		if (bb.getMinX() == bb.getMaxX()) {
			bb.setMinX(bb.getMinX() - BUFFER_VALUE);
			bb.setMaxX(bb.getMaxX() + BUFFER_VALUE);
		}
		if (bb.getMinY() == bb.getMaxY()) {
			bb.setMinY(bb.getMinY() - BUFFER_VALUE);
			bb.setMaxY(bb.getMaxY() + BUFFER_VALUE);
		}

		// set dummy z range (otherwise the RTree can't deal correctly with it)
		bb.setMinZ(-BUFFER_VALUE);
		bb.setMaxZ(BUFFER_VALUE);

		String name = findInstanceName(instance);

		// create the way-point
		// XXX in abstract method?
		InstanceWaypoint wp = new InstanceWaypoint(pos, bb, ref, geometries,
				instance.getDefinition(), name);

		// each way-point must have its own marker, as the marker stores the
		// marker areas
		wp.setMarker(createMarker(wp));

		return wp;
	}

	/**
	 * Determine the name for the given instance.
	 * 
	 * @param instance the instance
	 * @return the instance name or <code>null</code> if unknown
	 */
	private String findInstanceName(Instance instance) {
		for (String namePath : DEFAULT_NAME_PATHS) {
			Collection<Object> values = PropertyResolver.getValues(instance, namePath);
			if (values != null) {
				for (Object value : values) {
					if (value instanceof Instance) {
						value = ((Instance) value).getValue();
					}

					if (value != null) {
						try {
							String name = ConversionUtil.getAs(value, String.class);
							if (name != null && !name.isEmpty()) {
								return name;
							}
						} catch (Exception e) {
							// ignore
						}
					}
				}
			}
		}

		// no name found
		return null;
	}

	/**
	 * Create a new marker for a way-point.
	 * 
	 * @param wp the way-point
	 * @return the marker
	 */
	private Marker<? super InstanceWaypoint> createMarker(InstanceWaypoint wp) {
//		return new InstanceMarker();
		return new StyledInstanceMarker(wp);
	}

	/**
	 * Determine the bounding box for a geometry.
	 * 
	 * @param geometry the geometry
	 * @return the bounding box or <code>null</code> if it is either an empty
	 *         geometry or the bounding box cannot be determined
	 */
	public static BoundingBox getBoundingBox(Geometry geometry) {
		Geometry envelope = geometry.getEnvelope();
		if (envelope instanceof Point) {
			Point point = (Point) envelope;
			if (!point.isEmpty()) { // not an empty geometry
				// a bounding box representing the point
				return new BoundingBox(point.getX(), point.getY(), 0, point.getX(), point.getY(),
						0);
			}
		}
		else if (envelope instanceof Polygon) {
			Polygon rect = (Polygon) envelope;
			if (!rect.isEmpty()) {
				Double maxX = null, maxY = null, minX = null, minY = null;

				Coordinate[] points = rect.getCoordinates();
				for (Coordinate point : points) {
					// maximum x ordinate
					if (maxX == null) {
						maxX = point.x;
					}
					else {
						maxX = Math.max(maxX, point.x);
					}
					// maximum y ordinate
					if (maxY == null) {
						maxY = point.y;
					}
					else {
						maxY = Math.max(maxY, point.y);
					}
					// minimum x ordinate
					if (minX == null) {
						minX = point.x;
					}
					else {
						minX = Math.min(minX, point.x);
					}
					// minimum y ordinate
					if (minY == null) {
						minY = point.y;
					}
					else {
						minY = Math.min(minY, point.y);
					}
				}

				if (maxX != null && maxY != null && minX != null && minY != null) {
					return new BoundingBox(minX, minY, 0, maxX, maxY, 0);
				}
			}
		}

		return null;
	}

	/**
	 * @see AbstractTileOverlayPainter#getMaxOverlap()
	 */
	@Override
	protected int getMaxOverlap() {
		return 12;
	}

	/**
	 * @return the instance service
	 */
	public InstanceService getInstanceService() {
		return instanceService;
	}

	/**
	 * @return the data set
	 */
	public DataSet getDataSet() {
		return dataSet;
	}

	/**
	 * Try to combine two selections.
	 * 
	 * @param oldSelection the first selection
	 * @param newSelection the second selection
	 * @return the combined selection
	 */
	@SuppressWarnings("unchecked")
	public static ISelection combineSelection(ISelection oldSelection, ISelection newSelection) {
		if (newSelection == null)
			return oldSelection;
		else if (oldSelection == null)
			return newSelection;

		if (oldSelection instanceof InstanceSelection
				&& newSelection instanceof InstanceSelection) {
			// combine scene selections
			Set<Object> values = new LinkedHashSet<Object>();

			values.addAll(((InstanceSelection) oldSelection).toList());
			values.addAll(((InstanceSelection) newSelection).toList());

			return new DefaultInstanceSelection(new ArrayList<Object>(values));
		}
		else {
			return newSelection;
		}
	}

	/**
	 * Selects the preferred selection.
	 * 
	 * @param oldSelection the first selection
	 * @param newSelection the second selection
	 * @return the preferred selection
	 */
	public static ISelection preferSelection(ISelection oldSelection, ISelection newSelection) {
		if (newSelection == null)
			return oldSelection;
		else if (oldSelection == null)
			return newSelection;

		// FIXME decide on which basis?

		// XXX for now, always return the new selection
		return newSelection;
	}

	/**
	 * Get the selection for a given polygon on the screen.
	 * 
	 * @param poly the polygon
	 * @return a selection or <code>null</code>
	 */
	public ISelection getSelection(java.awt.Polygon poly) {
		Set<InstanceWaypoint> wps = findWaypoints(poly);
		return createSelection(wps);
	}

	/**
	 * Get the selection for a given rectangle on the screen.
	 * 
	 * @param rect the rectangle
	 * @return a selection or <code>null</code>
	 */
	public ISelection getSelection(Rectangle rect) {
		Set<InstanceWaypoint> wps = findWaypoints(rect);
		return createSelection(wps);
	}

	private ISelection createSelection(Set<InstanceWaypoint> wps) {
		if (wps != null && !wps.isEmpty()) {
			List<InstanceReference> values = new ArrayList<InstanceReference>(wps.size());
			for (InstanceWaypoint wp : wps) {
				values.add(wp.getValue());
			}
			return new DefaultInstanceSelection(values);
		}

		return null;
	}

	/**
	 * Get the selection for the given point.
	 * 
	 * @param point the point (viewport coordinates)
	 * @return a selection or <code>null</code>
	 */
	public ISelection getSelection(java.awt.Point point) {
		InstanceWaypoint wp = findWaypoint(point);
		if (wp != null) {
			return new DefaultInstanceSelection(wp.getValue());
		}
		return null;
	}

	/**
	 * @see ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (!(selection instanceof InstanceSelection)) {
			// only accept instance selections
			return;
		}

		// called when the selection has changed, to update the state of the
		// way-points
		Refresher refresh = prepareRefresh(false);
		refresh.setImageOp(new FastBlurFilter(2));

		// collect instance references that are in the new selection
		Set<InstanceReference> selected = collectReferences(selection);

		Set<InstanceReference> toSelect = new HashSet<InstanceReference>();
		toSelect.addAll(selected);
		toSelect.removeAll(lastSelected);

		// select all that previously have not been selected but are in the new
		// selection
		for (InstanceReference selRef : toSelect) {
			InstanceWaypoint wp = findWaypoint(selRef);
			if (wp != null) {
				wp.setSelected(true, refresh);
			}
		}

		// unselect all that have previously been selected but are not in the
		// new selection
		lastSelected.removeAll(selected);
		for (InstanceReference selRef : lastSelected) {
			InstanceWaypoint wp = findWaypoint(selRef);
			if (wp != null) {
				wp.setSelected(false, refresh);
			}
		}

		lastSelected = selected;

		refresh.execute();
	}

	private Set<InstanceReference> collectReferences(ISelection selection) {
		Set<InstanceReference> selected = new HashSet<InstanceReference>();
		if (selection instanceof IStructuredSelection) {
			for (Object element : ((IStructuredSelection) selection).toList()) {
				if (element instanceof InstanceReference) {
					selected.add((InstanceReference) element);
				}
				else if (element instanceof Instance) {
					Instance instance = (Instance) element;
					InstanceReference ref;
					try {
						ref = instanceService.getReference(instance);// ,
																		// dataSet);
					} catch (IllegalArgumentException iae) {
						// instance has no dataset set
						ref = new PseudoInstanceReference(instance);
					}
					if (ref != null) {
						selected.add(ref);
					}
				}
			}
		}
		return selected;
	}

	/**
	 * @see GenericWaypointPainter#clearWaypoints()
	 */
	@Override
	public void clearWaypoints() {
		super.clearWaypoints();

		lastSelected.clear();
	}

	/**
	 * @return the styleListener
	 */
	public StyleServiceListener getStyleListener() {
		return styleListener;
	}

	/**
	 * @return the geometryListener
	 */
	public GeometrySchemaServiceListener getGeometryListener() {
		return geometryListener;
	}

	/**
	 * @see ClipPainter#setClip(Clip)
	 */
	@Override
	public void setClip(Clip clip) {
		this.clip = clip;
	}

	/**
	 * @see AbstractTileOverlayPainter#drawOverlay(Graphics2D, BufferedImage,
	 *      int, int, int, int, int, Rectangle, PixelConverter)
	 */
	@Override
	protected void drawOverlay(Graphics2D gfx, BufferedImage img, int zoom, int tilePosX,
			int tilePosY, int tileWidth, int tileHeight, Rectangle viewportBounds,
			PixelConverter converter) {
		if (clip == null) {
			super.drawOverlay(gfx, img, zoom, tilePosX, tilePosY, tileWidth, tileHeight,
					viewportBounds, converter);
		}
		else {
			Shape clipShape = clip.getClip(viewportBounds, tilePosX, tilePosY, tileWidth,
					tileHeight);
			if (clipShape != null) { // drawing allowed
				Shape orgClip = gfx.getClip();
				gfx.clip(clipShape);
				super.drawOverlay(gfx, img, zoom, tilePosX, tilePosY, tileWidth, tileHeight,
						viewportBounds, converter);
				gfx.setClip(orgClip);
			}
		}
	}

	/**
	 * @see GenericWaypointPainter#findWaypoint(Object)
	 */
	@Override
	public InstanceWaypoint findWaypoint(InstanceReference object) {
		return super.findWaypoint(object);
	}

	/**
	 * @see InstanceServiceListener#transformationToggled(boolean)
	 */
	@Override
	public void transformationToggled(boolean enabled) {
		// ignore
	}

	/**
	 * @see InstanceServiceListener#datasetAboutToChange(DataSet)
	 */
	@Override
	public void datasetAboutToChange(DataSet type) {
		// ignore
	}

}
