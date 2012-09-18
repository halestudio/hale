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

package eu.esdihumboldt.hale.common.instance.helper;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;

/**
 * Instance traverser that traverses the model breadth first.
 * 
 * @author Simon Templer
 * @deprecated This is not breath first!
 */
@Deprecated
public class BreadthFirstInstanceTraverser implements InstanceTraverser {

	/**
	 * @see InstanceTraverser#traverse(Instance, InstanceTraversalCallback)
	 */
	@Override
	public boolean traverse(Instance instance, InstanceTraversalCallback callback) {
		return traverse(instance, callback, null);
	}

	private boolean traverse(Instance instance, InstanceTraversalCallback callback, QName name) {
		if (callback.visit(instance, name)) {
			// traverse value (if applicable)
			Object value = instance.getValue();
			if (value != null) {
				if (!traverse(value, callback, name)) {
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
		return traverse(group, callback, null);
	}

	private boolean traverse(Group group, InstanceTraversalCallback callback, QName name) {
		if (callback.visit(group, name)) {
			// traverse children
			return traverseChildren(group, callback);
		}

		return false;
	}

	/**
	 * Traverse the children of a given group.
	 * 
	 * @param group the group
	 * @param callback the traversal callback
	 * @return if traversal shall be continued
	 */
	protected boolean traverseChildren(Group group, InstanceTraversalCallback callback) {
		for (QName name : group.getPropertyNames()) {
			Object[] values = group.getProperty(name);
			if (values != null) {
				for (Object value : values) {
					if (!traverse(value, callback, name)) {
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
		return traverse(value, callback, null);
	}

	private boolean traverse(Object value, InstanceTraversalCallback callback, QName name) {
		if (value instanceof Instance) {
			return traverse((Instance) value, callback, name);
		}
		if (value instanceof Group) {
			return traverse((Group) value, callback, name);
		}

		return callback.visit(value, name);
	}

}
