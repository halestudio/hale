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

package eu.esdihumboldt.gmlhandler;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import org.apache.log4j.Logger;
import org.deegree.commons.xml.XMLParsingException;
import org.deegree.cs.exceptions.TransformationException;
import org.deegree.cs.exceptions.UnknownCRSException;
import org.deegree.feature.Feature;
import org.deegree.feature.FeatureCollection;
import org.deegree.feature.property.Property;
import org.deegree.feature.types.ApplicationSchema;
import org.deegree.feature.types.FeatureType;
import org.deegree.feature.types.property.PropertyType;
import org.deegree.gml.GMLInputFactory;
import org.deegree.gml.GMLStreamReader;
import org.deegree.gml.GMLVersion;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.vividsolutions.jts.io.gml2.GMLHandler;

/**
 * 
 * Test-class for the GMLHandler functionality.
 * 
 * @author Anna Pitaev
 * @partner 04 / Logica
 * @version $Id$
 */
public class GmlHandlerTest {

	/**
	 * Logger for this class
	 */
	private static final Logger LOG = Logger.getLogger(GmlHandlerTest.class);

	/** application schema location */
	private static final String SCHEMA_LOCATION_GML32 = "urn:x-inspire:specification:gmlas:HydroPhysicalWaters:3.0 http://svn.esdi-humboldt.eu/repo/humboldt2/trunk/cst/eu.esdihumboldt.cst.corefunctions/src/test/resource/eu/esdihumboldt/cst/corefunctions/inspire/inspire_v3.0_xsd"
			+ "HydroPhysicalWaters.xsd";

	/** http-based URL for the application schema */
	private static final String SCHEMA_URL = "http://svn.esdi-humboldt.eu/repo/humboldt2/trunk/cst/eu.esdihumboldt.cst.corefunctions/src/test/resource/eu/esdihumboldt/cst/corefunctions/inspire/inspire_v3.0_xsd/"
			+ "HydroPhysicalWaters.xsd";
	/** source gml location */
	private static final String GML32_INSTANCE_LOCATION = "http://svn.esdi-humboldt.eu/repo/humboldt2/branches/humboldt-deegree3/resource/sourceData/va_target_v3.gml";

	/** generated instance location */
	private static final String GML32_GENERATED_LOCATION = "src/test/resources/va_target_v3_generated.gml";
	
	/** handler to proceed gmldata */
	private static GmlHandler gmlHandler;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// pre-define namespaces
		HashMap<String, String> namespaces = new HashMap<String, String>();
		namespaces.put("gco", "http://www.isotc211.org/2005/gco");
		namespaces.put("gmd", "http://www.isotc211.org/2005/gmd");
		namespaces.put("gn",
				"urn:x-inspire:specification:gmlas:GeographicalNames:3.0");
		namespaces.put("hy-p",
				"urn:x-inspire:specification:gmlas:HydroPhysicalWaters:3.0");
		namespaces.put("hy", "urn:x-inspire:specification:gmlas:HydroBase:3.0");
		namespaces.put("base",
				"urn:x-inspire:specification:gmlas:BaseTypes:3.2");
		namespaces.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		// set up GMLHandler with the test configuration
		gmlHandler = new GmlHandler(GMLVersions.gml3_2_1, SCHEMA_URL,
				namespaces);
		gmlHandler.setGmlUrl(GML32_INSTANCE_LOCATION);
		
