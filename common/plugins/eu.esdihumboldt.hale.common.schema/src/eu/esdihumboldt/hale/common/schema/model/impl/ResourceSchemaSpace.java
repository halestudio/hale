/*
 * Copyright (c) 2021 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.common.schema.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappingRelevantFlag;

/**
 * {@link SchemaSpace} implementation with resource Id.
 * 
 * @author Kapil Agnihotri
 */
public class ResourceSchemaSpace implements SchemaSpace {

	private final Map<String, Schema> schemasMap = new HashMap<String, Schema>();

	private Collection<TypeDefinition> allTypes;

	private Collection<TypeDefinition> mappingRelevantTypes;

	/**
	 * Adds a schema.
	 * 
	 * @param resourceId resource id of the schema.
	 * 
	 * @param schema the schema to add
	 * @return this schema space for chaining
	 */
	public ResourceSchemaSpace addSchema(String resourceId, Schema schema) {
		synchronized (this) {
			schemasMap.put(resourceId, schema);
			if (allTypes != null) {
				allTypes.addAll(schema.getTypes());
			}
			if (mappingRelevantTypes != null) {
				mappingRelevantTypes.addAll(schema.getMappingRelevantTypes());
			}
		}
		return this;
	}

	/**
	 * Removes a schema.
	 * 
	 * @param resourceId resource id of the schema.
	 * 
	 * @return returns the schema to which this map previously associated the
	 *         key, or null if the map contained no mapping for the key.
	 */
	public Schema removeSchema(String resourceId) {
		Schema removed = schemasMap.remove(resourceId);

		// re-initialize.
		allTypes = null;
		getTypes();
		mappingRelevantTypes = null;
		getMappingRelevantTypes();
		return removed;

	}

	/**
	 * @see TypeIndex#getTypes()
	 */
	@Override
	public Collection<? extends TypeDefinition> getTypes() {
		synchronized (this) {
			if (allTypes == null) {
				allTypes = new HashSet<TypeDefinition>();
				for (Schema schema : schemasMap.values()) {
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
			for (Schema schema : schemasMap.values()) {
				TypeDefinition result = schema.getType(name);
				if (result != null) {
					return result;
				}
			}

			return null;
		}
	}

	/**
	 * @see TypeIndex#getMappingRelevantTypes()
	 */
	@Override
	public Collection<? extends TypeDefinition> getMappingRelevantTypes() {
		synchronized (this) {
			if (mappingRelevantTypes == null) {
				mappingRelevantTypes = new HashSet<TypeDefinition>();
				for (Schema schema : schemasMap.values()) {
					mappingRelevantTypes.addAll(schema.getMappingRelevantTypes());
				}
			}
			return mappingRelevantTypes;
		}
	}

	/**
	 * @see SchemaSpace#getSchemas()
	 */
	@Override
	public Iterable<? extends Schema> getSchemas() {
		return new ArrayList<Schema>(schemasMap.values());
	}

	/**
	 * @param resourceId resource id of the schema.
	 * @return schema.
	 */
	public Schema getSchemas(String resourceId) {
		return schemasMap.get(resourceId);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.schema.model.TypeIndex#toggleMappingRelevant(java.util.Collection)
	 */
	@Override
	public void toggleMappingRelevant(Collection<? extends TypeDefinition> types) {
		synchronized (this) {
			for (TypeDefinition type : types) {
				Schema container = null;
				for (Schema schema : schemasMap.values())
					if (schema.getTypes().contains(type)) {
						container = schema;
						break;
					}
				// toggle type in its schema
				if (container != null)
					container.toggleMappingRelevant(Collections.singletonList(type));
				else {
					// shouldn't happen, but to be safe toggle it in this case
					// too
					Definition<TypeConstraint> def = type;
					((AbstractDefinition<TypeConstraint>) def).setConstraint(MappingRelevantFlag
							.get(!type.getConstraint(MappingRelevantFlag.class).isEnabled()));
				}
				// was toggled, update own list
				if (mappingRelevantTypes != null)
					if (type.getConstraint(MappingRelevantFlag.class).isEnabled())
						mappingRelevantTypes.add(type);
					else
						mappingRelevantTypes.remove(type);
			}
		}
	}
}
