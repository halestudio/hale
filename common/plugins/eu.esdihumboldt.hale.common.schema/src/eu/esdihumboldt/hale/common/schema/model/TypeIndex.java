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

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappingRelevantFlag;

/**
 * A type index holds a set of type definitions and a corresponding name index.
 * 
 * @author Simon Templer
 */
public interface TypeIndex {

	/**
	 * Get all types in the index
	 * 
	 * @return all type definitions
	 */
	public Collection<? extends TypeDefinition> getTypes();

	/**
	 * Get the type with the given name
	 * 
	 * @param name the type name
	 * @return the type definition or <code>null</code> if no type with the
	 *         given name exists in the index
	 */
	public TypeDefinition getType(QName name);

	/**
	 * Get all mappable types, i.e. types that are flagged with an enabled
	 * {@link MappingRelevantFlag}
	 * 
	 * @return the mappable types
	 */
	public Collection<? extends TypeDefinition> getMappingRelevantTypes();

	/**
	 * Toggles the mappable flag of the given types.
	 * 
	 * @param types the types to toggle
	 */
	public void toggleMappingRelevant(Collection<? extends TypeDefinition> types);
}
