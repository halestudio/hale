/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 *
 * Component    : HALE
 * Created on   : Jun 3, 2009 -- 4:50:10 PM
 */
package eu.esdihumboldt.hale.schemaprovider.provider;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAttribute;
import org.apache.ws.commons.schema.XmlSchemaAttributeGroup;
import org.apache.ws.commons.schema.XmlSchemaAttributeGroupRef;
import org.apache.ws.commons.schema.XmlSchemaChoice;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaComplexContentExtension;
import org.apache.ws.commons.schema.XmlSchemaComplexContentRestriction;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaContent;
import org.apache.ws.commons.schema.XmlSchemaContentModel;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaExternal;
import org.apache.ws.commons.schema.XmlSchemaInclude;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;
import org.apache.ws.commons.schema.XmlSchemaParticle;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.apache.ws.commons.schema.XmlSchemaSimpleContentExtension;
import org.apache.ws.commons.schema.XmlSchemaSimpleContentRestriction;
import org.apache.ws.commons.schema.XmlSchemaSimpleType;
import org.apache.ws.commons.schema.XmlSchemaUse;
import org.apache.ws.commons.schema.resolver.DefaultURIResolver;
import org.apache.ws.commons.schema.resolver.URIResolver;
import org.apache.ws.commons.schema.utils.NamespacePrefixList;
import org.geotools.feature.NameImpl;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;

import com.vividsolutions.jts.geom.Geometry;

import de.cs3d.util.logging.AGroup;
import de.cs3d.util.logging.AGroupFactory;
import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.cache.Request;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.impl.LogProgressIndicator;
import eu.esdihumboldt.hale.schemaprovider.AbstractSchemaProvider;
import eu.esdihumboldt.hale.schemaprovider.HumboldtURIResolver;
import eu.esdihumboldt.hale.schemaprovider.Messages;
import eu.esdihumboldt.hale.schemaprovider.Schema;
import eu.esdihumboldt.hale.schemaprovider.SchemaProvider;
import eu.esdihumboldt.hale.schemaprovider.model.AnonymousType;
import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;
import eu.esdihumboldt.hale.schemaprovider.provider.internal.SchemaResult;
import eu.esdihumboldt.hale.schemaprovider.provider.internal.apache.AbstractElementAttribute;
import eu.esdihumboldt.hale.schemaprovider.provider.internal.apache.DefaultAttribute;
import eu.esdihumboldt.hale.schemaprovider.provider.internal.apache.DefaultResolveAttribute;
import eu.esdihumboldt.hale.schemaprovider.provider.internal.apache.ElementReferenceAttribute;
import eu.esdihumboldt.hale.schemaprovider.provider.internal.apache.ProgressURIResolver;
import eu.esdihumboldt.hale.schemaprovider.provider.internal.apache.SchemaAttribute;
import eu.esdihumboldt.hale.schemaprovider.provider.internal.apache.SchemaReferenceResolver;
import eu.esdihumboldt.hale.schemaprovider.provider.internal.apache.SchemaTypeAttribute;
import eu.esdihumboldt.hale.schemaprovider.provider.internal.apache.SchemaTypeResolver;
import eu.esdihumboldt.hale.schemaprovider.provider.internal.apache.TypeUtil;
import eu.esdihumboldt.util.DependencyOrderedList;

/**
 * The main functionality of this class is to load an XML schema file (XSD)
 * and create a FeatureType collection. This implementation is based on the
 * Apache XmlSchema library (http://ws.apache.org/commons/XmlSchema/). It is
 * necessary use this library instead of the GeoTools Xml schema loader, because
 * the GeoTools version cannot handle GML 3.2 based files.
 * 
 * @author Bernd Schneiders, Logica; Thorsten Reitz, Fraunhofer IGD;
 *   Simon Templer, Fraunhofer IGD
 * @version $Id$
 */
