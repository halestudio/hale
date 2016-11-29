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
 * Constants for interpolation operation
 * 
 * @author Arun
 */
public interface UniversalGridConstants {

	/**
	 * Default Grid step size factor
	 */
	public static final int DEFAULT_GRID_STEP_FACTOR = 2;

	/**
	 * Default distance factor between two coordinates while searching for new
	 * coordinates.
	 */
	public static final int DEFAULT_COORDINATE_DISTANCE_FACTOR = 4;

	/**
	 * Default rounding scale
	 */
	public static final int DEFAULT_ROUNDING_SCALE = 6;
}
