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
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappableFlag;

/**
 * Default {@link TypeIndex} implementation
 * @author Simon Templer
 */
public class DefaultTypeIndex implements TypeIndex {

	private final Map<QName, TypeDefinition> types = new HashMap<QName, TypeDefinition>();
	private Set<TypeDefinition> mappableTypes;

	/**
	 * Add a type to the type index.
	 * 
	 * @param type the type to add
	 */
	public void addType(TypeDefinition type) {
		synchronized (this) {
			types.put(type.getName(), type);

			if (mappableTypes != null && type.getConstraint(MappableFlag.class).isEnabled())
				mappableTypes.add(type); 
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
	 * @see TypeIndex#getMappableTypes()
	 */
	@Override
	public Collection<? extends TypeDefinition> getMappableTypes() {
		synchronized (this) {
			if (mappableTypes == null) {
				mappableTypes = new HashSet<TypeDefinition>();
				for (TypeDefinition type : types.values()) {
					if (type.getConstraint(MappableFlag.class).isEnabled()) {
						mappableTypes.add(type);
					}
				}
			}
			return Collections.unmodifiableCollection(mappableTypes);
		}
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