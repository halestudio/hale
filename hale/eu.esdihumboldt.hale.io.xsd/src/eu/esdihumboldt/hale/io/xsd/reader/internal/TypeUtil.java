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

package eu.esdihumboldt.hale.io.xsd.reader.internal;

import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchemaEnumerationFacet;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;
import org.apache.ws.commons.schema.XmlSchemaPatternFacet;
import org.apache.ws.commons.schema.XmlSchemaSimpleType;
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeContent;
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeList;
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeRestriction;
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeUnion;
import org.geotools.feature.NameImpl;
import org.geotools.feature.type.AttributeTypeImpl;
import org.geotools.xs.XSSchema;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.Name;

import eu.esdihumboldt.hale.core.io.report.IOReporter;
import eu.esdihumboldt.hale.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.io.xsd.constraint.RestrictionFlag;
import eu.esdihumboldt.hale.io.xsd.constraint.SuperTypeBinding;
import eu.esdihumboldt.hale.io.xsd.reader.internal.constraint.UnionBindingConstraint;
import eu.esdihumboldt.hale.io.xsd.reader.internal.constraint.UnionEnumerationConstraint;
import eu.esdihumboldt.hale.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.schema.model.constraints.type.AbstractFlag;
import eu.esdihumboldt.hale.schema.model.constraints.type.BindingConstraint;
import eu.esdihumboldt.hale.schema.model.constraints.type.EnumerationConstraint;
import eu.esdihumboldt.hale.schema.model.constraints.type.MappableFlag;
import eu.esdihumboldt.hale.schema.model.constraints.type.SimpleFlag;

/**
 * Utility methods regarding type resolving
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class TypeUtil {
	
//	private static final ALogger log = ALoggerFactory.getLogger(TypeUtil.class);
//	
//	private static final AGroup TYPE_RESOLVE = AGroupFactory.getGroup(Messages.getString("TypeUtil.0")); //$NON-NLS-1$

	/**
	 * The XML simple types schema
	 */
	protected static final XSSchema xsSchema = new XSSchema();
	
//	/**
//	 * The GML schema
//	 */
//	protected static final GMLSchema gml3Schema = new GMLSchema();
	
//	/**
//	 * Geotools bindings location string
//	 */
//	private static final String GEOTOOLS_LOC = Messages.getString("TypeUtil.1"); //$NON-NLS-1$
//	
//	/**
//	 * Geotools bindings location prefix
//	 */
//	private static final String GEOTOOLS_LOC_PREFIX = Messages.getString("TypeUtil.2"); //$NON-NLS-1$
//
//	/**
//	 * GML 3.2 namespace
//	 */
//	private static final String NAMESPACE_GML3_2 = "http://www.opengis.net/gml/3.2"; //$NON-NLS-1$
//
//	private static final String NAMESPACE_GML = "http://www.opengis.net/gml"; //$NON-NLS-1$
	
	/**
	 * Set of XML schema types that should get a String binding but don't get
	 * one through the Geotools bindings
	 * 
	 * @see "http://www.w3schools.com/Schema/schema_dtypes_string.asp"
	 */
	private static final Set<String> XS_STRING_TYPES = new HashSet<String>();
	static {
		XS_STRING_TYPES.add("ID"); //$NON-NLS-1$
		XS_STRING_TYPES.add("IDREF"); //$NON-NLS-1$
		XS_STRING_TYPES.add("NCName"); //$NON-NLS-1$
		XS_STRING_TYPES.add("token"); //$NON-NLS-1$
		XS_STRING_TYPES.add("Name"); //$NON-NLS-1$
		XS_STRING_TYPES.add("language"); //$NON-NLS-1$
		XS_STRING_TYPES.add("ENTITY"); //$NON-NLS-1$
		XS_STRING_TYPES.add("ENTITIES"); //$NON-NLS-1$
		XS_STRING_TYPES.add("NMTOKEN"); //$NON-NLS-1$
		XS_STRING_TYPES.add("NMTOKENS"); //$NON-NLS-1$
		XS_STRING_TYPES.add("normalizedString"); //$NON-NLS-1$
		XS_STRING_TYPES.add("QName"); //$NON-NLS-1$
	}
	
