/*
 * Copyright (c) 2015 Data Harmonisation Panel
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
 * Type of extends the extent transformation can calculate.
 * 
 * @author Simon Templer
 */
public enum ExtentType {
	/** Bounding box */
	BBOX(ExtentFunction.PARAM_BOUNDING_BOX), //
	/** Convex hull */
	CONVEX_HULL(ExtentFunction.PARAM_CONVEX_HULL), //
	/** Union */
	UNION(ExtentFunction.PARAM_UNION);

	/**
	 * The string identifier of an extent type.
	 */
	public final String id;

	ExtentType(String id) {
		this.id = id;
	}

	/**
	 * Get the extent type for an extent type string identifier.
	 * 
	 * @param id the string identifier
	 * @return the extent type
	 */
	public static ExtentType forId(String id) {
		switch (id) {
		case ExtentFunction.PARAM_BOUNDING_BOX:
			return BBOX;
		case ExtentFunction.PARAM_CONVEX_HULL:
			return CONVEX_HULL;
		case ExtentFunction.PARAM_UNION:
			return UNION;
		default:
			throw new IllegalArgumentException("Unknown extent type");
		}
	}
}
