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

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;

/**
 * Instance traverser that traverses the model breadth first.
 * @author Simon Templer
 */
public class BreadthFirstInstanceTraverser implements InstanceTraverser {

	/**
	 * @see InstanceTraverser#traverse(Instance, InstanceTraversalCallback)
	 */
	@Override
	public boolean traverse(Instance instance, InstanceTraversalCallback callback) {
		if (callback.visit(instance)) {
			// traverse value (if applicable)
			Object value = instance.getValue();
			if (value != null) {
				 if (!traverse(value, callback)) {
					 return false;
				 }
			}
			
			// traverse children
			return traverseChildren(instance, callback);
		}
		
		return false;
	}

	/**
	 * @see InstanceTraverser#traverse(Group, InstanceTraversalCallback)
	 */
	@Override
	public boolean traverse(Group group, InstanceTraversalCallback callback) {
		if (callback.visit(group)) {
			// traverse children
			return traverseChildren(group, callback);
		}
		
		return false;
	}

	/**
	 * Traverse the children of a given group.
	 * @param group the group
	 * @param callback the traversal callback
	 * @return if traversal shall be continued
	 */
	protected boolean traverseChildren(Group group, InstanceTraversalCallback callback) {
		for (QName name : group.getPropertyNames()) {
			Object[] values = group.getProperty(name);
			if (values != null) {
				for (Object value : values) {
					if (!traverse(value, callback)) {
						return false;
					}
				}
			}
		}
		
		return true;
	}

	/**
	 * @see InstanceTraverser#traverse(Object, InstanceTraversalCallback)
	 */
	@Override
	public boolean traverse(Object value, InstanceTraversalCallback callback) {
		if (value instanceof Instance) {
			return traverse((Instance) value, callback);
		}
		if (value instanceof Group) {
			return traverse((Group) value, callback);
		}
		
		return callback.visit(value);
	}

}
