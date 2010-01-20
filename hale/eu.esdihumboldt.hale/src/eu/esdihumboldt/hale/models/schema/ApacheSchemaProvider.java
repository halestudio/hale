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
package eu.esdihumboldt.hale.models.schema;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
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
import org.apache.ws.commons.schema.XmlSchemaEnumerationFacet;
import org.apache.ws.commons.schema.XmlSchemaExternal;
import org.apache.ws.commons.schema.XmlSchemaInclude;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;
import org.apache.ws.commons.schema.XmlSchemaParticle;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.apache.ws.commons.schema.XmlSchemaSimpleContentExtension;
import org.apache.ws.commons.schema.XmlSchemaSimpleType;
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeRestriction;
import org.geotools.feature.AttributeTypeBuilder;
import org.geotools.feature.NameImpl;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.feature.type.AttributeDescriptorImpl;
import org.geotools.gml3.GMLSchema;
import org.geotools.xs.XSSchema;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;

import com.vividsolutions.jts.geom.Geometry;

import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.models.provider.SchemaProvider;

/**
 * Implementation of {@link SchemaService}.
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
	 * Represents the definition of an attribute
	 */
	private static class AttributeDefinition {
		/**
		 * Name of the feature type
		 */
		private final String name;
		private final Name typeName;
		private final XmlSchemaElement element;

		/**
		 * Constructor
		 * 
		 * @param name the attribute name
		 * @param typeName the name of the attribute type
		 * @param element the element defining the attribute
		 */
		public AttributeDefinition(String name, Name typeName,
				XmlSchemaElement element) {
			super();
			this.name = name;
			this.typeName = typeName;
			this.element = element;
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
		 * @return the element
		 */
		public XmlSchemaElement getElement() {
			return element;
		}
		
	}
	
	/**
	 * The log
	 */
	private static Logger _log = Logger.getLogger(ApacheSchemaProvider.class);
	
	/**
	 * Returns the attribute type for an enumeration.
	 * 
	 * @param element the defining element
	 * 
	 * @return the attribute type or <code>null</code>
	 */
	private AttributeType getEnumAttributeType(XmlSchemaElement element) {
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
	private AttributeType getEnumAttributeType(XmlSchemaSimpleType simpleType, Name name) {
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
		return type;
	}
	
	/**
	 * Get an attribute type from a feature type map
	 * 
	 * @param name the attribute type name
	 * @param featureTypes the feature type map
	 * @return the attribute type or <code>null</code> if the corresponding
	 *   type was not found in the set
	 */
	private AttributeType getSchemaAttributeType(Name name, Map<Name, AttributeType> featureTypes) {
		return featureTypes.get(name);
	}
	
	/**
	 * Get the attribute type for an attribute definition
	 * 
	 * @param attribute the attribute definition
	 * @param featureTypes the local feature types
	 * @param importedFeatureTypes the imported feature types
	 *  
	 * @return the attribute type, may be <code>null</code> if it could not be
	 *    resolved
	 */
	private AttributeType getAttributeType(AttributeDefinition attribute,
			Map<Name, AttributeType> featureTypes, Map<Name, AttributeType> importedFeatureTypes) {
		XSSchema xsSchema = new XSSchema();
		AttributeType ty = xsSchema.get(attribute.getTypeName());
			
		// Try to resolve the attribute bindings
		
		if (ty == null) {
			ty = getSchemaAttributeType(attribute.getTypeName(), featureTypes);
		}
		if (ty == null) {
			ty = getSchemaAttributeType(attribute.getTypeName(), importedFeatureTypes);
		}
		
		if (ty == null) {
			// Bindings for enumeration types
			ty = getEnumAttributeType(attribute.getElement());
		}
		
		if (ty == null) {
			// GML bindings
			GMLSchema gmlSchema = new GMLSchema();
			ty = gmlSchema.get(attribute.getTypeName());
		}
		if (ty == null ) {
			_log.warn("Type NOT found: " + attribute.getTypeName().getLocalPart());
		}
		
		return ty;
	}
	
	/**
	 * Extracts attribute definitions from a {@link XmlSchemaParticle}.
	 * 
	 * @param particle the particle
	 * 
	 * @return the list of attribute definitions
	 */
	private List<AttributeDefinition> getAttributesFromParticle(XmlSchemaParticle particle) {
		List<AttributeDefinition> attributeResults = new ArrayList<AttributeDefinition>();
		
		if (particle instanceof XmlSchemaSequence) {
			XmlSchemaSequence sequence = (XmlSchemaSequence)particle;
			for (int j = 0; j < sequence.getItems().getCount(); j++) {
				XmlSchemaObject object = sequence.getItems().getItem(j);
				if (object instanceof XmlSchemaElement) {
					XmlSchemaElement element = (XmlSchemaElement)object;										
					Name attributeName = null;
					if (element.getSchemaTypeName() != null) {
						attributeName = new NameImpl(
							element.getSchemaTypeName().getNamespaceURI(),
							element.getSchemaTypeName().getLocalPart());
					}
					else if (element.getRefName() != null) {
						attributeName = new NameImpl(
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
									attributeName = new NameImpl(
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
								List<AttributeDefinition> attributes = getAttributesFromParticle(p);
								//XXX fix property name
								if (attributes.size() == 1) {
									AttributeDefinition org = attributes.get(0);
									
									attributeResults.add(new AttributeDefinition(
											element.getName(), 
											org.getTypeName(), 
											element));
								}
								else {
									attributeResults.addAll(attributes);
								}
									
								continue;
							}
						}
						else if (element.getSchemaType() instanceof XmlSchemaSimpleType) {
							QName qname = element.getQName();
							attributeName = new NameImpl(
									qname.getNamespaceURI(),
									qname.getLocalPart());
						}
					}
					if (attributeName == null) {
						_log.warn("Schema type name is null! " + element.getName());
					}
					else {
						attributeResults.add(new AttributeDefinition(
								element.getName(), 
								attributeName, 
								element));
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
		
		Map<Name, AttributeType> types = loadSchema(schema, new HashMap<String, Map<Name, AttributeType>>());
		
		List<FeatureType> featureTypes = new ArrayList<FeatureType>();
		for (AttributeType type : types.values()) {
			if (type instanceof FeatureType) {
				featureTypes.add((FeatureType) type);
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
	protected Map<Name, AttributeType> loadSchema(XmlSchema schema, Map<String, Map<Name, AttributeType>> imports) {
		String namespace = schema.getTargetNamespace();
		if (namespace == null || namespace.isEmpty()) {
			// default to gml schema
			namespace = "http://www.opengis.net/gml";
		}
		
		// Map of type names / types for the result
		Map<Name, AttributeType> featureTypes = new HashMap<Name, AttributeType>();
		
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
		Map<Name, AttributeType> importedFeatureTypes = new HashMap<Name, AttributeType>();
		
		// add imported types
		for (Entry<String, Map<Name, AttributeType>> entry : imports.entrySet()) {
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
				AttributeType importType = null;
				
				while (importName != null && (importType = importedFeatureTypes.get(importName)) != null) {
					featureTypes.put(importName, importType);
					
					AttributeType superType = importType.getSuper();
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
				AttributeType simpleType = null;
				
				if (simpleType == null) {
					simpleType = getEnumAttributeType((XmlSchemaSimpleType) item, typeName);
				}
				//TODO other methods of resolving the type
				
				if (simpleType != null) {
					featureTypes.put(typeName, simpleType);
				}
				else {
					_log.error("No attribute type generated for simple type " + typeName.toString());
				}
			}
			else if (item instanceof XmlSchemaComplexType) {
				Name superTypeName = getSuperTypeName((XmlSchemaComplexType) item);
				
				// get the attributes
				List<AttributeDefinition> attributeDefinitions = getAttributes((XmlSchemaComplexType) item);
				
				// As it is not possible to set the super type of an existing feature type
				// we need to recreate all feature types. But now set the corresponding 
				// super type.
				SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
				ftbuilder.setSuperType(null);
				ftbuilder.setName(typeName.getLocalPart()); //getTypeName(names, name));
				ftbuilder.setNamespaceURI(typeName.getNamespaceURI()); //schema.getTargetNamespace());
				ftbuilder.setAbstract(((XmlSchemaComplexType) item).isAbstract());
				
				for (AttributeDefinition attribute : attributeDefinitions) {
					AttributeType attributeType = getAttributeType(attribute, featureTypes, importedFeatureTypes);
					
					if (attributeType != null) {
						if (attribute.getName().equals("geometry")) {
							AttributeTypeBuilder builder = new AttributeTypeBuilder();
							builder.setBinding(Geometry.class);
							builder.setName(attributeType.getName().getLocalPart());
							builder.setNillable(true);
							attributeType = builder.buildType();
						}
						
						AttributeDescriptor desc = new AttributeDescriptorImpl(
								attributeType, new NameImpl(schema.getTargetNamespace(), attribute.getName()),0, 0, true, null); // FIXME nillable determination
						// set the name of the Default geometry property explicitly, 
						// otherwise nothing will be returned when calling 
						// getGeometryDescriptor().
						if (Geometry.class.isAssignableFrom(desc.getType().getBinding())) {
							ftbuilder.setDefaultGeometry(desc.getName().getLocalPart());
						}
			
						ftbuilder.add(desc);
					}
					else _log.warn("Attribute type NOT found: " + attribute.getName());
				}
				
				if (superTypeName != null) {
					superTypeName = getTypeName(names, superTypeName);
					
					// Find super type
					AttributeType tempType = featureTypes.get(superTypeName);
					FeatureType superType = ((tempType instanceof FeatureType)?((FeatureType) tempType):(null));
					
					if (superType == null) {
						SimpleFeatureTypeBuilder stbuilder = new SimpleFeatureTypeBuilder();
						stbuilder.setSuperType(null);
						stbuilder.setName(superTypeName.getLocalPart());
						stbuilder.setNamespaceURI(superTypeName.getNamespaceURI());
						superType = stbuilder.buildFeatureType();
						featureTypes.put(superTypeName, superType);
						
						_log.warn("Super type not found, creating an empty super type: " + superTypeName.getURI());
					}
					
					if (superType != null) {
						ftbuilder.setSuperType((SimpleFeatureType)superType);
						// add super type properties
						Collection<PropertyDescriptor> descriptors = superType.getDescriptors();
						for (PropertyDescriptor descriptor : descriptors) {
							ftbuilder.add((AttributeDescriptor)descriptor);						
						}
						/*if (ftbuilder.getDefaultGeometry() == null 
								&& superType.getGeometryDescriptor() != null) {
							//ftbuilder.add("the_geom", Geometry.class ); //XXX ???
							//XXX test
							GeometryDescriptor desc = superType.getGeometryDescriptor();
							ftbuilder.add(desc);
							ftbuilder.setDefaultGeometry(superType.getGeometryDescriptor().getLocalName());
						}*/
					}
				}
				
				SimpleFeatureType ft = ftbuilder.buildFeatureType();
				
				if (ft.getGeometryDescriptor() == null) {
//					_log.warn("For FeatureType " + ft.getTypeName()
//							+ ", no GeometryProperty was assigned.");
				}
				
				featureTypes.put(typeName, ft);
			}
		}
		
		return featureTypes;
	}
	
	/**
	 * Get the attributes for the given item
	 * 
	 * @param item the complex type item
	 *  
	 * @return the attributes as a list of {@link AttributeDefinition}s
	 */
	private List<AttributeDefinition> getAttributes(XmlSchemaComplexType item) {
		XmlSchemaContentModel model = item.getContentModel();
		if (model != null ) {
			XmlSchemaContent content = model.getContent();

			if (content instanceof XmlSchemaComplexContentExtension) {
				if (((XmlSchemaComplexContentExtension)content).getParticle() != null) {
					XmlSchemaParticle particle = ((XmlSchemaComplexContentExtension)content).getParticle();
					return getAttributesFromParticle(particle);
				}
			}
		}
		else if (((XmlSchemaComplexType)item).getParticle() != null) {
			XmlSchemaParticle particle = ((XmlSchemaComplexType)item).getParticle();
			if (particle instanceof XmlSchemaSequence) {
				return getAttributesFromParticle(particle);
			}
		}
		
		return new ArrayList<AttributeDefinition>();
	}
	
	/**
	 * Get the attributes type names for the given item
	 * 
	 * @param item the complex type item
	 * @return the attribute type names
	 */
	private Set<Name> getAttributeTypeNames(XmlSchemaComplexType item) {
		List<AttributeDefinition> attributes = getAttributes(item);
		
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



