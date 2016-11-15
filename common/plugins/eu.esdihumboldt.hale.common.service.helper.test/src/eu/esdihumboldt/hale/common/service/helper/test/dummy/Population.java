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

package eu.esdihumboldt.hale.common.service.helper.test.dummy;

/**
 * Represents property population.
 * 
 */
public interface Population {

	/**
	 * Constant representing unknown population count.
	 */
	public static final int UNKNOWN = -1;

	/**
	 * Get the overall count of the instance or property values.
	 * 
	 * @return the overall count
	 */
	public int getOverallCount();

	/**
	 * Get how many of the parents of the property actually have at least one
	 * value for this property. For instances the overall count is returned as
	 * they have no parents.
	 * 
	 * @return the count of parents that have such a property value, or the
	 *         overall count if the population is associated to an instance
	 */
	public int getParentsCount();

}
