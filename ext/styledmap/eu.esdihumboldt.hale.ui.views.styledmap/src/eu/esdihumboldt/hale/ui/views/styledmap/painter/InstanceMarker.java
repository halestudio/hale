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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.IllegalGeoPositionException;
import org.jdesktop.swingx.mapviewer.PixelConverter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import de.fhg.igd.geom.BoundingBox;
import de.fhg.igd.geom.Point3D;
import de.fhg.igd.geom.shape.Line2D;
import de.fhg.igd.geom.shape.Surface;
import de.fhg.igd.mapviewer.marker.AbstractMarker;
import de.fhg.igd.mapviewer.marker.BoundingBoxMarker;
import de.fhg.igd.mapviewer.marker.Marker;
import de.fhg.igd.mapviewer.marker.SimpleCircleMarker;
import de.fhg.igd.mapviewer.marker.area.Area;
import de.fhg.igd.mapviewer.marker.area.MultiArea;
import de.fhg.igd.mapviewer.marker.area.PolygonArea;
import de.fhg.igd.mapviewer.waypoints.SelectableWaypoint;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.ui.style.StyleHelper;
import eu.esdihumboldt.hale.ui.style.service.internal.StylePreferences;
import eu.esdihumboldt.hale.ui.views.styledmap.util.CRSConverter;
import eu.esdihumboldt.hale.ui.views.styledmap.util.CRSDecode;

