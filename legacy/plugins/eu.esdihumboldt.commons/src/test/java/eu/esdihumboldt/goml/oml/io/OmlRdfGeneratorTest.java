/*
 * HUMBOLDT: A Framework for Data Harmonistation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2008 to 2010.
 */
package eu.esdihumboldt.goml.oml.io;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.exceptions.ConfigurationException;
import org.custommonkey.xmlunit.jaxp13.Validator;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import eu.esdihumboldt.commons.goml.align.Alignment;
import eu.esdihumboldt.commons.goml.oml.io.OmlRdfGenerator;
import eu.esdihumboldt.commons.goml.oml.io.OmlRdfReader;
import eu.esdihumboldt.utilities.TestUtilities;

/**
 * @author Anna Pitaev, Logica Mark Doyle, Logica
 * 
 */
public class OmlRdfGeneratorTest {
	/**
	 * Logger for this class
	 */
	private static final Logger LOG = Logger
			.getLogger(OmlRdfGeneratorTest.class);

	/**
	 * The schema used in the tests. TODO Externalise.
	 */
	private static final String OML_ALIGNMENT_SCHEMA = "align.xsd";

	/**
	 * The OML source file used in the tests. TODO Externalise.
	 */
	private static final String TEST_OML_SOURCE_FILE = "test_newFilterOML_TR.xml";

	/**
	 * File name to serialise xml instance to. TODO Externalise.
	 */
	private static final String TEST_GENERATED_OML_FILE = "testWriteFromReadOmlGenerated.xml";

	/**
	 * If we want to keep a copy of the generated xml instance after the test
	 * set this to true. TODO externalise!
	 */
	private boolean keepGeneratedFile = true;

	/**
	 * Temporary folder used by the tests. Cleaned up after the tests have run.
	 */
	@Rule
	public static TemporaryFolder TEMP_FOLDER = new TemporaryFolder();

	/**
	 * Single static method run once before any tests. Used to do any setup
	 * common to all tests.
	 */
	@BeforeClass
	public static void setUpParsers() {
		// Set up which XML classes we will use for the test.
		XMLUnit.setControlParser("org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
		XMLUnit.setTestParser("org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
		XMLUnit.setSAXParserFactory("org.apache.xerces.jaxp.SAXParserFactoryImpl");
		XMLUnit.setIgnoreWhitespace(true);
	}

	/**
	 * Tests the softwares ability to read an OML file in and serialise it back
	 * to a file. The schema is validated and serialised xml instance is
	 * validated against the schema.
	 * 
	 * @throws URISyntaxException
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 * @throws ConfigurationException
	 * @throws SAXException
	 * @throws MalformedURLException
	 */
	@SuppressWarnings("unchecked")
	// XMLUnit uses a raw List
	@Test
	public final void testWriteFromReadOML() throws URISyntaxException,
			JAXBException, FileNotFoundException, ConfigurationException,
			SAXException, MalformedURLException {
		// Read in the test file.
		LOG.trace("Reading " + TEST_OML_SOURCE_FILE + " test file");
		URI uri = new URI(OmlRdfGeneratorTest.class.getResource(
				OmlRdfGeneratorTest.TEST_OML_SOURCE_FILE).getFile());
		Alignment alignment = new OmlRdfReader().read(new URL("file", null, uri
				.getPath()));

		// Serialise the alignment instance to an xml file.
		LOG.trace("Generating and writing oml file");
		OmlRdfGenerator omlGenerator = new OmlRdfGenerator();
		String xmlGenerationPath = TEMP_FOLDER.getRoot().getPath() + "/"
				+ OmlRdfGeneratorTest.TEST_GENERATED_OML_FILE;
		omlGenerator.write(alignment, xmlGenerationPath);

		// If we want a copy (for debug purposes) copy the file to the user
		// home. Required because the @Rule for TEMP_FOLDER
		// cleans up after the test
		if (keepGeneratedFile) {
			TestUtilities.copyTo(new File(xmlGenerationPath),
					new File(System.getProperty("user.home")
							+ "/testWriteFromReadOmlGeneratedInstance.xml"));
		}

		// Create an XML validator
		LOG.trace("Validating generated xml");
		Validator validator = new Validator();

		// Get the schema file.
		File schemaFile = new File(getClass().getResource(
				"/schema/" + OmlRdfGeneratorTest.OML_ALIGNMENT_SCHEMA).toURI());
		assertTrue("Checking if the schema file exists", schemaFile.exists());
		assertTrue("Checking if we can read the schema file",
				schemaFile.canRead());

		// If tracing is enabled print out the schema file to the console.
		// Useful for debugging.
		if (LOG.isTraceEnabled()) {
			TestUtilities.printFileToConsole(schemaFile);
		}

		// Add the schema to the validator, any XML instances are now validated
		// against this schema.
		validator.addSchemaSource(new StreamSource(schemaFile));

		// Validate the XML schema.
		assertTrue("The schema is invalid", validator.isSchemaValid());

		// Validate the XML instance, if the instance is invalid print out the
		// reasons why.
		StreamSource generatedXmlStreamSource = new StreamSource(
				xmlGenerationPath);
		boolean instanceValid = validator
				.isInstanceValid(generatedXmlStreamSource);
		if (!instanceValid) {
			List<SAXParseException> errors = validator
					.getInstanceErrors(generatedXmlStreamSource);
			LOG.info("There were " + errors.size() + " parse errors");
			for (SAXParseException e : errors) {
				LOG.info("Line:" + e.getLineNumber() + " Column:"
						+ e.getColumnNumber() + " - " + e);
			}
		}

		// Note: XMLUnit asserts don't work with these validators so I must use
		// the returned boolean
		// Not the prettiest but it works.
		assertTrue(
				"The generated xml instance is invalid.  Errors have been logged as per log4j configuration",
				instanceValid);
		// Assert that the source and generated XML are semantically equivalent.
		// XMLAssert.assertXMLEqual("The XML is not equivalent", new
		// InputSource(sourceOmlUri.getPath()), new
		// InputSource(xmlGenerationPath));
	}

}
