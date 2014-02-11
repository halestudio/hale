/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.align.io;

import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.AbstractEntityType;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Resolves schema entities from JAXB entities.
 * 
 * XXX this class exposes an interface with internal classes
 * 
 * @author Simon Templer
 */
public interface EntityResolver {

	/**
	 * Resolve a schema entity based on the given JAXB entity.
	 * 
	 * @param entity the entity
	 * @param schema the schema
	 * @param schemaSpace the schema space
	 * @return the schema entity
	 * @throws IllegalStateException if resolving the entity is not possible
	 */
	public Entity resolve(AbstractEntityType entity, TypeIndex schema, SchemaSpaceID schemaSpace);

}
