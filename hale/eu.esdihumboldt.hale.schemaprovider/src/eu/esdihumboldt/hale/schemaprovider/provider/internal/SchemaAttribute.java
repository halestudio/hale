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

package eu.esdihumboldt.hale.schemaprovider.provider.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaEnumerationFacet;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;
import org.apache.ws.commons.schema.XmlSchemaSimpleType;
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeRestriction;
import org.geotools.feature.AttributeTypeBuilder;
import org.geotools.feature.NameImpl;
import org.geotools.gml3.GMLSchema;
import org.geotools.xs.XSSchema;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.Name;

import com.vividsolutions.jts.geom.Geometry;

import eu.esdihumboldt.hale.schemaprovider.EnumAttributeTypeImpl;
import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * Represents the definition of an attribute
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class SchemaAttribute extends AbstractSchemaAttribute {
	
	private static final Logger _log = Logger.getLogger(SchemaAttribute.class);
	
	private static final XSSchema xsSchema = new XSSchema();
	
	private static final GMLSchema gmlSchema = new GMLSchema();
	
	//private final XmlSchemaElement element;

	/**
	 * Constructor
	 * 
	 * @param declaringType the declaring type, if it is <code>null</code>,
	 *   the attribute type will not be determined
	 * @param name the attribute name
	 * @param typeName the name of the attribute type
	 * @param element the element defining the attribute
	 * @param featureTypes the local feature types
	 * @param importedFeatureTypes the imported feature types
	 */
	public SchemaAttribute(TypeDefinition declaringType, String name, Name typeName,
			XmlSchemaElement element, Map<Name, TypeDefinition> featureTypes, 
			Map<Name, TypeDefinition> importedFeatureTypes) {
		super(declaringType, name, typeName, element);
		
		if (declaringType != null) {
			// set the declaring type
			declaringType.addDeclaredAttribute(this);
			
			// determine the attribute type
			determineAttributeType(element, featureTypes, importedFeatureTypes);
		}
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param other the schema attribute to copy
	 */
	protected SchemaAttribute(SchemaAttribute other) {
		super(other);
	}
	
	/**
	 * Tries to determine the attribute type
	 * 
	 * @param element the schema element 
	 * @param featureTypes the local feature types
	 * @param importedFeatureTypes the imported feature types
	 */
	protected void determineAttributeType(XmlSchemaElement element, Map<Name, TypeDefinition> featureTypes, Map<Name, TypeDefinition> importedFeatureTypes) {
		TypeDefinition typeDef = getXSType(getTypeName());
			
		// Try to resolve the attribute bindings
		
		if (typeDef == null) {
			typeDef = getSchemaAttributeType(getTypeName(), featureTypes);
		}
		if (typeDef == null) {
			typeDef = getSchemaAttributeType(getTypeName(), importedFeatureTypes);
		}
		
		if (typeDef == null) {
			// Bindings for enumeration types
			typeDef = getEnumAttributeType(element);
		}
		
		if (typeDef == null) {
			// GML bindings
			AttributeType gmlType = gmlSchema.get(getTypeName());
			if (gmlType != null) {
				typeDef = new TypeDefinition(getTypeName(), gmlType, null);
			}
		}
		if (typeDef == null ) {
			_log.warn("Type NOT found: " + getTypeName().getLocalPart());
		}
		
		typeDef = checkAttributeType(typeDef);
		
		setAttributeType(typeDef);
	}
	
	/**
	 * Check if the given type definition should be set as the attribute type
	 * 
	 * @param typeDef the type definition
	 * 
	 * @return the type definition that shall be set as the attribute type
	 */
	protected TypeDefinition checkAttributeType(TypeDefinition typeDef) {
		// inspire geometry attributes
		if (getName().equals("geometry") && typeDef != null && 
				!Geometry.class.isAssignableFrom(typeDef.getType().getBinding())) {
			// create an attribute type with a geometry binding
			AttributeTypeBuilder builder = new AttributeTypeBuilder();
			builder.setBinding(Geometry.class);
			builder.setName(getTypeName().getLocalPart());
			builder.setNamespaceURI(getTypeName().getNamespaceURI());
			builder.setNillable(true);
			AttributeType attributeType = builder.buildType();
			
			return new TypeDefinition(getTypeName(), attributeType, null);
		}
		
		// default: leave type untouched
		return typeDef;
	}
	
	

	/**
	 * Returns the attribute type for an enumeration.
	 * 
	 * @param element the defining element
	 * 
	 * @return the attribute type or <code>null</code>
	 */
	private static TypeDefinition getEnumAttributeType(XmlSchemaElement element) {
		if (element.getSchemaType() instanceof XmlSchemaSimpleType) {
			return getEnumAttributeType((XmlSchemaSimpleType) element.getSchemaType(), null);
		}
		else {
			return null;
		}
	}
	
	/**
	 * Returns the attribute type for an enumeration.
	 * 
	 * @param simpleType the simple type
	 * @param name the custom type name or <code>null</code>
	 * 
	 * @return the attribute type or <code>null</code>
	 */
	public static TypeDefinition getEnumAttributeType(XmlSchemaSimpleType simpleType, Name name) {
		AttributeType type = null;
		if (simpleType.getContent() instanceof  XmlSchemaSimpleTypeRestriction) {
			XmlSchemaSimpleTypeRestriction content = (XmlSchemaSimpleTypeRestriction)simpleType.getContent();
			
			Name attributeName = new NameImpl(
					content.getBaseTypeName().getNamespaceURI(),
					content.getBaseTypeName().getLocalPart());
			type =  new XSSchema().get(attributeName);
			
			List<String> values = new ArrayList<String>();
			XmlSchemaObjectCollection facets = content.getFacets();
			for (int i = 0; i < facets.getCount(); i++) {
				XmlSchemaObject facet = facets.getItem(i);
				if (facet instanceof XmlSchemaEnumerationFacet) {
					String value = ((XmlSchemaEnumerationFacet) facet).getValue().toString();
					values.add(value);
				}
			}
			
			if (!values.isEmpty()) {
				type = new EnumAttributeTypeImpl(type, values, name);
			}
		}
		
		if (type != null) {
			TypeDefinition typeDef = new TypeDefinition(name, type, null);
			return typeDef;
		}
		else {
			return null;
		}
	}
	
	/**
	 * Get an attribute type from a feature type map
	 * 
	 * @param name the attribute type name
	 * @param featureTypes the feature type map
	 * @return the attribute type or <code>null</code> if the corresponding
	 *   type was not found in the set
	 */
	private static TypeDefinition getSchemaAttributeType(Name name, Map<Name, TypeDefinition> featureTypes) {
		return featureTypes.get(name);
	}
	
	/**
	 * Get the XML schema type
	 * 
	 * @param name the type name
	 * 
	 * @return the type definition or <code>null</code>
	 */
	private static TypeDefinition getXSType(Name name) {
		AttributeType ty = xsSchema.get(name);
		
		if (ty != null) {
			return new TypeDefinition(name, ty, null);
		}
		else {
			return null;
		}
	}

	/**
	 * @see AttributeDefinition#copyAttribute(TypeDefinition)
	 */
	@Override
	public AttributeDefinition copyAttribute(TypeDefinition parentType) {
		SchemaAttribute copy = new SchemaAttribute(this);
		copy.setParentType(parentType);
		return copy;
	}
	
}
