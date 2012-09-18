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

package eu.esdihumboldt.hale.common.schema.model;

/**
 * Definition of a property or property group
 * 
 * @param <C> the supported constraint type
 * 
 * @author Simon Templer
 */
public interface ChildDefinition<C> extends Definition<C> {

	/**
	 * Get the parent type of the child. This can either be the declaring type
	 * or a sub-type.
	 * 
	 * @return the parent type of the property
	 */
	public TypeDefinition getParentType();

	/**
	 * Get the definition of the group declaring the property.
	 * 
	 * @return the group declaring the property
	 */
	public DefinitionGroup getDeclaringGroup();

	/**
	 * Return as a property definition is possible. This is convenience for
	 * avoiding casts and instanceof expressions when handling children.
	 * 
	 * @return a property definition if this child definition is one, otherwise
	 *         <code>null</code>
	 */
	public PropertyDefinition asProperty();

	/**
	 * Return as a group definition is possible. This is convenience for
	 * avoiding casts and instanceof expressions when handling children.
	 * 
	 * @return a group definition if this child definition is one, otherwise
	 *         <code>null</code>
	 */
	public GroupPropertyDefinition asGroup();

}
