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
import org.geotools.feature.NameImpl;
import org.geotools.gml3.GMLSchema;
import org.geotools.xs.XSSchema;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.Name;

import eu.esdihumboldt.hale.schemaprovider.EnumAttributeTypeImpl;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public abstract class TypeUtil {
	
	private static final Logger log = Logger.getLogger(TypeUtil.class);

	/**
	 * The XS schema
	 */
	protected static final XSSchema xsSchema = new XSSchema();
	
	/**
	 * The GML schema
	 */
	protected static final GMLSchema gmlSchema = new GMLSchema();
	
	/**
	 * Resolve an attribute type
	 * 
	 * @param typeName
	 * @param types
	 * @param importedTypes
	 * @return the type definition or <code>null</code>
	 */
	public static TypeDefinition resolveAttributeType(Name typeName, Map<Name, TypeDefinition> types, Map<Name, TypeDefinition> importedTypes) {
		TypeDefinition typeDef = getXSType(typeName);
		
		// Try to resolve the attribute bindings
		
		if (typeDef == null) {
			typeDef = getSchemaAttributeType(typeName, types);
		}
		if (typeDef == null) {
			typeDef = getSchemaAttributeType(typeName, importedTypes);
		}
		
		if (typeDef == null) {
			// GML bindings
			AttributeType gmlType = gmlSchema.get(typeName);
			if (gmlType != null) {
				typeDef = new TypeDefinition(typeName, gmlType, null);
			}
		}
		
		if (typeDef == null ) {
			log.warn("Type NOT found: " + typeName.getLocalPart());
		}
		
		return typeDef;
	}
	
	/**
	 * Resolve an element type
	 * 
	 * @param element 
	 * @param typeName
	 * @param types
	 * @param importedTypes
	 * @return the type definition or <code>null</code>
	 */
	public static TypeDefinition resolveElementType(XmlSchemaElement element, Name typeName, Map<Name, TypeDefinition> types, Map<Name, TypeDefinition> importedTypes) {
		TypeDefinition typeDef = getXSType(typeName);
	
		// Try to resolve the attribute bindings
		
		if (typeDef == null) {
			typeDef = getSchemaAttributeType(typeName, types);
		}
		if (typeDef == null) {
			typeDef = getSchemaAttributeType(typeName, importedTypes);
		}
		
		if (typeDef == null) {
			// Bindings for enumeration types
			typeDef = getEnumAttributeType(element, typeName);
		}
		
		if (typeDef == null) {
			// GML bindings
			AttributeType gmlType = gmlSchema.get(typeName);
			if (gmlType != null) {
				typeDef = new TypeDefinition(typeName, gmlType, null);
			}
		}
		if (typeDef == null ) {
			log.warn("Type NOT found: " + typeName.getLocalPart());
		}
		
		return typeDef;
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
	 * Returns the attribute type for an enumeration.
	 * 
	 * @param element the defining element
	 * @param typeName 
	 * 
	 * @return the attribute type or <code>null</code>
	 */
	private static TypeDefinition getEnumAttributeType(XmlSchemaElement element, Name typeName) {
		if (element.getSchemaType() instanceof XmlSchemaSimpleType) {
			return getEnumAttributeType((XmlSchemaSimpleType) element.getSchemaType(), typeName);
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
	
}
