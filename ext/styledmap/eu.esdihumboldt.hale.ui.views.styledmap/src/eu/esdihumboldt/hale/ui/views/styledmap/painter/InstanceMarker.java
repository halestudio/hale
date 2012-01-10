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
import java.util.Collection;
import java.util.List;

import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.PixelConverter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import de.cs3d.common.metamodel.Point3D;
import de.cs3d.common.metamodel.shape.Line2D;
import de.cs3d.common.metamodel.shape.Surface;
import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import de.fhg.igd.mapviewer.marker.BoundingBoxMarker;
import de.fhg.igd.mapviewer.marker.Marker;
import de.fhg.igd.mapviewer.marker.SimpleCircleMarker;
import de.fhg.igd.mapviewer.marker.area.Area;
import de.fhg.igd.mapviewer.marker.area.MultiArea;
import de.fhg.igd.mapviewer.marker.area.PolygonArea;
import de.fhg.igd.mapviewer.waypoints.SelectableWaypoint;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.ui.style.StyleHelper;
import eu.esdihumboldt.hale.ui.style.service.internal.StylePreferences;
import eu.esdihumboldt.hale.ui.views.styledmap.util.CRSConverter;
import eu.esdihumboldt.hale.ui.views.styledmap.util.CRSDecode;

/**
 * Instance marker painter.
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public class InstanceMarker extends BoundingBoxMarker<InstanceWaypoint> {
	
	/**
	 * Get the geometry factory instance for internal useage.
	 * @return the geometry factory
	 */
	private static GeometryFactory getGeometryFactory() {
		if (geometryFactory == null) {
			geometryFactory = new GeometryFactory();
		}
		return geometryFactory;
	}
	
	private static final ALogger log = ALoggerFactory.getLogger(InstanceMarker.class);
	
	private static GeometryFactory geometryFactory;
	
	private final int defaultPointSize = 7;
	
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
		
		if (areas.size() == 1) {
			return areas.get(0);
		}
		else if (!areas.isEmpty()) {
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
			return paintPoint((Point) geometry, g, crsDefinition, context, 
					converter, zoom);
		}
		
		if (geometry instanceof Polygon) {
			return paintPolygon((Polygon) geometry, g, crsDefinition, context, 
					converter, zoom);
		}
		
