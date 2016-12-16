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
 * Circle by center point interpolation
 * 
 * @author Arun
 */
public class CircleByCenterPointInterpolation extends ArcByCenterPointInterpolation {

	/**
	 * Constructor
	 * 
	 * <p>
	 * As there are no coordinates given, every generated coordinates of circle
	 * will be moved to universal grid
	 * </p>
	 * 
	 * @param center center of the circle
	 * @param radius radius of the circle
	 * @param maxPositionalError max positional error
	 */
	public CircleByCenterPointInterpolation(Coordinate center, double radius,
			double maxPositionalError) {
		super(center, radius, maxPositionalError);
	}

}
