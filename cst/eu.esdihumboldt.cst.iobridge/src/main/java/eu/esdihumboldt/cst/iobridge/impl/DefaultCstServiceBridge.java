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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.xml.stream.XMLStreamException;

import org.deegree.cs.exceptions.TransformationException;
import org.deegree.cs.exceptions.UnknownCRSException;
import org.geotools.feature.FeatureCollection;
import org.geotools.gml3.GMLConfiguration;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.cst.iobridge.CstServiceBridge;
import eu.esdihumboldt.cst.transformer.service.CstServiceFactory;
import eu.esdihumboldt.gmlhandler.GmlHandler;
import eu.esdihumboldt.gmlhandler.gt2deegree.GtToDgConvertor;
import eu.esdihumboldt.goml.align.Alignment;
import eu.esdihumboldt.goml.oml.io.OmlRdfReader;
import eu.esdihumboldt.hale.gmlparser.GmlHelper;
import eu.esdihumboldt.hale.gmlparser.HaleGMLParser;
import eu.esdihumboldt.hale.gmlparser.GmlHelper.ConfigurationType;
import eu.esdihumboldt.hale.schemaprovider.Schema;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;
import eu.esdihumboldt.hale.schemaprovider.provider.ApacheSchemaProvider;

/**
 * This class is the default implementation of the {@link CstServiceBridge}. It
 * expects to get local paths to the schema, the mapping and the GML it has to
 * load and process.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class DefaultCstServiceBridge implements CstServiceBridge {

	private URL outputDirectory;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.esdihumboldt.cst.iobridge.CstServiceBridge#transform(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	public String transform(String schemaFilename, String omlFilename,
			String gmlFilename, String outputFilename)  {

		// perform the transformation
		FeatureCollection<?, ?> result = CstServiceFactory.getInstance()
				.transform(this.loadGml(gmlFilename),
						this.loadMapping(omlFilename),
						this.loadSchema(schemaFilename));

		// encode the transformed data and store it temporarily, return the
		// temporary file location
		
				
		/*try {
			this.encodeGML(result, new URL(outputFilename), schemaFilename);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("Couldn't create output file ",e);
		}*/
			
		/*
		 * 
		 * try { GmlHandler handler =
		 * GmlHandler.getDefaultInstance(schemaFilename,
		 * outputFilename.getPath());
		 * handler.writeFC(GtToDgConvertor.convertGtToDg(result)); } catch
		 * (FileNotFoundException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } catch (XMLStreamException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } catch
		 * (UnknownCRSException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } catch (TransformationException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
		return outputFilename;
	}

	public URL getOutputDir() {
		return outputDirectory;
	}

	public void setOutputDir(URL output) {
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
					"An exception occured when trying to write out GML: ", e);
		}

	}

	private Set<FeatureType> loadSchema(String schemaFilename) {
		ApacheSchemaProvider asp = new ApacheSchemaProvider();
		Set<FeatureType> result = new HashSet<FeatureType>();
		try {
			Schema schema = asp.loadSchema(new URI(schemaFilename), null);
			if (schema != null) {
				for (SchemaElement se : schema.getElements().values()) {
					if (se.getFeatureType() != null) {
						result.add(se.getFeatureType());
					}
				}
			}
		} catch (URISyntaxException e) {
			throw new RuntimeException("Parsing the schema Filename to a URI "
					+ "failed.", e);
		} catch (IOException e) {
			throw new RuntimeException("Reading from the provided schema "
					+ "location failed.", e);
		}
		return result;
	}

	private Alignment loadMapping(String omlFilename) {
		OmlRdfReader reader = new OmlRdfReader();
		Alignment al = reader.read(omlFilename);
		return al;
	}

	private FeatureCollection<FeatureType, Feature> loadGml(String gmlFilename) {
		try {
			// InputStream xml = new FileInputStream(new File(gmlFilename));
			InputStream xml = new URL(gmlFilename).openStream();
			//TODO get source schema location to enable application schema support while parsing
			URI schemaLocation = null;
			//TODO determine GML version to use for parser configuration
			ConfigurationType type = ConfigurationType.GML3;
			return GmlHelper.loadGml(xml, type, schemaLocation);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String transform(String schemaFilename, String omlFilename,
			String gmlFilename) {
		String outputFilename =(this.getClass().getResource("")
				.toExternalForm()
				+ UUID.randomUUID() + ".gml");
		// TODO Auto-generated method stub
		return transform(schemaFilename, omlFilename,
				gmlFilename,  outputFilename);
	}
}
