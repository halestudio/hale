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

import java.util.Collection;

/**
 * Represents a type definition
 * 
 * @author Simon Templer
 */
public interface TypeDefinition extends Definition<TypeConstraint>, DefinitionGroup {

	/**
	 * Get the super type
	 * 
	 * @return the super type, may be <code>null</code>
	 */
	public TypeDefinition getSuperType();

	/**
	 * Get the sub types
	 * 
	 * @return the list of sub types, may not be modified
	 */
	public Collection<? extends TypeDefinition> getSubTypes();

	/**
	 * Get all children that an instance of the type may have. Usually these are
	 * the declared children and the super type children.
	 * 
	 * @return the child definitions
	 */
	public Collection<? extends ChildDefinition<?>> getChildren();

}
