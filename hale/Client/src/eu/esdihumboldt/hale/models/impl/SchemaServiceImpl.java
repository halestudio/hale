package eu.esdihumboldt.hale.models.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.xerces.jaxp.SAXParserFactoryImpl;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.xml.SchemaFactory;
import org.geotools.xml.schema.ComplexType;
import org.geotools.xml.schema.Element;
import org.geotools.xml.schema.Schema;
import org.geotools.xml.schema.SimpleType;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.xml.sax.SAXException;

import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.rcp.Application;

/**
 * Implementation of {@link SchemaService}.
 */
public class SchemaServiceImpl implements SchemaService {
	
	private static Logger _log = Logger.getLogger(SchemaServiceImpl.class);
	
	private static SchemaServiceImpl instance = new SchemaServiceImpl();

	/** FeatureType collection of the source schema */
	Collection<FeatureType> sourceSchema;

	/** FeatureType collection of the target schema */
	Collection<FeatureType> targetSchema;
	
	private Collection<HaleServiceListener> listeners = new HashSet<HaleServiceListener>();
	
	private SchemaServiceImpl() {
		_log.setLevel(Level.INFO);
	}
	
	public static SchemaService getInstance() {
		return SchemaServiceImpl.instance;
	}

	/**
	 * @see eu.esdihumboldt.hale.models.SchemaService#cleanSourceSchema()
	 */
	public boolean cleanSourceSchema() {
		this.sourceSchema.clear();
		this.updateListeners();
		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.models.SchemaService#cleanTargetSchema()
	 */
	public boolean cleanTargetSchema() {
		this.targetSchema.clear();
		this.updateListeners();
		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.models.SchemaService#getSourceSchema()
	 */
	public Collection<FeatureType> getSourceSchema() {
		return sourceSchema;
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
		this.sourceSchema = loadSchema(file);
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
		this.targetSchema = loadSchema(file);
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
		return this.listeners.add(sl);
	}
	
	/**
	 * Inform {@link HaleServiceListener}s of an update.
	 */
	private void updateListeners() {
		for (HaleServiceListener hsl : this.listeners) {
			hsl.update();
		}
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
			// is = new FileInputStream(pathToSourceSchema);
			String path = file.getPath();

			is = new FileInputStream(path);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		XmlSchemaCollection schemaCol = new XmlSchemaCollection();
		XmlSchema prepSchema = schemaCol.read(new StreamSource(is), null);
		// System.out.println("Source schema has " +
		// prepSchema.getIncludes().getCount()+ " includes");

		// write schema to memory
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		prepSchema.write(out);
		Schema schema = null;
		Collection<FeatureType> collection = new ArrayList<org.opengis.feature.type.FeatureType>();
		try {
			schema = SchemaFactory.getInstance(null, new ByteArrayInputStream(
					out.toByteArray()));
		} catch (Exception uhe) {
			_log.error("Imported Schema only available on-line, but "
					+ "cannot be retrieved.", uhe);
		}

		// Schema[] imports = schema.getImports();
		// for (Schema s : imports) {
		// _log.debug("Imported URI + Name: " + s.getURI() + " " +
		// s.getTargetNamespace());
		// }

		Collection<SimpleFeatureType> inTypes = new HashSet<SimpleFeatureType>();

		// Build first a list of FeatureTypes
		for (ComplexType type : schema.getComplexTypes()) {
			SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
			// System.out.println("FeatureType: " + type.getName());
			builder.setName(type.getName());
			builder.setNamespaceURI(type.getNamespace());
			builder.setAbstract(type.isAbstract());

			if (type.getParent() != null) {
				
				 System.out.println("Feature type: " + type.getName() +
				  ", parent feature type: " + type.getParent().getName());
				 

				for (Element element : type.getChildElements()) {
					if (element.getType() instanceof SimpleType) {
						builder.add(element.getName(), element.getType()
								.getClass());
						
						  System.out.println("\telement: " + element.getName()
						 + ", " + element.getType().getName());
						 
					}

				}
				inTypes.add(builder.buildFeatureType());
			}
		}

		// Build collection of feature type with there parents
		
		for (ComplexType type : schema.getComplexTypes()) {
			
			// Create builder
			SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
			builder.setName(type.getName());
			builder.setNamespaceURI(type.getNamespace());
			builder.setAbstract(type.isAbstract());
			if (type.getParent() instanceof ComplexType) {
				

				if (type.getParent() != null) {
					System.out.println("Feature type: " + type.getName()
							+ ", parent feature type: "
							+ type.getParent().getName());

					for (Element element : type.getChildElements()) {
						if (element.getType() instanceof SimpleType) {
							// System.out.println("\tsimpl0e type element: "
							// + element.getName());
							builder.add(element.getName(), element.getType()
									.getClass());
							System.out.println("\telement: "
									+ element.getName() + ", "
									+ element.getType().getName());
						}

					}

					if (type.getParent().getName()
							.equals("AbstractFeatureType")) {
						builder.setSuperType(null);
					} else {
						for (SimpleFeatureType featureType : inTypes) {
							if (featureType.getName().getLocalPart().equals(
									type.getParent().getName())) {
								builder.setSuperType(featureType);
								System.out.println("Parent type set to "
										+ featureType.getName());
							}
						}
					}
					
				}
				
			}
			collection.add(builder.buildFeatureType());
		}

		return collection;

	}
}
