/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
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
	 * @param type the type entity
	 */
	public TypeBean(Type type) {
		super(type.getDefinition().getDefinition().getName(), 
				FilterDefinitionManager.getInstance().asString(type.getDefinition().getFilter()));
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
	protected TypeEntityDefinition createEntityDefinition(TypeIndex index,
			SchemaSpaceID schemaSpace) {
		TypeDefinition typeDef = index.getType(getTypeName());
		return new TypeEntityDefinition(typeDef, schemaSpace, 
				FilterDefinitionManager.getInstance().parse(getFilter()));
	}

}
