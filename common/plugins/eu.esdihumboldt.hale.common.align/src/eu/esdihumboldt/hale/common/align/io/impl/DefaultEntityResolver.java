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

package eu.esdihumboldt.hale.common.align.io.impl;

import eu.esdihumboldt.hale.common.align.io.EntityResolver;
import eu.esdihumboldt.hale.common.align.io.impl.internal.JaxbToEntityDefinition;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.AbstractEntityType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ClassType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.PropertyType;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultProperty;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultType;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Default {@link EntityResolver} implementation.
 * 
 * @author Simon Templer
 */
public class DefaultEntityResolver implements EntityResolver {

	private static DefaultEntityResolver instance;

	/**
	 * Get the singleton default entity resolver instance.
	 * 
	 * @return the entity resolver
	 */
	public static EntityResolver getInstance() {
		synchronized (DefaultEntityResolver.class) {
			if (instance == null) {
				instance = new DefaultEntityResolver();
			}
		}
		return instance;
	}

	@Override
	public Entity resolve(AbstractEntityType entity, TypeIndex schema, SchemaSpaceID schemaSpace) {
		// must first check for PropertyType as it inherits from ClassType
		if (entity instanceof PropertyType) {
			return resolveProperty((PropertyType) entity, schema, schemaSpace);
		}
		if (entity instanceof ClassType) {
			return resolveType((ClassType) entity, schema, schemaSpace);
		}
		throw new IllegalArgumentException("Illegal type of entity");
	}

	@Override
	public Entity resolveProperty(PropertyType entity, TypeIndex schema, SchemaSpaceID schemaSpace) {
		return new DefaultProperty(JaxbToEntityDefinition.convert(entity, schema, schemaSpace));
	}

	@Override
	public Entity resolveType(ClassType entity, TypeIndex schema, SchemaSpaceID schemaSpace) {
		return new DefaultType(JaxbToEntityDefinition.convert(entity, schema, schemaSpace));
	}

}
