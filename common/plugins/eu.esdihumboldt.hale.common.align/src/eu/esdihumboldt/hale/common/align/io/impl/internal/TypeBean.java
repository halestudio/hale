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

package eu.esdihumboldt.hale.common.align.io.impl.internal;

import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultType;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.instance.extension.filter.FilterDefinitionManager;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Represents a {@link Type}
 * 
 * @author Simon Templer
 */
public class TypeBean extends EntityBean<TypeEntityDefinition> {

	/**
	 * Default constructor
	 */
	public TypeBean() {
		super();
	}

	/**
	 * Creates a type entity bean based on the given type entity
	 * 
	 * @param type the type entity
	 */
	public TypeBean(Type type) {
		super(type.getDefinition().getDefinition().getName(), FilterDefinitionManager.getInstance()
				.asString(type.getDefinition().getFilter()));
	}

	/**
	 * @see EntityBean#createEntity(TypeIndex, SchemaSpaceID)
	 */
	@Override
	public Entity createEntity(TypeIndex types, SchemaSpaceID schemaSpace) {
		return new DefaultType(createEntityDefinition(types, schemaSpace));
	}

	/**
	 * @see EntityBean#createEntityDefinition(TypeIndex, SchemaSpaceID)
	 */
	@Override
	protected TypeEntityDefinition createEntityDefinition(TypeIndex index, SchemaSpaceID schemaSpace) {
		TypeDefinition typeDef = index.getType(getTypeName());
		return new TypeEntityDefinition(typeDef, schemaSpace, FilterDefinitionManager.getInstance()
				.parse(getFilter()));
	}

}
