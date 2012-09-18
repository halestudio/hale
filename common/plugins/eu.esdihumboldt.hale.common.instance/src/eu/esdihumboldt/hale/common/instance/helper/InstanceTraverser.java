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
