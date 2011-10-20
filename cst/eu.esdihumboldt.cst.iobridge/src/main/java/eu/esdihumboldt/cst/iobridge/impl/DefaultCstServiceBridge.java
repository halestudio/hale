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
package eu.esdihumboldt.cst.iobridge.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import org.geotools.feature.FeatureCollection;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.commons.goml.align.Alignment;
import eu.esdihumboldt.commons.goml.oml.io.OmlRdfReader;
import eu.esdihumboldt.cst.iobridge.CstServiceBridge;
import eu.esdihumboldt.cst.iobridge.TransformationException;
import eu.esdihumboldt.cst.transformer.service.CstServiceFactory;
import eu.esdihumboldt.gmlhandler.GmlHandler;
import eu.esdihumboldt.gmlhandler.gt2deegree.GtToDgConvertor;
import eu.esdihumboldt.gmlhandler.gt2deegree.TypeIndex;
import eu.esdihumboldt.hale.gmlparser.GmlHelper;
import eu.esdihumboldt.hale.gmlparser.GmlHelper.ConfigurationType;
import eu.esdihumboldt.hale.schemaprovider.Schema;
import eu.esdihumboldt.hale.schemaprovider.model.Definition;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;
import eu.esdihumboldt.hale.schemaprovider.provider.ApacheSchemaProvider;

import eu.esdihumboldt.hale.cache.Request;

/**
 * This class is the default implementation of the {@link CstServiceBridge}. It
 * expects to get local paths to the schema, the mapping and the GML it has to
 * load and process.
 * 
 * @author Thorsten Reitz, Simon Templer
 * @version $Id$
 */
public class DefaultCstServiceBridge implements CstServiceBridge {

	private String outputDirectory;

	/**
	 * 
	 * @param schemaFilename
	 * @param omlFilename
	 * @param gmlFilename
	 * @param outputFilename
	 * @param sourceSchema
	 * @param sourceVersion
	 * @return
	 * @throws TransformationException
	 */
	@SuppressWarnings("unchecked")
	public String transform(String schemaFilename, String omlFilename,
			String gmlFilename, String outputFilename, String sourceSchema, ConfigurationType sourceVersion) throws TransformationException  {

		Schema schema = this.loadSchema(schemaFilename);
		
		TypeIndex typeIndex = new TypeIndex();
		Map<Definition, FeatureType> types;
		if (schema != null) {
			types = schema.getTypes();
			for (Entry<Definition, FeatureType> entry : types.entrySet()) {
				Definition def = entry.getKey();
				typeIndex.addType((def instanceof SchemaElement)?(((SchemaElement) def).getType()):((TypeDefinition) def));
			}
		}
		else {
			types = new HashMap<Definition, FeatureType>();
		}
		
		// perform the transformation
		FeatureCollection<FeatureType, Feature> result = 
			(FeatureCollection<FeatureType, Feature>) CstServiceFactory.getInstance()
				.transform(this.loadGml(gmlFilename, sourceSchema, sourceVersion),
						this.loadMapping(omlFilename),
						new HashSet<FeatureType>(types.values()));

		// encode the transformed data and store it temporarily, return the
		// temporary file location
		
				
		/*try {
			this.encodeGML(result, new URL(outputFilename), schemaFilename);
		} catch (MalformedURLException e) {
			throw new RuntimeException("Couldn't create temporary output file: ", e);
		}
		//this.encodeGML(result, outputFilename, schemaFilename);
		*/
		
		try {
			GmlHandler handler = GmlHandler.getDefaultInstance(schemaFilename, (new URL(outputFilename)).getFile());
			GtToDgConvertor converter = new GtToDgConvertor(typeIndex);
			org.deegree.feature.FeatureCollection fc = converter.convertGtToDg(result);
			handler.writeFC(fc, schema.getNamespace(), schema.getPrefixes());
		} catch (Exception e) {
			throw new TransformationException(e);
		} 
		
		return outputFilename.toString();
	}

	public String getOutputDir() {
		return outputDirectory;
	}

	public void setOutputDir(String output) {
		this.outputDirectory = output;
	}

