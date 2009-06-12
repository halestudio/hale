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
package eu.esdihumboldt.hale.models.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

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
import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.SchemaService;

/**
 * Implementation of {@link SchemaService}.
 * The main functionality of this class is to load an XML schema file (XSD)
 * and create a FeatureType collection. This implementation is based on the
 * Apache XmlSchema library (http://ws.apache.org/commons/XmlSchema/). It is
 * necessary use this library instead of the GeoTools Xml schema loader, because
 * the GeoTools version cannot handle GML 3.2 based files.
 * 
 * @author Bernd Schneiders, Logica; Thorsten Reitz, Fraunhofer IGD
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

	/** FeatureType collection of the source schema */
	Collection<FeatureType> sourceSchema;

	/** FeatureType collection of the target schema */
	Collection<FeatureType> targetSchema;
	
	private Collection<HaleServiceListener> listeners = new HashSet<HaleServiceListener>();
	
	private String sourceNamespace = "";
	private String targetNamespace = "";
	
	private URL sourceLocation = null;
	private URL targetLocation = null;
	
	private SchemaServiceImplApache() {
		_log.setLevel(Level.INFO);
	}
	
	public static SchemaService getInstance() {
		return SchemaServiceImplApache.instance;
	}

	/**
	 * @see eu.esdihumboldt.hale.models.SchemaService#cleanSourceSchema()
	 */
	public boolean cleanSourceSchema() {
		if (this.sourceSchema != null && this.sourceSchema.size() != 0) {
			this.sourceSchema.clear();
		}
		this.sourceLocation = null;
		this.sourceNamespace = "";
		this.updateListeners();
		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.models.SchemaService#cleanTargetSchema()
	 */
	public boolean cleanTargetSchema() {
		if (this.targetSchema != null && this.targetSchema.size() != 0) {
			this.targetSchema.clear();
		}
		this.targetLocation = null;
		this.targetNamespace = "";
		this.updateListeners();
		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.models.SchemaService#getSourceSchema()
	 */
	public Collection<FeatureType> getSourceSchema() {
		return this.sourceSchema;
	}

	/**
	 * @see eu.esdihumboldt.hale.models.SchemaService#getTargetSchema()
	 */
	public Collection<FeatureType> getTargetSchema() {
		return this.targetSchema;
	}

	/**
	 * @see eu.esdihumboldt.hale.models.SchemaService#loadSourceSchema(java.net.URI)
	 */
	public boolean loadSourceSchema(URI file) {
		this.cleanSourceSchema();
		this.sourceSchema = loadSchema(file);
		try {
			this.sourceLocation = new URL("file://" + file.toString());
		} catch (MalformedURLException e) {
			throw new RuntimeException("The source location " + file.getPath()
					+ "could not be saved: ", e);
		}
		if (this.sourceSchema != null && this.sourceSchema.size() > 0) {
			this.sourceNamespace = this.sourceSchema.iterator().next()
					.getName().getNamespaceURI().toString();
		}
		if (this.sourceSchema != null) {
			this.updateListeners();
			return true;
		} 
		else {
			return false;
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.models.SchemaService#loadTargetSchema(java.net.URI)
	 */
	public boolean loadTargetSchema(URI file) {
		this.cleanTargetSchema();
		this.targetSchema = loadSchema(file);
		try {
			this.targetLocation = new URL("file://" + file.toString());
		} catch (MalformedURLException e) {
			throw new RuntimeException("The target location " + file.getPath()
					+ "could not be saved: ", e);
		}
		if (this.targetSchema != null && this.targetSchema.size() > 0) {
			this.targetNamespace = this.targetSchema.iterator().next()
					.getName().getNamespaceURI().toString();
		}
		if (targetSchema != null) {
			this.updateListeners();
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 *  Copies a file
	 * 
	 * @param in
	 * @param out
	 * @throws Exception
	 */
	  public static void copyFile(File in, File out) throws Exception {
		    FileInputStream fis  = new FileInputStream(in);
		    FileOutputStream fos = new FileOutputStream(out);
		    try {
		        byte[] buf = new byte[1024];
		        int i = 0;
		        while ((i = fis.read(buf)) != -1) {
		            fos.write(buf, 0, i);
		        }
		    } 
		    catch (Exception e) {
		        throw e;
		    }
		    finally {
		        if (fis != null) fis.close();
		        if (fos != null) fos.close();
		    }
		  }

	  public static String getFileContent(File in) throws Exception {
		    FileInputStream fis  = new FileInputStream(in);
		    StringBuffer content = new StringBuffer();
		    try {
		        byte[] buf = new byte[1024];
		        int i = 0;
		        while ((i = fis.read(buf)) != -1) {
		            
		        	content.append(new String(buf, 0, i));
		        }
		    } 
		    catch (Exception e) {
		        throw e;
		    }
		    finally {
		        if (fis != null) fis.close();
		    }
		    return content.toString();
		  }
	  
	  public static void writeFileContent(File out, String content) throws Exception {
		    FileOutputStream fos  = new FileOutputStream(out);
		    try {
		        	fos.write(content.getBytes());
		    } 
		    catch (Exception e) {
		        throw e;
		    }
		    finally {
		        if (fos != null) fos.close();
		    }
		  }	
	
	
	public boolean addListener(HaleServiceListener sl) {
		_log.info("Adding a listener.");
		return this.listeners.add(sl);
	}
	
	/**
	 * Inform {@link HaleServiceListener}s of an update.
	 */
	private void updateListeners() {
		for (HaleServiceListener hsl : this.listeners) {
			_log.info("Updating a listener.");
			hsl.update();
		}
	}

	/**
	 * Searches a FeatureType in a FeatureType collection by a given name.
	 * 
	 * @param featureTypes Collection of FeatureTypes
	 * @param name Name (LocalPart) of a FeatureType to be find.
	 * @return 
	 */
	private FeatureType findFeatureType(Collection<FeatureType> featureTypes, String name) {
		for (FeatureType featureType : featureTypes) {
			String featureTypeName = featureType.getName().getLocalPart();
			if (featureTypeName.equals(name)) return featureType; 
		}
		return null;
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
	 * 
	 * @param name
	 * @param featureTypes
	 * @return
	 */
	private AttributeType getSchemaAttributeType(Name name, Collection<FeatureType> featureTypes) {
		AttributeType type = null;
		
		for (FeatureType featureType : featureTypes) {
			if (   featureType.getName().getLocalPart().equals(name.getLocalPart())
				&& featureType.getName().getNamespaceURI().equals(name.getNamespaceURI()))
			{
				AttributeTypeBuilder builder = new  AttributeTypeBuilder();
				builder.setBinding(featureType.getBinding());
				builder.setName(name.getLocalPart());
				
				type = builder.buildType();
				break;
			}
				
		}
		return type;
	}
	
	/**
	 * Extracts attributeTypes from a XmlSchemaParticle.
	 * 
	 * @param particle
	 * @param superTypeName
	 * @param featureTypes
	 * @return
	 */
	private List<AttributeResult> getAttributeTypesFromParticle(XmlSchemaParticle particle, String superTypeName, Collection<FeatureType> featureTypes) {
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
								attributeResults.addAll(getAttributeTypesFromParticle(p, superTypeName, featureTypes));
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
						// GML bindings
						GMLSchema gmlSchema = new GMLSchema();
						ty = gmlSchema.get(attributeName);
					}
					if (ty == null) {
						// Bindings for enumeration types
						ty = getEnumAttributeType(element);
					}
					if (ty == null) {
						ty = getSchemaAttributeType(attributeName, featureTypes);
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
	 * Method to load a XSD schema file and build a collection of FeatureTypes.
	 * 
	 * @param file
	 *            URI which represents a file
	 * @return Collection FeatureType collection.
	 */
	public Collection<FeatureType> loadSchema(URI file) {
		// use XML Schema to load schema with all its subschema to the memory
		InputStream is = null;
		try {
			String path = file.toString().replace("%20", " ").replace("\\\\", "/");
			is = new FileInputStream(path);
			
		} catch (Throwable e) {
			_log.error("-- path not resolved: " + file);
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		XmlSchema schema = null;
		try {
			XmlSchemaCollection schemaCol = new XmlSchemaCollection();
			// Check if the file is located on web
			if (file.getHost() == null) {
				schemaCol.setSchemaResolver(new HumboldtURIResolver());
			    schemaCol.setBaseUri(findBaseUri(file));
			}
			schema = schemaCol.read(new StreamSource(is), null);
			is.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	
		XmlSchemaObjectCollection items = schema.getItems();
		List<XmlSchemaComplexType> types = new ArrayList<XmlSchemaComplexType>();
		for (int i = 0; i < items.getCount(); i++) {
			if (items.getItem(i) instanceof XmlSchemaComplexType)
				types.add((XmlSchemaComplexType)items.getItem(i));
		}
		
		
		// Build a collection of feature types based on all complex and simple
		// types in the schema.
		Collection<FeatureType> tmpFeatureTypes = new ArrayList<FeatureType>();
		Collection<FeatureType> featureTypes = new ArrayList<FeatureType>();
		Collection<Name> superTypes = new HashSet<Name>();
		for (int i = 0; i < items.getCount(); i++) {
			XmlSchemaObject item = items.getItem(i);
			String name = null;
			if (item instanceof XmlSchemaComplexType) {				
				name = ((XmlSchemaComplexType)item).getName();
				
				// As the complex type could be have an super type which is
				// not directly defined in the schema (e.g. GML super types)
				// we need to store all super type and find in 3. all missing
				// feature types.
				Name superType = (getSuperTypeName((XmlSchemaComplexType)item));
				if (superType != null) superTypes.add(superType);
				
			} else if (item instanceof XmlSchemaSimpleType) {
				name = ((XmlSchemaSimpleType)item).getName();
			}
			
			// If the item has a name, we create a feature type based on it.
			if (name != null) {
				SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
				ftbuilder.setSuperType(null);
				ftbuilder.setName(name);
				ftbuilder.setNamespaceURI(schema.getTargetNamespace());
				SimpleFeatureType ft = ftbuilder.buildFeatureType();
				tmpFeatureTypes.add(ft);
			} 
		}
		
		// Check if for each super type a feature type is existing
		for (Name superType : superTypes) {
			boolean found = false;
			for (FeatureType featureType : tmpFeatureTypes) {
				// Check if feature type and super type are equal. If, we dont need to create a
				// new feature type.
				if (   superType.getLocalPart().equals(featureType.getName().getLocalPart())
					&& superType.getNamespaceURI().equals(featureType.getName().getNamespaceURI())) {
					found = true;
					break;
				}
			}
			
			// If the super type is not in the feature type collection, we need
			// to create a new feature type based on this super type.
			if (found == false) {
				SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
				ftbuilder.setSuperType(null);
				ftbuilder.setName(superType.getLocalPart());
				ftbuilder.setNamespaceURI(superType.getNamespaceURI());
				SimpleFeatureType ft = ftbuilder.buildFeatureType();
				tmpFeatureTypes.add(ft);
				
				// Add the feature type also to the featureTypes list, because we now
				// that this featureType does not have an super type (in this schema)
				featureTypes.add(ft);
			}
		}
		
		// Assign in the second run super types to the feature types where necessary 
		for (int i = 0; i < items.getCount(); i++) {
			XmlSchemaObject item = items.getItem(i);

			String name = "";
			String superTypeName = null;
			
			List<AttributeResult> attributeResults = new ArrayList<AttributeResult>();
			
			if (item instanceof XmlSchemaComplexType) {
				name = ((XmlSchemaComplexType)items.getItem(i)).getName();
				XmlSchemaContentModel model = ((XmlSchemaComplexType)item).getContentModel();
				if (model != null ) {
					XmlSchemaContent content = model.getContent();

					if (content instanceof XmlSchemaComplexContentExtension) {
						superTypeName = ((XmlSchemaComplexContentExtension)content).getBaseTypeName().getLocalPart();
						if (((XmlSchemaComplexContentExtension)content).getParticle() != null) {
							XmlSchemaParticle particle = ((XmlSchemaComplexContentExtension)content).getParticle();
							attributeResults = getAttributeTypesFromParticle(particle, superTypeName, tmpFeatureTypes);
						}
					}
				}
				else if (((XmlSchemaComplexType)item).getParticle() != null) {
					XmlSchemaParticle particle = ((XmlSchemaComplexType)item).getParticle();
					if (particle instanceof XmlSchemaSequence) {
						attributeResults = getAttributeTypesFromParticle(particle, superTypeName, tmpFeatureTypes);
					}
				}
			}
//			else if (items.getItem(i) instanceof XmlSchemaElement) {
//				name = ((XmlSchemaElement)items.getItem(i)).getName();
//			}
			

			// As it is not possible to set the super type of an existing feature type
			// we need to recreate all feature types. But now set the corresponding 
			// super type.
			SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
			ftbuilder.setSuperType(null);
			ftbuilder.setName(name);
			ftbuilder.setNamespaceURI(schema.getTargetNamespace());
			
			for (int a = 0; a < attributeResults.size(); a++) {
				if (attributeResults.get(a).getType() != null) {
					AttributeResult res = attributeResults.get(a);
					AttributeType t = res.getType();
					AttributeDescriptor desc = new AttributeDescriptorImpl(t, new NameImpl(res.getName()),0, 0, false, null);
					ftbuilder.add(desc);
				}
				else _log.warn("Attribute type NOT found: " + attributeResults.get(a).getName());
			}
			
				
			if (superTypeName != null) {
				// Find super type
				FeatureType superType = findFeatureType(tmpFeatureTypes, superTypeName);
				
				if (superType != null) {
					ftbuilder.setSuperType((SimpleFeatureType)superType);
					Collection<PropertyDescriptor> descriptors = superType.getDescriptors();
					for (PropertyDescriptor descriptor : descriptors) {
						ftbuilder.add((AttributeDescriptor)descriptor);						
					}
				}
			}
			SimpleFeatureType ft = ftbuilder.buildFeatureType();
			featureTypes.add(ft);
		}
		
		// Remove the empty feature type which is always appearing
		List<FeatureType> ft = new ArrayList<FeatureType>();
		for(FeatureType featureType : featureTypes) {
			if (featureType.getName().getLocalPart().equals("")) {
				ft.add(featureType);
			}
		}
		featureTypes.removeAll(ft);
		
		// Prints the feature type to the console (for debugging only)
		//SchemaPrinter.printFeatureTypeCollection(featureTypes);
		
		return featureTypes;
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
		return this.sourceNamespace;
	}

	/**
	 * @see SchemaService#getSourceURL()
	 */
	public URL getSourceURL() {
		return this.sourceLocation;
	}

	/**
	 * @see SchemaService#getTargetNameSpace()
	 */
	public String getTargetNameSpace() {
		return this.targetNamespace;
	}

	/**
	 * @see SchemaService#getTargetURL()
	 */
	public URL getTargetURL() {
		return this.targetLocation;
	}

	/* (non-Javadoc)
	 * @see eu.esdihumboldt.hale.models.SchemaService#getFeatureTypeByName(java.lang.String)
	 */
	public FeatureType getFeatureTypeByName(String name) {
		FeatureType result = null;
		// handles cases where a full name was given.
		if (!this.sourceNamespace.equals("") && name.contains(this.sourceNamespace)) {
			for (FeatureType ft : this.sourceSchema) {
				if (ft.getName().getLocalPart().equals(name)) {
					result = ft;
					break;
				}
			}
		}
		else if (!this.targetNamespace.equals("") && name.contains(this.targetNamespace)) {
			for (FeatureType ft : this.targetSchema) {
				if (ft.getName().getLocalPart().equals(name)) {
					result = ft;
					break;
				}
			}
		}
		// handle case where only the local part was given.
		else {
			Collection<FeatureType> allFTs = new HashSet<FeatureType>();
			allFTs.addAll(this.sourceSchema);
			allFTs.addAll(this.targetSchema);
			for (FeatureType ft : allFTs) {
				if (ft.getName().getLocalPart().equals(name)) {
					result = ft;
					break;
				}
			}
		}
		return result;
	}
}



