/*
 * GeoUtil.java
 *
 * Created on June 26, 2006, 10:44 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.mapviewer.util;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;
import org.jdesktop.swingx.mapviewer.TileProvider;
import org.w3c.dom.Document;

/**
 * These are math utilities for converting between pixels, tiles, and geographic
 * coordinates. Implements a Google Maps style mercator projection.
 * 
 * @author joshy
 */
@Deprecated
public final class GeoUtil {

	/**
	 * Get the map size at the given zoom level
	 * 
	 * @param zoom the zoom level
	 * @param info the tile factory info
	 * 
	 * @return the size of the map at the given zoom, in tiles (num tiles tall
	 *         by num tiles wide)
	 */
	public static Dimension getMapSize(int zoom, TileProvider info) {
		return new Dimension(info.getMapWidthInTiles(zoom), info.getMapWidthInTiles(zoom));
	}

	/**
	 * Check if parameters specify a valid tile
	 * 
	 * @param x the tile x ordinate
	 * @param y the tile y ordinate
	 * @param zoomLevel the zoom level
	 * @param info the tile factory info
	 * 
	 * @return true if this point in <em>tiles</em> is valid at this zoom level.
	 *         For example, if the zoom level is 0 (zoomed all the way out,
	 *         where there is only one tile), then x,y must be 0,0
	 */
	public static boolean isValidTile(int x, int y, int zoomLevel, TileFactoryInfo info) {
		// int x = (int)coord.getX();
		// int y = (int)coord.getY();
		// if off the map to the top or left
		if (x < 0 || y < 0) {
			return false;
		}
		// if of the map to the right
		if (info.getMapCenterInPixelsAtZoom(zoomLevel).getX() * 2 <= x
				* info.getTileSize(zoomLevel)) {
			return false;
		}
		// if off the map to the bottom
		if (info.getMapCenterInPixelsAtZoom(zoomLevel).getY() * 2 <= y
				* info.getTileSize(zoomLevel)) {
			return false;
		}
		// if out of zoom bounds
		if (zoomLevel < info.getMinimumZoomLevel() || zoomLevel > info.getMaximumZoomLevel()) {
			return false;
		}
		return true;
	}

	/**
	 * Given a position (latitude/longitude pair) and a zoom level, return the
	 * appropriate point in <em>pixels</em>. The zoom level is necessary because
	 * pixel coordinates are in terms of the zoom level
	 * 
	 * 
	 * @param c A lat/lon pair
	 * @param zoomLevel the zoom level to extract the pixel coordinate for
	 * @param info the tile factory info
	 * 
	 * @return the position on the map in pixels
	 */
	public static Point2D getBitmapCoordinate(GeoPosition c, int zoomLevel, TileFactoryInfo info) {
		return getBitmapCoordinate(c.getY(), c.getX(), zoomLevel, info);
	}

	/**
	 * Given a position (latitude/longitude pair) and a zoom level, return the
	 * appropriate point in <em>pixels</em>. The zoom level is necessary because
	 * pixel coordinates are in terms of the zoom level
	 * 
	 * 
	 * @param latitude
	 * @param longitude
	 * @param zoomLevel the zoom level to extract the pixel coordinate for
	 * @param info the tile factory info
	 * 
	 * @return the position on the map in pixels
	 */
	@SuppressWarnings("javadoc")
	public static Point2D getBitmapCoordinate(double latitude, double longitude, int zoomLevel,
			TileFactoryInfo info) {

		double x = info.getMapCenterInPixelsAtZoom(zoomLevel).getX()
				+ longitude * info.getLongitudeDegreeWidthInPixels(zoomLevel);
		double e = Math.sin(latitude * (Math.PI / 180.0));
		if (e > 0.9999) {
			e = 0.9999;
		}
		if (e < -0.9999) {
			e = -0.9999;
		}
		double y = info.getMapCenterInPixelsAtZoom(zoomLevel).getY()
				+ 0.5 * Math.log((1 + e) / (1 - e)) * -1
						* (info.getLongitudeRadianWidthInPixels(zoomLevel));
		return new Point2D.Double(x, y);
	}

