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

package eu.esdihumboldt.hale.common.instance.model;

/**
 * Represents a reference to an instance. Implementations must implement
 * {@link #hashCode()} and {@link #equals(Object)} to uniquely identify an
 * instance.
 * 
 * @author Simon Templer
 */
public interface InstanceReference {

	// XXX move getDataSet to InstanceReference interface applicable for
	// InstanceService?

	/**
	 * Get the data set the instance is associated to.
	 * 
	 * @return the instance data set, <code>null</code> if not set
	 */
	public DataSet getDataSet();

}
