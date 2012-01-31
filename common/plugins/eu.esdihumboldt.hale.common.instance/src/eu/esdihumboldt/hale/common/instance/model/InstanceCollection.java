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
 * Instance collection interface
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface InstanceCollection extends InstanceResolver {
	
	/**
	 * Get an iterator over all instances contained in the collection. The
	 * iterator must be disposed after use (using {@link ResourceIterator#close()}).
	 * 
	 * @return an iterator over the instances 
	 */
	public ResourceIterator<Instance> iterator();
	
	/**
	 * Constant for unknown collection size
	 */
	public static final int UNKNOWN_SIZE = -1;

	/**
	 * States if the collection has a known size.
	 * @return if the collection size is known
	 */
	public boolean hasSize();
	
	/**
	 * Get the collection size if known.
	 * @see #hasSize()
	 * 
	 * @return the collection size or {@link #UNKNOWN_SIZE}
	 */
	public int size();
	
	/**
	 * States if the collection has no instances. This must return a valid value
	 * even if {@link #hasSize()} returns false.
	 * @return if the collection is empty
	 */
	public boolean isEmpty();
	
	/**
	 * Select the instances in the collection, matching the given filter.
	 * @param filter the instance filter
	 * @return the instance collection representing the selection
	 */
	public InstanceCollection select(Filter filter);
	
	//TODO what else is needed?
	// public InstanceCollection[] partition(...);
	
}
