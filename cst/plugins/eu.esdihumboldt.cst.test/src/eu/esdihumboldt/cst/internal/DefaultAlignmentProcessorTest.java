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

import eu.esdihumboldt.hale.common.align.io.impl.DefaultAlignmentIO;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Transformation;
import eu.esdihumboldt.hale.common.align.transformation.service.AlignmentProcessor;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.DefaultIOReporter;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.Locatable;
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
		assertEquals(5, alignment.getCells().size());
		
		Transformation transformation = processor.process(alignment);
		assertNotNull(transformation);
		
		//TODO check transformation instructions (mainly for completeness in this case)
	}
	
	/**
	 * Test based on a simple mapping with a retype and renames, where high
	 * cardinalities are allowed. 
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testCardinalityRename() throws Exception {
		Alignment alignment = loadAlignment(
				getClass().getResource("/testdata/cardrename/t1.xsd").toURI(), 
				getClass().getResource("/testdata/cardrename/t2.xsd").toURI(), 
				getClass().getResource("/testdata/cardrename/t1t2.xml").toURI());
		
		assertNotNull(alignment);
		assertEquals(5, alignment.getCells().size());
		
		Transformation transformation = processor.process(alignment);
		assertNotNull(transformation);
		
		//TODO check transformation instructions
	}
	
	/**
	 * Test where multiple properties from the source type are mapped to
	 * a sub property of a single property of the target type.
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testPropertyMerge() throws Exception {
		Alignment alignment = loadAlignment(
				getClass().getResource("/testdata/propmerge/t1.xsd").toURI(), 
				getClass().getResource("/testdata/propmerge/t2.xsd").toURI(), 
				getClass().getResource("/testdata/propmerge/t1t2.xml").toURI());
		
		assertNotNull(alignment);
		assertEquals(5, alignment.getCells().size());
		
		Transformation transformation = processor.process(alignment);
		assertNotNull(transformation);
		
		//TODO check transformation instructions
	}
	
	/**
	 * Test where multiple properties from a simple source type are mapped to
	 * a complex property structure in the target type.
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testPropertiesMix() throws Exception {
		Alignment alignment = loadAlignment(
				getClass().getResource("/testdata/propmix/t1.xsd").toURI(), 
				getClass().getResource("/testdata/propmix/t2.xsd").toURI(), 
				getClass().getResource("/testdata/propmix/t1t2.xml").toURI());
		
		assertNotNull(alignment);
		assertEquals(7, alignment.getCells().size());
		
		Transformation transformation = processor.process(alignment);
		assertNotNull(transformation);
		
		//TODO check transformation instructions
	}
	
	/**
	 * Test where multiple properties from a simple source type are mapped to
	 * a complex property structure in the target type. In this case there
	 * are no facts that the decision which way to group the source properties
	 * to an address can be based on.
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testPropertiesMix2() throws Exception {
		Alignment alignment = loadAlignment(
				getClass().getResource("/testdata/propmix2/t1.xsd").toURI(), 
				getClass().getResource("/testdata/propmix2/t2.xsd").toURI(), 
				getClass().getResource("/testdata/propmix2/t1t2.xml").toURI());
		
		assertNotNull(alignment);
		assertEquals(9, alignment.getCells().size());
		
		Transformation transformation = processor.process(alignment);
		assertNotNull(transformation);
		
		//TODO check transformation instructions
	}
	
	/**
	 * Test where properties from a simple source type are mapped to
	 * to complex properties in that target type, with some of the needed 
	 * information being given only implicit through the corresponding
	 * source property.
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testImplicitAssign() throws Exception {
		Alignment alignment = loadAlignment(
				getClass().getResource("/testdata/impassign/t1.xsd").toURI(), 
				getClass().getResource("/testdata/impassign/t2.xsd").toURI(), 
				getClass().getResource("/testdata/impassign/t1t2.xml").toURI());
		
		assertNotNull(alignment);
		/*
		 * FIXME the alignment is still missing the assignments for the language 
		 * values, which must be in correspondence with the related source property
		 */
		assertEquals(5, alignment.getCells().size());
		
		Transformation transformation = processor.process(alignment);
		assertNotNull(transformation);
		
		//TODO check transformation instructions
	}
	
	/**
	 * Test where properties from a simple source type are mapped to
	 * to a complex property with simple content and attributes.
	 * @throws Exception if an error occurs executing the test
	 */
	@Test
	public void testSimpleContentAttribute() throws Exception {
		Alignment alignment = loadAlignment(
				getClass().getResource("/testdata/simpleatt/t1.xsd").toURI(), 
				getClass().getResource("/testdata/simpleatt/t2.xsd").toURI(), 
				getClass().getResource("/testdata/simpleatt/t1t2.xml").toURI());
		
		assertNotNull(alignment);
		assertEquals(7, alignment.getCells().size());
		
		Transformation transformation = processor.process(alignment);
		assertNotNull(transformation);
		
		//TODO check transformation instructions
	}
	
	private Alignment loadAlignment(URI sourceSchemaLocation, 
			URI targetSchemaLocation, final URI alignmentLocation) throws IOProviderConfigurationException, IOException, MarshalException, ValidationException, MappingException {
		// load source schema
		Schema source = readXMLSchema(new DefaultInputSupplier(sourceSchemaLocation));
		
		// load target schema
		Schema target = readXMLSchema(new DefaultInputSupplier(targetSchemaLocation));

		// load alignment
		IOReporter report = new DefaultIOReporter(new Locatable() {
			
			@Override
			public URI getLocation() {
				return alignmentLocation;
			}
		}, "Load alignment", true) {
			
			@Override
			protected String getSuccessSummary() {
				return "Alignment successfully loaded";
			}
			
			@Override
			protected String getFailSummary() {
				return "Failed to load alignment";
			}
		};
		Alignment result = DefaultAlignmentIO.load(alignmentLocation.toURL().openStream(), report , 
				source, target);
		
		assertTrue("Errors are contained in the report", report.getErrors().isEmpty());
		
		return result;
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
