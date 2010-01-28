/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.schemaprovider.model;

import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.Name;

/**
 * Represents an attribute definition
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public abstract class AttributeDefinition {
	
	private final String name;
	
	private final Name typeName;
	
	private TypeDefinition attributeType;
	
	private TypeDefinition declaringType;

	/**
	 * Create an attribute definition
	 * 
	 * @param name the attribute name
	 * @param typeName the name of the attribute type
	 * @param attributeType the corresponding attribute type, may be <code>null</code>
	 */
	public AttributeDefinition(String name, Name typeName,
			TypeDefinition attributeType) {
		super();
		this.name = name;
		this.typeName = typeName;
		this.attributeType = attributeType;
	}
	
	/**
	 * Create an attribute descriptor
	 * 
	 * @return the attribute descriptor
	 */
	public abstract AttributeDescriptor createAttributeDescriptor();

	/**
	 * Set the declaring type
	 * 
	 * @param declaringType the declaring type
	 */
	void setDeclaringType(TypeDefinition declaringType) {
		this.declaringType = declaringType;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the typeName
	 */
	public Name getTypeName() {
		return typeName;
	}

	/**
	 * @return the attributeType
	 */
	public TypeDefinition getAttributeType() {
		return attributeType;
	}

	/**
	 * @return the declaringType
	 */
	public TypeDefinition getDeclaringType() {
		return declaringType;
	}

	/**
	 * @param attributeType the attributeType to set
	 */
	public void setAttributeType(TypeDefinition attributeType) {
		this.attributeType = attributeType;
	}

}
