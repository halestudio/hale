/*
 * GeoPosition.java
 *
 * Created on March 31, 2006, 9:15 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.mapviewer;

/**
 * An immutable coordinate in the real (geographic) world, composed of a x and y
 * coordinate and the corresponding epsg code.
 * 
 * @author rbair
 * @author Simon Templer
 * 
 * @version $Id$
 */
public class GeoPosition {

	/**
	 * The epsg code for the WGS84 coordinate reference system (long/lat)
	 */
	public static final int WGS_84_EPSG = 4326;

	private double y; /* or latitude */
	private double x; /* or longitude */

	private int epsg;

	/**
	 * Creates a new instance of GeoPosition from the specified x and y
	 * coordinates.
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param epsg the coordinate reference system epsg code
	 */
	public GeoPosition(double x, double y, int epsg) {
		this.y = y;
		this.x = x;

		this.epsg = epsg;
	}

	/**
	 * Creates a new instance of GeoPosition from the specified latitude ant
	 * longitude in the WGS84 coordinate reference system.
	 * 
	 * @param latitude the latitude
	 * @param longitude the longitude
	 */
	private GeoPosition(double latitude, double longitude) {
		this.y = latitude;
		this.x = longitude;

		this.epsg = WGS_84_EPSG;
	}

	/**
	 * Creates a new instance of GeoPosition from the specified latitude and
	 * longitude. Each are specified as degrees, minutes, and seconds; not as
	 * decimal degrees. Use the other constructor for those.
	 * 
	 * @param latDegrees the degrees part of the current latitude
	 * @param latMinutes the minutes part of the current latitude
	 * @param latSeconds the seconds part of the current latitude
	 * @param lonDegrees the degrees part of the current longitude
	 * @param lonMinutes the minutes part of the current longitude
	 * @param lonSeconds the seconds part of the current longitude
	 */
	public GeoPosition(double latDegrees, double latMinutes, double latSeconds, double lonDegrees,
			double lonMinutes, double lonSeconds) {
		this(latDegrees + (latMinutes + latSeconds / 60.0) / 60.0,
				lonDegrees + (lonMinutes + lonSeconds / 60.0) / 60.0);
	}

	/**
	 * Get the y coordinate
	 * 
	 * @return the y coordinate
	 */
	public double getY() {
		return y;
	}

	/**
	 * Get the x coordinate
	 * 
	 * @return the x coordinate
	 */
	public double getX() {
		return x;
	}

	/**
	 * Get the epsg code
	 * 
	 * @return the epsg code
	 */
	public int getEpsgCode() {
		return epsg;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + epsg;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	/**
	 * Returns true the specified GeoPosition and this GeoPosition represent the
	 * exact same coordinates in the same coordinate system
	 * 
	 * @param obj a GeoPosition to compare this GeoPosition to
	 * @return returns true if the specified GeoPosition is equal to this one
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GeoPosition other = (GeoPosition) obj;
		if (epsg != other.epsg)
			return false;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return true;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[" + x + ", " + y + " (" + epsg + ")]";
	}
}