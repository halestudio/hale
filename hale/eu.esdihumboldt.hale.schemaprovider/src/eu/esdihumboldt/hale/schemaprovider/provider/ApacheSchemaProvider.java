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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAttribute;
import org.apache.ws.commons.schema.XmlSchemaChoice;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaComplexContentExtension;
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
import org.apache.ws.commons.schema.XmlSchemaSimpleType;
import org.apache.ws.commons.schema.resolver.DefaultURIResolver;
import org.apache.ws.commons.schema.resolver.URIResolver;
import org.geotools.feature.NameImpl;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;

import com.vividsolutions.jts.geom.Geometry;

import de.cs3d.util.logging.AGroup;
import de.cs3d.util.logging.AGroupFactory;
import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.schemaprovider.AbstractSchemaProvider;
import eu.esdihumboldt.hale.schemaprovider.HumboldtURIResolver;
import eu.esdihumboldt.hale.schemaprovider.LogProgressIndicator;
import eu.esdihumboldt.hale.schemaprovider.ProgressIndicator;
import eu.esdihumboldt.hale.schemaprovider.Schema;
import eu.esdihumboldt.hale.schemaprovider.SchemaProvider;
import eu.esdihumboldt.hale.schemaprovider.model.AnonymousType;
import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;
import eu.esdihumboldt.hale.schemaprovider.provider.internal.DependencyOrderedList;
import eu.esdihumboldt.hale.schemaprovider.provider.internal.SchemaResult;
import eu.esdihumboldt.hale.schemaprovider.provider.internal.apache.AbstractElementAttribute;
import eu.esdihumboldt.hale.schemaprovider.provider.internal.apache.DefaultAttribute;
import eu.esdihumboldt.hale.schemaprovider.provider.internal.apache.DefaultResolveAttribute;
import eu.esdihumboldt.hale.schemaprovider.provider.internal.apache.ElementReferenceAttribute;
import eu.esdihumboldt.hale.schemaprovider.provider.internal.apache.ProgressURIResolver;
import eu.esdihumboldt.hale.schemaprovider.provider.internal.apache.SchemaAttribute;
import eu.esdihumboldt.hale.schemaprovider.provider.internal.apache.SchemaTypeAttribute;
import eu.esdihumboldt.hale.schemaprovider.provider.internal.apache.SchemaTypeResolver;
import eu.esdihumboldt.hale.schemaprovider.provider.internal.apache.TypeUtil;

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
public class ApacheSchemaProvider 
	extends AbstractSchemaProvider {
	
	/**
	 * The log
	 */
	private static ALogger _log = ALoggerFactory.getLogger(ApacheSchemaProvider.class);
	
	private static final AGroup NO_DEFINITION = AGroupFactory.getGroup("No type definition found for elements"); 
	
	/**
	 * Default constructor 
	 */
	public ApacheSchemaProvider() {
		super();
		
		addSupportedFormat("xsd");
		addSupportedFormat("gml");
		addSupportedFormat("xml");
	}

	/**
	 * Extracts attribute definitions from a {@link XmlSchemaParticle}.
	 * 
	 * @param elements local element definitions
	 * @param importedElements imported element definitions
	 * @param typeDef the definition of the declaring type
	 * @param particle the particle
	 * @param schemaTypes the schema types 
	 * 
	 * @return the list of attribute definitions
	 */
	private List<AttributeDefinition> getAttributesFromParticle(Map<Name, SchemaElement> elements, 
			Map<Name, SchemaElement> importedElements, TypeDefinition typeDef, XmlSchemaParticle particle, 
			SchemaTypeResolver schemaTypes) {
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
							(XmlSchemaElement) object, typeDef, elements, 
							importedElements, schemaTypes);
					if (attribute != null) {
						attributeResults.add(attribute);
					}
					// </element>
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
							(XmlSchemaElement) object, typeDef, elements, 
							importedElements, schemaTypes);
					if (attribute != null) {
						attribute.setNillable(true); //XXX set nillable because its a choice
						attributeResults.add(attribute);
					}
					// </element>
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
	 * @param elements local element definitions
	 * @param importedElements imported element definitions
	 * @param schemaTypes the schema types
	 * 
	 * @return an attribute definition or <code>null</code>
	 */
	private AbstractElementAttribute getAttributeFromElement(
			XmlSchemaElement element, TypeDefinition declaringType,
			Map<Name, SchemaElement> elements, Map<Name, SchemaElement> importedElements,
			SchemaTypeResolver schemaTypes) {
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
			SchemaElement reference = elements.get(elementName);
			if (reference == null) {
				// imported element definition
				reference = importedElements.get(elementName);
			}
			if (reference == null) {
				_log.warn("Reference to element " + element.getRefName().getNamespaceURI() + "/" + element.getRefName().getLocalPart() +" not found");
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
					if (content instanceof XmlSchemaComplexContentExtension) {
						// <complexContent>
						//   <extension base="...">
						qname = ((XmlSchemaComplexContentExtension)content).getBaseTypeName();
						
						if (declaringType != null) {
							Name superTypeName = new NameImpl(qname.getNamespaceURI(), qname.getLocalPart());
							
							// try to get the type definition of the super type
							TypeDefinition superType = TypeUtil.resolveAttributeType(superTypeName, schemaTypes);
							if (superType == null) {
								_log.error("Couldn't resolve super type: " + superTypeName.getNamespaceURI() + "/" + superTypeName.getLocalPart());
							}
							
							// create an anonymous type that extends the super type
							Name anonymousName = new NameImpl(declaringType.getIdentifier() + "/" + element.getName(), superTypeName.getLocalPart() + "Extension");
							TypeDefinition anonymousType = new AnonymousType(anonymousName, null, superType, (schemaTypes != null)?(schemaTypes.getSchemaLocation()):(null));
							
							// add attributes to the anonymous type
							// adding the attributes will happen automatically when the AbstractSchemaAttribute is created
							getAttributes(elements, importedElements, anonymousType, complexType, schemaTypes);
							
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
						// </complexContent>
					} else if (content instanceof XmlSchemaSimpleContentExtension) {
						// <simpleContent>
						//   <extension base="...">
						qname = ((XmlSchemaSimpleContentExtension)content).getBaseTypeName();
						
						if (declaringType != null) {
							// create an anonymous type that extends the type referenced by qname
							// with additional attributes
							Name superTypeName = new NameImpl(qname.getNamespaceURI(), qname.getLocalPart());
							
							// try to get the type definition of the super type
							TypeDefinition superType = TypeUtil.resolveAttributeType(superTypeName, schemaTypes);
							if (superType == null) {
								_log.error("Couldn't resolve super type: " + superTypeName.getNamespaceURI() + "/" + superTypeName.getLocalPart());
							}
							
							// create an anonymous type that extends the super type
							Name anonymousName = new NameImpl(declaringType.getIdentifier() + "/" + element.getName(), superTypeName.getLocalPart() + "Extension");
							// for now use the super attribute type, because attributes aren't added as attribute descriptors
							AttributeType attributeType = superType.getType(null); 
							TypeDefinition anonymousType = new AnonymousType(anonymousName, attributeType, superType, (schemaTypes != null)?(schemaTypes.getSchemaLocation()):(null));
							
							// add attributes to the anonymous type
							// adding the attributes will happen automatically when the AbstractSchemaAttribute is created
							getAttributes(elements, importedElements, anonymousType, complexType, schemaTypes);
							
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
						Name anonymousName = new NameImpl(declaringType.getIdentifier() + "/" + element.getName(), "AnonymousType");
						TypeDefinition anonymousType = new AnonymousType(anonymousName, null, null, (schemaTypes != null)?(schemaTypes.getSchemaLocation()):(null));
						
						// add attributes to the anonymous type
						// adding the attributes will happen automatically when the AbstractSchemaAttribute is created
						getAttributes(elements, importedElements, anonymousType, complexType, schemaTypes);
						
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
					_log.error("Could not resolve type for element " + element.getName());
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
			if (content instanceof XmlSchemaComplexContentExtension) {
				if (((XmlSchemaComplexContentExtension)content).getBaseTypeName() != null) {
					superType = new NameImpl(
							((XmlSchemaComplexContentExtension)content).getBaseTypeName().getNamespaceURI(),
							((XmlSchemaComplexContentExtension)content).getBaseTypeName().getLocalPart());
				}
			}
			else if (content instanceof XmlSchemaSimpleContentExtension) {
				if (((XmlSchemaSimpleContentExtension)content).getBaseTypeName() != null) {
					superType = new NameImpl(
							((XmlSchemaSimpleContentExtension)content).getBaseTypeName().getNamespaceURI(),
							((XmlSchemaSimpleContentExtension)content).getBaseTypeName().getLocalPart());
				}
			}
		}
		
		return superType;
	}
	
	/**
	 * @see SchemaProvider#loadSchema(java.net.URI, ProgressIndicator)
	 */
	public Schema loadSchema(URI location, ProgressIndicator progress) throws IOException {
		if (progress == null) {
			progress = new LogProgressIndicator();
		}
		
		// use XML Schema to load schema with all its subschema to the memory
		InputStream is = null;
		URL locationURL;
		locationURL = location.toURL();
		is = locationURL.openStream();
		
		progress.setCurrentTask("Loading schema");

		XmlSchema schema = null;
		XmlSchemaCollection schemaCol = new XmlSchemaCollection();
		// Check if the file is located on web
		if (location.getHost() == null) {
			schemaCol.setSchemaResolver(new ProgressURIResolver(new HumboldtURIResolver(), progress));
			schemaCol.setBaseUri(findBaseUri(location));
		} else if (location.getScheme().equals("bundleresource")) {
			schemaCol.setSchemaResolver(new ProgressURIResolver(new HumboldtURIResolver(), progress));
			schemaCol.setBaseUri(findBaseUri(location) + "/");
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
			namespace = "http://www.opengis.net/gml";
		}
		
		schema.setSourceURI(location.toString());
		
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
				_log.warn(NO_DEFINITION, "No type definition for element " + element.getElementName().getLocalPart());
			}
		}

		return new Schema(elements, namespace, locationURL);
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
			namespace = "http://www.opengis.net/gml";
		}
		
		// Map of type names / types for the result
		Map<Name, TypeDefinition> featureTypes = new HashMap<Name, TypeDefinition>();
		// name mapping: element name -> type name
		Map<Name, SchemaElement> elements = new HashMap<Name, SchemaElement>();
		// result
		SchemaResult result = new SchemaResult(featureTypes, elements);
		
		// type names for type definitions where is no element
		Set<String> schemaTypeNames = new HashSet<String>();
		
		// the schema items
		XmlSchemaObjectCollection items = schema.getItems();
		
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
				else if (element.getQName() != null) {
					typeName = new NameImpl(
							element.getQName().getNamespaceURI(),
							element.getQName().getLocalPart());
				} 
				
				Name elementName = new NameImpl(namespace, element.getName());
				// create schema element
				SchemaElement schemaElement = new SchemaElement(elementName, typeName, null);
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
		}
		
		// Set of include locations
		Set<String> includes = new HashSet<String>();
		
		// handle imports
		XmlSchemaObjectCollection externalItems = schema.getIncludes();
		if (externalItems.getCount() > 0) {
			_log.info("Loading includes and imports for schema at " + schemaLocation);
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
				_log.error("Error adding imported schema", e);
			}
		}
		
		_log.info("Creating types for schema at " + schemaLocation);
		
		progress.setCurrentTask("Analyzing schema " + namespace);
		
		// map for all imported types
		Map<Name, TypeDefinition> importedFeatureTypes = new HashMap<Name, TypeDefinition>();
		// name mapping for imported types: element name -> type name
		Map<Name, SchemaElement> importedElements = new HashMap<Name, SchemaElement>();
		
		// add imported types
		for (Entry<String, SchemaResult> entry : imports.entrySet()) {
			if (entry.getValue() != null) {
				if (includes.contains(entry.getKey())) {
					// is include, add to result
					featureTypes.putAll(entry.getValue().getTypes());
					elements.putAll(entry.getValue().getElements());
				}
				else {
					// is import, don't add to result
					importedFeatureTypes.putAll(entry.getValue().getTypes());
					importedElements.putAll(entry.getValue().getElements());
				}
			}
		}
		
		// schema type resolver combining the informations for resolving types
		SchemaTypeResolver typeResolver = new SchemaTypeResolver(featureTypes, importedFeatureTypes, schemaLocation);
		
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
				typeDependencies = getAttributeTypeNames(elements, importedElements, (XmlSchemaComplexType) item);
				
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
		
		// 3rd pass: create feature types 
		for (Name typeName : typeNames.getItems()) {
			XmlSchemaObject item = typeDefinitions.get(typeName);

			if (item == null) {
				_log.error("No definition for " + typeName.toString());
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
					_log.warn("No attribute type generated for simple type " + typeName.toString());
				}
			}
			else if (item instanceof XmlSchemaComplexType) {
				// determine the super type name
				Name superTypeName = getSuperTypeName((XmlSchemaComplexType) item);
				
				TypeDefinition superType = null;
				if (superTypeName != null) {
					// find super type
					superType = TypeUtil.resolveAttributeType(superTypeName, typeResolver);
					
					// create empty super type if it was not found
					if (superType == null) {
						superType = new TypeDefinition(superTypeName, null, null);
						superType.setLocation("Empty type generated by HALE");
						superType.setAbstract(true);
						// add super type to feature map
						featureTypes.put(superTypeName, superType);
					}
				}
				
				// create type definition
				TypeDefinition typeDef = new TypeDefinition(typeName, null, superType);
				typeDef.setLocation(schemaLocation);
				
				// determine the defined attributes and add them to the declaring type
				List<AttributeDefinition> attributes = getAttributes(
						elements,
						importedElements,
						typeDef, // definition of the declaring type
						(XmlSchemaComplexType) item,
						typeResolver);
				
				// reuse the super type's attribute type where appropriate
				if (superType != null && superType.isAttributeTypeSet()) {
					// determine if any new elements have been added in the subtype
					boolean reuseBinding = true;
					
					// special case: super type is AbstractFeatureType but no FeatureType instance
					if (superType.isFeatureType() && !(superType.getType(null) instanceof FeatureType)) {
						reuseBinding = false;
					}
					
					// check if additional elements are defined
					if (!Geometry.class.isAssignableFrom(superType.getType(null).getBinding())) { // special case: super type binding is Geometry -> ignore additional elements
						Iterator<AttributeDefinition> it = attributes.iterator();
						while (reuseBinding && it.hasNext()) {
							if (it.next().isElement()) {
								reuseBinding = false;
							}
						}
					}
					
					if (reuseBinding) {
						// reuse attribute type
						typeDef.setType(superType.getType(null));
					}
				}
				// special case geometry property types: use a geometry binding if all elements have geometry bindings
				else {
					AttributeType type = null;
					Iterator<AttributeDefinition> it = attributes.iterator();
					while (it.hasNext()) {
						AttributeDefinition def = it.next();
						if (def.isElement() && def.getAttributeType() != null && def.getAttributeType().isAttributeTypeSet()) {
							AttributeType t = def.getAttributeType().getType(null);
							if (t != null) {
								Class<?> b = t.getBinding();
								if (Geometry.class.isAssignableFrom(b)) {
									type = t;
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
	 * @param elements map of element names to type names
	 * @param importedElements map of element names to imported type names
	 * @param typeDef the definition of the declaring type 
	 * @param item the complex type item
	 * @param schemaTypes the schema types
	 *  
	 * @return the attributes as a list of {@link SchemaAttribute}s
	 */
	private List<AttributeDefinition> getAttributes(Map<Name, SchemaElement> elements, 
			Map<Name, SchemaElement> importedElements, TypeDefinition typeDef, XmlSchemaComplexType item,
			SchemaTypeResolver schemaTypes) {
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
					attributes.addAll(getAttributesFromParticle(elements, importedElements, typeDef, particle, schemaTypes));
				}
				// attributes
				XmlSchemaObjectCollection attributeCollection = extension.getAttributes();
				if (attributeCollection != null) {
					attributes.addAll(getAttributesFromCollection(attributeCollection, typeDef, schemaTypes));
				}
				//   </extension>
				// </complexContent>
			}
			else if (content instanceof XmlSchemaSimpleContentExtension) {
				// <simpleContent>
				//   <extension base="...">
				XmlSchemaSimpleContentExtension extension = (XmlSchemaSimpleContentExtension) content;
				// attributes
				XmlSchemaObjectCollection attributeCollection = extension.getAttributes();
				if (attributeCollection != null) {
					attributes.addAll(getAttributesFromCollection(attributeCollection, typeDef, schemaTypes));
				}
				//   </extension>
				// </simpleContent>
			}
		}
		else if (item.getParticle() != null) {
			// no complex content (instead e.g. <sequence>)
			XmlSchemaComplexType complexType = item;
			// particle (e.g. sequence)
			XmlSchemaParticle particle = complexType.getParticle();
			List<AttributeDefinition> tmp = getAttributesFromParticle(elements, importedElements, typeDef, particle, schemaTypes);
			if (tmp != null) {
				attributes.addAll(tmp);
			}
			// attributes
			XmlSchemaObjectCollection attributeCollection = complexType.getAttributes();
			if (attributeCollection != null) {
				attributes.addAll(getAttributesFromCollection(attributeCollection, typeDef, schemaTypes));
			}
		}
		
		return attributes; 
		// </complexType>
	}
	
	private Collection<AttributeDefinition> getAttributesFromCollection(
			XmlSchemaObjectCollection attributeCollection, TypeDefinition declaringType,
			SchemaTypeResolver schemaTypes) {
		List<AttributeDefinition> attributeResults = new ArrayList<AttributeDefinition>();
		
		for (int index = 0; index < attributeCollection.getCount(); index++) {
			XmlSchemaObject object = attributeCollection.getItem(index);
			if (object instanceof XmlSchemaAttribute) {
				// <attribute ... />
				XmlSchemaAttribute attribute = (XmlSchemaAttribute) object;
				
				// create attributes
				QName typeName = attribute.getSchemaTypeName();
				if (typeName != null) {
					attributeResults.add(new DefaultResolveAttribute(
							declaringType, 
							new NameImpl(typeName.getNamespaceURI(), typeName.getLocalPart()), 
							attribute, 
							schemaTypes));
				}
				else if (attribute.getSchemaType() != null) {
					if (declaringType != null) {
						QName name = attribute.getSchemaType().getQName();
						Name attributeTypeName = (name != null)?
								(new NameImpl(name.getNamespaceURI(), name.getLocalPart())):
								(new NameImpl(declaringType.getName().getNamespaceURI() + "/" + declaringType.getName().getLocalPart(), "AnonymousAttribute" + index));
						TypeDefinition attributeType = TypeUtil.resolveSimpleType(
								attributeTypeName, 
								attribute.getSchemaType(), 
								schemaTypes);
						
						attributeResults.add(new DefaultAttribute(declaringType, attributeTypeName, attribute, attributeType));
					}
				}
			}
		}
		
		return attributeResults;
	}

	/**
	 * Get the attributes type names for the given item
	 * 
	 * @param elementTypeMap map of element names to type names 
	 * @param importedElementTypeMap map of element names to imported type names
	 * @param item the complex type item
	 * @return the attribute type names
	 */
	private Set<Name> getAttributeTypeNames(Map<Name, SchemaElement> elementTypeMap, 
			Map<Name, SchemaElement> importedElementTypeMap, XmlSchemaComplexType item) {
		List<AttributeDefinition> attributes = getAttributes(elementTypeMap, importedElementTypeMap, null, item, null);
		
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
		String baseUri = "";
		baseUri = uri.toString();
		if (baseUri.matches("^.*?\\/.+")) {
			baseUri = baseUri.substring(0, baseUri.lastIndexOf("/"));
		}
		_log.info("Base URI for schemas to be used: " + baseUri);
		return baseUri;
	}
	
}



