/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.common.service.population;

/**
 * Represents instance or property population.
 * 
 * @author Simon Templer
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
