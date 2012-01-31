/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.views.styledmap.painter;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.PixelConverter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import de.cs3d.common.metamodel.Point3D;
import de.cs3d.common.metamodel.helperGeometry.BoundingBox;
import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import de.fhg.igd.mapviewer.AbstractTileOverlayPainter;
import de.fhg.igd.mapviewer.marker.Marker;
import de.fhg.igd.mapviewer.waypoints.GenericWaypoint;
import de.fhg.igd.mapviewer.waypoints.GenericWaypointPainter;
import de.fhg.igd.mapviewer.waypoints.MarkerWaypointRenderer;
import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.geometry.GeometryUtil;
import eu.esdihumboldt.hale.ui.geometry.service.GeometrySchemaServiceListener;
import eu.esdihumboldt.hale.ui.selection.InstanceSelection;
import eu.esdihumboldt.hale.ui.selection.impl.DefaultInstanceSelection;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.service.instance.InstanceServiceListener;
import eu.esdihumboldt.hale.ui.style.service.StyleService;
import eu.esdihumboldt.hale.ui.style.service.StyleServiceListener;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.Clip;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.ClipPainter;
import eu.esdihumboldt.hale.ui.views.styledmap.util.CRSConverter;
import eu.esdihumboldt.hale.ui.views.styledmap.util.CRSDecode;

/**
 * Abstract instance painter implementation based on an {@link InstanceService}.
 * @author Simon Templer
 */
