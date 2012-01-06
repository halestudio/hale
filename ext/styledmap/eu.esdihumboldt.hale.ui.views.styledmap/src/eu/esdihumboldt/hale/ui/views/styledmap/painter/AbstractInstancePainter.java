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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.geotools.referencing.CRS;
import org.jdesktop.swingx.mapviewer.GeoPosition;
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
import de.fhg.igd.mapviewer.marker.BoundingBoxMarker;
import de.fhg.igd.mapviewer.marker.Marker;
import de.fhg.igd.mapviewer.marker.SimpleCircleMarker;
import de.fhg.igd.mapviewer.waypoints.CustomWaypointPainter;
import de.fhg.igd.mapviewer.waypoints.GenericWaypoint;
import de.fhg.igd.mapviewer.waypoints.GenericWaypointPainter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.ui.geometry.GeometryUtil;
import eu.esdihumboldt.hale.ui.service.instance.DataSet;
import eu.esdihumboldt.hale.ui.service.instance.InstanceReference;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.service.instance.InstanceServiceListener;

/**
 * Abstract instance painter implementation based on an {@link InstanceService}.
 * @author Simon Templer
 */
public abstract class AbstractInstancePainter extends
		GenericWaypointPainter<InstanceReference, InstanceWaypoint> implements InstanceServiceListener {
	
	private static final ALogger log = ALoggerFactory.getLogger(AbstractInstancePainter.class);

	private final InstanceService instanceService;
	
	private final DataSet dataSet;
	
	private CoordinateReferenceSystem waypointCRS;

	/**
	 * Create an instance painter.
	 * @param instanceService the instance service
	 * @param dataSet the data set
	 */
	public AbstractInstancePainter(InstanceService instanceService,
			DataSet dataSet) {
		super();
		this.instanceService = instanceService;
		this.dataSet = dataSet;
		
		instanceService.addListener(this); //XXX instead only install when visible and active?!
	}

	/**
	 * @see InstanceServiceListener#datasetChanged(DataSet)
	 */
	@Override
	public void datasetChanged(DataSet type) {
		if (type == dataSet) {
			resetWaypoints();
		}
	}

	/**
	 * Get the CRS for use in way-point bounding boxes. 
	 * @return the way-point CRS
	 */
	public CoordinateReferenceSystem getWaypointCRS() {
		if (waypointCRS == null) {
			try {
				waypointCRS = CRS.decode("EPSG:" + GenericWaypoint.COMMON_EPSG);
			} catch (Throwable e) {
				throw new IllegalStateException("Could not decode way-point CRS", e);
			}
		}
		
		return waypointCRS;
	}
	
	/**
	 * Reset way-points to those currently in the instance service.
	 */
	private void resetWaypoints() {
		clearWaypoints();
		
		//XXX only mappable type instances for source?!
		InstanceCollection instances = instanceService.getInstances(dataSet);
		
		// add way-points for instances 
		ResourceIterator<Instance> it = instances.iterator();
		Refresher refresh = prepareRefresh();
		try {
			while (it.hasNext()) {
				Instance instance = it.next();
				
				InstanceWaypoint wp = createWaypoint(instance, instanceService);
				
				if (wp != null) {
					addWaypoint(wp, refresh);
				}
			}
		} finally {
			it.close();
			refresh.execute();
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
		InstanceReference ref = instanceService.getReference(instance, getDataSet());
		
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
						//FIXME obtain converter through cache!
						GeoConverter conv = new GeoConverter(crs, getWaypointCRS());

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
		
		// create the way-point
		//XXX in abstract method?
		InstanceWaypoint wp = new InstanceWaypoint(pos, bb, ref, geometries);
		
		//FIXME improve, externalize!
		wp.setMarker(new AbstractInstanceMarker() {
			
			@Override
			protected Color getPaintColor(InstanceWaypoint context) {
				return Color.RED;
			}

			@Override
			protected Color getBorderColor(InstanceWaypoint context) {
				return Color.BLUE;
			}
		});
		
		return wp;
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
	 * @see CustomWaypointPainter#dispose()
	 */
	@Override
	public void dispose() {
		instanceService.removeListener(this); //XXX instead only install when visible and active?!
		
		super.dispose();
	}
	
}
