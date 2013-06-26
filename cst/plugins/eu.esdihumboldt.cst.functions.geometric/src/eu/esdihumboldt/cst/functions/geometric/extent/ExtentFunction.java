/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.cst.functions.geometric.extent;

/**
 * Constants for function computing extents.
 * 
 * @author Simon Templer
 */
public interface ExtentFunction {

	/**
	 * The compute extent function id
	 */
	public static final String ID = "eu.esdihumboldt.cst.functions.geometric.extent";

	/**
	 * The type of the parameter
	 */
	public static final String PARAM_TYPE = "type";

	/**
	 * Convex Hull choice for extent functionality
	 */
	public static final String PARAM_CONVEX_HULL = "convexhull";

	/**
	 * Bounding Box choice for extent functionality
	 */
	public static final String PARAM_BOUNDING_BOX = "boundingbox";

	/**
	 * Union choice for extent functionality
	 */
	public static final String PARAM_UNION = "union";

}