/**
 * Instance marker painter.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public class InstanceMarker extends BoundingBoxMarker<InstanceWaypoint> {

	/**
	 * Get the geometry factory instance for internal usage.
	 * 
	 * @return the geometry factory
	 */
	private static GeometryFactory getGeometryFactory() {
		if (geometryFactory == null) {
			geometryFactory = new GeometryFactory();
		}
		return geometryFactory;
	}

	private static final ALogger log = ALoggerFactory.getLogger(InstanceMarker.class);

	private static volatile GeometryFactory geometryFactory;

	/**
	 * Overlap for geometry pixel bounding boxes when checking against graphics
	 * bounds.
	 */
	private static final int GEOMETRY_PIXEL_BB_OVERLAP = 5;

	private final int defaultPointSize = 7;

	/**
	 * Cache for geometry bounding boxes in the map CRS. Will be cleared on map
	 * change.
	 */
	private final Map<Geometry, BoundingBox> geometryMapBBs = new IdentityHashMap<Geometry, BoundingBox>();

	/**
	 * @see AbstractMarker#reset()
	 */
	@Override
	public void reset() {
		synchronized (geometryMapBBs) {
			geometryMapBBs.clear();
		}

		super.reset();
	}

	/**
	 * Reset the marker areas.
	 */
	protected void areaReset() {
		super.reset();
	}

	/**
	 * @see BoundingBoxMarker#doPaintMarker(Graphics2D, SelectableWaypoint,
	 *      PixelConverter, int, int, int, int, int, Rectangle, boolean)
	 */
	@Override
	protected Area doPaintMarker(Graphics2D g, InstanceWaypoint context, PixelConverter converter,
			int zoom, int minX, int minY, int maxX, int maxY, Rectangle gBounds,
			boolean calulateArea) {
		List<Area> areas = (!calulateArea) ? (null) : (new ArrayList<Area>());

		List<GeometryProperty<?>> geometries = context.getGeometries();

		// map CRS
		CoordinateReferenceSystem mapCRS;
		try {
			mapCRS = CRSDecode.getLonLatCRS(converter.getMapEpsg());
			// map (GeoPosition) assumes lon/lat order
		} catch (Throwable e) {
			log.error("Could not decode map CRS", e);
			return null;
		}

		// paint each geometry
		for (GeometryProperty<?> geometry : geometries) {
			Area geometryArea = paintGeometry(g, geometry.getCRSDefinition(),
					geometry.getGeometry(), context, converter, zoom, geometries.size() == 1,
					gBounds, mapCRS, calulateArea);
			if (areas != null && geometryArea != null) {
				areas.add(geometryArea);
			}
		}

		if (areas == null) {
			return null;
		}
		if (areas.size() == 1) {
			return areas.get(0);
		}
		else if (!areas.isEmpty()) {
			return new MultiArea(areas);
		}

		return null;
	}

	/**
	 * Paint a geometry.
	 * 
	 * @param g the graphics to paint on
	 * @param crsDefinition the CRS definition associated with the geometry
	 * @param geometry the geometry
	 * @param context the context
	 * @param converter the pixel converter
	 * @param zoom the zoom level
	 * @param singleGeometry if this is the only geometry associated to the
	 *            marker
	 * @param gBounds the graphics bounds
	 * @param mapCRS the map coordinate reference system
	 * @param calculateArea if the area representing the marker should be
	 *            calculated, if <code>false</code> is given here the return
	 *            value is ignored and should be <code>null</code>
	 * @return the area the geometry occupies (in pixel coordinates), or
	 *         <code>null</code> if nothing has been painted
	 */
	protected Area paintGeometry(Graphics2D g, CRSDefinition crsDefinition, Geometry geometry,
			InstanceWaypoint context, PixelConverter converter, int zoom, boolean singleGeometry,
			Rectangle gBounds, CoordinateReferenceSystem mapCRS, boolean calculateArea) {
		if (geometry instanceof GeometryCollection) {
			// paint each geometry in a geometry collection
			List<Area> areas = (calculateArea) ? (new ArrayList<Area>()) : (null);
			GeometryCollection collection = (GeometryCollection) geometry;
			for (int i = 0; i < collection.getNumGeometries(); i++) {
				Geometry geom = collection.getGeometryN(i);
				Area geomArea = paintGeometry(g, crsDefinition, geom, context, converter, zoom,
						singleGeometry && collection.getNumGeometries() == 1, gBounds, mapCRS,
						calculateArea);
				if (areas != null && geomArea != null) {
					areas.add(geomArea);
				}
			}
			if (areas == null || areas.isEmpty()) {
				return null;
			}
			else {
				return new MultiArea(areas);
			}
		}

		// check if geometry lies inside tile
		// if the area must be calculated we must process all geometries
		// if it is the only geometry the check that was already made is OK
		if (!calculateArea && !singleGeometry) {
			// we can safely return null inside this method, as no area has to
			// be calculated

			// determine bounding box
			BoundingBox geometryBB;
			synchronized (geometryMapBBs) {
				// retrieve cached bounding box
				geometryBB = geometryMapBBs.get(geometry);
				if (geometryBB == null) {
					// if none available, try to calculate BB
					BoundingBox calcBB = BoundingBox.compute(geometry);
					if (calcBB != null && calcBB.checkIntegrity()) {
						try {
							// get CRS converter
							CRSConverter conv = CRSConverter.getConverter(crsDefinition.getCRS(),
									mapCRS);

							// manually convert to map CRS
							geometryBB = conv.convert(calcBB);

							// put BB in cache
							geometryMapBBs.put(geometry, geometryBB);
						} catch (Throwable e) {
							log.error("Error checking geometry bounding box", e);
							return null;
						}
					}
				}
			}

			if (geometryBB != null) {
				try {
					GeoPosition minCorner = new GeoPosition(geometryBB.getMinX(),
							geometryBB.getMinY(), converter.getMapEpsg());
					GeoPosition maxCorner = new GeoPosition(geometryBB.getMaxX(),
							geometryBB.getMaxY(), converter.getMapEpsg());

					// determine pixel coordinates
					Point2D minPixels = converter.geoToPixel(minCorner, zoom);
					Point2D maxPixels = converter.geoToPixel(maxCorner, zoom);

					// geometry pixel bounding box
					int minX = Math.min((int) minPixels.getX(), (int) maxPixels.getX());
					int minY = Math.min((int) minPixels.getY(), (int) maxPixels.getY());
					int maxX = Math.max((int) minPixels.getX(), (int) maxPixels.getX());
					int maxY = Math.max((int) minPixels.getY(), (int) maxPixels.getY());
					// add overlap
					minX -= GEOMETRY_PIXEL_BB_OVERLAP;
					minY -= GEOMETRY_PIXEL_BB_OVERLAP;
					maxX += GEOMETRY_PIXEL_BB_OVERLAP;
					maxY += GEOMETRY_PIXEL_BB_OVERLAP;
					// create bounding box
					Rectangle geometryPixelBB = new Rectangle(minX, minY, maxX - minX, maxY - minY);

					if (!gBounds.intersects(geometryPixelBB)
							&& !gBounds.contains(geometryPixelBB)) {
						// geometry does not lie in tile
						return null;
					}
				} catch (Throwable e) {
					log.error("Error checking geometry bounding box", e);
					return null;
				}
			}
			else {
				return null; // empty or invalid bounding box
			}
		}

		if (geometry instanceof Point) {
			return paintPoint((Point) geometry, g, crsDefinition, context, converter, zoom, mapCRS,
					calculateArea);
		}

		if (geometry instanceof Polygon) {
			return paintPolygon((Polygon) geometry, g, crsDefinition, context, converter, zoom,
					mapCRS, calculateArea);
		}

//		if (geometry instanceof LinearRing) {
//			//TODO any special handling needed?
//		}

		if (geometry instanceof LineString) {
			return paintLine((LineString) geometry, g, crsDefinition, context, converter, zoom,
					mapCRS, calculateArea);
		}

		return null;
	}

	/**
	 * Paint a point geometry.
	 * 
	 * @param geometry the point
	 * @param g the graphics object to paint on
	 * @param crsDefinition the CRS definition associated to the geometry
	 * @param context the context
	 * @param converter the pixel converter
	 * @param zoom the zoom level
	 * @param mapCRS the map coordinate reference system
	 * @param calculateArea if the area representing the marker should be
	 *            calculated, if <code>false</code> is given here the return
	 *            value is ignored and should be <code>null</code>
	 * @return the point marker area or <code>null</code> if painting failed
	 */
	protected Area paintPoint(Point geometry, Graphics2D g, CRSDefinition crsDefinition,
			InstanceWaypoint context, PixelConverter converter, int zoom,
			CoordinateReferenceSystem mapCRS, boolean calculateArea) {
		try {
			/*
			 * Conversion to map pixel coordinates: Though most of the time the
			 * result will be the origin (0,0), e.g. for way-points representing
			 * a single point, the coordinates may also be different, e.g. for
			 * MultiPoint way-points.
			 */

			// get CRS converter
			CRSConverter conv = CRSConverter.getConverter(crsDefinition.getCRS(), mapCRS);

			// manually convert to map CRS
			Point3D mapPoint = conv.convert(geometry.getX(), geometry.getY(), 0);

			GeoPosition pos = new GeoPosition(mapPoint.getX(), mapPoint.getY(),
					converter.getMapEpsg());
			// determine pixel coordinates
			Point2D point = converter.geoToPixel(pos, zoom);

			int x = (int) point.getX();
			int y = (int) point.getY();

			// TODO support style

			// fall-back: circle
			if (applyFill(g, context)) {
				g.fillOval(x - defaultPointSize / 2, y - defaultPointSize / 2, defaultPointSize,
						defaultPointSize);
			}

			if (applyStroke(g, context)) {
				// TODO respect stroke width?
				g.drawOval(x - defaultPointSize / 2 - 1, y - defaultPointSize / 2 - 1,
						defaultPointSize + 1, defaultPointSize + 1);
			}

			if (calculateArea) {
				return new PolygonArea(new java.awt.Polygon(
						new int[] { x - defaultPointSize / 2 - 1, x + defaultPointSize / 2 + 1,
								x + defaultPointSize / 2 + 1, x - defaultPointSize / 2 - 1 },
						new int[] { y - defaultPointSize / 2 - 1, y - defaultPointSize / 2 - 1,
								y + defaultPointSize / 2 + 1, y + defaultPointSize / 2 + 1 },
						4));
			}
			else {
				return null;
			}
		} catch (Exception e) {
			log.error("Error painting instance point geometry", e);
			return null;
		}
	}

	/**
	 * Paint a polygon geometry.
	 * 
	 * @param geometry the polygon
	 * @param g the graphics object to paint on
	 * @param crsDefinition the CRS definition associated to the geometry
	 * @param context the context
	 * @param converter the pixel converter
	 * @param zoom the zoom level
	 * @param mapCRS the map coordinate reference system
	 * @param calculateArea if the area representing the marker should be
	 *            calculated, if <code>false</code> is given here the return
	 *            value is ignored and should be <code>null</code>
	 * @return the polygon area or <code>null</code> if painting failed
	 */
	protected Area paintPolygon(Polygon geometry, Graphics2D g, CRSDefinition crsDefinition,
			InstanceWaypoint context, PixelConverter converter, int zoom,
			CoordinateReferenceSystem mapCRS, boolean calculateArea) {
		try {
			// get CRS converter
			CRSConverter conv = CRSConverter.getConverter(crsDefinition.getCRS(), mapCRS);

			// exterior
			Coordinate[] coordinates = geometry.getExteriorRing().getCoordinates();
			java.awt.Polygon outerPolygon = createPolygon(coordinates, conv, converter, zoom);

			if (geometry.getNumInteriorRing() > 0) {
				// polygon has interior geometries

				java.awt.geom.Area drawArea = new java.awt.geom.Area(outerPolygon);

				// interior
				for (int i = 0; i < geometry.getNumInteriorRing(); i++) {
					LineString interior = geometry.getInteriorRingN(i);
					java.awt.Polygon innerPolygon = createPolygon(interior.getCoordinates(), conv,
							converter, zoom);
					drawArea.subtract(new java.awt.geom.Area(innerPolygon));
				}

				if (applyFill(g, context)) {
					g.fill(drawArea);
				}

				if (applyStroke(g, context)) {
					g.draw(drawArea);
				}

				if (calculateArea) {
					return new AdvancedPolygonArea(drawArea, outerPolygon);
				}
			}
			else {
				// polygon has no interior
				// use polygon instead of Area for painting, as painting small
				// Areas sometimes produces strange results (some are not
				// visible)
				if (applyFill(g, context)) {
					g.fill(outerPolygon);
				}

				if (applyStroke(g, context)) {
					g.draw(outerPolygon);
				}

				if (calculateArea) {
					return new PolygonArea(outerPolygon);
				}
			}

			return null; // no calculateArea set
		} catch (Exception e) {
			log.error("Error painting instance polygon geometry", e);
			return null;
		}
	}

	private java.awt.Polygon createPolygon(Coordinate[] coordinates, CRSConverter geoConverter,
			PixelConverter pixelConverter, int zoom)
					throws TransformException, IllegalGeoPositionException {
		java.awt.Polygon result = new java.awt.Polygon();
		for (Coordinate coord : coordinates) {
			// manually convert to map CRS
			Point3D mapPoint = geoConverter.convert(coord.x, coord.y, 0);

			GeoPosition pos = new GeoPosition(mapPoint.getX(), mapPoint.getY(),
					pixelConverter.getMapEpsg());
			Point2D point = pixelConverter.geoToPixel(pos, zoom);

			result.addPoint((int) point.getX(), (int) point.getY());
		}
		return result;
	}

	/**
	 * Paint a line string geometry.
	 * 
	 * @param geometry the line string
	 * @param g the graphics object to paint on
	 * @param crsDefinition the CRS definition associated to the geometry
	 * @param context the context
	 * @param converter the pixel converter
	 * @param zoom the zoom level
	 * @param mapCRS the map coordinate reference system
	 * @param calculateArea if the area representing the marker should be
	 *            calculated, if <code>false</code> is given here the return
	 *            value is ignored and should be <code>null</code>
	 * @return the polygon area or <code>null</code> if painting failed
	 */
	protected Area paintLine(LineString geometry, Graphics2D g, CRSDefinition crsDefinition,
			InstanceWaypoint context, PixelConverter converter, int zoom,
			CoordinateReferenceSystem mapCRS, boolean calculateArea) {
		Coordinate[] coordinates = geometry.getCoordinates();
		if (coordinates.length <= 0) {
			return null;
		}
		if (coordinates.length == 1) {
			// fall back to point drawing
			Point point = getGeometryFactory().createPoint(coordinates[0]);
			return paintPoint(point, g, crsDefinition, context, converter, zoom, mapCRS,
					calculateArea);
		}

		try {
			// get CRS converter
			CRSConverter conv = CRSConverter.getConverter(crsDefinition.getCRS(), mapCRS);

			List<Point2D> mapPoints = new ArrayList<Point2D>(coordinates.length);
			for (Coordinate coord : coordinates) {
				// manually convert to map CRS
				Point3D mapPoint = conv.convert(coord.x, coord.y, 0);

				GeoPosition pos = new GeoPosition(mapPoint.getX(), mapPoint.getY(),
						converter.getMapEpsg());
				Point2D point = converter.geoToPixel(pos, zoom);

				mapPoints.add(point);
			}

			if (applyStroke(g, context)) {
				for (int i = 0; i < mapPoints.size() - 1; i++) {
					// draw each connecting line
					Point2D p1 = mapPoints.get(i);
					Point2D p2 = mapPoints.get(i + 1);
					g.drawLine((int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY());
				}
			}
			else {
				log.warn("Stroke disabled in style, LineString is not rendered");
			}

			if (!calculateArea) {
				return null;
			}

			// use a buffer around the line as area
			java.awt.Polygon[] buffer = createBufferPolygon(mapPoints, 3); // XXX
																			// buffer
																			// size
																			// is
																			// in
																			// pixels,
																			// which
																			// value
																			// is
																			// ok?
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
	 * 
	 * @param linePoints the points defining the line
	 * @param distance the buffer size
	 * @return the buffer polygon(s)
	 */
	private static java.awt.Polygon[] createBufferPolygon(List<Point2D> linePoints,
			double distance) {
		// create metamodel line
		de.fhg.igd.geom.Point2D[] convertedLinePoints = new de.fhg.igd.geom.Point2D[linePoints
				.size()];

		int index = 0;
		for (Point2D point : linePoints) {
			convertedLinePoints[index] = new de.fhg.igd.geom.Point2D(point.getX(), point.getY());
			index++;
		}

		Line2D line = new Line2D(convertedLinePoints);

		Surface buffer = line.computeBuffer(distance);
		return buffer.toAWTPolygons(1, 1, new de.fhg.igd.geom.Point2D(0, 0));
	}

	/**
	 * @see BoundingBoxMarker#getFallbackMarker(SelectableWaypoint)
	 */
	@Override
	protected Marker<? super InstanceWaypoint> getFallbackMarker(InstanceWaypoint context) {
		return new SimpleCircleMarker(7, getPaintColor(context), getBorderColor(context),
				Color.BLACK, false);
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
	 * Get the stroke for drawing lines.
	 * 
	 * @param context the context
	 * @return the stroke
	 */
	protected java.awt.Stroke getLineStroke(InstanceWaypoint context) {
		if (context.isSelected()) {
			return new BasicStroke(StylePreferences.getSelectionWidth());
		}
		else {
			return new BasicStroke(StylePreferences.getDefaultWidth());
		}
	}

	/**
	 * @see BoundingBoxMarker#applyStroke(Graphics2D, SelectableWaypoint)
	 */
	@Override
	protected boolean applyStroke(Graphics2D g, InstanceWaypoint context) {
		g.setStroke(getLineStroke(context));
		g.setColor(getBorderColor(context));

		return true;
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
		return StylePreferences.getDefaultColor(context.getValue().getDataSet());
	}

}
