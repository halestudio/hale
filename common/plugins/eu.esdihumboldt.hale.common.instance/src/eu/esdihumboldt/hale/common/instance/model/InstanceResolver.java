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
 * Interface for instance resolvers, that allow getting a reference for an
 * instance and vice versa.
 * @author Simon Templer
 */
public interface InstanceResolver {
	
	/**
	 * Get a reference to an instance that can be used to retrieve the
	 * given instance using {@link #getInstance(InstanceReference)}.
	 * @param instance the instance, must have originated from this resolver
	 * @return the reference
	 */
	public InstanceReference getReference(Instance instance);
	
	/**
	 * Get the instance referenced by the given reference.
	 * @param reference the instance reference
	 * @return the referenced instance or <code>null</code> if it does not exist
	 *   or the reference is invalid
	 */
	public Instance getInstance(InstanceReference reference);

}
