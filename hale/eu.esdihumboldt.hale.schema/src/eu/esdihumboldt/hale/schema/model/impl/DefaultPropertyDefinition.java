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

package eu.esdihumboldt.hale.schema.model.impl;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.schema.model.Definition;
import eu.esdihumboldt.hale.schema.model.PropertyConstraint;
import eu.esdihumboldt.hale.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.schema.model.TypeDefinition;

/**
 * Default {@link PropertyDefinition} implementation
 * @author Simon Templer
 */
public class DefaultPropertyDefinition extends AbstractDefinition<PropertyConstraint> implements
		PropertyDefinition {
	
	/**
	 * The type associated with the property content
	 */
	private final TypeDefinition propertyType;
	
	/**
	 * The type declaring the property
	 */
	private final DefaultTypeDefinition declaringType;
	
	/**
	 * Create a new property and add it to the declaring type
	 * 
	 * @param name the property qualified name
	 * @param declaringType the declaring type
	 * @param propertyType the property type
	 */
	public DefaultPropertyDefinition(QName name,
			DefaultTypeDefinition declaringType,
			TypeDefinition propertyType) {
		super(name);
		this.declaringType = declaringType;
		this.propertyType = propertyType;
		
		declaringType.addDeclaredProperty(this);
	}
	
	/**
	 * @see Definition#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		if (getParentType() == null) {
			return name.getNamespaceURI() + "/" + name.getLocalPart();
		}
		else {
			return getParentType().getIdentifier() + "/" + name.getLocalPart(); //$NON-NLS-1$
		}
	}
	
	/**
	 * @see PropertyDefinition#getDeclaringType()
	 */
	@Override
	public TypeDefinition getDeclaringType() {
		return declaringType;
	}

	/**
	 * Returns the declaring type by default.
	 * 
	 * @see PropertyDefinition#getParentType()
	 */
	@Override
	public TypeDefinition getParentType() {
		return getDeclaringType();
	}

	/**
	 * @see PropertyDefinition#getPropertyType()
	 */
	@Override
	public TypeDefinition getPropertyType() {
		return propertyType;
	}

}