@Deprecated
public class ApacheSchemaProvider 
	extends AbstractSchemaProvider {
	
	/**
	 * The log
	 */
	private static ALogger _log = ALoggerFactory.getLogger(ApacheSchemaProvider.class);
	
	private static final AGroup NO_DEFINITION = AGroupFactory.getGroup(Messages.getString("ApacheSchemaProvider.0"));  //$NON-NLS-1$
	
	private static final AGroup MISSING_ATTRIBUTE_REF = AGroupFactory.getGroup(Messages.getString("ApacheSchemaProvider.1")); //$NON-NLS-1$
	
	/**
	 * Default constructor 
	 */
	public ApacheSchemaProvider() {
		super();
		
		addSupportedFormat("xsd"); //$NON-NLS-1$
		addSupportedFormat("gml"); //$NON-NLS-1$
		addSupportedFormat("xml"); //$NON-NLS-1$
	}

	/**
	 * Extracts attribute definitions from a {@link XmlSchemaParticle}.
	 * 
	 * @param typeDef the definition of the declaring type
	 * @param particle the particle
	 * @param schemaTypes the schema types 
	 * @param referenceResolver the reference resolver
	 * 
	 * @return the list of attribute definitions
	 */
	private List<AttributeDefinition> getAttributesFromParticle(TypeDefinition typeDef, XmlSchemaParticle particle, 
			SchemaTypeResolver schemaTypes, SchemaReferenceResolver referenceResolver) {
		List<AttributeDefinition> attributeResults = new ArrayList<AttributeDefinition>();
		
		// particle:
		if (particle instanceof XmlSchemaSequence) {
			// <sequence>
			XmlSchemaSequence sequence = (XmlSchemaSequence)particle;
			for (int j = 0; j < sequence.getItems().getCount(); j++) {
				XmlSchemaObject object = sequence.getItems().getItem(j);
				if (object instanceof XmlSchemaElement) {
					// <element>
					AbstractElementAttribute attribute = getAttributeFromElement(
							(XmlSchemaElement) object, typeDef, schemaTypes, 
							referenceResolver);
					if (attribute != null) {
						attributeResults.add(attribute);
					}
					// </element>
				}
				else if (object instanceof XmlSchemaParticle) {
					// contained particles, e.g. a choice TODO wrap those attributes?
					attributeResults.addAll(getAttributesFromParticle(typeDef, 
							(XmlSchemaParticle) object, schemaTypes, 
							referenceResolver));
				}
			}
			// </sequence>
		}
		else if (particle instanceof XmlSchemaChoice) {
			//FIXME how to correctly deal with this? for now we add all choices
			// <choice>
			XmlSchemaChoice choice = (XmlSchemaChoice) particle;
			for (int j = 0; j < choice.getItems().getCount(); j++) {
				XmlSchemaObject object = choice.getItems().getItem(j);
				if (object instanceof XmlSchemaElement) {
					// <element>
					AbstractElementAttribute attribute = getAttributeFromElement(
							(XmlSchemaElement) object, typeDef, schemaTypes, 
							referenceResolver);
					if (attribute != null) {
						attribute.setMinOccurs(0); //XXX set minOccurs to zero because its a choice
						attributeResults.add(attribute);
					}
					// </element>
				}
				else if (object instanceof XmlSchemaParticle) {
					// contained particles, e.g. a choice TODO wrap those attributes?
					attributeResults.addAll(getAttributesFromParticle(typeDef, 
							(XmlSchemaParticle) object, schemaTypes, 
							referenceResolver));
				}
			}
			// </choice>
		}
		
		return attributeResults;
	}

	/**
	 * Get an attribute from an element
	 * 
	 * @param element the schema element
	 * @param declaringType the definition of the declaring type
	 * @param schemaTypes the schema types
	 * @param referenceResolver the reference resolver
	 * 
	 * @return an attribute definition or <code>null</code>
	 */
	private AbstractElementAttribute getAttributeFromElement(
			XmlSchemaElement element, TypeDefinition declaringType,
			SchemaTypeResolver schemaTypes, SchemaReferenceResolver referenceResolver) {
		if (element.getSchemaTypeName() != null) {
			// element referencing a type
			// <element name="ELEMENT_NAME" type="SCHEMA_TYPE_NAME" />
			return new SchemaAttribute(
					declaringType,
					element.getName(), 
					new NameImpl(element.getSchemaTypeName().getNamespaceURI(), 
							element.getSchemaTypeName().getLocalPart()), 
					element,
					schemaTypes);
		}
		else if (element.getRefName() != null) {
			// references another element
			// <element ref="REF_NAME" />
			Name elementName = new NameImpl(
					element.getRefName().getNamespaceURI(),
					element.getRefName().getLocalPart());
			
			// local element definition
			SchemaElement reference = referenceResolver.getSchemaElement(elementName);
			if (reference == null) {
				_log.warn("Reference to element " + element.getRefName().getNamespaceURI() + "/" + element.getRefName().getLocalPart() +" not found"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				return null;
			}
			else {
				return new ElementReferenceAttribute(
						declaringType, 
						element.getName(), 
						reference.getTypeName(), 
						element, 
						reference);
			}
		}
		else if (element.getSchemaType() != null) {
			// element w/o type or ref
			if (element.getSchemaType() instanceof XmlSchemaComplexType) {
				// <element ...>
				//   <complexType>
				XmlSchemaComplexType complexType = (XmlSchemaComplexType) element.getSchemaType();
				XmlSchemaContentModel model = complexType.getContentModel();
				XmlSchemaParticle particle = complexType.getParticle();
				if (model != null) {
					XmlSchemaContent content = model.getContent();
					
					QName qname = null;
					if (content instanceof XmlSchemaComplexContentExtension || 
							content instanceof XmlSchemaComplexContentRestriction) {
						// <complexContent>
						//   <extension base="..."> / <restriction ...>
						String nameExt;
						if (content instanceof XmlSchemaComplexContentExtension) {
							qname = ((XmlSchemaComplexContentExtension) content).getBaseTypeName();
							nameExt = "Extension"; //$NON-NLS-1$
						}
						else {
							qname = ((XmlSchemaComplexContentRestriction) content).getBaseTypeName();
							nameExt = "Restriction"; //$NON-NLS-1$
						}
						
						if (declaringType != null) {
							Name superTypeName = new NameImpl(qname.getNamespaceURI(), qname.getLocalPart());
							
							// try to get the type definition of the super type
							TypeDefinition superType = TypeUtil.resolveAttributeType(superTypeName, schemaTypes);
							if (superType == null) {
								_log.error("Couldn't resolve super type: " + superTypeName.getNamespaceURI() + "/" + superTypeName.getLocalPart()); //$NON-NLS-1$ //$NON-NLS-2$
							}
							
							// create an anonymous type that extends the super type
							Name anonymousName = new NameImpl(declaringType.getIdentifier() + "/" + element.getName(), superTypeName.getLocalPart() + nameExt); //$NON-NLS-1$
							TypeDefinition anonymousType = new AnonymousType(anonymousName, null, superType, (schemaTypes != null)?(schemaTypes.getSchemaLocation()):(null));
							
							// add attributes to the anonymous type
							// adding the attributes will happen automatically when the AbstractSchemaAttribute is created
							getAttributes(anonymousType, complexType, schemaTypes, referenceResolver);
							
							// add the anonymous type to the type map - needed for type resolution in SchemaAttribute
							// it's enough for it to be added to the imported types map
							if (schemaTypes != null) {
								schemaTypes.getImportedTypes().put(anonymousName, anonymousType);
							}
							
							// create an attribute with the anonymous type
							SchemaAttribute result = new SchemaAttribute(declaringType, element.getName(), anonymousName, element, schemaTypes);
							return result;
						}
						
						//   </extension> / </restriction>
						// </complexContent>
					} else if (content instanceof XmlSchemaSimpleContentExtension
							|| content instanceof XmlSchemaSimpleContentRestriction) { 
						// <simpleContent>
						//   <extension base="..."> / <restriction ...>
						String nameExt;
						if (content instanceof XmlSchemaSimpleContentExtension) {
							qname = ((XmlSchemaSimpleContentExtension) content).getBaseTypeName();
							nameExt = "Extension"; //$NON-NLS-1$
						}
						else {
							qname = ((XmlSchemaSimpleContentRestriction) content).getBaseTypeName();
							nameExt = "Restriction"; //$NON-NLS-1$
						}
						
						if (declaringType != null) {
							// create an anonymous type that extends the type referenced by qname
							// with additional attributes
							Name superTypeName = new NameImpl(qname.getNamespaceURI(), qname.getLocalPart());
							
							// try to get the type definition of the super type
							TypeDefinition superType = TypeUtil.resolveAttributeType(superTypeName, schemaTypes);
							if (superType == null) {
								_log.error("Couldn't resolve super type: " + superTypeName.getNamespaceURI() + "/" + superTypeName.getLocalPart()); //$NON-NLS-1$ //$NON-NLS-2$
							}
							
							// create an anonymous type that extends the super type
							Name anonymousName = new NameImpl(declaringType.getIdentifier() + "/" + element.getName(), superTypeName.getLocalPart() + nameExt); //$NON-NLS-1$
							// for now use the super attribute type, because attributes aren't added as attribute descriptors
							AttributeType attributeType = superType.getType(null); 
							TypeDefinition anonymousType = new AnonymousType(anonymousName, attributeType, superType, (schemaTypes != null)?(schemaTypes.getSchemaLocation()):(null));
							
							// add attributes to the anonymous type
							// adding the attributes will happen automatically when the AbstractSchemaAttribute is created
							getAttributes(anonymousType, complexType, schemaTypes, referenceResolver);
							
							// add the anonymous type to the type map - needed for type resolution in SchemaAttribute
							// it's enough for it to be added to the imported types map
							if (schemaTypes != null) {
								schemaTypes.getImportedTypes().put(anonymousName, anonymousType);
							}
							
							// create an attribute with the anonymous type
							SchemaAttribute result = new SchemaAttribute(declaringType, element.getName(), anonymousName, element, schemaTypes);
							return result;
						}
						
						//   </extension>
						// </simpleContent>
					}
					
					if (qname != null) {
						// return base type for dependency resolution
						return new SchemaAttribute(
								declaringType,
								element.getName(), 
								new NameImpl(qname.getNamespaceURI(), qname.getLocalPart()), 
								element,
								schemaTypes);
					}
					else {
						return null;
					}
				} else if (particle != null) {
					// this where we get when there is an anonymous complex type as property type
					if (declaringType == null) {
						// called only to get the type name for dependency resolution - not needed for anonymous types
						return null;
					}
					else {
						// create an anonymous type
						Name anonymousName = new NameImpl(declaringType.getIdentifier() + "/" + element.getName(), "AnonymousType"); //$NON-NLS-1$ //$NON-NLS-2$
						TypeDefinition anonymousType = new AnonymousType(anonymousName, null, null, (schemaTypes != null)?(schemaTypes.getSchemaLocation()):(null));
						
						// add attributes to the anonymous type
						// adding the attributes will happen automatically when the AbstractSchemaAttribute is created
						getAttributes(anonymousType, complexType, schemaTypes, referenceResolver);
						
						// add the anonymous type to the type map - needed for type resolution in SchemaAttribute
						// it's enough for it to be added to the imported types map
						if (schemaTypes != null) {
							schemaTypes.getImportedTypes().put(anonymousName, anonymousType);
						}
						
						// create an attribute with the anonymous type
						SchemaAttribute result = new SchemaAttribute(declaringType, element.getName(), anonymousName, element, schemaTypes);
						return result;
					}
				}
				//   </complexType>
				// </element>
			}
			else if (element.getSchemaType() instanceof XmlSchemaSimpleType) {
				// simple schema type
				TypeDefinition type = TypeUtil.resolveSimpleType(null, (XmlSchemaSimpleType) element.getSchemaType(), schemaTypes);
				if (type != null) {
					return new SchemaTypeAttribute(
							declaringType,
							element.getName(), 
							element,
							type);
				}
				else {
					_log.error("Could not resolve type for element " + element.getName()); //$NON-NLS-1$
				}
			}
		}
		
		return null;
	}

	/**
	 * Find a super type name based on a complex type
	 * 
	 * @param item the complex type defining a super type
	 * 
	 * @return the name of the super type or <code>null</code>
	 */
	private Name getSuperTypeName(XmlSchemaComplexType item) {
		Name superType = null;
		
		XmlSchemaContentModel model = item.getContentModel();
		if (model != null ) {
			XmlSchemaContent content = model.getContent();
			QName qname = null;
			if (content instanceof XmlSchemaComplexContentExtension) {
				qname = ((XmlSchemaComplexContentExtension) content).getBaseTypeName();
			}
			else if (content instanceof XmlSchemaComplexContentRestriction) { // restriction
				qname = ((XmlSchemaComplexContentRestriction) content).getBaseTypeName();
			}
			else if (content instanceof XmlSchemaSimpleContentExtension) {
				qname = ((XmlSchemaSimpleContentExtension) content).getBaseTypeName();
			}
			else if (content instanceof XmlSchemaSimpleContentRestriction) { // restriction
				qname = ((XmlSchemaSimpleContentRestriction) content).getBaseTypeName();
			}
	
			if (qname != null) {
				superType = new NameImpl(
						qname.getNamespaceURI(),
						qname.getLocalPart());
			}
		}
		
		return superType;
	}
	
	/**
	 * Determines if the super type relation of the given item is a restriction
	 * 
	 * @param item the complex type defining a super type
	 * 
	 * @return if the super type relation of the given item is a restriction
	 */
	private boolean isRestriction(XmlSchemaComplexType item) {
		XmlSchemaContentModel model = item.getContentModel();
		if (model != null ) {
			XmlSchemaContent content = model.getContent();
			if (content instanceof XmlSchemaComplexContentRestriction || content instanceof XmlSchemaSimpleContentRestriction) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * @see SchemaProvider#loadSchema(java.net.URI, ProgressIndicator)
	 */
	@Override
	public Schema loadSchema(URI location, ProgressIndicator progress) throws IOException {
		if (progress == null) {
			progress = new LogProgressIndicator();
		}
		
		// use XML Schema to load schema with all its subschema to the memory
		InputStream is = null;
		URL locationURL;
		locationURL = location.toURL();
		
		try {
			is = Request.getInstance().get(location);
		} catch (Exception e) {
			is = locationURL.openStream();
		}
		
		progress.setCurrentTask(Messages.getString("ApacheSchemaProvider.21")); //$NON-NLS-1$

		XmlSchema schema = null;
		XmlSchemaCollection schemaCol = new XmlSchemaCollection();
		// Check if the file is located on web
		if (location.getHost() == null) {
			schemaCol.setSchemaResolver(new ProgressURIResolver(new HumboldtURIResolver(), progress));
			schemaCol.setBaseUri(findBaseUri(location));
		} else if (location.getScheme().equals("bundleresource")) { //$NON-NLS-1$
			schemaCol.setSchemaResolver(new ProgressURIResolver(new HumboldtURIResolver(), progress));
			schemaCol.setBaseUri(findBaseUri(location) + "/"); //$NON-NLS-1$
		}
		else {
			URIResolver resolver = schemaCol.getSchemaResolver();
			schemaCol.setSchemaResolver(new ProgressURIResolver((resolver == null)?(new DefaultURIResolver()):(resolver), progress));
		}
		schema = schemaCol.read(new StreamSource(is), null);
		
		is.close();

		String namespace = schema.getTargetNamespace();
		if (namespace == null || namespace.isEmpty()) {
			// default to gml schema
			namespace = "http://www.opengis.net/gml"; //$NON-NLS-1$
		}
		
		schema.setSourceURI(location.toString());
		NamespacePrefixList namespaces = schema.getNamespaceContext();
		Map<String, String> prefixes = new HashMap<String, String>();
		for (String prefix : namespaces.getDeclaredPrefixes()) {
			prefixes.put(namespaces.getNamespaceURI(prefix), prefix);
		}
		
		HashMap<String, SchemaResult> imports = new HashMap<String, SchemaResult>();
		imports.put(location.toString(), null);

		SchemaResult schemaResult = loadSchema(location.toString(), schema,
				imports, progress);

		Map<String, SchemaElement> elements = new HashMap<String, SchemaElement>();
		for (SchemaElement element : schemaResult.getElements().values()) {
			if (element.getType() != null) {
				if (element.getType().isComplexType()) {
					elements.put(element.getIdentifier(), element);
				}
			}
			else {
				_log.warn(NO_DEFINITION, "No type definition for element " + element.getElementName().getLocalPart()); //$NON-NLS-1$
			}
		}

		Schema result = new Schema(elements, namespace, locationURL, prefixes);
		
		Map<Name, SchemaElement> allElements = new HashMap<Name, SchemaElement>();
		Map<Name, TypeDefinition> allTypes = new HashMap<Name, TypeDefinition>();
		
		allElements.putAll(schemaResult.getElements());
		allTypes.putAll(schemaResult.getTypes());
		
		for (SchemaResult sr : imports.values()) {
			allElements.putAll(sr.getElements());
			allTypes.putAll(sr.getTypes());
		}
		
		result.setAllElements(allElements);
		result.setAllTypes(allTypes);
		
		return result;
	}

	/**
	 * Load the feature types defined by the given schema
	 * 
	 * @param schemaLocation the schema location 
	 * @param schema the schema
	 * @param imports the imports/includes that were already
	 *   loaded or where loading has been started
	 * @param progress the progress indicator
	 * @return the map of feature type names and types
	 */
	protected SchemaResult loadSchema(String schemaLocation, XmlSchema schema, Map<String, SchemaResult> imports, ProgressIndicator progress) {
		String namespace = schema.getTargetNamespace();
		if (namespace == null || namespace.isEmpty()) {
			// default to gml schema
			namespace = "http://www.opengis.net/gml"; //$NON-NLS-1$
		}

		// attribute name mapped to attribute definition
		Map<Name, XmlSchemaAttribute> schemaAttributes = new HashMap<Name, XmlSchemaAttribute>();
		// attribute group name mapped to attribute group definition
		Map<Name, XmlSchemaAttributeGroup> schemaAttributeGroups = new HashMap<Name, XmlSchemaAttributeGroup>();
		// Map of type names / types for the result
		Map<Name, TypeDefinition> featureTypes = new HashMap<Name, TypeDefinition>();
		// name mapping: element name -> type name
		Map<Name, SchemaElement> elements = new HashMap<Name, SchemaElement>();
		// result
		SchemaResult result = new SchemaResult(featureTypes, elements, 
				schemaAttributes, schemaAttributeGroups);
		
		// type names for type definitions where is no element
		Set<String> schemaTypeNames = new HashSet<String>();
		
		// the schema items
		XmlSchemaObjectCollection items = schema.getItems();
		
		Map<XmlSchemaElement, Name> anonymousTypes = new HashMap<XmlSchemaElement, Name>();
		
		// first pass - find names for types
		for (int i = 0; i < items.getCount(); i++) {
			XmlSchemaObject item = items.getItem(i);
			
			if (item instanceof XmlSchemaElement) {
				XmlSchemaElement element = (XmlSchemaElement) item;
				// retrieve local name part of XmlSchemaElement and of 
				// XmlSchemaComplexType to substitute name later on.
				Name typeName = null;
				if (element.getSchemaTypeName() != null) {
					typeName = new NameImpl(
							element.getSchemaTypeName().getNamespaceURI(), 
							element.getSchemaTypeName().getLocalPart());
				}
				else if (element.getSchemaType() != null) {
					// element has internal type definition, generate anonymous type name
					typeName = new NameImpl(element.getQName().getNamespaceURI(),
							element.getQName().getLocalPart() + "_AnonymousType"); //$NON-NLS-1$
					anonymousTypes.put(element, typeName);
				}
				else if (element.getQName() != null) {
					typeName = new NameImpl(
							element.getQName().getNamespaceURI(),
							element.getQName().getLocalPart());
				} 
				
				Name elementName = new NameImpl(namespace, element.getName());
				Name subGroup = null;
				if (element.getSubstitutionGroup() != null) {
					subGroup = new NameImpl(
							element.getSubstitutionGroup().getNamespaceURI(), 
							element.getSubstitutionGroup().getLocalPart());
				}
				// create schema element
				SchemaElement schemaElement = new SchemaElement(elementName, 
						typeName, null, subGroup);
				schemaElement.setLocation(schemaLocation);
				// get description
				String description = SchemaAttribute.getDescription(element);
				schemaElement.setDescription(description);
				// store element in map
				elements.put(elementName, schemaElement);
			}
			else if (item instanceof XmlSchemaComplexType) {
				schemaTypeNames.add(((XmlSchemaComplexType)item).getName());
			}
			else if (item instanceof XmlSchemaSimpleType) {
				schemaTypeNames.add(((XmlSchemaSimpleType)item).getName());
			}
			else if (item instanceof XmlSchemaAttribute) {
				// schema attribute that might be referenced somewhere
				XmlSchemaAttribute att = (XmlSchemaAttribute) item;
				if (att.getQName() != null) {
					schemaAttributes.put(new NameImpl(att.getQName().getNamespaceURI(), att.getQName().getLocalPart()), att);
				}
				else {
					_log.warn("Attribute not processed: " + att.getName()); //$NON-NLS-1$
				}
			}
			else if (item instanceof XmlSchemaAttributeGroup) {
				// schema attribute group that might be referenced somewhere
				XmlSchemaAttributeGroup group = (XmlSchemaAttributeGroup) item;
				if (group.getName() != null) {
					schemaAttributeGroups.put(new NameImpl(group.getName().getNamespaceURI(), group.getName().getLocalPart()), group);
				}
				else {
					_log.warn("Attribute group not processed"); //$NON-NLS-1$
				}
			}
		}
		
		// Set of include locations
		Set<String> includes = new HashSet<String>();
		
		// handle imports
		XmlSchemaObjectCollection externalItems = schema.getIncludes();
		if (externalItems.getCount() > 0) {
			_log.info("Loading includes and imports for schema at " + schemaLocation); //$NON-NLS-1$
		}
		
		// add self to imports (allows resolving references to elements that are defined here)
		imports.put(schemaLocation, result);
		
		for (int i = 0; i < externalItems.getCount(); i++) {
			try {
				XmlSchemaExternal imp = (XmlSchemaExternal) externalItems.getItem(i);
				XmlSchema importedSchema = imp.getSchema();
				String location = importedSchema.getSourceURI();
				if (!(imports.containsKey(location))) { // only add schemas that were not already added
					imports.put(location, null); // place a marker in the map to prevent loading the location in the call to loadSchema 
					imports.put(location, loadSchema(location, importedSchema, imports, progress));
				}
				if (imp instanceof XmlSchemaInclude) {
					includes.add(location);
				}
			} catch (Throwable e) {
				_log.error("Error adding imported schema", e); //$NON-NLS-1$
			}
		}
		
		_log.info("Creating types for schema at " + schemaLocation); //$NON-NLS-1$
		
		progress.setCurrentTask(MessageFormat.format(
				Messages.getString("ApacheSchemaProvider.33"), namespace)); //$NON-NLS-1$
		
		// map for all imported types
		Map<Name, TypeDefinition> importedFeatureTypes = new HashMap<Name, TypeDefinition>();
		// name mapping for imported types: element name -> type name
		Map<Name, SchemaElement> importedElements = new HashMap<Name, SchemaElement>();
		// imported attributes
		Map<Name, XmlSchemaAttribute> importedSchemaAttributes = new HashMap<Name, XmlSchemaAttribute>();
		// imported attribute groups
		Map<Name, XmlSchemaAttributeGroup> importedSchemaAttributeGroups = new HashMap<Name, XmlSchemaAttributeGroup>();
		
		// add imported types
		for (Entry<String, SchemaResult> entry : imports.entrySet()) {
			if (entry.getValue() != null) {
				if (includes.contains(entry.getKey())) {
					// is include, add to result
					featureTypes.putAll(entry.getValue().getTypes());
					elements.putAll(entry.getValue().getElements());
					schemaAttributes.putAll(entry.getValue().getSchemaAttributes());
					schemaAttributeGroups.putAll(entry.getValue().getSchemaAttributeGroups());
				}
				else {
					// is import, don't add to result
					importedFeatureTypes.putAll(entry.getValue().getTypes());
					importedElements.putAll(entry.getValue().getElements());
					importedSchemaAttributes.putAll(entry.getValue().getSchemaAttributes());
					importedSchemaAttributeGroups.putAll(entry.getValue().getSchemaAttributeGroups());
				}
			}
		}
		
		// schema type resolver combining the informations for resolving types
		SchemaTypeResolver typeResolver = new SchemaTypeResolver(featureTypes, importedFeatureTypes, schemaLocation);
		
		// schema reference resolver combining the informations for resolving schema elements, attributes and attribute groups
		SchemaReferenceResolver referenceResolver = new SchemaReferenceResolver(
				elements, importedElements, schemaAttributes, 
				importedSchemaAttributes, schemaAttributeGroups, 
				importedSchemaAttributeGroups);
		
		// Map of type names to definitions
		Map<Name, XmlSchemaObject> typeDefinitions = new HashMap<Name, XmlSchemaObject>();
		
		// Dependency map for building the dependency list
		Map<Name, Set<Name>> dependencies = new HashMap<Name, Set<Name>>();
		
		// 2nd pass - determine dependencies
		for (int i = 0; i < items.getCount(); i++) {
			XmlSchemaObject item = items.getItem(i);
			String name = null;
			Set<Name> typeDependencies = null; // the type dependencies including the super type
			Name superTypeName = null; // the super type name
			
			if (item instanceof XmlSchemaComplexType) {				
				name = ((XmlSchemaComplexType)item).getName();
				
				// get the attribute type names
				typeDependencies = getAttributeTypeNames((XmlSchemaComplexType) item, referenceResolver);
				
				// get the name of the super type 
				superTypeName = getSuperTypeName((XmlSchemaComplexType)item);
				if (superTypeName != null) {
					typeDependencies.add(superTypeName);
				}
				
			} else if (item instanceof XmlSchemaSimpleType) {
				name = ((XmlSchemaSimpleType)item).getName();
				
				// union/list referencing dependencies
				typeDependencies = TypeUtil.getSimpleTypeDependencies(new NameImpl(namespace, name), (XmlSchemaSimpleType) item);
			}
			
			// if the item is a type we remember the type definition and determine its local dependencies
			if (name != null) {
				// determine the real type name
				Name typeName = new NameImpl(namespace, name);
				
				// determine the local dependency set
				Set<Name> localDependencies = new HashSet<Name>();
				
				if (typeDependencies != null) {
					for (Name dependency : typeDependencies) {
						if (dependency.getNamespaceURI().equals(namespace) && 
								((!featureTypes.containsKey(dependency) && referencesType(elements, dependency)) ||
								schemaTypeNames.contains(dependency.getLocalPart()))) {
							// local type, add to local dependencies
							localDependencies.add(dependency);
						}
					}
				}
				
				// add imported super types to the result set
				Name importName = superTypeName;
				TypeDefinition importType = null;
				
				while (importName != null && (importType = importedFeatureTypes.get(importName)) != null) {
					featureTypes.put(importName, importType);
					
					TypeDefinition superType = importType.getSuperType();
					if (superType != null) {
						importName = superType.getName(); 
					}
					else {
						importName = null;
					}
				}
				
				// remember type definition
				typeDefinitions.put(typeName, item);
				// store local dependencies in dependency map
				dependencies.put(typeName, localDependencies);
			}
		}
		
		// create dependency ordered list
		DependencyOrderedList<Name> typeNames = new DependencyOrderedList<Name>(dependencies);
		
		// append anonymous types
		for (Entry<XmlSchemaElement, Name> entry : anonymousTypes.entrySet()) {
			typeNames.append(entry.getValue());
			typeDefinitions.put(entry.getValue(), entry.getKey().getSchemaType());
		}
		
		// 3rd pass: create feature types 
		for (Name typeName : typeNames.getItems()) {
			XmlSchemaObject item = typeDefinitions.get(typeName);

			if (item == null) {
				_log.error("No definition for " + typeName.toString()); //$NON-NLS-1$
			}
			else if (item instanceof XmlSchemaSimpleType) {
				// attribute type from simple schema types
				TypeDefinition simpleType = TypeUtil.resolveSimpleType(
						typeName, (XmlSchemaSimpleType) item, typeResolver);
				
				if (simpleType != null) {
					// create a simple type
					featureTypes.put(typeName, simpleType);
				}
				else {
					_log.warn("No attribute type generated for simple type " + typeName.toString()); //$NON-NLS-1$
				}
			}
			else if (item instanceof XmlSchemaComplexType) {
				// determine the super type name
				Name superTypeName = getSuperTypeName((XmlSchemaComplexType) item);
				boolean isRestriction = isRestriction((XmlSchemaComplexType) item);
				
				TypeDefinition superType = null;
				if (superTypeName != null) {
					// find super type
					superType = TypeUtil.resolveAttributeType(superTypeName, typeResolver);
					
					// create empty super type if it was not found
					if (superType == null) {
						superType = new TypeDefinition(superTypeName, null, null);
						superType.setLocation(Messages.getString("ApacheSchemaProvider.36")); //$NON-NLS-1$
						superType.setAbstract(true);
						// add super type to feature map
						featureTypes.put(superTypeName, superType);
					}
				}
				
				// create type definition
				TypeDefinition typeDef = new TypeDefinition(typeName, null, 
						superType, isRestriction);
				typeDef.setLocation(schemaLocation);
				
				// determine the defined attributes and add them to the declaring type
				List<AttributeDefinition> attributes = getAttributes(
						typeDef, // definition of the declaring type
						(XmlSchemaComplexType) item,
						typeResolver,
						referenceResolver);
				
				// reuse the super type's attribute type where appropriate
				if (superType != null && superType.isAttributeTypeSet()) {
					// determine if any new elements have been added in the subtype
					boolean reuseBinding = true;
					
					// special case: super type is AbstractFeatureType but no FeatureType instance
					if (superType.isFeatureType() && !(superType.getType(null) instanceof FeatureType)) {
						reuseBinding = false;
					}
					
					// check if additional elements are defined
					
					if (Geometry.class.isAssignableFrom(superType.getType(null).getBinding())) {
						// special case: super type binding is Geometry -> ignore additional elements
					}
					else {
						Iterator<AttributeDefinition> it = attributes.iterator();
						while (reuseBinding && it.hasNext()) {
							if (it.next().isElement()) {
								reuseBinding = false;
							}
						}
					}
					
					AttributeType pt = TypeUtil.getPredefinedAttributeType(typeName);
					if (pt != null && !pt.getBinding().equals(Collection.class) && !pt.getBinding().equals(Object.class)) { //TODO if binding is collection search for super type with better binding?
						typeDef.setType(pt); // assures e.g. point binding for PointType instead of reusing the geometry binding from the super type
					}
					else if (reuseBinding) {
						// reuse attribute type
						typeDef.setType(superType.getType(null));
					}
				}
				// special case geometry property types: use a geometry binding if all elements have geometry bindings
				if (superType == null) { // only if no super type is defined XXX could be improved to check also for super types with no properties/elements 
					AttributeType type = null;
					Iterator<AttributeDefinition> it = attributes.iterator();
					while (it.hasNext()) {
						AttributeDefinition def = it.next();
						if (def.isElement() && def.getAttributeType() != null && def.getAttributeType().isAttributeTypeSet()) {
							AttributeType t = def.getAttributeType().getType(null);
							if (t != null) {
								Class<?> b = t.getBinding();
								if (Geometry.class.isAssignableFrom(b)) {
									if (type == null) {
										type = t;
									}
									else if (!type.getBinding().equals(b) && !type.getBinding().equals(Geometry.class)) {
										// attribute type with geometry binding if multiple geometry properties with differen bindings are present
										type = AbstractElementAttribute.createDefaultGeometryAttributeType(typeName);
									}
								}
								else {
									type = null;
									break;
								}
							}
						}
					}
					
					if (type != null) {
						typeDef.setType(type);
					}
				}
				
				// set additional properties
				typeDef.setAbstract(((XmlSchemaComplexType) item).isAbstract());
				
				// add type definition
				featureTypes.put(typeName, typeDef);
				
				// types that are resolved later may need the type information associated to the schema element
				for (SchemaElement element : elements.values()) {
					if (element.getTypeName().equals(typeName)) {
						element.setType(typeDef);
					}
				}
			}
		}
		
		// populate schema items with type definitions
		for (SchemaElement element : elements.values()) {
			TypeDefinition elementDef = featureTypes.get(element.getTypeName());
			if (elementDef != null) {
				element.setType(elementDef);
			}
			else {
				elementDef = element.getType();
				
				if (elementDef == null) {
					elementDef = TypeUtil.resolveAttributeType(element.getTypeName(), typeResolver); //TypeUtil.getXSType(element.getTypeName());
				}
				
				if (elementDef == null) {
					//_log.warn("Couldn't find definition for element " + element.getDisplayName());
				}
				else {
					element.setType(elementDef);
				}
			}
		}
		
		return result;
	}
	
	private boolean referencesType(Map<Name, SchemaElement> elements,
			Name dependency) {
		//elements.containsValue(dependency)
		//TODO
		//XXX for now, return false
		return false;
	}

	/**
	 * Get the attributes for the given item
	 * 
	 * @param typeDef the definition of the declaring type 
	 * @param item the complex type item
	 * @param schemaTypes the schema types
	 * @param referenceResolver the reference resolver
	 *  
	 * @return the attributes as a list of {@link SchemaAttribute}s
	 */
	private List<AttributeDefinition> getAttributes(TypeDefinition typeDef, XmlSchemaComplexType item,
			SchemaTypeResolver schemaTypes, SchemaReferenceResolver referenceResolver) {
		ArrayList<AttributeDefinition> attributes = new ArrayList<AttributeDefinition>();
		
		// item:
		// <complexType ...>
		XmlSchemaContentModel model = item.getContentModel();
		if (model != null ) {
			XmlSchemaContent content = model.getContent();

			if (content instanceof XmlSchemaComplexContentExtension) {
				// <complexContent>
				//   <extension base="...">
				XmlSchemaComplexContentExtension extension = (XmlSchemaComplexContentExtension) content;
				// particle (e.g. sequence)
				if (extension.getParticle() != null) {
					XmlSchemaParticle particle = extension.getParticle();
					attributes.addAll(getAttributesFromParticle(typeDef, particle, schemaTypes, referenceResolver));
				}
				// attributes
				XmlSchemaObjectCollection attributeCollection = extension.getAttributes();
				if (attributeCollection != null) {
					attributes.addAll(getAttributesFromCollection(attributeCollection, typeDef, schemaTypes, referenceResolver, null));
				}
				//   </extension>
				// </complexContent>
			}
			else if (content instanceof XmlSchemaComplexContentRestriction) {
				// <complexContent>
				//   <restriction base="...">
				XmlSchemaComplexContentRestriction restriction = (XmlSchemaComplexContentRestriction) content;
				// particle (e.g. sequence)
				if (restriction.getParticle() != null) {
					XmlSchemaParticle particle = restriction.getParticle();
					attributes.addAll(getAttributesFromParticle(typeDef, particle, schemaTypes, referenceResolver));
				}
				// attributes
				XmlSchemaObjectCollection attributeCollection = restriction.getAttributes();
				if (attributeCollection != null) {
					attributes.addAll(getAttributesFromCollection(attributeCollection, typeDef, schemaTypes, referenceResolver, null));
				}
				//   </restriction>
				// </complexContent>
			}
			else if (content instanceof XmlSchemaSimpleContentExtension) {
				// <simpleContent>
				//   <extension base="...">
				XmlSchemaSimpleContentExtension extension = (XmlSchemaSimpleContentExtension) content;
				// attributes
				XmlSchemaObjectCollection attributeCollection = extension.getAttributes();
				if (attributeCollection != null) {
					attributes.addAll(getAttributesFromCollection(attributeCollection, typeDef, schemaTypes, referenceResolver, null));
				}
				//   </extension>
				// </simpleContent>
			}
			else if (content instanceof XmlSchemaSimpleContentRestriction) {
				// <simpleContent>
				//   <restriction base="...">
				XmlSchemaSimpleContentRestriction restriction = (XmlSchemaSimpleContentRestriction) content;
				// attributes
				XmlSchemaObjectCollection attributeCollection = restriction.getAttributes();
				if (attributeCollection != null) {
					attributes.addAll(getAttributesFromCollection(attributeCollection, typeDef, schemaTypes, referenceResolver, null));
				}
				//   </restriction>
				// </simpleContent>
			}
		}
		else {
			// no complex content (instead e.g. <sequence>)
			XmlSchemaComplexType complexType = item;
			// particle (e.g. sequence)
			if (item.getParticle() != null) {
				XmlSchemaParticle particle = complexType.getParticle();
				List<AttributeDefinition> tmp = getAttributesFromParticle(typeDef, particle, schemaTypes, referenceResolver);
				if (tmp != null) {
					attributes.addAll(tmp);
				}
			}
			// attributes
			XmlSchemaObjectCollection attributeCollection = complexType.getAttributes();
			if (attributeCollection != null) {
				attributes.addAll(getAttributesFromCollection(attributeCollection, typeDef, schemaTypes, referenceResolver, null));
			}
		}
		
		return attributes; 
		// </complexType>
	}
	
	private Collection<AttributeDefinition> getAttributesFromCollection(
			XmlSchemaObjectCollection attributeCollection, TypeDefinition declaringType,
			SchemaTypeResolver schemaTypes, SchemaReferenceResolver referenceResolver,
			String indexPrefix) {
		if (indexPrefix == null) {
			indexPrefix = ""; //$NON-NLS-1$
		}
		
		List<AttributeDefinition> attributeResults = new ArrayList<AttributeDefinition>();
		
		for (int index = 0; index < attributeCollection.getCount(); index++) {
			XmlSchemaObject object = attributeCollection.getItem(index);
			if (object instanceof XmlSchemaAttribute) {
				// <attribute ... />
				XmlSchemaAttribute attribute = (XmlSchemaAttribute) object;
				
				AttributeDefinition attDef = createAttribute(attribute, 
						declaringType, schemaTypes, referenceResolver, 
						indexPrefix + index, null);
				if (attDef != null) {
					attributeResults.add(attDef);
				}
			}
			else if (object instanceof XmlSchemaAttributeGroup) {
				XmlSchemaAttributeGroup group = (XmlSchemaAttributeGroup) object;
				
				attributeResults.addAll(createAttributes(group, declaringType, 
						schemaTypes, referenceResolver, indexPrefix + index));
			}
			else if (object instanceof XmlSchemaAttributeGroupRef) {
				XmlSchemaAttributeGroupRef groupRef = (XmlSchemaAttributeGroupRef) object;
				
				if (groupRef.getRefName() != null) {
					XmlSchemaAttributeGroup group = referenceResolver.getSchemaAttributeGroup(new NameImpl(
							groupRef.getRefName().getNamespaceURI(), 
							groupRef.getRefName().getLocalPart()));
					
					if (group != null) {
						attributeResults.addAll(createAttributes(group, 
								declaringType, schemaTypes, referenceResolver, indexPrefix + index));
					}
					else {
						_log.warn("Reference to attribute group " + groupRef.getRefName() + " could not be resolved"); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
			}
		}
		
		return attributeResults;
	}

	private Collection<? extends AttributeDefinition> createAttributes(
			XmlSchemaAttributeGroup group, TypeDefinition declaringType,
			SchemaTypeResolver schemaTypes, SchemaReferenceResolver referenceResolver, 
			String index) {
		return getAttributesFromCollection(group.getAttributes(), 
				declaringType, schemaTypes, referenceResolver, index + "_"); //$NON-NLS-1$
	}

	private AttributeDefinition createAttribute(XmlSchemaAttribute attribute, 
			TypeDefinition declaringType, SchemaTypeResolver schemaTypes, 
			SchemaReferenceResolver referenceResolver, String index, 
			XmlSchemaUse useOverride) {
		// create attributes
		QName typeName = attribute.getSchemaTypeName();
		if (typeName != null) {
			return new DefaultResolveAttribute(
					declaringType, 
					new NameImpl(typeName.getNamespaceURI(), typeName.getLocalPart()), 
					attribute, 
					schemaTypes,
					(useOverride != null)?(useOverride):(attribute.getUse()));
		}
		else if (attribute.getSchemaType() != null) {
			if (declaringType != null) {
				QName name = attribute.getSchemaType().getQName();
				Name attributeTypeName = (name != null)?
						(new NameImpl(name.getNamespaceURI(), name.getLocalPart())):
						(new NameImpl(declaringType.getName().getNamespaceURI() + "/" + declaringType.getName().getLocalPart(), "AnonymousAttribute" + index)); //$NON-NLS-1$ //$NON-NLS-2$
				TypeDefinition attributeType = TypeUtil.resolveSimpleType(
						attributeTypeName, 
						attribute.getSchemaType(), 
						schemaTypes);
				
				return new DefaultAttribute(declaringType, attributeTypeName, 
						attribute, attributeType, 
						(useOverride != null)?(useOverride):(attribute.getUse()));
			}
		}
		else if (attribute.getRefName() != null) {
			// <attribute ref="REF_NAME" />
			XmlSchemaAttribute referencedAtt = referenceResolver.getSchemaAttribute(new NameImpl(
					attribute.getRefName().getNamespaceURI(), 
					attribute.getRefName().getLocalPart()));
			
			if (referencedAtt != null) {
				return createAttribute(referencedAtt, declaringType, schemaTypes, referenceResolver, index, attribute.getUse());
			}
			else {
				_log.warn(MISSING_ATTRIBUTE_REF, "Reference to attribute " + attribute.getRefName() + " could not be resolved"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		
		return null;
	}

	/**
	 * Get the attributes type names for the given item
	 * 
	 * @param item the complex type item
	 * @param referenceResolver the reference resolver
	 * @return the attribute type names
	 */
	private Set<Name> getAttributeTypeNames(XmlSchemaComplexType item, 
			SchemaReferenceResolver referenceResolver) {
		List<AttributeDefinition> attributes = getAttributes(null, item, null, 
				referenceResolver);
		
		Set<Name> typeNames = new HashSet<Name>();
		
		for (AttributeDefinition def : attributes) {
			typeNames.add(def.getTypeName());
		}
		
		return typeNames;
	}

	/**
	 * Get the base URI for the given URI
	 * 
	 * @param uri the URI
	 * 
	 * @return the base URI as string
	 */
	private String findBaseUri(URI uri) {
		String baseUri = ""; //$NON-NLS-1$
		baseUri = uri.toString();
		if (baseUri.matches("^.*?\\/.+")) { //$NON-NLS-1$
			baseUri = baseUri.substring(0, baseUri.lastIndexOf("/")); //$NON-NLS-1$
		}
		_log.info("Base URI for schemas to be used: " + baseUri); //$NON-NLS-1$
		return baseUri;
	}
	
}