//	/**
//	 * Get the attribute type for an GML type
//	 * 
//	 * @param typeName the type name
//	 * 
//	 * @return the attribute type or <code>null</code>
//	 */
//	public static AttributeType getGMLAttributeType(Name typeName) {
//		AttributeType gmlType = gml3Schema.get(typeName);
//		if (gmlType == null && typeName.getNamespaceURI().equals(NAMESPACE_GML3_2)) {
//			// try again with GML2/3 namespace
//			gmlType = gml3Schema.get(new NameImpl(NAMESPACE_GML, typeName.getLocalPart()));
//			//FIXME replicate type with correct namespace?
//		}
//		return gmlType;
//	}
//	
//	/**
//	 * Get the predefined attribute type (GML or XS) with the given type name
//	 * 
//	 * @param typeName the type name
//	 * 
//	 * @return the attribute type or <code>null</code>
//	 */
//	public static AttributeType getPredefinedAttributeType(Name typeName) {
//		AttributeType result = xsSchema.get(typeName);
//		
//		if (result == null) {
//			result = getGMLAttributeType(typeName);
//		}
//		
//		return result;
//	}

	/**
	 * Configure a type with defaults if possible, e.g. for simple types
	 * 
	 * @param type the type to configure
	 */
	public static void configureType(XmlTypeDefinition type) {
		// XSD simple types
		if (configureXsdSimpleType(type)) {
			return;
		}
		
		//TODO more configuration options?
		//TODO e.g. GML?
	}
	
	/**
	 * Configure the given type as XML schema simple type if possible
	 * 
	 * @param type the type to configure
	 * @return if the type could be configured as XSD simple type
	 */
	@SuppressWarnings("unchecked")
	private static boolean configureXsdSimpleType(XmlTypeDefinition type) {
		Name typeName = new NameImpl(type.getName().getNamespaceURI(), 
				type.getName().getLocalPart());
		
		AttributeType ty = xsSchema.get(typeName);
		
		// special case: ID etc. - assure String binding
		if (ty != null && XS_STRING_TYPES.contains(typeName.getLocalPart())) {
			ty = new AttributeTypeImpl(typeName, java.lang.String.class, false, false,
	                Collections.EMPTY_LIST, XSSchema.NCNAME_TYPE, null);
		}
		
		if (ty != null) {
			// configure type
			
			// set binding
			type.setConstraint(BindingConstraint.getBinding(ty.getBinding()));
			// simple type flag
			type.setConstraint(SimpleFlag.ENABLED);
			// not abstract
			type.setConstraint(AbstractFlag.DISABLED);
			// not mappable
			type.setConstraint(MappableFlag.DISABLED);
			
			type.setLocation(URI.create(XMLConstants.W3C_XML_SCHEMA_NS_URI));
			if (ty.getDescription() != null) {
				type.setDescription(ty.getDescription().toString());
			}
			
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Configure a type definition for a simple type based on the 
	 * {@link XmlSchemaSimpleType}.
	 * 
	 * @param type the type definition
	 * @param simpleType the schema simple type
	 * @param index the XML index for resolving type definitions
	 * @param reporter the report
	 */
	public static void configureSimpleType(XmlTypeDefinition type,
			XmlSchemaSimpleType simpleType, XmlIndex index, IOReporter reporter) {
		XmlSchemaSimpleTypeContent content = simpleType.getContent();
		
		// it's a simple type
		type.setConstraint(SimpleFlag.ENABLED);
		
		if (content instanceof XmlSchemaSimpleTypeUnion) {
			// simple type union
			configureSimpleTypeUnion(type, (XmlSchemaSimpleTypeUnion) content, 
					index, reporter);
		}
		else if (content instanceof XmlSchemaSimpleTypeList) {
			// simple type list
			configureSimpleTypeList(type, (XmlSchemaSimpleTypeList) content, 
					index, reporter);
		}
		else if (content instanceof XmlSchemaSimpleTypeRestriction) {
			// simple type restriction
			configureSimpleTypeRestriction(type, (XmlSchemaSimpleTypeRestriction) content, 
					index, reporter);
		}
		else {
			reporter.error(new IOMessageImpl(MessageFormat.format("Unrecognized simple type {0}", type.getName()),
					null, simpleType.getLineNumber(), simpleType.getLinePosition()));
		}
	}

	/**
	 * Configure a type definition for a simple type based on a simple type
	 * restriction.
	 * 
	 * @param type the type definition
	 * @param restriction the simple type restriction
	 * @param index the XML index for resolving type definitions
	 * @param reporter the report
	 */
	private static void configureSimpleTypeRestriction(XmlTypeDefinition type,
			XmlSchemaSimpleTypeRestriction restriction, XmlIndex index,
			IOReporter reporter) {
		QName baseTypeName = restriction.getBaseTypeName();
	
		// resolve super type
		XmlTypeDefinition baseTypeDef = index.getOrCreateType(baseTypeName);
		// set super type
		type.setSuperType(baseTypeDef);
		// mark as restriction
		type.setConstraint(RestrictionFlag.ENABLED);
		
		// assign super type binding
		type.setConstraint(new SuperTypeBinding(type));
		
		//TODO improve support for enumeration and facets in general 
		List<String> values = new ArrayList<String>();
		XmlSchemaObjectCollection facets = restriction.getFacets();
		for (int i = 0; i < facets.getCount(); i++) {
			XmlSchemaObject facet = facets.getItem(i);
			if (facet instanceof XmlSchemaEnumerationFacet) {
				String value = ((XmlSchemaEnumerationFacet) facet).getValue().toString();
				values.add(value);
			}
			else if (facet instanceof XmlSchemaPatternFacet) {
				//TODO support for patterns
			}
			else {
				//TODO support for other facets?
			}
		}
		
		if (!values.isEmpty()) {
			// set enumeration constraint
			//XXX conversion to be done?
			type.setConstraint(new EnumerationConstraint<String>(values, false)); 
		}
	}

	/**
	 * Configure a type definition for a simple type based on a simple type
	 * list.
	 * 
	 * @param type the type definition
	 * @param list the simple type list
	 * @param index the XML index for resolving type definitions
	 * @param reporter the report
	 */
	private static void configureSimpleTypeList(XmlTypeDefinition type,
			XmlSchemaSimpleTypeList list, XmlIndex index, IOReporter reporter) {
		//FIXME support for list types
//		AttributeType attributeType = createListAttributeType(typeName, dependencies, list, schemaTypes);
//		
//		typeDef = new TypeDefinition(typeName, attributeType, null);
		//TODO use item type information
//		/*if (list.getItemType() == null) {
//		 
//		}
//		else if (list.getItemTypeName() != null) {
//			
//		}*/
//		
//		AttributeTypeBuilder typeBuilder = new AttributeTypeBuilder();
//		typeBuilder.setBinding(List.class);
//		typeBuilder.setName(typeName.getLocalPart());
//		typeBuilder.setNamespaceURI(typeName.getNamespaceURI());
//		typeBuilder.setNillable(true);
//		return typeBuilder.buildType();
	}

	/**
	 * Configure a type definition for a simple type based on a simple type
	 * union.
	 * 
	 * @param type the type definition
	 * @param union the simple type union
	 * @param index the XML index for resolving type definitions
	 * @param reporter the report
	 */
	private static void configureSimpleTypeUnion(XmlTypeDefinition type,
			XmlSchemaSimpleTypeUnion union, XmlIndex index,
			IOReporter reporter) {
		XmlSchemaObjectCollection baseTypes = union.getBaseTypes();
		
		// collect type definitions
		Set<TypeDefinition> unionTypes = new HashSet<TypeDefinition>();
		
		// base type definitions
		if (baseTypes != null && baseTypes.getCount() > 0) {
			for (int i = 0; i < baseTypes.getCount(); i++) {
				XmlSchemaObject baseType = baseTypes.getItem(i);
				if (baseType instanceof XmlSchemaSimpleType) {
					XmlSchemaSimpleType simpleType = (XmlSchemaSimpleType) baseType;
					XmlTypeDefinition baseDef;
					if (simpleType.getQName() != null) {
						// named type
						baseDef = index.getOrCreateType(simpleType.getQName());
					}
					else {
						// anonymous type
						QName baseName = new QName(type.getName().getNamespaceURI() + 
								"/" + type.getName().getLocalPart(), "AnonymousType" + i); //$NON-NLS-1$ //$NON-NLS-2$
						
						baseDef = new AnonymousXmlType(baseName);
					}
					
					configureSimpleType(baseDef, simpleType, index, reporter);
					unionTypes.add(baseDef);
				}
				else {
					reporter.error(new IOMessageImpl(
							"Unrecognized base type for simple type union", 
							null, union.getLineNumber(), union.getLinePosition()));
				}
			}
		}
		
		// references base types by name
		QName[] members = union.getMemberTypesQNames();
		if (members != null) {
			for (QName name : members) {
				if (name != null) {
					XmlTypeDefinition baseDef = index.getOrCreateType(name);
					unionTypes.add(baseDef);
				}
			}
		}
		
		//TODO set constraints
		// binding constraint
		type.setConstraint(new UnionBindingConstraint(unionTypes));
		// enumeration constraint
		type.setConstraint(new UnionEnumerationConstraint(unionTypes));
	}
	
}
