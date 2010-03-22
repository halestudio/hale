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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.apache.ws.commons.schema.XmlSchema;
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
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeContent;
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeList;
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeUnion;
import org.apache.ws.commons.schema.resolver.DefaultURIResolver;
import org.apache.ws.commons.schema.resolver.URIResolver;
import org.geotools.feature.NameImpl;
import org.opengis.feature.type.Name;

import eu.esdihumboldt.hale.schemaprovider.HumboldtURIResolver;
import eu.esdihumboldt.hale.schemaprovider.LogProgressIndicator;
import eu.esdihumboldt.hale.schemaprovider.ProgressIndicator;
import eu.esdihumboldt.hale.schemaprovider.Schema;
import eu.esdihumboldt.hale.schemaprovider.SchemaProvider;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;
import eu.esdihumboldt.hale.schemaprovider.provider.internal.AbstractSchemaAttribute;
import eu.esdihumboldt.hale.schemaprovider.provider.internal.DependencyOrderedList;
import eu.esdihumboldt.hale.schemaprovider.provider.internal.ElementReferenceAttribute;
import eu.esdihumboldt.hale.schemaprovider.provider.internal.ProgressURIResolver;
import eu.esdihumboldt.hale.schemaprovider.provider.internal.SchemaAttribute;
import eu.esdihumboldt.hale.schemaprovider.provider.internal.SchemaResult;

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
	implements SchemaProvider {
	
	/**
	 * The log
	 */
	private static Logger _log = Logger.getLogger(ApacheSchemaProvider.class);

	/**
	 * Extracts attribute definitions from a {@link XmlSchemaParticle}.
	 * 
	 * @param elementTypeMap map of element names to type names
	 * @param importedElementTypeMap map of element names to imported type names
	 * @param typeDef the definition of the declaring type
	 * @param particle the particle
	 * @param featureTypes 
	 * @param importedFeatureTypes 
	 * 
	 * @return the list of attribute definitions
	 */
	private List<AbstractSchemaAttribute> getAttributesFromParticle(Map<Name, SchemaElement> elementTypeMap, Map<Name, SchemaElement> importedElementTypeMap, TypeDefinition typeDef, XmlSchemaParticle particle, Map<Name, TypeDefinition> featureTypes, Map<Name, TypeDefinition> importedFeatureTypes) {
		List<AbstractSchemaAttribute> attributeResults = new ArrayList<AbstractSchemaAttribute>();
		
		if (particle instanceof XmlSchemaSequence) {
			XmlSchemaSequence sequence = (XmlSchemaSequence)particle;
			for (int j = 0; j < sequence.getItems().getCount(); j++) {
				XmlSchemaObject object = sequence.getItems().getItem(j);
				if (object instanceof XmlSchemaElement) {
					XmlSchemaElement element = (XmlSchemaElement)object;										
					Name attributeTypeName = null;
					if (element.getSchemaTypeName() != null) {
						// element with a type
						// <element name="ELEMENT_NAME" type="SCHEMA_TYPE_NAME" />
						attributeTypeName = new NameImpl(
							element.getSchemaTypeName().getNamespaceURI(),
							element.getSchemaTypeName().getLocalPart());
					}
					else if (element.getRefName() != null) {
						// References another element
						// <element ref="REF_NAME" />
						Name elementName = new NameImpl(
								element.getRefName().getNamespaceURI(),
								element.getRefName().getLocalPart());
						// local element definition
						SchemaElement reference = elementTypeMap.get(elementName);
						if (reference == null) {
							// imported element definition
							reference = importedElementTypeMap.get(elementName);
						}
						if (reference == null) {
							_log.error("Reference to element " + element.getRefName().getLocalPart() +" not found");
						}
						else {
							attributeResults.add(
									new ElementReferenceAttribute(
											typeDef, 
											element.getName(), 
											reference.getTypeName(), 
											element, 
											reference));
							continue;
						}
					}
					else if (element.getSchemaType() != null) {
						if (element.getSchemaType() instanceof XmlSchemaComplexType) {
							XmlSchemaContentModel model = ((XmlSchemaComplexType)element.getSchemaType()).getContentModel();
							XmlSchemaParticle p = ((XmlSchemaComplexType)element.getSchemaType()).getParticle();
							if (model != null) {
								XmlSchemaContent content = model.getContent();
								
								QName qname = null;
								if (content instanceof XmlSchemaComplexContentExtension) {
									qname = ((XmlSchemaComplexContentExtension)content).getBaseTypeName();
								} else if (content instanceof XmlSchemaSimpleContentExtension) {
									qname = ((XmlSchemaSimpleContentExtension)content).getBaseTypeName();
								}
								
								if (qname != null) {
									attributeTypeName = new NameImpl(
											qname.getNamespaceURI(),
											qname.getLocalPart());
								}
							} else if (p != null) {
								// this where we get when there is an anonymous complex type as property type
								/*
								 * FIXME the type is not properly resolved, the call
								 * to getAttributesFromParticle just returns the
								 * base type of the property type
								 */
								List<AbstractSchemaAttribute> attributes = getAttributesFromParticle(elementTypeMap, importedElementTypeMap, typeDef, p, featureTypes, importedFeatureTypes);
								//XXX fix property name
								if (attributes.size() == 1) {
									AbstractSchemaAttribute org = attributes.get(0);
									if (org.getDeclaringType() != null) {
										// remove wrong property
										org.getDeclaringType().removeDeclaredAttribute(org);
									}
									
									if (org instanceof SchemaAttribute) {
										attributeResults.add(new SchemaAttribute(
												typeDef,
												element.getName(), 
												org.getTypeName(), 
												element,
												featureTypes,
												importedFeatureTypes));
									}
									else if (org instanceof ElementReferenceAttribute) {
										attributeResults.add(new ElementReferenceAttribute(
												typeDef, 
												element.getName(), 
												org.getTypeName(), 
												element, 
												((ElementReferenceAttribute) org).getReference()));
									}
									else {
										_log.error("Illegal attribute type, skipping");
									}
								}
								else {
									attributeResults.addAll(attributes);
								}
									
								continue;
							}
						}
						else if (element.getSchemaType() instanceof XmlSchemaSimpleType) {
							QName qname = element.getQName();
							attributeTypeName = new NameImpl(
									qname.getNamespaceURI(),
									qname.getLocalPart());
						}
					}
					if (attributeTypeName == null) {
						_log.warn("Schema type name is null! " + element.getName());
					}
					else {
						attributeResults.add(new SchemaAttribute(
								typeDef,
								element.getName(), 
								attributeTypeName, 
								element,
								featureTypes,
								importedFeatureTypes));
					}
				}
			}
		}
		
		return attributeResults;
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
			if (element.getType().isComplexType()) {
				elements.put(element.getIdentifier(), element);
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
				
				// no dependencies
			}
			
			// if the item is a type we remember the type definition and determine its local dependencies
			if (name != null) {
				// determine the real type name
				Name typeName = new NameImpl(namespace, name); //XXX getTypeName(names, new NameImpl(namespace, name));
				
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
				TypeDefinition simpleType = null;
				
				if (simpleType == null) {
					simpleType = SchemaAttribute.getEnumAttributeType((XmlSchemaSimpleType) item, typeName);
				}
				//TODO other methods of resolving the type
				if (simpleType == null) {
					XmlSchemaSimpleTypeContent content = ((XmlSchemaSimpleType) item).getContent();
					
					if (content instanceof XmlSchemaSimpleTypeUnion) {
						XmlSchemaSimpleTypeUnion union = (XmlSchemaSimpleTypeUnion) content;
						
						//TODO handle unions
						simpleType = new TypeDefinition(typeName, null, null); //XXX
					}
					else if (content instanceof XmlSchemaSimpleTypeList) {
						XmlSchemaSimpleTypeList list = (XmlSchemaSimpleTypeList) content;
						
						//TODO handle lists
						simpleType = new TypeDefinition(typeName, null, null); //XXX
					}
				}
				
				if (simpleType != null) {
					// create a simple type
					featureTypes.put(typeName, simpleType);
				}
				else {
					_log.error("No attribute type generated for simple type " + typeName.toString());
				}
			}
			else if (item instanceof XmlSchemaComplexType) {
				// determine the super type name
				Name superTypeName = getSuperTypeName((XmlSchemaComplexType) item);
				
				TypeDefinition superType = null;
				if (superTypeName != null) {
					// find super type
					superType = featureTypes.get(superTypeName);
					
					// create empty super type if it was not found
					if (superType == null) {
						superType = new TypeDefinition(superTypeName, null, null);
						superType.setAbstract(true);
						// add super type to feature map
						featureTypes.put(superTypeName, superType);
					}
				}
				
				// create type definition
				TypeDefinition typeDef = new TypeDefinition(typeName, null, superType);
				
				// determine the defined attributes and add them to the declaring type
				getAttributes(
						elements,
						importedElements,
						typeDef, // definition of the declaring type
						(XmlSchemaComplexType) item,
						featureTypes,
						importedFeatureTypes);
				
				// set additional properties
				typeDef.setAbstract(((XmlSchemaComplexType) item).isAbstract());
				
				// set description
				/*XXX moved to schema element - String description = descriptions.get(typeName.getLocalPart());
				if (description != null) {
					typeDef.setDescription(description);
				}*/
				
				// add type definition
				featureTypes.put(typeName, typeDef);
			}
		}
		
		// populate schema items with type definitions
		for (SchemaElement element : elements.values()) {
			element.setType(featureTypes.get(element.getTypeName()));
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
	 * @param elementTypeMap map of element names to type names
	 * @param importedElementTypeMap map of element names to imported type names
	 * @param typeDef the definition of the declaring type 
	 * @param item the complex type item
	 * @param featureTypes 
	 * @param importedFeatureTypes 
	 *  
	 * @return the attributes as a list of {@link SchemaAttribute}s
	 */
	private List<AbstractSchemaAttribute> getAttributes(Map<Name, SchemaElement> elementTypeMap, Map<Name, SchemaElement> importedElementTypeMap, TypeDefinition typeDef, XmlSchemaComplexType item, Map<Name, TypeDefinition> featureTypes, Map<Name, TypeDefinition> importedFeatureTypes) {
		XmlSchemaContentModel model = item.getContentModel();
		if (model != null ) {
			XmlSchemaContent content = model.getContent();

			if (content instanceof XmlSchemaComplexContentExtension) {
				if (((XmlSchemaComplexContentExtension)content).getParticle() != null) {
					XmlSchemaParticle particle = ((XmlSchemaComplexContentExtension)content).getParticle();
					return getAttributesFromParticle(elementTypeMap, importedElementTypeMap, typeDef, particle, featureTypes, importedFeatureTypes);
				}
			}
		}
		else if (((XmlSchemaComplexType)item).getParticle() != null) {
			XmlSchemaParticle particle = ((XmlSchemaComplexType)item).getParticle();
			if (particle instanceof XmlSchemaSequence) {
				return getAttributesFromParticle(elementTypeMap, importedElementTypeMap, typeDef, particle, featureTypes, importedFeatureTypes);
			}
		}
		
		return new ArrayList<AbstractSchemaAttribute>();
	}
	
	/**
	 * Get the attributes type names for the given item
	 * 
	 * @param elementTypeMap map of element names to type names 
	 * @param importedElementTypeMap map of element names to imported type names
	 * @param item the complex type item
	 * @return the attribute type names
	 */
	private Set<Name> getAttributeTypeNames(Map<Name, SchemaElement> elementTypeMap, Map<Name, SchemaElement> importedElementTypeMap, XmlSchemaComplexType item) {
		List<AbstractSchemaAttribute> attributes = getAttributes(elementTypeMap, importedElementTypeMap, null, item, null, null);
		
		Set<Name> typeNames = new HashSet<Name>();
		
		for (AbstractSchemaAttribute def : attributes) {
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



