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

package eu.esdihumboldt.hale.common.schema.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappableFlag;

/**
 * Default {@link SchemaSpace} implementation
 * @author Simon Templer
 */
public class DefaultSchemaSpace implements SchemaSpace {
	
	private final Set<Schema> schemas = new HashSet<Schema>();
	
	private Collection<TypeDefinition> allTypes;
	
	private Collection<TypeDefinition> mappableTypes;
	
	/**
	 * Adds a schema
	 * 
	 * @param schema the schema to add
	 */
	public void addSchema(Schema schema) {
		synchronized (this) {
			schemas.add(schema);
			if (allTypes != null) {
				allTypes.addAll(schema.getTypes());
			}
			if (mappableTypes != null) {
				mappableTypes.addAll(schema.getMappableTypes());
			}
		}
	}
	
	//XXX needed? will result in problems with shared types because the load order of the schemas is not documented
//	/**
//	 * Removes a schema
//	 * 
//	 * @param schema the schema to remove
//	 */
//	public void removeSchema(Schema schema) {
//		
//	}

	/**
	 * @see TypeIndex#getTypes()
	 */
	@Override
	public Collection<? extends TypeDefinition> getTypes() {
		synchronized (this) {
			if (allTypes == null) {
				allTypes = new HashSet<TypeDefinition>();
				for (Schema schema : schemas) {
					allTypes.addAll(schema.getTypes());
				}
			}
			return allTypes;
		}
	}

	/**
	 * @see TypeIndex#getType(QName)
	 */
	@Override
	public TypeDefinition getType(QName name) {
		synchronized (this) {
			for (Schema schema : schemas) {
				TypeDefinition result = schema.getType(name);
				if (result != null) {
					return result;
				}
			}
			
			return null;
		}
	}

	/**
	 * @see TypeIndex#getMappableTypes()
	 */
	@Override
	public Collection<? extends TypeDefinition> getMappableTypes() {
		synchronized (this) {
			if (mappableTypes == null) {
				mappableTypes = new HashSet<TypeDefinition>();
				for (Schema schema : schemas) {
					mappableTypes.addAll(schema.getMappableTypes());
				}
			}
			return mappableTypes;
		}
	}

	/**
	 * @see SchemaSpace#getSchemas()
	 */
	@Override
	public Iterable<? extends Schema> getSchemas() {
		return new ArrayList<Schema>(schemas);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.schema.model.TypeIndex#toggleMappable(java.util.Collection)
	 */
	@Override
	public void toggleMappable(Collection<? extends TypeDefinition> types) {
		synchronized (this) {
			for (TypeDefinition type : types) {
				Definition<TypeConstraint> def = type;
				if (type.getConstraint(MappableFlag.class).isEnabled()) {
					if (mappableTypes != null && mappableTypes.contains(type))
						mappableTypes.remove(type);
					((AbstractDefinition<TypeConstraint>) def).setConstraint(MappableFlag.DISABLED);
				} else {
					if (mappableTypes != null)
						mappableTypes.add(type);
					((AbstractDefinition<TypeConstraint>) def).setConstraint(MappableFlag.ENABLED);
				}
			}
		}
	}
}
