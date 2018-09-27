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

package eu.esdihumboldt.hale.common.align.model;

import java.util.List;

import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Definition of an entity. Represents either a type or a property.
 * 
 * @author Simon Templer
 * @since 2.5
 */
public interface EntityDefinition {

	/**
	 * Get the definition of the type or property represented by the entity
	 * definition.
	 * 
	 * @return the definition of the type or property
	 */
	public Definition<?> getDefinition();

	/**
	 * Get the type definition that is associated with the entity. This is
	 * either the type represented by the entity or the topmost parent to the
	 * property represented by the entity.
	 * 
	 * @return the type definition
	 */
	public TypeDefinition getType();

	/**
	 * Get the filter applied to the entity type.
	 * 
	 * @return the entity filter, may be <code>null</code>
	 */
	public Filter getFilter();

	/**
	 * Get the property path. Each path item is an instance context name paired
	 * with a child definition. The default instance context name is
	 * <code>null</code>.
	 * 
	 * @return the path down to the property represented by the entity, an empty
	 *         list if the entity represents a type
	 */
	public List<ChildContext> getPropertyPath();

	/**
	 * Get the schema space the entity definition is associated to. The schema
	 * space itself is no characteristic of the entity, but is needed as
	 * additional information to differentiate between source and target schema
	 * entities with the same names.
	 * 
	 * @return the identifier of the entity definition's schema space
	 */
	public SchemaSpaceID getSchemaSpace();

	/**
	 * Get the last element in the property path.
	 * 
	 * @return the last path element or <code>null</code> if the path is empty
	 */
	default ChildContext getLastPathElement() {
		List<ChildContext> path = getPropertyPath();
		if (path == null || path.isEmpty()) {
			return null;
		}
		else {
			return path.get(path.size() - 1);
		}
	}

}
