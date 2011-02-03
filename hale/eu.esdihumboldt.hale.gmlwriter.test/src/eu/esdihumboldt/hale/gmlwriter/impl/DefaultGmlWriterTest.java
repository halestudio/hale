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

package eu.esdihumboldt.hale.gmlwriter.impl;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.geotools.feature.AttributeImpl;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureImpl;
import org.geotools.feature.GeometryAttributeImpl;
import org.geotools.feature.type.AttributeDescriptorImpl;
import org.geotools.feature.type.GeometryDescriptorImpl;
import org.geotools.filter.identity.FeatureIdImpl;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.identity.Identifier;
import org.xml.sax.SAXException;

import eu.esdihumboldt.hale.gmlparser.CstFeatureCollection;
import eu.esdihumboldt.hale.gmlparser.GmlHelper;
import eu.esdihumboldt.hale.gmlparser.GmlHelper.ConfigurationType;
import eu.esdihumboldt.hale.schemaprovider.Schema;
import eu.esdihumboldt.hale.schemaprovider.SchemaProvider;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;
import eu.esdihumboldt.hale.schemaprovider.provider.ApacheSchemaProvider;
import eu.esdihumboldt.tools.FeatureInspector;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link DefaultGmlWriter}
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class DefaultGmlWriterTest {
	
	/**
	 * If temporary files shall be deleted
	 */
	private static final boolean DEL_TEMP_FILES = false;

	/**
	 * Test writing a simple feature from a simple schema
	 * 
	 * @throws Exception if any error occurs 
	 */
	@Test
	public void testSimpleWrite() throws Exception {
		SchemaProvider sp = new ApacheSchemaProvider();
		
		// load the sample schema
		Schema schema = sp.loadSchema(getClass().getResource("/data/sample_wva/wfs_va.xsd").toURI(), null);
		
		FeatureCollection<FeatureType, Feature> fc = new CstFeatureCollection();
		
		// create feature
		Feature feature = createFeature(schema.getElements().values().iterator().next().getType());
		
		// set some values
		FeatureInspector.setPropertyValue(feature, Arrays.asList("LENGTH"), 10.2);
		FeatureInspector.setPropertyValue(feature, Arrays.asList("NAME"), "Test");
		
		fc.add(feature );
		
		// write to file
		DefaultGmlWriter writer = new DefaultGmlWriter();
		File outFile = File.createTempFile("simpleWrite", ".gml"); 
		OutputStream out = new FileOutputStream(outFile);
		try {
			writer.writeFeatures(fc, schema, out );
		} finally {
			out.flush();
			out.close();
		}
		
		//XXX
		if (!DEL_TEMP_FILES && Desktop.isDesktopSupported()) {
			Desktop.getDesktop().open(outFile);
		}
		
		URI schemaLocation = schema.getLocation().toURI();
		validate(schemaLocation, outFile.toURI());
		
		FeatureCollection<FeatureType, Feature> loaded = loadGML(outFile, schemaLocation);
		
		assertEquals(1, loaded.size());
		
		Feature l = loaded.iterator().next();
		//XXX conversion?
		assertEquals("10.2", FeatureInspector.getPropertyValue(l, Arrays.asList("LENGTH"), null));
		assertEquals("Test", FeatureInspector.getPropertyValue(l, Arrays.asList("NAME"), null));
		
		if (DEL_TEMP_FILES) {
			outFile.deleteOnExit();
		}
	}

	/**
	 * Load GML from a file
	 * 
	 * @param gmlFile the GML file 
	 * @param schemaLocation the schema location
	 * @return the features
	 * @throws IOException if loading the file fails
	 */
	private FeatureCollection<FeatureType, Feature> loadGML(File gmlFile, URI schemaLocation) throws IOException {
		InputStream in = new FileInputStream(gmlFile);
		ConfigurationType type;
		try {
			type = GmlHelper.determineVersion(in, ConfigurationType.GML3);
		} finally {
			in.close();
		}
		
		in = new FileInputStream(gmlFile);
		try {
			return GmlHelper.loadGml(in, type, schemaLocation);
		} finally {
			in.close();
		}
	}

	/**
	 * Validate an XML file against a schema
	 * 
	 * @param schemaLocation the location of the schema
	 * @param xmlLocation the location of the xml file
	 * @throws IOException if I/O operations fail
	 * @throws MalformedURLException if a wrong URI is given 
	 * @throws SAXException if validation fails
	 */
	private void validate(URI schemaLocation, URI xmlLocation) throws MalformedURLException, IOException, SAXException {
		javax.xml.validation.Schema schema;
		try {
			// create a SchemaFactory capable of understanding WXS schemas
		    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	
		    // load a WXS schema, represented by a Schema instance
		    Source schemaFile = new StreamSource(schemaLocation.toURL().openStream());
		    schema = factory.newSchema(schemaFile);
		} catch (Exception e) {
			System.err.println("Parsing the schema for validation failed");
			e.printStackTrace();
			return;
		}

	    // create a Validator instance, which can be used to validate an instance document
	    Validator validator = schema.newValidator();

	    // validate the XML document
        validator.validate(new StreamSource(xmlLocation.toURL().openStream()));
	}

	private Feature createFeature(TypeDefinition type) {
		Collection<Property> properties = new HashSet<Property>();
		SimpleFeatureType targetType = ((SimpleFeatureType) type.getFeatureType());
		for (AttributeDescriptor ad : targetType.getAttributeDescriptors()) {
			Identifier id = new FeatureIdImpl(ad.getLocalName());
			// create normal AttributeImpls
			if (ad instanceof GeometryDescriptorImpl) {
				properties.add(new GeometryAttributeImpl(
						null, (GeometryDescriptor)ad, id));
			}
			else if (ad instanceof AttributeDescriptorImpl) {
				properties.add(new AttributeImpl(null, ad, id));
			}
		}
		return new FeatureImpl(properties, targetType, 
					new FeatureIdImpl(UUID.randomUUID().toString()));
	}
	
}
