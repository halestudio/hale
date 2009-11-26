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

import org.apache.log4j.Level;
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

import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.models.UpdateMessage;

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
public class SchemaServiceImplApache 
	implements SchemaService {
	
	/**
	 * This class is used as a simple container to store
	 * a AttributeType / feature type name pair.
	 */
	public class AttributeResult {
		/**
		 * Name of the feature type
		 */
		String name;
		AttributeType type;

		/**
		 * Constructor
		 * 
		 * @param name the attribute name
		 * @param type the attribute type
		 */
		public AttributeResult(String name, AttributeType type) {
			super();
			this.name = name;
			this.type = type;
		}
		
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}
		/**
		 * @return the type
		 */
		public AttributeType getType() {
			return type;
		}
		/**
		 * @param type the type to set
		 */
		public void setType(AttributeType type) {
			this.type = type;
		}
	}
	
	
	private static Logger _log = Logger.getLogger(SchemaServiceImplApache.class);
	
	private static SchemaServiceImplApache instance = new SchemaServiceImplApache();

	/** Source schema */
	private Schema sourceSchema = Schema.EMPTY_SCHEMA;

	/** Target schema */
	private Schema targetSchema = Schema.EMPTY_SCHEMA;
	
	private Collection<HaleServiceListener> listeners = new HashSet<HaleServiceListener>();
	
	/**
	 * Default constructor
	 */
	private SchemaServiceImplApache() {
		_log.setLevel(Level.INFO);
	}
	
	/**
	 * Get the {@link SchemaServiceImplApache} instance
	 * 
	 * @return the {@link SchemaServiceImplApache} instance
	 */
	public static SchemaService getInstance() {
		return SchemaServiceImplApache.instance;
	}

	/**
	 * @see SchemaService#cleanSourceSchema()
	 */
	public boolean cleanSourceSchema() {
		sourceSchema = Schema.EMPTY_SCHEMA;
		updateListeners();
		
		return true;
	}

	/**
	 * @see SchemaService#cleanTargetSchema()
	 */
	public boolean cleanTargetSchema() {
		targetSchema = Schema.EMPTY_SCHEMA;
		updateListeners();
		
		return true;
	}

	/**
	 * @see SchemaService#getSourceSchema()
	 */
	public Collection<FeatureType> getSourceSchema() {
		return sourceSchema.getFeatureTypes();
	}

	/**
	 * @see SchemaService#getTargetSchema()
	 */
	public Collection<FeatureType> getTargetSchema() {
		return targetSchema.getFeatureTypes();
	}

	/**
	 * @see SchemaService#loadSchema(URI, SchemaType)
	 */
	public boolean loadSchema(URI location, SchemaType type) {
		Schema schema = this.loadSchema(location);
		
		if (type.equals(SchemaType.SOURCE)) {
			sourceSchema = schema;
		} 
		else {
			targetSchema = schema;
		}
		
		this.updateListeners();
		return true;
	}
	
	/**
	 * @see SchemaService#loadSchema(List, SchemaService.SchemaType)
	 */
	@Override
	public boolean loadSchema(List<URI> uris, SchemaType type) {
		throw new UnsupportedOperationException("Not implemented");
	}
	
	public boolean addListener(HaleServiceListener sl) {
		return this.listeners.add(sl);
	}
	
	/**
	 * Inform {@link HaleServiceListener}s of an update.
	 */
	@SuppressWarnings("unchecked")
	private void updateListeners() {
		for (HaleServiceListener hsl : this.listeners) {
			hsl.update(new UpdateMessage(SchemaService.class, null)); // FIXME
		}
	}

	/**
	 * Returns the AttributeType for an enumeration.
	 * 
	 * @param element
	 * @return
	 */
	private AttributeType getEnumAttributeType(XmlSchemaElement element) {
		AttributeType type = null;
		if (element.getSchemaType() instanceof XmlSchemaSimpleType) {
			XmlSchemaSimpleType simpleType = (XmlSchemaSimpleType)element.getSchemaType();
			if (simpleType.getContent() instanceof  XmlSchemaSimpleTypeRestriction) {
				XmlSchemaSimpleTypeRestriction content = (XmlSchemaSimpleTypeRestriction)simpleType.getContent();
				
				Name attributeName = new NameImpl(
						content.getBaseTypeName().getNamespaceURI(),
						content.getBaseTypeName().getLocalPart());
				type =  new XSSchema().get(attributeName);
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
	private AttributeType getSchemaAttributeType(Name name, Map<Name, FeatureType> featureTypes) {
		AttributeType type = null;
		
		FeatureType featureType = featureTypes.get(name); 
		if (featureType != null) {
			AttributeTypeBuilder builder = new  AttributeTypeBuilder();
			builder.setBinding(featureType.getBinding());
			builder.setName(name.getLocalPart());
			
			type = builder.buildType();
		}
				
		return type;
	}
	
	/**
	 * Extracts attributeTypes from a XmlSchemaParticle.
	 * 
	 * TODO use {@link #getAttributeTypeNamesFromParticle(XmlSchemaParticle)} instead?
	 * 
	 * @param particle
	 * @param superTypeName
	 * @param featureTypes
	 * @param importedFeatureTypes 
	 * @return
	 */
	private List<AttributeResult> getAttributeTypesFromParticle(XmlSchemaParticle particle,
			Map<Name, FeatureType> featureTypes, Map<Name, FeatureType> importedFeatureTypes) {
		List<AttributeResult> attributeResults = new ArrayList<AttributeResult>();
		
		XSSchema xsSchema = new XSSchema();
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
								
								attributeName = new NameImpl(
										qname.getNamespaceURI(),
										qname.getLocalPart());
							} else if (p != null) {
								attributeResults.addAll(getAttributeTypesFromParticle(p, featureTypes, importedFeatureTypes));
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
						continue;
					}
					AttributeType ty = xsSchema.get(attributeName);
					
					// Try to resolve the attribute bindings
					
					if (ty == null) {
						ty = getSchemaAttributeType(attributeName, featureTypes);
					}
					if (ty == null) {
						ty = getSchemaAttributeType(attributeName, importedFeatureTypes);
					}
					
					if (ty == null) {
						// GML bindings
						GMLSchema gmlSchema = new GMLSchema();
						ty = gmlSchema.get(attributeName);
					}
					if (ty == null) {
						// Bindings for enumeration types
						ty = getEnumAttributeType(element);
					}
					if (ty == null ) {
						_log.warn("Type NOT found: " + attributeName.getLocalPart());
					}
					
					AttributeResult ar = new AttributeResult(element.getName(), ty);
					attributeResults.add(ar);
				}
			}
		}
		return attributeResults;
	}
	
	/**
	 * Extracts attribute type names from a XmlSchemaParticle.
	 * 
	 * @param particle
	 * @param superTypeName
	 * @param featureTypes
	 * @param importedFeatureTypes 
	 * @return
	 */
	private Set<Name> getAttributeTypeNamesFromParticle(XmlSchemaParticle particle) {
		Set<Name> attributeResults = new HashSet<Name>();
		
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
								
								attributeName = new NameImpl(
										qname.getNamespaceURI(),
										qname.getLocalPart());
							} else if (p != null) {
								attributeResults.addAll(getAttributeTypeNamesFromParticle(p));
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
						attributeResults.add(attributeName);
					}
				}
			}
		}
		
		return attributeResults;
	}

	/**
	 * Find a super type name based on a complex type
	 * @param item
	 * @return
	 */
	private Name getSuperTypeName(XmlSchemaComplexType item) {
		Name superType = null;
		
		if (item instanceof XmlSchemaComplexType) {
			XmlSchemaContentModel model = ((XmlSchemaComplexType)item).getContentModel();
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
	 * Method to load a XSD schema file and build a collection of FeatureTypes.
	 * 
	 * @param location
	 *            URI which represents a file
	 * @return Collection FeatureType collection.
	 */
	protected Schema loadSchema(URI location) {
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
		
		Map<Name, FeatureType> types = loadSchema(schema, new HashMap<String, Map<Name, FeatureType>>());
		
		return new Schema(types.values(), namespace, locationURL);
	}
		
	/**
	 * Load the feature types defined by the given schema
	 * 
	 * @param schema the schema
	 * @param imports the imports/includes that were already
	 *   loaded or where loading has been started
	 * @return the map of feature type names and types
	 */
	protected Map<Name, FeatureType> loadSchema(XmlSchema schema, Map<String, Map<Name, FeatureType>> imports) {
		String namespace = schema.getTargetNamespace();
		if (namespace == null || namespace.isEmpty()) {
			// default to gml schema
			namespace = "http://www.opengis.net/gml";
		}
		
		// Map of type names / types for the result
		Map<Name, FeatureType> featureTypes = new HashMap<Name, FeatureType>();
		
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
		Map<Name, FeatureType> importedFeatureTypes = new HashMap<Name, FeatureType>();
		
		// add imported types
		for (Entry<String, Map<Name, FeatureType>> entry : imports.entrySet()) {
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
						if (dependency.getNamespaceURI().equals(namespace) && names.containsValue(dependency.getLocalPart())) {
							// local type, add to local dependencies
							localDependencies.add(dependency);
						}
					}
				}
				
				// add imported super types to the result set
				Name importName = superTypeName;
				FeatureType importType = null;
				
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

			if (item == null || item instanceof XmlSchemaSimpleType) {
				SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
				ftbuilder.setSuperType(null);
				ftbuilder.setName(typeName.getLocalPart());
				ftbuilder.setNamespaceURI(typeName.getNamespaceURI());
				SimpleFeatureType simpleType = ftbuilder.buildFeatureType();
				featureTypes.put(typeName, simpleType);
			}
			else if (item instanceof XmlSchemaComplexType) {
				Name superTypeName = getSuperTypeName((XmlSchemaComplexType) item);
				List<AttributeResult> attributeResults = getAttributes((XmlSchemaComplexType) item, featureTypes, importedFeatureTypes);
				
				// As it is not possible to set the super type of an existing feature type
				// we need to recreate all feature types. But now set the corresponding 
				// super type.
				SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
				ftbuilder.setSuperType(null);
				ftbuilder.setName(typeName.getLocalPart()); //getTypeName(names, name));
				ftbuilder.setNamespaceURI(typeName.getNamespaceURI()); //schema.getTargetNamespace());
				ftbuilder.setAbstract(((XmlSchemaComplexType) item).isAbstract());
				
				for (int a = 0; a < attributeResults.size(); a++) {
					if (attributeResults.get(a).getType() != null) {
						AttributeResult res = attributeResults.get(a);
						AttributeType t = res.getType();
						AttributeDescriptor desc = new AttributeDescriptorImpl(
								t, new NameImpl(schema.getTargetNamespace(), res.getName()),0, 0, false, null);
						// set the name of the Default geometry property explicitly, 
						// otherwise nothing will be returned when calling 
						// getGeometryDescriptor().
						if (Geometry.class.isAssignableFrom(desc.getType().getBinding())) {
							ftbuilder.setDefaultGeometry(desc.getName().getLocalPart());
						}
						//XXX hack
//						else if (res.getName().equals("geometry")) {
//							ftbuilder.setDefaultGeometry(res.getName());
//						}
						//XXX hack
						ftbuilder.add(desc);
					}
					else _log.warn("Attribute type NOT found: " + attributeResults.get(a).getName());
				}
				
				if (superTypeName != null) {
					superTypeName = getTypeName(names, superTypeName);
					
					// Find super type
					FeatureType superType = featureTypes.get(superTypeName);
					
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
	 * TODO use {@link #getAttributeTypeNames(XmlSchemaComplexType)} ?
	 * 
	 * @param item the complex type item
	 * @return the attributes as a list of {@link AttributeResult}s
	 */
	private List<AttributeResult> getAttributes(XmlSchemaComplexType item, Map<Name, FeatureType> featureTypes,
			Map<Name, FeatureType> importedFeatureTypes) {
		XmlSchemaContentModel model = ((XmlSchemaComplexType)item).getContentModel();
		if (model != null ) {
			XmlSchemaContent content = model.getContent();

			if (content instanceof XmlSchemaComplexContentExtension) {
				if (((XmlSchemaComplexContentExtension)content).getParticle() != null) {
					XmlSchemaParticle particle = ((XmlSchemaComplexContentExtension)content).getParticle();
					return getAttributeTypesFromParticle(particle, featureTypes, importedFeatureTypes);
				}
			}
		}
		else if (((XmlSchemaComplexType)item).getParticle() != null) {
			XmlSchemaParticle particle = ((XmlSchemaComplexType)item).getParticle();
			if (particle instanceof XmlSchemaSequence) {
				return getAttributeTypesFromParticle(particle, featureTypes, importedFeatureTypes);
			}
		}
		
		return new ArrayList<AttributeResult>();
	}
	
	/**
	 * Get the attributes type names for the given item
	 * 
	 * @param item the complex type item
	 * @return the attribute type names
	 */
	private Set<Name> getAttributeTypeNames(XmlSchemaComplexType item) {
		XmlSchemaContentModel model = ((XmlSchemaComplexType)item).getContentModel();
		if (model != null ) {
			XmlSchemaContent content = model.getContent();

			if (content instanceof XmlSchemaComplexContentExtension) {
				if (((XmlSchemaComplexContentExtension)content).getParticle() != null) {
					XmlSchemaParticle particle = ((XmlSchemaComplexContentExtension)content).getParticle();
					return getAttributeTypeNamesFromParticle(particle);
				}
			}
		}
		else if (((XmlSchemaComplexType)item).getParticle() != null) {
			XmlSchemaParticle particle = ((XmlSchemaComplexType)item).getParticle();
			if (particle instanceof XmlSchemaSequence) {
				return getAttributeTypeNamesFromParticle(particle);
			}
		}
		
		return new HashSet<Name>();
	}

	private String findBaseUri(URI file) {
		String baseUri = "";
		baseUri = file.toString();
		if (baseUri.matches("^.*?\\/.+")) {
			baseUri = baseUri.substring(0, baseUri.lastIndexOf("/"));
		}
		_log.info("Base URI for schemas to be used: " + baseUri);
		return baseUri;
	}

	/**
	 * @see SchemaService#getSourceNameSpace()
	 */
	public String getSourceNameSpace() {
		return sourceSchema.getNamespace();
	}

	/**
	 * @see SchemaService#getSourceURL()
	 */
	public URL getSourceURL() {
		return sourceSchema.getLocation();
	}

	/**
	 * @see SchemaService#getTargetNameSpace()
	 */
	public String getTargetNameSpace() {
		return targetSchema.getNamespace();
	}

	/**
	 * @see SchemaService#getTargetURL()
	 */
	public URL getTargetURL() {
		return targetSchema.getLocation();
	}

	/**
	 * @see SchemaService#getFeatureTypeByName(java.lang.String)
	 */
	public FeatureType getFeatureTypeByName(String name) {
		FeatureType result = null;
		// handles cases where a full name was given.
		if (!getSourceNameSpace().equals("") && name.contains(getSourceNameSpace())) {
			for (FeatureType ft : getSourceSchema()) {
				if (ft.getName().getLocalPart().equals(name)) {
					result = ft;
					break;
				}
			}
		}
		else if (!getTargetNameSpace().equals("") && name.contains(getTargetNameSpace())) {
			for (FeatureType ft : getTargetSchema()) {
				if (ft.getName().getLocalPart().equals(name)) {
					result = ft;
					break;
				}
			}
		}
		// handle case where only the local part was given.
		else {
			Collection<FeatureType> allFTs = new HashSet<FeatureType>();
			allFTs.addAll(getSourceSchema());
			allFTs.addAll(getTargetSchema());
			for (FeatureType ft : allFTs) {
				if (ft.getName().getLocalPart().equals(name)) {
					result = ft;
					break;
				}
			}
		}
		return result;
	}
	
	/**
	 * @see SchemaService#getSchema(SchemaService.SchemaType)
	 */
	public Collection<FeatureType> getSchema(SchemaType schemaType) {
		if (SchemaType.SOURCE.equals(schemaType)) {
			return getSourceSchema();
		}
		else {
			return getTargetSchema();
		}
	}
	
}