		// set target gml destination
		 gmlHandler.setTargetGmlUrl(GML32_GENERATED_LOCATION);

	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * Test method for
	 * {@link eu.esdihumboldt.gmlhandler.GmlHandler#readSchema()}.
	 */
	@Test
	public final void testReadSchema() {
		// read application schema
		try {
			ApplicationSchema schema = gmlHandler.readSchema();
			// validate root FeatureTypes
			FeatureType[] rootFTypes = schema.getRootFeatureTypes();

			assertEquals(17, rootFTypes.length);
			for (FeatureType rootType : rootFTypes) {
				LOG.debug("Root Feature Type : "
						+ rootType.getName().getNamespaceURI() + ":"
						+ rootType.getName().getLocalPart());
			}
			// compair the count of the FeatureTypes
			FeatureType[] ftypes = schema.getFeatureTypes();
			for (FeatureType ftype : ftypes) {
				LOG.debug("Application Schema Type : "
						+ ftype.getName().getNamespaceURI() + ":"
						+ ftype.getName().getLocalPart());
				// validate a single Feature Type
				if (ftype.getName().getLocalPart().equals("Rapids")) {
					assertEquals("hy-p", ftype.getName().getPrefix());
					assertEquals(
							"urn:x-inspire:specification:gmlas:HydroPhysicalWaters:3.0",
							ftype.getName().getNamespaceURI());
					// check parent type
					FeatureType pType = schema.getParentFt(ftype);
					LOG.debug("Parent Typy of hy-p:Rapids is "
							+ pType.getName().toString());
					assertEquals("FluvialPoint", pType.getName().getLocalPart());
					// check property declarations list
					List<PropertyType> pDeclarations = (List<PropertyType>) ftype
							.getPropertyDeclarations();
					assertEquals(8, pDeclarations.size());
					for (PropertyType propType : pDeclarations) {
						LOG.debug("Property List of hy-p:Rapids contains : "
								+ propType.getName().toString());
					}

				}
			}

			assertEquals(49, ftypes.length);

		} catch (MalformedURLException e) {
			LOG.error(e.getStackTrace().toString());
			fail(e.getMessage());
			
		} catch (ClassCastException e) {

			LOG.error(e);
			fail(e.getMessage());
		} catch (ClassNotFoundException e) {

			LOG.error(e);
			fail(e.getMessage());
		} catch (InstantiationException e) {

			LOG.error(e);
			fail(e.getMessage());
		} catch (IllegalAccessException e) {

			LOG.error(e);
			fail(e.getMessage());
		}

	}

	/**
	 * Test method for {@link eu.esdihumboldt.gmlhandler.GmlHandler#readFC()}.
	 */
	@Test
	public final void testReadFC() {
		LOG.info("Reading of source Feature Collection..");
		try {
			URL url = new URL(GML32_INSTANCE_LOCATION);
			// read FeatureCollection
			FeatureCollection fc = null;
			fc = gmlHandler.readFC();

			// check feature collection size
			assertEquals(998, fc.size());
			LOG.info("Feature Collection made up of " + fc.size());
			// validate a single feature with id=Watercourses=VA.942

			Iterator<Feature> fcIterator = fc.iterator();
			LOG.info("Verifying of the single features");
			while (fcIterator.hasNext()) {
				Feature feature = fcIterator.next();
				LOG.debug("Found feature with ID = " + feature.getId());
				if (feature.getId().equals("Watercourses_VA.942")) {
					LOG.info("Verifying feature with feature id = Watercourses_VA.942");
					//check feature name
					assertEquals("Watercourse", feature.getName().getLocalPart());
					//check feature property list
					Property [] props = feature.getProperties();
	                assertEquals(18, props.length);
	                //check  property localType
	                for (Property prop : props){
	                	LOG.debug("Found property " + prop.getName().toString());
	                	if (prop.getName().getLocalPart().equals("localType")){
	                		LOG.info("Verifying of  Property Watercourse:localType");
	                			assertEquals("Fluss, Bach", prop.getValue().toString());
	                			
	                		}
	                }
	                

				}

			}

		} catch (MalformedURLException e) {

			LOG.error(e);
			fail(e.getMessage());

		} catch (XMLParsingException e) {

			LOG.error(e);
			fail(e.getMessage());
		} catch (ClassCastException e) {

			LOG.error(e);
			fail(e.getMessage());
		} catch (XMLStreamException e) {
			LOG.error(e);
			fail(e.getMessage());
		} catch (FactoryConfigurationError e) {
			LOG.error(e);
			fail(e.getMessage());
		} catch (IOException e) {
			LOG.error(e);
			fail(e.getMessage());
		} catch (ClassNotFoundException e) {
			LOG.error(e);
			fail(e.getMessage());
		} catch (InstantiationException e) {
			LOG.error(e);
			fail(e.getMessage());
		} catch (IllegalAccessException e) {
			LOG.error(e);
			fail(e.getMessage());
		} catch (UnknownCRSException e) {
			LOG.error(e);
			fail(e.getMessage());
		}

	}

	/**
	 * Test method for
	 * {@link eu.esdihumboldt.gmlhandler.GmlHandler#writeFC(org.deegree.feature.FeatureCollection)}
	 * .
	 */
	@Test
	public final void testWriteFC() {
		
		// TODO provide XUNIT-based testing
		// TODO clean up generated file after 
		
		try {
			gmlHandler.writeFC(gmlHandler.readFC());
		} catch (XMLParsingException e) {
			LOG.error(e);
			fail(e.getMessage());
		} catch (FileNotFoundException e) {
			LOG.error(e);
			fail(e.getMessage());
		} catch (ClassCastException e) {
			LOG.error(e);
			fail(e.getMessage());
		} catch (XMLStreamException e) {
			LOG.error(e);
			fail(e.getMessage());
		} catch (UnknownCRSException e) {
			LOG.error(e);
			fail(e.getMessage());
		} catch (TransformationException e) {
			LOG.error(e);
			fail(e.getMessage());
		} catch (FactoryConfigurationError e) {
			LOG.error(e);
			fail(e.getMessage());
		} catch (IOException e) {
			LOG.error(e);
			fail(e.getMessage());
		} catch (ClassNotFoundException e) {
			LOG.error(e);
			fail(e.getMessage());
		} catch (InstantiationException e) {
			LOG.error(e);
			fail(e.getMessage());
		} catch (IllegalAccessException e) {
			LOG.error(e);
			fail(e.getMessage());
		}
		

	}

}
