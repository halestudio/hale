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

package eu.esdihumboldt.hale.instance.model;

/**
 * Instance collection interface
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface InstanceCollection extends Iterable<Instance> {
	
	/**
	 * Constant for unknown collection size
	 */
	public static final int UNKNOWN_SIZE = -1;

	/**
	 * States if the collection has a known size
	 * 
	 * @return if the collection size is known
	 */
	public boolean hasSize();
	
	/**
	 * Get the collection size if known.
	 * @see #hasSize()
	 * 
	 * @return the collection size or 
	 */
	public int size();
	
	//TODO what else is needed?
	// public InstanceCollection filter(Filter filter);
	// public InstanceCollection[] partition(...);
	
}
