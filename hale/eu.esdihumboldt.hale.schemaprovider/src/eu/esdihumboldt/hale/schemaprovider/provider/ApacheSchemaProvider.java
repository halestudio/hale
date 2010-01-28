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
import org.geotools.feature.NameImpl;
import org.opengis.feature.type.Name;

import eu.esdihumboldt.hale.schemaprovider.HumboldtURIResolver;
import eu.esdihumboldt.hale.schemaprovider.Schema;
import eu.esdihumboldt.hale.schemaprovider.SchemaProvider;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;
import eu.esdihumboldt.hale.schemaprovider.provider.internal.DependencyOrderedList;
import eu.esdihumboldt.hale.schemaprovider.provider.internal.SchemaAttribute;

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
	 * @param typeDef the definition of the declaring type
	 * @param particle the particle
	 * @param featureTypes 
	 * @param importedFeatureTypes 
	 * 
	 * @return the list of attribute definitions
	 */
	private List<SchemaAttribute> getAttributesFromParticle(TypeDefinition typeDef, XmlSchemaParticle particle, Map<Name, TypeDefinition> featureTypes, Map<Name, TypeDefinition> importedFeatureTypes) {
		List<SchemaAttribute> attributeResults = new ArrayList<SchemaAttribute>();
		
		if (particle instanceof XmlSchemaSequence) {
			XmlSchemaSequence sequence = (XmlSchemaSequence)particle;
			for (int j = 0; j < sequence.getItems().getCount(); j++) {
				XmlSchemaObject object = sequence.getItems().getItem(j);
				if (object instanceof XmlSchemaElement) {
					XmlSchemaElement element = (XmlSchemaElement)object;										
					Name attributeTypeName = null;
					if (element.getSchemaTypeName() != null) {
						attributeTypeName = new NameImpl(
							element.getSchemaTypeName().getNamespaceURI(),
							element.getSchemaTypeName().getLocalPart());
					}
					else if (element.getRefName() != null) {
						attributeTypeName = new NameImpl(
							element.getRefName().getNamespaceURI(),
							element.getRefName().getLocalPart());
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
								List<SchemaAttribute> attributes = getAttributesFromParticle(typeDef, p, featureTypes, importedFeatureTypes);
								//XXX fix property name
								if (attributes.size() == 1) {
									SchemaAttribute org = attributes.get(0);
									if (org.getDeclaringType() != null) {
										// remove wrong property
										org.getDeclaringType().removeDeclaredAttribute(org);
									}
									
									attributeResults.add(new SchemaAttribute(
											typeDef,
											element.getName(), 
											org.getTypeName(), 
											element,
											featureTypes,
											importedFeatureTypes));
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
	 * Get the type name
	 * 
	 * @param names mapping for types to names
	 * @param type the type
	 * @return the name (if found, else the type)
	 */
	private Name getTypeName(Map<String, String> names, Name type) {
		String localName = names.get(type.getLocalPart());
		
		if (localName != null && !localName.isEmpty())
			return new NameImpl(type.getNamespaceURI(), localName);
		else
			return type;
	}
	
	/**
	 * @see SchemaProvider#loadSchema(java.net.URI)
	 */
	public Schema loadSchema(URI location) {
		// use XML Schema to load schema with all its subschema to the memory
		InputStream is = null;
		URL locationURL;
		try {
			locationURL = location.toURL();
			is = locationURL.openStream();
		} catch (Throwable e) {
			_log.error("File URI could not be resolved.", e);
			throw new RuntimeException(e);
		}
		XmlSchema schema = null;
		try {
			XmlSchemaCollection schemaCol = new XmlSchemaCollection();
			// Check if the file is located on web
			if (location.getHost() == null) {
				schemaCol.setSchemaResolver(new HumboldtURIResolver());
			    schemaCol.setBaseUri(findBaseUri(location));
			}
			schema = schemaCol.read(new StreamSource(is), null);
			is.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		String namespace = schema.getTargetNamespace();
		if (namespace == null || namespace.isEmpty()) {
			// default to gml schema
			namespace = "http://www.opengis.net/gml";
		}
		
		Map<Name, TypeDefinition> types = loadSchema(schema, new HashMap<String, Map<Name, TypeDefinition>>());
		
		List<TypeDefinition> featureTypes = new ArrayList<TypeDefinition>();
		for (TypeDefinition type : types.values()) {
			if (type.isComplexType()) {
				featureTypes.add(type);
			}
		}
		
		return new Schema(featureTypes, namespace, locationURL);
	}
		
	/**
	 * Load the feature types defined by the given schema
	 * 
	 * @param schema the schema
	 * @param imports the imports/includes that were already
	 *   loaded or where loading has been started
	 * @return the map of feature type names and types
	 */
	protected Map<Name, TypeDefinition> loadSchema(XmlSchema schema, Map<String, Map<Name, TypeDefinition>> imports) {
		String namespace = schema.getTargetNamespace();
		if (namespace == null || namespace.isEmpty()) {
			// default to gml schema
			namespace = "http://www.opengis.net/gml";
		}
		
		// Map of type names / types for the result
		Map<Name, TypeDefinition> featureTypes = new HashMap<Name, TypeDefinition>();
		
		// Set of include locations
		Set<String> includes = new HashSet<String>();
		
		// handle imports
		XmlSchemaObjectCollection externalItems = schema.getIncludes();
		for (int i = 0; i < externalItems.getCount(); i++) {
			try {
				XmlSchemaExternal imp = (XmlSchemaExternal) externalItems.getItem(i);
				XmlSchema importedSchema = imp.getSchema();
				String location = importedSchema.getSourceURI();
				if (!(imports.containsKey(location))) { // only add schemas that were not already added
					imports.put(location, null); // place a marker in the map to prevent loading the location in the call to loadSchema 
					imports.put(location, loadSchema(importedSchema, imports));
				}
				if (imp instanceof XmlSchemaInclude) {
					includes.add(location);
				}
			} catch (Throwable e) {
				_log.error("Error adding imported schema", e);
			}
		}
		
		// map for all imported types
		Map<Name, TypeDefinition> importedFeatureTypes = new HashMap<Name, TypeDefinition>();
		
		// add imported types
		for (Entry<String, Map<Name, TypeDefinition>> entry : imports.entrySet()) {
			if (entry.getValue() != null) {
				if (includes.contains(entry.getKey())) {
					// is include, add to result
					featureTypes.putAll(entry.getValue());
				}
				else {
					// is import, don't add to result
					importedFeatureTypes.putAll(entry.getValue());
				}
			}
		}
		
		// name mapping (schema type name / feature type name)
		Map<String, String> names = new HashMap<String, String>();
		
		// descriptions (element (feature type) name / description)
		Map<String, String> descriptions = new HashMap<String, String>();
		
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
				String typeName = null;
				if (element.getSchemaTypeName() != null) {
					typeName = element.getSchemaTypeName().getLocalPart();
				}
				else if (element.getQName() != null) {
					typeName = element.getQName().getLocalPart();
				} 
				
				String elementName = element.getName();
				names.put(typeName, elementName);
				
				String description = SchemaAttribute.getDescription(element);
				if (description != null) {
					descriptions.put(elementName, description);
				}
			}
			else if (item instanceof XmlSchemaComplexType) {
				schemaTypeNames.add(((XmlSchemaComplexType)item).getName());
			}
			else if (item instanceof XmlSchemaSimpleType) {
				schemaTypeNames.add(((XmlSchemaSimpleType)item).getName());
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
				typeDependencies = getAttributeTypeNames((XmlSchemaComplexType) item);
				
				// get the name of the super type 
				Name superType = (getSuperTypeName((XmlSchemaComplexType)item));
				if (superType != null) {
					superTypeName = getTypeName(names, superType);
					if (superTypeName != null) {
						typeDependencies.add(superTypeName);
					}
				}
				
			} else if (item instanceof XmlSchemaSimpleType) {
				name = ((XmlSchemaSimpleType)item).getName();
				
				// no dependencies
			}
			
			// if the item is a type we remember the type definition and determine its local dependencies
			if (name != null) {
				// determine the real type name
				Name typeName = getTypeName(names, new NameImpl(namespace, name));
				
				// determine the local dependency set
				Set<Name> localDependencies = new HashSet<Name>();
				
				if (typeDependencies != null) {
					for (Name dependency : typeDependencies) {
						if (dependency.getNamespaceURI().equals(namespace) && 
								(names.containsValue(dependency.getLocalPart()) ||
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
					// determine correct name
					superTypeName = getTypeName(names, superTypeName);
					
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
						typeDef, // definition of the declaring type
						(XmlSchemaComplexType) item,
						featureTypes,
						importedFeatureTypes);
				
				// set additional properties
				typeDef.setAbstract(((XmlSchemaComplexType) item).isAbstract());
				
				// set description
				String description = descriptions.get(typeName.getLocalPart());
				if (description != null) {
					typeDef.setDescription(description);
				}
				
				// add type definition
				featureTypes.put(typeName, typeDef);
			}
		}
		
		return featureTypes;
	}
	
	/**
	 * Get the attributes for the given item
	 * 
	 * @param typeDef the definition of the declaring type 
	 * @param item the complex type item
	 * @param featureTypes 
	 * @param importedFeatureTypes 
	 *  
	 * @return the attributes as a list of {@link SchemaAttribute}s
	 */
	private List<SchemaAttribute> getAttributes(TypeDefinition typeDef, XmlSchemaComplexType item, Map<Name, TypeDefinition> featureTypes, Map<Name, TypeDefinition> importedFeatureTypes) {
		XmlSchemaContentModel model = item.getContentModel();
		if (model != null ) {
			XmlSchemaContent content = model.getContent();

			if (content instanceof XmlSchemaComplexContentExtension) {
				if (((XmlSchemaComplexContentExtension)content).getParticle() != null) {
					XmlSchemaParticle particle = ((XmlSchemaComplexContentExtension)content).getParticle();
					return getAttributesFromParticle(typeDef, particle, featureTypes, importedFeatureTypes);
				}
			}
		}
		else if (((XmlSchemaComplexType)item).getParticle() != null) {
			XmlSchemaParticle particle = ((XmlSchemaComplexType)item).getParticle();
			if (particle instanceof XmlSchemaSequence) {
				return getAttributesFromParticle(typeDef, particle, featureTypes, importedFeatureTypes);
			}
		}
		
		return new ArrayList<SchemaAttribute>();
	}
	
	/**
	 * Get the attributes type names for the given item
	 * 
	 * @param item the complex type item
	 * @return the attribute type names
	 */
	private Set<Name> getAttributeTypeNames(XmlSchemaComplexType item) {
		List<SchemaAttribute> attributes = getAttributes(null, item, null, null);
		
		Set<Name> typeNames = new HashSet<Name>();
		
		for (SchemaAttribute def : attributes) {
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



