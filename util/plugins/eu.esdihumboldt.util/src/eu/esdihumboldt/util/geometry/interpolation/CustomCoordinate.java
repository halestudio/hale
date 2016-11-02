/*
 * Copyright (c) 2016 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.util.geometry.interpolation;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * representation of a Point
 * 
 * @author Arun
 */
public class CustomCoordinate extends Coordinate {

	private Quadrant quadrant;

	/**
	 * A constructor
	 * 
	 * @param x double value of x axis
	 * @param y double value of y axis
	 * @param quadrant quadrant in which this coordinate
	 */
	public CustomCoordinate(double x, double y) {
		super(x, y);
		this.quadrant = null;
	}

	/**
	 * A constructor
	 * 
	 * @param x double value of x axis
	 * @param y double value of y axis
	 * @param quadrant quadrant in which this coordinate
	 */
	public CustomCoordinate(double x, double y, Quadrant quadrant) {
		super(x, y);
		this.quadrant = quadrant;
	}

	/**
	 * @return the quadrant
	 */
	public Quadrant getQuadrant() {
		return quadrant;
	}

	/**
	 * @param quadrant the quadrant to set
	 */
	public void setQuadrant(Quadrant quadrant) {
		this.quadrant = quadrant;
	}

	/**
	 * @see com.vividsolutions.jts.geom.Coordinate#toString()
	 */
	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ", " + quadrant + ")";
	}

}
