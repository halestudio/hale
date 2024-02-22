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

package eu.esdihumboldt.hale.common.schema.model.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappableFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappingRelevantFlag;

/**
 * Default {@link TypeIndex} implementation
 * 
 * @author Simon Templer
 */
public class DefaultTypeIndex implements TypeIndex {

	private final Map<QName, TypeDefinition> types = new LinkedHashMap<QName, TypeDefinition>();
	private Set<TypeDefinition> mappingRelevantTypes;

	/**
	 * Add a type to the type index.
	 * 
	 * @param type the type to add
	 */
	public void addType(TypeDefinition type) {
		synchronized (this) {
			types.put(type.getName(), type);

			// check mappable flag, too for consistency
			if (mappingRelevantTypes != null
					&& type.getConstraint(MappingRelevantFlag.class).isEnabled()
					&& type.getConstraint(MappableFlag.class).isEnabled())
				mappingRelevantTypes.add(type);
		}
	}

	/**
	 * @see TypeIndex#getTypes()
	 */
	@Override
	public Collection<? extends TypeDefinition> getTypes() {
		return Collections.unmodifiableCollection(types.values());
	}

	/**
	 * @see TypeIndex#getType(QName)
	 */
	@Override
	public TypeDefinition getType(QName name) {
		synchronized (this) {
			return types.get(name);
		}
	}

	/**
	 * {@inheritDoc}<br>
	 * This method may not be called during model creation.
	 * 
	 * @see TypeIndex#getMappingRelevantTypes()
	 */
	@Override
	public Collection<? extends TypeDefinition> getMappingRelevantTypes() {
		synchronized (this) {
			if (mappingRelevantTypes == null) {
				mappingRelevantTypes = new LinkedHashSet<TypeDefinition>();
				for (TypeDefinition type : types.values()) {
					if (type.getConstraint(MappingRelevantFlag.class).isEnabled()
							&& type.getConstraint(MappableFlag.class).isEnabled()) {
						mappingRelevantTypes.add(type);
					}
				}
			}
			return Collections.unmodifiableCollection(mappingRelevantTypes);
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.common.schema.model.TypeIndex#toggleMappingRelevant(java.util.Collection)
	 */
	@Override
	public void toggleMappingRelevant(Collection<? extends TypeDefinition> types) {
		synchronized (this) {
			for (TypeDefinition type : types) {
				Definition<TypeConstraint> def = type;
				if (type.getConstraint(MappingRelevantFlag.class).isEnabled()) {
					if (mappingRelevantTypes != null)
						mappingRelevantTypes.remove(type);
					((AbstractDefinition<TypeConstraint>) def)
							.setConstraint(MappingRelevantFlag.DISABLED);
				}
				else {
					if (mappingRelevantTypes != null)
						mappingRelevantTypes.add(type);
					((AbstractDefinition<TypeConstraint>) def)
							.setConstraint(MappingRelevantFlag.ENABLED);
				}
			}
		}
	}
}
