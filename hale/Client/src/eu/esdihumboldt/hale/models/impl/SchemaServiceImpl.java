package eu.esdihumboldt.hale.models.impl;

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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
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
	@Override
	public boolean cleanSourceSchema() {
		this.sourceSchema.clear();
		this.updateListeners();
		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.models.SchemaService#cleanTargetSchema()
	 */
	@Override
	public boolean cleanTargetSchema() {
		this.targetSchema.clear();
		this.updateListeners();
		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.models.SchemaService#getSourceSchema()
	 */
	@Override
	public Collection<FeatureType> getSourceSchema() {
		return sourceSchema;
	}

	/**
	 * @see eu.esdihumboldt.hale.models.SchemaService#getTargetSchema()
	 */
	@Override
	public Collection<FeatureType> getTargetSchema() {
		return this.targetSchema;
	}

	/**
	 * @see eu.esdihumboldt.hale.models.SchemaService#loadSourceSchema(java.net.URI)
	 */
	@Override
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
	@Override
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
	/**
	 * Method to load a XSD schema file and build a collection of FeatureTypes.
	 * 
	 * @param file
	 *            URI which represents a file
	 * @return Collection FeatureType collection.
	 */
	private Collection<FeatureType> loadSchema(URI file) {
		Collection<FeatureType> collection = new ArrayList<org.opengis.feature.type.FeatureType>();
		
		InputStream is, is2;
		try {
			is = new FileInputStream(file.toString().replaceAll("\\+", " "));
			
			// Get the list of all sub schemas
			SchemaParser parser = new SchemaParser();
			Map<String, String> subSchemas = parser.parse(file.getPath());
			
			// Load all schemas
			Map<String, String> files = new HashMap<String, String>();
			for (String schema : subSchemas.keySet()) {

				File tempSchema = new File( "HALE_temp_schema_" + UUID.randomUUID().toString() + ".xsd" );				
				File sourceSchema = new File( subSchemas.get(schema) );
				
				files.put(subSchemas.get(schema), tempSchema.getAbsolutePath());
				System.out.println(sourceSchema.getAbsolutePath() + ", " + tempSchema.getAbsolutePath());
			}
			
			for (String schema : subSchemas.keySet()) {
				File sourceFile = new File(subSchemas.get(schema));
				String fileContent = getFileContent(sourceFile);
				
				for (String replacement : subSchemas.keySet())
				{
					fileContent = fileContent.replace(new StringBuffer(replacement),
						new StringBuffer(files.get(subSchemas.get(replacement))));
				}
				
				File tempFile = new File(files.get(subSchemas.get(schema)));
				writeFileContent(tempFile, fileContent);
				
			}
			
			// FIXME: Find good way of automatically importing parent schemas
//			String path = Application.getBasePath().replaceAll("\\+", " ");
//			is2 = new FileInputStream(Application.getBasePath().replaceAll("\\+", " ") + 
//			"resources/schema/inheritance/gmlsf2composite_and_featcoll.xsd");
			is2 = new FileInputStream("resources/schema/inheritance/gmlsf2composite_and_featcoll.xsd");

//			for (String s : subSchemas) {
//				System.out.println("schemaLocation: " + s);
//			}
			
//			for (int i = subSchemas.size() - 1; i > 0; i--) {
//				InputStream subSchemaInputStream = new FileInputStream(subSchemas.get(i));
//				SchemaFactory.getInstance(null, subSchemaInputStream);
//			}
			
			URI ns = new URI("c:/Humboldt/workspace/HALE_2/resources/schema/inheritance/gmlsf2composite_and_featcoll.xsd");
//			Schema schema2 = SchemaFactory.getInstance(ns, is2);
//			Schema schema = SchemaFactory.getInstance(null, is);
			
//			Application
			
			Schema schema = SchemaFactory.getInstance(null, is2);
			for ( String filename : files.values() ) {
				FileInputStream fis = new FileInputStream(filename);
				Schema subSchema = SchemaFactory.getInstance(null, fis);
			}
			

//			Schema schema = null;
			try {
				SchemaFactory.getInstance(null, is2);
			} catch (Exception uhe) {
				_log.error("Imported Schema only available on-line, but " +
						"cannot be retrieved.", uhe);
			}
			try {
				schema = SchemaFactory.getInstance(null, is);
			} catch (Exception uhe) {
				_log.error("Imported Schema only available on-line, but " +
						"cannot be retrieved.", uhe);
			}

//			Schema[] imports = schema.getImports();
//			for (Schema s : imports) {
//				_log.debug("Imported URI + Name: " + s.getURI() + " " + s.getTargetNamespace());
//			}
						
			Collection<SimpleFeatureType> inTypes = new HashSet<SimpleFeatureType>();

			// Build first a list of FeatureTypes
			for (ComplexType type : schema.getComplexTypes()) {
				SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
				builder.setName(type.getName());
				builder.setNamespaceURI(type.getNamespace());
				builder.setAbstract(type.isAbstract());

				if (type.getParent() != null) {
					System.out.println("Feature type: " + type.getName()
							+ ", parent feature type: "
							+ type.getParent().getName());

					for (Element element : type.getChildElements()) {
						if (element.getType() instanceof SimpleType) {
							builder.add(element.getName(), element.getType()
									.getClass());
						}
						System.out.println("\telement: " + element.getName()
								+ ", " + element.getType().getName());
					}
					inTypes.add(builder.buildFeatureType());
				}
			}

			for (ComplexType type : schema.getComplexTypes()) {
				if (type.getParent() instanceof ComplexType) {
					// Create builder
					SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
					builder.setName(type.getName());
					builder.setNamespaceURI(type.getNamespace());
					builder.setAbstract(type.isAbstract());

					if (type.getParent() != null) {
						System.out.println("Feature type: " + type.getName()
								+ ", parent feature type: "
								+ type.getParent().getName());

						for (Element element : type.getChildElements()) {
							if (element.getType() instanceof SimpleType) {
								// System.out.println("\tsimpl0e type element: "
								// + element.getName());
								builder.add(element.getName(), element
										.getType().getClass());
							}
							System.out.println("\telement: "
									+ element.getName() + ", "
									+ element.getType().getName());
						}

						if (type.getParent().getName().equals(
								"AbstractFeatureType")) {
							builder.setSuperType(null);
						} else {
							for (SimpleFeatureType featureType : inTypes) {
								if (featureType.getName().getLocalPart()
										.equals(type.getParent().getName())) {
									builder.setSuperType(featureType);
									System.out.println("Parent type set to "
											+ featureType.getName());
								}
							}
						}
						collection.add(builder.buildFeatureType());
					}
				}
			}
		} catch (FileNotFoundException e) {
			_log.error(e);
//		} catch (SAXException e) {
//			e.printStackTrace(); // FIXME
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return collection;
	}

	@Override
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
}
