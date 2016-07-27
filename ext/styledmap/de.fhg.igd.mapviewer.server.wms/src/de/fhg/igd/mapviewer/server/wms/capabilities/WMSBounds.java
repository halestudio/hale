/*
 * Copyright (c) 2016 Fraunhofer IGD
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
 *     Fraunhofer IGD <http://www.igd.fraunhofer.de/>
 */
package de.fhg.igd.mapviewer.server.wms.capabilities;

/**
 * WMS Layer Bounding Box
 * 
 * @author Simon Templer
 */
public class WMSBounds {

	private final String srs;

	private final double minx;

	private final double miny;

	private final double maxx;

	private final double maxy;

	/**
	 * Constructor
	 * 
	 * @param srs the string representation of the SRS
	 * @param minx the minimum x ordinate
	 * @param miny the minimum y ordinate
	 * @param maxx the maximum x ordinate
	 * @param maxy the maximum y ordinate
	 */
	public WMSBounds(String srs, double minx, double miny, double maxx, double maxy) {
		this.minx = minx;
		this.miny = miny;
		this.maxx = maxx;
		this.maxy = maxy;

		this.srs = srs;
	}

	/**
	 * @return the srs
	 */
	public String getSRS() {
		return srs;
	}

	/**
	 * @return the minx
	 */
	public double getMinX() {
		return minx;
	}

	/**
	 * @return the miny
	 */
	public double getMinY() {
		return miny;
	}

	/**
	 * @return the maxx
	 */
	public double getMaxX() {
		return maxx;
	}

	/**
	 * @return the maxy
	 */
	public double getMaxY() {
		return maxy;
	}

}
