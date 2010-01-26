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
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.geotools.feature.FeatureCollection;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.cst.iobridge.CstServiceBridge;
import eu.esdihumboldt.cst.transformer.service.CstServiceFactory;
import eu.esdihumboldt.goml.align.Alignment;
import eu.esdihumboldt.goml.oml.io.OmlRdfReader;
import eu.esdihumboldt.hale.schemaprovider.Schema;
import eu.esdihumboldt.hale.schemaprovider.provider.ApacheSchemaProvider;

/**
 * This class is the default implementation of the {@link CstServiceBridge}. It
 * expects to get local paths to the schema, the mapping and the GML it has to 
 * load and process.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class DefaultCstServiceBridge 
	implements CstServiceBridge {

	/* (non-Javadoc)
	 * @see eu.esdihumboldt.cst.iobridge.CstServiceBridge#transform(java.lang.String, java.lang.String, java.lang.String)
	 */
	public String transform(String schemaFilename, String omlFilename,
			String gmlFilename) {

		// perform the transformation
		FeatureCollection<?, ?> result = CstServiceFactory.getInstance().transform(
				this.loadGml(gmlFilename), 
				this.loadMapping(omlFilename), 
				this.loadSchema(schemaFilename));
		
		// encode the transformed data and store it temporarily, return the temporary file location
		URL outputFilename = this.getClass().getResource("temp/" 
				+ UUID.randomUUID() + ".gml");
		this.encodeGML(result, outputFilename, schemaFilename);
		return outputFilename.toString();
	}
	
	/**
	 * @param result
	 * @param outputPath
	 */
	private void encodeGML(FeatureCollection<?, ?> result, URL outputPath, String schemaPath) {
		// serialize out
		try {
			GmlGenerator gmlGenerator = new GmlGenerator(
					GmlGenerator.GmlVersion.gml3.name(), 
					result.getSchema().getName().getNamespaceURI(), 
					schemaPath);
			OutputStream out = new FileOutputStream(
					new File(outputPath.toString()));
			gmlGenerator.encode(result, out);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
	private Set<FeatureType> loadSchema(String schemaFilename) {
		ApacheSchemaProvider asp = new ApacheSchemaProvider();
		Set<FeatureType> result = new HashSet<FeatureType>();
		try {
			Schema schema = asp.loadSchema(new URI(schemaFilename));
			if (schema != null) {
				result.addAll(schema.getFeatureTypes());
			}
		} catch (URISyntaxException e) {
			throw new RuntimeException("Parsing the schema Filename to a URI " +
					"failed.", e);
		}
		return result;
	}
	
	private Alignment loadMapping(String omlFilename) {
		OmlRdfReader reader = new OmlRdfReader();
		return reader.read(omlFilename);
	}

	private FeatureCollection<?, ?> loadGml(String gmlFilename) {
		return null;
	}
}
