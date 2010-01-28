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

import java.util.ArrayList;
import java.util.List;

import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;

/**
 * Represents a type definition
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class TypeDefinition {
	
	/**
	 * The type name
	 */
	private final Name name;

	/**
	 * The feature type representing the type
	 */
	private AttributeType type;
	
	/**
	 * The super type definition
	 */
	private final TypeDefinition superType;
	
	/**
	 * If the type is abstract
	 */
	private boolean abstractType = false; 
	
	/**
	 * The list of declared attributes
	 */
	private final List<AttributeDefinition> declaredAttributes = new ArrayList<AttributeDefinition>();

	/**
	 * Create a new type definition
	 * 
	 * @param name the type name 
	 * @param type the corresponding feature type, may be <code>null</code>
	 * @param superType the super type, may be <code>null</code>
	 */
	public TypeDefinition(Name name, AttributeType type, 
			TypeDefinition superType) {
		super();
		this.name = name;
		this.type = type;
		this.superType = superType;
	}
	
	/**
	 * Determines if this type actually represents a feature type that is based
	 * on AbstractFeatureType
	 * 
	 * @return if this definition represents a feature type
	 */
	public boolean isFeatureType() {
		AttributeType type = getType();
		
		if (type != null && !(type instanceof FeatureType)) {
			return false;
		}
		if (name.getLocalPart().equalsIgnoreCase("AbstractFeatureType")) {
			return true;
		}
		else if (getSuperType() == null) {
			return false;
		}
		else {
			return getSuperType().isFeatureType();
		}
	}
	
	/**
	 * Determines if this type represents a complex type
	 * 
	 * @return if this definition represents a complex type
	 */
	public boolean isComplexType() {
		if (getType() != null) {
			return getType() instanceof FeatureType;
		}
		else {
			return isFeatureType();
		}
	}
	
	/**
	 * Add a declared attribute, the declaring type of the attribute will be set
	 * to this type
	 * 
	 * @param attribute the attribute definition
	 */
	public void addDeclaredAttribute(AttributeDefinition attribute) {
		attribute.setDeclaringType(this);
		declaredAttributes.add(attribute);
	}
	
	/**
	 * Removes a declared attribute
	 * 
	 * @param attribute
	 */
	public void removeDeclaredAttribute(AttributeDefinition attribute) {
		attribute.setDeclaringType(null);
		declaredAttributes.remove(attribute);
	}

	/**
	 * @return the name
	 */
	public Name getName() {
		return name;
	}

	/**
	 * @return the featureType
	 */
	public AttributeType getType() {
		if (type == null) {
			type = createFeatureType();
		}
		return type;
	}
	
	/**
	 * Get the feature type if available
	 * 
	 * @return the feature type or <code>null</code> if no type could be
	 *   determined or the type is not a feature type
	 */
	public FeatureType getFeatureType() {
		if (getType() != null && getType() instanceof FeatureType) {
			return (FeatureType) getType();
		}
		else {
			return null;
		}
	}

	/**
	 * Create the feature type from the super types and attributes, this method
	 *   will be called when there was no explicit type provided
	 * 
	 * @return the feature type
	 */
	protected FeatureType createFeatureType() {
		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
		
		if (getSuperType() != null) {
			// has super type
			if (getSuperType().getType() != null && getSuperType().getType() instanceof SimpleFeatureType) {
				builder.setSuperType((SimpleFeatureType) getSuperType().getType());
			}
			else {
				builder.setSuperType(null);
			}
		}
		else {
			builder.setSuperType(null);
		}
		
		// add all attributes
		TypeDefinition typeDef = this;
		while (typeDef != null) {
			// add attributes for current type
			for (AttributeDefinition attribute : typeDef.getDeclaredAttributes()) {
				AttributeDescriptor desc = attribute.createAttributeDescriptor();
				if (desc != null) {
					builder.add(desc);
				}
			}
			
			// switch to super type
			typeDef = typeDef.getSuperType();
		}
		
		// other properties
		builder.setAbstract(abstractType);
		
		builder.setName(getName());
		return builder.buildFeatureType();
	}

	/**
	 * @return the superType
	 */
	public TypeDefinition getSuperType() {
		return superType;
	}

	/**
	 * @return the declaredAttributes
	 */
	public List<AttributeDefinition> getDeclaredAttributes() {
		return declaredAttributes;
	}

	/**
	 * @return the abstractType
	 */
	public boolean isAbstract() {
		return abstractType;
	}

	/**
	 * @param abstractType the abstractType to set
	 */
	public void setAbstract(boolean abstractType) {
		this.abstractType = abstractType;
	}

}
