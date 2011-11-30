/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.cst.iobridge.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.geotools.feature.FeatureCollection;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.commons.goml.align.Alignment;
import eu.esdihumboldt.commons.goml.oml.io.OmlRdfReader;
import eu.esdihumboldt.cst.transformer.service.CstServiceFactory;
import eu.esdihumboldt.hale.core.io.ContentType;
import eu.esdihumboldt.hale.core.io.impl.LogProgressIndicator;
import eu.esdihumboldt.hale.core.io.report.IOReport;
import eu.esdihumboldt.hale.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.gmlparser.GmlHelper;
import eu.esdihumboldt.hale.gmlparser.GmlHelper.ConfigurationType;
import eu.esdihumboldt.hale.instance.io.InstanceWriter;
import eu.esdihumboldt.hale.io.gml.writer.GmlInstanceWriterFactory;
import eu.esdihumboldt.hale.schemaprovider.Schema;
import eu.esdihumboldt.hale.schemaprovider.SchemaProvider;
import eu.esdihumboldt.hale.schemaprovider.provider.ApacheSchemaProvider;
import eu.esdihumboldt.specification.cst.align.IAlignment;

/**
 * Transformation with output based on {@link InstanceWriter} 
 * @author Simon Templer
 */
public class CstTransformation {

	/**
	 * Transform a GML source data set to target GML schema
	 * @param sourceData the source data location
	 * @param sourceSchemaLocation the source schema location
	 * @param mappingLocation the mapping location
	 * @param targetSchemaLocation the target schema location
	 * @param outFile the output file
	 * @param srsName the SRS name, may be <code>null</code>
	 * @throws Exception if an error occurs
	 */
	public static void transform(URI sourceData, URI sourceSchemaLocation,
			URI mappingLocation, URI targetSchemaLocation, File outFile, String srsName)
			throws Exception {
		// load both schemas
		SchemaProvider sp = new ApacheSchemaProvider();
		Schema sourceSchema = sp.loadSchema(sourceSchemaLocation, null);
		Schema targetSchema = sp.loadSchema(targetSchemaLocation, null);
		
		// load source data
		FeatureCollection<FeatureType, Feature> fc = loadGML(sourceData, sourceSchema);
		
		// load alignment
		OmlRdfReader reader = new OmlRdfReader();
		Alignment alignment = reader.read(mappingLocation.toURL());
		
		// transform
		FeatureCollection<FeatureType, Feature> result = transform(fc, 
				alignment, targetSchema);
		
		// write
		// write to file
		InstanceWriter writer;
//		if (rootName != null) {
//			writer = new XmlInstanceWriterFactory().createProvider();
//			writer.setParameter(StreamGmlWriter.PARAM_ROOT_ELEMENT_NAMESPACE, rootNs);
//			writer.setParameter(StreamGmlWriter.PARAM_ROOT_ELEMENT_NAME, rootName);
//		}
//		else {
			// GML
			writer = new GmlInstanceWriterFactory().createProvider();
//		}
		
		writer.setContentType(ContentType.getContentType("GML"));
		writer.setCommonSRSName(srsName);
		writer.setInstances(result);
		writer.setTargetSchema(targetSchema);
		writer.setTarget(new FileIOSupplier(outFile));
		
		IOReport report = writer.execute(new LogProgressIndicator());
		//XXX what to do with the report and the messages
//		assertTrue("Writing the GML output not successful", report.isSuccess());
	}
	
	@SuppressWarnings("unchecked")
	private static FeatureCollection<FeatureType, Feature> transform(
			FeatureCollection<FeatureType, Feature> fc, IAlignment alignment, Schema targetSchema) {
		Set<FeatureType> types = new HashSet<FeatureType>(targetSchema.getTypes().values());
		return (FeatureCollection<FeatureType, Feature>) CstServiceFactory.getInstance().transform(fc, alignment, types);
	}
	
	/**
	 * Load GML from a file
	 * 
	 * @param sourceData the GML file 
	 * @param schema the schema location
	 * @return the features
	 * @throws IOException if loading the file fails
	 */
	private static FeatureCollection<FeatureType, Feature> loadGML(URI sourceData, Schema schema) throws IOException {
		InputStream in = sourceData.toURL().openStream();
		ConfigurationType type;
		try {
			type = GmlHelper.determineVersion(in, ConfigurationType.GML3);
		} finally {
			in.close();
		}
		
		in = sourceData.toURL().openStream();
		try {
			return GmlHelper.loadGml(in, type, schema);
		} finally {
			in.close();
		}
	}

}
