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

import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;

/**
 * Callback for {@link InstanceTraverser}s.
 * 
 * @author Simon Templer
 */
public interface InstanceTraversalCallback {

	/**
	 * Visit an instance.
	 * 
	 * @param instance the instance
	 * @param name the property name the instance is a value of,
	 *            <code>null</code> if it is the traversal root
	 * @param parent the parent group, if applicable
	 * @return if traversal shall be continued
	 */
	public boolean visit(Instance instance, @Nullable QName name, @Nullable DefinitionGroup parent);

	/**
	 * Visit a group that is not an {@link Instance}.
	 * 
	 * @param group the group
	 * @param name the property name the group is a value of, <code>null</code>
	 *            if it is the traversal root
	 * @param parent the parent group, if applicable
	 * @return if traversal shall be continued
	 */
	public boolean visit(Group group, @Nullable QName name, @Nullable DefinitionGroup parent);

	/**
	 * Visit a value that is neither {@link Instance} nor {@link Group}.
	 * 
	 * @param value the value
	 * @param name the property name the object is a value of, <code>null</code>
	 *            if it is the traversal root
	 * @param parent the parent group, if applicable
	 * @return if traversal shall be continued
	 */
	public boolean visit(Object value, @Nullable QName name, @Nullable DefinitionGroup parent);

}
