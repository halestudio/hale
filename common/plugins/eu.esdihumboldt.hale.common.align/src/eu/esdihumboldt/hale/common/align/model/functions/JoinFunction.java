/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.align.model.functions;

/**
 * Join function constants.
 * 
 * @author Kai Schwierczek
 */
public interface JoinFunction {

	/**
	 * Name of the parameter specifying the join function.
	 */
	public static final String PARAMETER_JOIN = "join";

	/**
	 * Name of the parameter that specifies if an inner join should be
	 * performed.
	 */
	public static final String PARAMETER_INNER_JOIN = "innerJoin";

	/**
	 * the join function Id
	 */
	public static final String ID = "eu.esdihumboldt.hale.align.join";

	/**
	 * Entity name for the source types to join.
	 */
	public static final String JOIN_TYPES = "types";
}
