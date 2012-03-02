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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappingRelevantFlag;

/**
 * Default {@link TypeIndex} implementation
 * @author Simon Templer
 */
public class DefaultTypeIndex implements TypeIndex {

	private final Map<QName, TypeDefinition> types = new HashMap<QName, TypeDefinition>();
	private Set<TypeDefinition> mappingRelevantTypes;

	/**
	 * Add a type to the type index.
	 * 
	 * @param type the type to add
	 */
	public void addType(TypeDefinition type) {
		synchronized (this) {
			types.put(type.getName(), type);

			if (mappingRelevantTypes != null && type.getConstraint(MappingRelevantFlag.class).isEnabled())
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
				mappingRelevantTypes = new HashSet<TypeDefinition>();
				for (TypeDefinition type : types.values()) {
					if (type.getConstraint(MappingRelevantFlag.class).isEnabled()) {
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
					if (mappingRelevantTypes != null && mappingRelevantTypes.contains(type))
						mappingRelevantTypes.remove(type);
					((AbstractDefinition<TypeConstraint>) def).setConstraint(MappingRelevantFlag.DISABLED);
				} else {
					if (mappingRelevantTypes != null)
						mappingRelevantTypes.add(type);
					((AbstractDefinition<TypeConstraint>) def).setConstraint(MappingRelevantFlag.ENABLED);
				}
			}
		}
	}
}