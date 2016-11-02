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

/**
 * Quadrant of XY coordinate system
 * 
 * @author Arun
 */
public enum Quadrant {

	/**
	 * First Quadrant
	 */
	first(1), //
	/**
	 * Second Quadrant
	 */
	second(2), //
	/**
	 * Third Quadrant
	 */
	third(3), //
	/**
	 * Fourth Quadrant
	 */
	fourth(4), //
	/**
	 * Center
	 */
	center(0);//

	private final int quadrant;

	Quadrant(int quadrant) {
		this.quadrant = quadrant;
	}

	/**
	 * get value of quadrant
	 * 
	 * @return integer value
	 */
	public int getValue() {
		return this.quadrant;
	}

}