public abstract class AbstractInstancePainter extends
		GenericWaypointPainter<InstanceReference, InstanceWaypoint> implements InstanceServiceListener,
		ISelectionListener, ClipPainter {
	
	private static final ALogger log = ALoggerFactory.getLogger(AbstractInstancePainter.class);

	private static final double BUFFER_VALUE = 0.0125;

	private final InstanceService instanceService;
	
	private final DataSet dataSet;
	
	private CoordinateReferenceSystem waypointCRS;
	
	private Clip clip;

	private final StyleServiceListener styleListener;
	
	private final GeometrySchemaServiceListener geometryListener;

	private Set<InstanceReference> lastSelected = new HashSet<InstanceReference>();

	/**
	 * Create an instance painter.
	 * @param instanceService the instance service
	 * @param dataSet the data set
	 */
	public AbstractInstancePainter(InstanceService instanceService,
			DataSet dataSet) {
		super(new MarkerWaypointRenderer<InstanceWaypoint>(), 
				4); // four worker threads
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
				//TODO only update way-points that are affected XXX save type def in way-point?
				//XXX for now: recreate all
				update(null);
				//TODO do it in a job or something like that to prevent the UI being blocked?
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
	 * Do a complete update of the way-points.
	 * Existing way-points are discarded.
	 * @param selection the current selection
	 */
	public void update(ISelection selection) {
		clearWaypoints();
		
		//XXX only mappable type instances for source?!
		InstanceCollection instances = instanceService.getInstances(dataSet);
		
		if (selection != null) {
			lastSelected = collectReferences(selection);
		}
		
		// add way-points for instances 
		ResourceIterator<Instance> it = instances.iterator();
		try {
			while (it.hasNext()) {
				Instance instance = it.next();
				
				InstanceWaypoint wp = createWaypoint(instance, instanceService);
				
				if (wp != null) {
					if (lastSelected.contains(wp.getValue())) {
						wp.setSelected(true, null); // refresh can be ignored because it's done for addWaypoint
					}
					addWaypoint(wp, null); // no refresher, as refreshAll is executed
				}
			}
		} finally {
			it.close();
			refreshAll();
		}
	}

	/**
	 * Create a way-point for an instance
	 * @param instance the instance
	 * @param instanceService the instance service
	 * @return the created way-point or <code>null</code> if 
	 */
	protected InstanceWaypoint createWaypoint(Instance instance,
			InstanceService instanceService) {
		// retrieve instance reference
		InstanceReference ref = instanceService.getReference(instance);//, getDataSet());
		
		BoundingBox bb = null;
		List<GeometryProperty<?>> geometries = new ArrayList<GeometryProperty<?>>(GeometryUtil.getDefaultGeometries(instance));
		ListIterator<GeometryProperty<?>> it = geometries.listIterator();
		while (it.hasNext()) {
			GeometryProperty<?> prop = it.next();
			
			// check if geometry is valid for display in map
			CoordinateReferenceSystem crs = (prop.getCRSDefinition() == null)
					?(null):(prop.getCRSDefinition().getCRS());
			
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
						log.error("Error converting instance bounding box to waypoint bounding box", e);
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
		bb.setMinZ(- BUFFER_VALUE);
		bb.setMaxZ(BUFFER_VALUE);
		
		// create the way-point
		//XXX in abstract method?
		InstanceWaypoint wp = new InstanceWaypoint(pos, bb, ref, geometries);
		
		// each way-point must have its own marker, as the marker stores the marker areas
		wp.setMarker(createMarker());
		
		return wp;
	}

	/**
	 * Create a new marker for a way-point.
	 * @return the marker
	 */
	private Marker<? super InstanceWaypoint> createMarker() {
//		return new InstanceMarker();
		return new StyledInstanceMarker();
	}

	/**
	 * Determine the bounding box for a geometry.
	 * @param geometry the geometry
	 * @return the bounding box or <code>null</code> if it is either an empty
	 *   geometry or the bounding box cannot be determined
	 */
	private BoundingBox getBoundingBox(Geometry geometry) {
		Geometry envelope = geometry.getEnvelope();
		if (envelope instanceof Point) {
			Point point = (Point) envelope;
			if (!point.isEmpty()) { // not an empty geometry
				// a bounding box representing the point
				return new BoundingBox(
						point.getX(), point.getY(), 0, 
						point.getX(), point.getY(), 0);
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
		
		if (oldSelection instanceof InstanceSelection && newSelection instanceof InstanceSelection) {
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
	 * @param oldSelection the first selection
	 * @param newSelection the second selection
	 * @return the preferred selection
	 */
	public static ISelection preferSelection(ISelection oldSelection, ISelection newSelection) {
		if (newSelection == null)
			return oldSelection;
		else if (oldSelection == null)
			return newSelection;
		
		//FIXME decide on which basis?
		
		//XXX for now, always return the new selection
		return newSelection;
	}

	/**
	 * Get the selection for a given polygon on the screen.
	 * @param poly the polygon
	 * @return a selection or <code>null</code>
	 */
	public ISelection getSelection(java.awt.Polygon poly) {
		Set<InstanceWaypoint> wps = findWaypoints(poly);
		return createSelection(wps);
	}

	/**
	 * Get the selection for a given rectangle on the screen.
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
		
		// called when the selection has changed, to update the state of the way-points
		Refresher refresh = prepareRefresh(false);
		
		// collect instance references that are in the new selection
		Set<InstanceReference> selected = collectReferences(selection);
		
		Set<InstanceReference> toSelect = new HashSet<InstanceReference>();
		toSelect.addAll(selected);
		toSelect.removeAll(lastSelected);
		
		// select all that previously have not been selected but are in the new selection
		for (InstanceReference selRef : toSelect) {
			InstanceWaypoint wp = findWaypoint(selRef);
			if (wp != null) {
				wp.setSelected(true, refresh);
			}
		}
		
		// unselect all that have previously been selected but are not in the new selection
		lastSelected.removeAll(selected);
		for (InstanceReference selRef : lastSelected) {
			InstanceWaypoint wp =  findWaypoint(selRef);
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
					InstanceReference ref = instanceService.getReference(instance);//, dataSet);
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
	 * @see AbstractTileOverlayPainter#drawOverlay(Graphics2D, BufferedImage, int, int, int, int, int, Rectangle, PixelConverter)
	 */
	@Override
	protected void drawOverlay(Graphics2D gfx, BufferedImage img, int zoom,
			int tilePosX, int tilePosY, int tileWidth, int tileHeight,
			Rectangle viewportBounds, PixelConverter converter) {
		if (clip == null) {
			super.drawOverlay(gfx, img, zoom, tilePosX, tilePosY, tileWidth, tileHeight,
					viewportBounds, converter);
		}
		else {
			Shape clipShape = clip.getClip(viewportBounds, tilePosX, tilePosY, tileWidth, tileHeight);
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

}
