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

package eu.esdihumboldt.cst.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.junit.Test;

import eu.esdihumboldt.hale.common.align.io.DefaultAlignmentIO;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Transformation;
import eu.esdihumboldt.hale.common.align.transformation.service.AlignmentProcessor;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeIndex;
import eu.esdihumboldt.hale.io.xsd.reader.XmlSchemaReader;

/**
 * Tests for the CST's alignment processor implementation
 * @author Simon Templer
 */
public class DefaultAlignmentProcessorTest {

	@SuppressWarnings("restriction")
	private AlignmentProcessor processor = new DefaultAlignmentProcessor();
	
	/**
	 * Test based on a very simple mapping with a retype and renames. 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testSimpleRename() throws Exception {
		Alignment alignment = loadAlignment(
				getClass().getResource("/testdata/simplerename/t1.xsd").toURI(), 
				getClass().getResource("/testdata/simplerename/t2.xsd").toURI(), 
				getClass().getResource("/testdata/simplerename/t1t2.xml").toURI());
		
		assertNotNull(alignment);
		assertEquals(4, alignment.getCells().size());
		
		Transformation transformation = processor.process(alignment);
		assertNotNull(transformation);
		
		//TODO check transformation instructions (mainly for completeness in this case)
	}
	
	private Alignment loadAlignment(URI sourceSchemaLocation, 
			URI targetSchemaLocation, URI alignmentLocation) throws IOProviderConfigurationException, IOException, MarshalException, ValidationException, MappingException {
		// load source schema
		Schema source = readXMLSchema(new DefaultInputSupplier(sourceSchemaLocation));
		
		// load target schema
		Schema target = readXMLSchema(new DefaultInputSupplier(targetSchemaLocation));

		// load alignment
		IOReporter reporter = null; //FIXME use report?!
		return DefaultAlignmentIO.load(alignmentLocation.toURL().openStream(), reporter, 
				source, target);
	}
	
	/**
	 * Reads a XML schema
	 * 
	 * @param input the input supplier
	 * @return the schema
	 * @throws IOProviderConfigurationException if the configuration of the
	 *   reader is invalid
	 * @throws IOException if reading the schema fails
	 */
	private Schema readXMLSchema(LocatableInputSupplier<? extends InputStream> input) throws IOProviderConfigurationException, IOException {
		XmlSchemaReader reader = new XmlSchemaReader();
//		reader.setContentType(XMLSchemaIO.XSD_CT);
		reader.setSharedTypes(new DefaultTypeIndex());
		reader.setSource(input);
		
		reader.validate();
		IOReport report = reader.execute(null);
		
		assertTrue(report.isSuccess());
		assertTrue("Errors are contained in the report", report.getErrors().isEmpty());
		
		return reader.getSchema();
	}

}
