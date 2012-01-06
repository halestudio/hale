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
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.geotools.referencing.CRS;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.PixelConverter;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import de.cs3d.common.metamodel.Point3D;
import de.fhg.igd.mapviewer.marker.BoundingBoxMarker;
import de.fhg.igd.mapviewer.marker.Marker;
import de.fhg.igd.mapviewer.marker.SimpleCircleMarker;
import de.fhg.igd.mapviewer.marker.area.Area;
import de.fhg.igd.mapviewer.marker.area.MultiArea;
import de.fhg.igd.mapviewer.marker.area.PolygonArea;
import de.fhg.igd.mapviewer.waypoints.SelectableWaypoint;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;

/**
 * Instance marker painter.
 * @author Simon Templer
 */
public abstract class AbstractInstanceMarker extends BoundingBoxMarker<InstanceWaypoint> {

	/**
	 * @see BoundingBoxMarker#doPaintMarker(Graphics2D, SelectableWaypoint, PixelConverter, int, int, int, int, int)
	 */
	@Override
	protected Area doPaintMarker(Graphics2D g, InstanceWaypoint context,
			PixelConverter converter, int zoom, int minX, int minY, int maxX,
			int maxY) {
		List<Area> areas = new ArrayList<Area>();

		List<GeometryProperty<?>> geometries = context.getGeometries();
		
		// paint each geometry
		for (GeometryProperty<?> geometry : geometries) {
			Area geometryArea = paintGeometry(g,
					geometry.getCRSDefinition(), 
					geometry.getGeometry(), 
					context, converter, zoom);
			if (geometryArea != null) {
				areas.add(geometryArea);
			}
		}
		
		if (!areas.isEmpty()) {
			return new MultiArea(areas);
		}
		
		//FIXME fall back to bounding box?
		return super.doPaintMarker(g, context, converter, zoom, minX, minY, maxX, maxY);
	}

	/**
	 * Paint a geometry.
	 * @param g the graphics to paint on
	 * @param crsDefinition the CRS definition associated with the geometry
	 * @param geometry the geometry
	 * @param context the context
	 * @param converter the pixel converter
	 * @param zoom the zoom level
	 * @return the area the geometry occupies (in pixel coordinates), or 
	 *   <code>null</code> if nothing has been painted
	 */
	protected Area paintGeometry(Graphics2D g, CRSDefinition crsDefinition, 
			Geometry geometry, InstanceWaypoint context, 
			PixelConverter converter, int zoom) {
		//TODO any special cases?
		
		if (geometry instanceof GeometryCollection) {
			// paint each geometry in a geometry collection
			List<Area> areas = new ArrayList<Area>();
			GeometryCollection collection = (GeometryCollection) geometry;
			for (int i = 0; i < collection.getNumGeometries(); i++) {
				Geometry geom = collection.getGeometryN(i);
				Area geomArea = paintGeometry(g, crsDefinition, geom, context, 
						converter, zoom);
				if (geomArea != null) {
					areas.add(geomArea);
				}
			}
			if (areas.isEmpty()) {
				return null;
			}
			else {
				return new MultiArea(areas);
			}
		}
		
		if (geometry instanceof Point) {
			//FIXME for now returns fall-back marker
			//FIXME but this is wrong! there could be multiple points for one way-point!
			return paintFallback(g, context, converter, zoom);
		}
		
		if (geometry instanceof Polygon) {
			return paintPolygon((Polygon) geometry, g, crsDefinition, context, 
					converter, zoom);
		}
		
		if (geometry instanceof LinearRing) {
			//TODO
			//XXX buffer for line as area?
		}
		
		if (geometry instanceof LineString) {
			//TODO
			//XXX buffer for line as area?
		}
		
		return null;
	}

	/**
	 * @param geometry
	 * @param g
	 * @param crsDefinition
	 * @param context
	 * @param converter
	 * @param zoom
	 * @return
	 */
	protected Area paintPolygon(Polygon geometry, Graphics2D g,
			CRSDefinition crsDefinition, InstanceWaypoint context,
			PixelConverter converter, int zoom) {
		// create polygon for drawing
		java.awt.Polygon drawPolygon = new java.awt.Polygon();
		
		// map CRS
		CoordinateReferenceSystem mapCRS;
		try {
			mapCRS = CRS.decode("EPSG:" + converter.getMapEpsg());
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			return null;
		}
		
		//FIXME instead get from cache
		GeoConverter conv;
		try {
			conv = new GeoConverter(crsDefinition.getCRS(), mapCRS);
		} catch (FactoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		
		Coordinate[] coordinates = geometry.getCoordinates();
		for (Coordinate coord : coordinates) {
			// manually convert to map CRS
			try {
				Point3D mapPoint = conv.convert(coord.x, coord.y, 0);
				
				GeoPosition pos = new GeoPosition(mapPoint.getX(), 
						mapPoint.getY(), converter.getMapEpsg());
				Point2D point = converter.geoToPixel(pos , zoom);
				
				drawPolygon.addPoint((int) point.getX(), (int) point.getY());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return null;
			}
		}
		
		//XXX setup fill TODO extra methods for setup
		g.setPaint(getPaintColor(context));
		g.fillPolygon(drawPolygon);

		//XXX setup stroke TODO extra methods for setup
		g.setColor(getBorderColor(context));
		g.drawPolygon(drawPolygon);
		
		return new PolygonArea(drawPolygon);
	}

	/**
	 * @see BoundingBoxMarker#getFallbackMarker(SelectableWaypoint)
	 */
	@Override
	protected Marker<? super InstanceWaypoint> getFallbackMarker(
			InstanceWaypoint context) {
		return new SimpleCircleMarker(7, getPaintColor(context), 
				getBorderColor(context), Color.BLACK, false);
	}

}