//		if (geometry instanceof LinearRing) {
//			//TODO any special handling needed?
//		}
		
		if (geometry instanceof LineString) {
			return paintLine((LineString) geometry, g, crsDefinition, context, 
					converter, zoom);
		}
		
		return null;
	}

	/**
	 * Paint a point geometry.
	 * @param geometry the point
	 * @param g the graphics object to paint on
	 * @param crsDefinition the CRS definition associated to the geometry
	 * @param context the context
	 * @param converter the pixel converter
	 * @param zoom the zoom level
	 * @return the point marker area or <code>null</code> if painting failed
	 */
	protected Area paintPoint(Point geometry, Graphics2D g,
			CRSDefinition crsDefinition, InstanceWaypoint context,
			PixelConverter converter, int zoom) {
		try {
			// map CRS
			CoordinateReferenceSystem mapCRS = CRSDecode.getCRS(converter.getMapEpsg());
			
			// get CRS converter
			CRSConverter conv = CRSConverter.getConverter(crsDefinition.getCRS(), mapCRS);
			
			// manually convert to map CRS
			Point3D mapPoint = conv.convert(geometry.getX(), geometry.getY(), 0);
			
			GeoPosition pos = new GeoPosition(mapPoint.getX(), 
					mapPoint.getY(), converter.getMapEpsg());
			// determine pixel coordinates
			Point2D point = converter.geoToPixel(pos , zoom);
	
			int x = (int) point.getX();
			int y = (int) point.getY();
			
			//TODO support style
			
			// fall-back: circle
			if (applyFill(g, context)) {
				g.fillOval(
						x - defaultPointSize / 2, 
						y - defaultPointSize / 2, 
						defaultPointSize, 
						defaultPointSize);
			}
			
			if (applyStroke(g, context)) {
				//TODO respect stroke width?
				g.drawOval(
						x - defaultPointSize / 2 - 1, 
						y - defaultPointSize / 2 - 1, 
						defaultPointSize + 1, 
						defaultPointSize + 1);
			}
			
			return new PolygonArea(new java.awt.Polygon(
					new int[]{x - defaultPointSize / 2 - 1, x + defaultPointSize / 2 + 1, x + defaultPointSize / 2 + 1, x - defaultPointSize / 2 - 1},
					new int[]{y - defaultPointSize / 2 - 1, y - defaultPointSize / 2 - 1, y + defaultPointSize / 2 + 1, y + defaultPointSize / 2 + 1}, 4));
		} catch (Exception e) {
			log.error("Error painting instance point geometry", e);
			return null;
		}
	}

	/**
	 * Paint a polygon geometry.
	 * @param geometry the polygon
	 * @param g the graphics object to paint on
	 * @param crsDefinition the CRS definition associated to the geometry
	 * @param context the context
	 * @param converter the pixel converter
	 * @param zoom the zoom level
	 * @return the polygon area or <code>null</code> if painting failed
	 */
	protected Area paintPolygon(Polygon geometry, Graphics2D g,
			CRSDefinition crsDefinition, InstanceWaypoint context,
			PixelConverter converter, int zoom) {
		// create polygon for drawing
		java.awt.Polygon drawPolygon = new java.awt.Polygon();
		
		try {
			// map CRS
			CoordinateReferenceSystem mapCRS = CRSDecode.getCRS(converter.getMapEpsg());
			
			// get CRS converter
			CRSConverter conv = CRSConverter.getConverter(crsDefinition.getCRS(), mapCRS);
			
			Coordinate[] coordinates = geometry.getCoordinates();
			for (Coordinate coord : coordinates) {
				// manually convert to map CRS
				Point3D mapPoint = conv.convert(coord.x, coord.y, 0);
				
				GeoPosition pos = new GeoPosition(mapPoint.getX(), 
						mapPoint.getY(), converter.getMapEpsg());
				Point2D point = converter.geoToPixel(pos , zoom);
				
				drawPolygon.addPoint((int) point.getX(), (int) point.getY());
			}
			
			if (applyFill(g, context)) {
				g.fillPolygon(drawPolygon);
			}

			if (applyStroke(g, context)) {
				g.drawPolygon(drawPolygon);
			}
			
			return new PolygonArea(drawPolygon);
		} catch (Exception e) {
			log.error("Error painting instance polygon geometry", e);
			return null;
		}
	}
	
	/**
	 * Paint a line string geometry.
	 * @param geometry the line string
	 * @param g the graphics object to paint on
	 * @param crsDefinition the CRS definition associated to the geometry
	 * @param context the context
	 * @param converter the pixel converter
	 * @param zoom the zoom level
	 * @return the polygon area or <code>null</code> if painting failed
	 */
	protected Area paintLine(LineString geometry, Graphics2D g,
			CRSDefinition crsDefinition, InstanceWaypoint context,
			PixelConverter converter, int zoom) {
		Coordinate[] coordinates = geometry.getCoordinates();
		if (coordinates.length <= 0) {
			return null;
		}
		if (coordinates.length == 1) {
			// fall back to point drawing
			Point point = getGeometryFactory().createPoint(coordinates[0]);
			return paintPoint(point, g, crsDefinition, context, converter, zoom);
		}
		
		try {
			// map CRS
			CoordinateReferenceSystem mapCRS = CRSDecode.getCRS(converter.getMapEpsg());
			
			// get CRS converter
			CRSConverter conv = CRSConverter.getConverter(crsDefinition.getCRS(), mapCRS);
			
			List<Point2D> mapPoints = new ArrayList<Point2D>(coordinates.length);
			for (Coordinate coord : coordinates) {
				// manually convert to map CRS
				Point3D mapPoint = conv.convert(coord.x, coord.y, 0);
				
				GeoPosition pos = new GeoPosition(mapPoint.getX(), 
						mapPoint.getY(), converter.getMapEpsg());
				Point2D point = converter.geoToPixel(pos , zoom);
				
				mapPoints.add(point);
			}
			
			if (applyStroke(g, context)) {
				for (int i = 0; i < mapPoints.size() - 1; i++) {
					// draw each connecting line
					Point2D p1 = mapPoints.get(i);
					Point2D p2 = mapPoints.get(i + 1);
					g.drawLine(
							(int) p1.getX(), 
							(int) p1.getY(), 
							(int) p2.getX(), 
							(int) p2.getY());
				}
			}
			else {
				log.warn("Stroke disabled in style, LineString is not rednered");
			}
			
			// use a buffer around the line as area
			java.awt.Polygon[] buffer = createBufferPolygon(mapPoints, 3); //XXX buffer size is in pixels, which value is ok?
			if (buffer.length == 0) {
				return null;
			}
			else if (buffer.length == 1) {
				return new PolygonArea(buffer[0]);
			}
			else {
				Collection<Area> areas = new ArrayList<Area>();
				for (java.awt.Polygon bufferPoly : buffer) {
					areas.add(new PolygonArea(bufferPoly));
				}
				return new MultiArea(areas);
			}
		} catch (Exception e) {
			log.error("Error painting instance polygon geometry", e);
			return null;
		}
	}
	
	/**
	 * Create a buffered polygon for the given line and a distance.
	 * @param linePoints the points defining the line 
	 * @param distance the buffer size
	 * @return the buffer polygon(s)
	 */
	private static java.awt.Polygon[] createBufferPolygon(List<Point2D> linePoints, double distance) {
		// create metamodel line
		de.cs3d.common.metamodel.Point2D[] convertedLinePoints = new de.cs3d.common.metamodel.Point2D[linePoints.size()];
		
		int index = 0;
		for (Point2D point : linePoints) {
			convertedLinePoints[index] = new de.cs3d.common.metamodel.Point2D(point.getX(), point.getY());
			index++;
		}
		
		Line2D line = new Line2D(convertedLinePoints);
		
		Surface buffer = line.computeBuffer(distance);
		return buffer.toAWTPolygons(1, 1, new de.cs3d.common.metamodel.Point2D(0, 0));
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

	/**
	 * @see BoundingBoxMarker#getPaintColor(SelectableWaypoint)
	 */
	@Override
	protected Color getPaintColor(InstanceWaypoint context) {
		// default color with applied transparency
		Color color = getBorderColor(context);
		return new Color(color.getRed(), color.getGreen(), color.getRed(), 
				(int) (255 * StyleHelper.DEFAULT_FILL_OPACITY));
	}

	/**
	 * @see BoundingBoxMarker#getBorderColor(SelectableWaypoint)
	 */
	@Override
	protected Color getBorderColor(InstanceWaypoint context) {
		if (context.isSelected()) {
			// get selection color
			return StylePreferences.getSelectionColor();
		}
		// get default color
		return StylePreferences.getDefaultColor();
	}

}
