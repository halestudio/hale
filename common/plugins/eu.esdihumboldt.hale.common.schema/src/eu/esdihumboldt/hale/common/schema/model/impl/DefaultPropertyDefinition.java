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

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Default {@link PropertyDefinition} implementation
 * 
 * @author Simon Templer
 */
public class DefaultPropertyDefinition extends AbstractChildDefinition<PropertyConstraint>
		implements PropertyDefinition {

	/**
	 * The type associated with the property content
	 */
	private final TypeDefinition propertyType;

	/**
	 * Create a new property and add it to the parent group
	 * 
	 * @param name the property qualified name
	 * @param parentGroup the parent group
	 * @param propertyType the property type
	 */
	public DefaultPropertyDefinition(QName name, DefinitionGroup parentGroup,
			TypeDefinition propertyType) {
		super(name, parentGroup);
		this.propertyType = propertyType;
	}

	/**
	 * @see PropertyDefinition#getPropertyType()
	 */
	@Override
	public TypeDefinition getPropertyType() {
		return propertyType;
	}

	/**
	 * @see AbstractDefinition#getDescription()
	 */
	@Override
	public String getDescription() {
		String desc = super.getDescription();
		if (desc == null || desc.isEmpty()) {
			return getPropertyType().getDescription();
		}
		return desc;
	}

	/**
	 * @see AbstractDefinition#toString()
	 */
	@Override
	public String toString() {
		return "[property] " + super.toString();
	}

	/**
	 * @see ChildDefinition#asProperty()
	 */
	@Override
	public PropertyDefinition asProperty() {
		return this;
	}

	/**
	 * @see ChildDefinition#asGroup()
	 */
	@Override
	public GroupPropertyDefinition asGroup() {
		return null;
	}

}
