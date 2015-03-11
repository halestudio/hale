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
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;

/**
 * Instance traverser that traverses the model depth first.
 * 
 * @author Simon Templer
 */
public class DepthFirstInstanceTraverser implements InstanceTraverser {

	private final boolean cancelChildTraversalOnly;

	/**
	 * Creates a depth first instance traverser.
	 */
	public DepthFirstInstanceTraverser() {
		this(false);
	}

	/**
	 * Creates a depth first instance traverser.
	 * 
	 * @param cancelChildTraversalOnly if when the callback cancels the
	 *            traversal, only the traversal of the children should be
	 *            canceled (meaning traversal is continued but not down from the
	 *            current object)
	 */
	public DepthFirstInstanceTraverser(boolean cancelChildTraversalOnly) {
		super();
		this.cancelChildTraversalOnly = cancelChildTraversalOnly;
	}

	/**
	 * @see InstanceTraverser#traverse(Instance, InstanceTraversalCallback)
	 */
	@Override
	public boolean traverse(Instance instance, InstanceTraversalCallback callback) {
		return traverse(instance, callback, null, null);
	}

	private boolean traverse(Instance instance, InstanceTraversalCallback callback, QName name,
			DefinitionGroup parent) {
		if (callback.visit(instance, name, parent)) {
			// traverse value (if applicable)
			Object value = instance.getValue();
			if (value != null) {
				if (!traverse(value, callback, name, parent)) {
					if (!cancelChildTraversalOnly) {
						// cancel whole traversal
						return false;
					}
					else {
						// only skip traversing children
						return true;
					}
				}
			}

			// traverse children
			return traverseChildren(instance, callback);
		}
		else if (!cancelChildTraversalOnly) {
			// cancel whole traversal
			return false;
		}
		else {
			// only skipped traversing the current instance
			return true;
		}
	}

	/**
	 * @see InstanceTraverser#traverse(Group, InstanceTraversalCallback)
	 */
	@Override
	public boolean traverse(Group group, InstanceTraversalCallback callback) {
		return traverse(group, callback, null, null);
	}

	private boolean traverse(Group group, InstanceTraversalCallback callback, QName name,
			DefinitionGroup parent) {
		if (callback.visit(group, name, parent)) {
			// traverse children
			return traverseChildren(group, callback);
		}
		else if (!cancelChildTraversalOnly) {
			// cancel whole traversal
			return false;
		}
		else {
			// only skipped traversing the current group
			return true;
		}
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
					if (!traverse(value, callback, name, group.getDefinition())) {
						if (!cancelChildTraversalOnly) {
							return false;
						}
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
		return traverse(value, callback, null, null);
	}

	private boolean traverse(Object value, InstanceTraversalCallback callback, QName name,
			DefinitionGroup parent) {
		if (value instanceof Instance) {
			return traverse((Instance) value, callback, name, parent);
		}
		if (value instanceof Group) {
			return traverse((Group) value, callback, name, parent);
		}

		return callback.visit(value, name, parent);
	}

}