	/**
	 * convert an on screen pixel coordinate and a zoom level to a geo position
	 * 
	 * @param pixelCoordinate the pixel coordinate
	 * @param zoom the zoom level
	 * @param info the tile factory info
	 * 
	 * @return the corresponding geo position in WGS84
	 */
	public static GeoPosition getPosition(Point2D pixelCoordinate, int zoom, TileFactoryInfo info) {
		// p(" --bitmap to latlon : " + coord + " " + zoom);
		double wx = pixelCoordinate.getX();
		double wy = pixelCoordinate.getY();
		// this reverses getBitmapCoordinates
		double flon = (wx - info.getMapCenterInPixelsAtZoom(zoom).getX())
				/ info.getLongitudeDegreeWidthInPixels(zoom);
		double e1 = (wy - info.getMapCenterInPixelsAtZoom(zoom).getY())
				/ (-1 * info.getLongitudeRadianWidthInPixels(zoom));
		double e2 = (2 * Math.atan(Math.exp(e1)) - Math.PI / 2) / (Math.PI / 180.0);
		double flat = e2;
		GeoPosition wc = new GeoPosition(flon, flat, GeoPosition.WGS_84_EPSG);
		return wc;
	}

	/**
	 * Convert a street address into a position. Uses the Yahoo GeoCoder. You
	 * must supply your own yahoo id
	 * 
	 * @param fields an array of Street, City and State (must be a US state)
	 * @throws java.io.IOException if the request fails.
	 * @return the position of this street address
	 */
	public static GeoPosition getPositionForAddress(String[] fields) throws IOException {
		return getPositionForAddress(fields[0], fields[1], fields[2]);
	}

	/**
	 * Convert a street address into a position. Uses the Yahoo GeoCoder. You
	 * must supply your own yahoo id.
	 * 
	 * @param street Street
	 * @param city City
	 * @param state State (must be a US state)
	 * @throws java.io.IOException if the request fails.
	 * @return the position of this street address
	 */
	public static GeoPosition getPositionForAddress(String street, String city, String state)
			throws IOException {
		try {
			URL load = new URL("http://api.local.yahoo.com/MapsService/V1/geocode?"
					+ "appid=joshy688" + "&street=" + street.replace(' ', '+') + "&city="
					+ city.replace(' ', '+') + "&state=" + state.replace(' ', '+'));
			// System.out.println("using address: " + load);
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(load.openConnection().getInputStream());
			XPath xpath = XPathFactory.newInstance().newXPath();
			// NodeList str =
			// (NodeList)xpath.evaluate("//Result",doc,XPathConstants.NODESET);
			Double lat = (Double) xpath.evaluate("//Result/Latitude/text()", doc,
					XPathConstants.NUMBER);
			Double lon = (Double) xpath.evaluate("//Result/Longitude/text()", doc,
					XPathConstants.NUMBER);
			// System.out.println("got address at: " + lat + " " + lon);
			return new GeoPosition(lon, lat, GeoPosition.WGS_84_EPSG);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException(
					"Failed to retrieve location information from the internet: " + e.toString());
		}
	}

	/**
	 * Gets the map bounds.
	 * 
	 * @param mapViewer The map viewer.
	 * @return Returns the bounds.
	 */
	/*
	 * public static GeoBounds getMapBounds(JXMapViewer mapViewer) { return new
	 * GeoBounds(getMapGeoBounds(mapViewer)); }
	 */

	/**
	 * Gets the bounds as a set of two <code>GeoPosition</code> objects.
	 * 
	 * @param mapViewer The map viewer.
	 * @return Returns the set of two <code>GeoPosition</code> objects that
	 *         represent the north west and south east corners of the map.
	 */
	/*
	 * private static Set<GeoPosition> getMapGeoBounds(JXMapViewer mapViewer) {
	 * Set<GeoPosition> set = new HashSet<GeoPosition>(); TileFactory
	 * tileFactory = mapViewer.getTileFactory(); int zoom = mapViewer.getZoom();
	 * Rectangle2D bounds = mapViewer.getViewportBounds(); Point2D pt = new
	 * Point2D.Double(bounds.getX(), bounds.getY());
	 * set.add(tileFactory.pixelToGeo(pt, zoom)); pt = new
	 * Point2D.Double(bounds.getX() + bounds.getWidth(), bounds .getY() +
	 * bounds.getHeight()); set.add(tileFactory.pixelToGeo(pt, zoom)); return
	 * set; }
	 */

}