	/**
	 * @param result
	 * @param outputPath
	 */
	private void encodeGML(FeatureCollection<?, ?> result, URL outputPath,
			String schemaPath) {
		// serialize out
		try {
			GmlGenerator gmlGenerator = new GmlGenerator(
					GmlGenerator.GmlVersion.gml3.name(), result.getSchema()
							.getName().getNamespaceURI(), schemaPath);
			OutputStream out = new FileOutputStream(new File(outputPath
					.getPath()));

			gmlGenerator.encode(result, out);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(
					"An exception occured when trying to write out GML: ", e); //$NON-NLS-1$
		}

	}

	private Schema loadSchema(String schemaFilename) {
		ApacheSchemaProvider asp = new ApacheSchemaProvider();
		try {
			return asp.loadSchema(new URI(schemaFilename), null);
		} catch (URISyntaxException e) {
			throw new RuntimeException("Parsing the schema Filename to a URI " //$NON-NLS-1$
					+ "failed.", e); //$NON-NLS-1$
		} catch (IOException e) {
			throw new RuntimeException("Reading from the provided schema " //$NON-NLS-1$
					+ "location failed.", e); //$NON-NLS-1$
		}
	}

	/**
	 * Load the mapping
	 * 
	 * @param omlFilename the OML file name
	 * 
	 * @return the mapping
	 */
	private Alignment loadMapping(String omlFilename) {
		OmlRdfReader reader = new OmlRdfReader();
		Alignment al = reader.read(omlFilename);
		return al;
	}

	private FeatureCollection<FeatureType, Feature> loadGml(String gmlFilename, 
			String sourceSchema, ConfigurationType sourceVersion) {
		try {
			InputStream xml = getGMLStream(gmlFilename);
			
			if (sourceVersion == null) {
				// try to determine type from the data
				try {
					sourceVersion = GmlHelper.determineVersion(xml, ConfigurationType.GML3);
				} finally {
					xml.close();
					// reopen stream for parsing
					xml = getGMLStream(gmlFilename);
				}
			}
			
			// get source schema location to enable application schema support while parsing
			URI schemaLocation;
			if (sourceSchema != null) {
				try {
					schemaLocation = new URI(sourceSchema);
				} catch (Exception e) {
					// try filename
					try {
						schemaLocation = new File(sourceSchema).toURI();
					} catch (Exception e1) {
						throw new RuntimeException("Reading from the provided " + //$NON-NLS-1$
								"source schema location failed.", e); //$NON-NLS-1$
					}
				}
			}
			else {
				schemaLocation = null;
			}
			
			return GmlHelper.loadGml(xml, sourceVersion, schemaLocation);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private InputStream getGMLStream(String gmlFilename) throws MalformedURLException,
			IOException {
		// InputStream xml = new FileInputStream(new File(gmlFilename));
		
		try {
			return Request.getInstance().get(gmlFilename);
		}catch (Exception e) {
			return new URL(gmlFilename).openStream();
		}
	}

	/**
	 * @see CstServiceBridge#transform(String, String, String)
	 */
	@Override
	public String transform(String schemaFilename, String omlFilename,
			String gmlFilename) throws TransformationException  {
		return transform(schemaFilename, omlFilename, gmlFilename,
				null, null);
	}
	
	/**
	 * @see CstServiceBridge#transform(String, String, String, String, ConfigurationType)
	 */
	@Override
	public String transform(String schemaFilename, String omlFilename,
			String gmlFilename, String sourceSchema, ConfigurationType sourceVersion) throws TransformationException  {
		String outputFilename;
		
		if (!this.outputDirectory.equals("")) {
			outputFilename = this.outputDirectory + UUID.randomUUID() + ".gml";
		} else {
			outputFilename = (this.getClass().getResource("") //$NON-NLS-1$
					.toExternalForm()
					+ UUID.randomUUID() + ".gml"); //$NON-NLS-1$
		}
		
		return transform(schemaFilename, omlFilename, gmlFilename, outputFilename, sourceSchema, sourceVersion);
	}
}
