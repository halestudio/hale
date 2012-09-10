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

package eu.esdihumboldt.hale.common.instance.helper;

import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;

/**
 * Traverser for instances. Uses an {@link InstanceTraversalCallback} for
 * visiting the model objects.
 * 
 * @author Simon Templer
 */
public interface InstanceTraverser {

	/**
	 * Traverse the given instance.
	 * 
	 * @param instance the instance
	 * @param callback the traversal callback
	 * @return if traversal shall be continued, can be safely ignored if called
	 *         from outside the traverser
	 */
	public boolean traverse(Instance instance, InstanceTraversalCallback callback);

	/**
	 * Traverse the given group.
	 * 
	 * @param group the group
	 * @param callback the traversal callback
	 * @return if traversal shall be continued, can be safely ignored if called
	 *         from outside the traverser
	 */
	public boolean traverse(Group group, InstanceTraversalCallback callback);

	/**
	 * Traverse the given value.
	 * 
	 * @param value the value, if a {@link Group} or {@link Instance} the call
	 *            must be handed over to the respective traverse methods
	 * @param callback the traversal callback
	 * @return if traversal shall be continued, can be safely ignored if called
	 *         from outside the traverser
	 */
	public boolean traverse(Object value, InstanceTraversalCallback callback);

}
