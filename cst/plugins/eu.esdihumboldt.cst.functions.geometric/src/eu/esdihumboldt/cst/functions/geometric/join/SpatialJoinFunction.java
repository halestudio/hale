/*
 * Copyright (c) 2017 wetransform GmbH
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

package eu.esdihumboldt.cst.functions.geometric.join;

/**
 * Spatial Join function constants.
 * 
 * @author Florian Esser
 */
public interface SpatialJoinFunction {

	/**
	 * the join function Id
	 */
	public static final String ID = "eu.esdihumboldt.cst.functions.geometric.spatialjoin";

	/**
	 * Name of the parameter specifying the join function.
	 */
	public static final String PARAMETER_SPATIAL_JOIN = "spatialjoin";

	/**
	 * Entity name for the source types to join.
	 */
	public static final String JOIN_TYPES = "types";
}
