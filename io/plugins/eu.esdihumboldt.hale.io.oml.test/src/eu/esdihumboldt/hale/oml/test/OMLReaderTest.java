package eu.esdihumboldt.hale.oml.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Iterator;

import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeIndex;
import eu.esdihumboldt.hale.io.oml.OmlReader;
import eu.esdihumboldt.hale.io.xsd.reader.XmlSchemaReader;

public class OMLReaderTest {
	
	static Alignment alignment = null;
	
	@BeforeClass
	public static void load() throws MarshalException, ValidationException, IOProviderConfigurationException, IOException, MappingException, URISyntaxException {
		alignment = loadAlignment(
				OMLReaderTest.class.getResource("/testdata/testOML/t2.xsd").toURI(), 
				OMLReaderTest.class.getResource("/testdata/testOML/t2.xsd").toURI(), 
				OMLReaderTest.class.getResource("/testdata/testOML/testOMLmapping.goml").toURI());
	}
	
	@Test
	public void testOMLreader() throws Exception {
		
		assertNotNull(alignment);
	}
	
	@Test
	public void testCellCount() {
		
		Collection<? extends Cell> cells = alignment.getCells();
		
		assertEquals(2, cells.size());
	}
	
	@Test
	public void testFunction() {
		
		Collection<? extends Cell> cells = alignment.getCells();
		
		Iterator<? extends Cell> it = cells.iterator();
		
		Cell cell1 = it.next();
		Cell cell2 = it.next();
		
		assertEquals("eu.esdihumboldt.hale.align.retype", cell1.getTransformationIdentifier());
		assertEquals("eu.esdihumboldt.hale.align.formattedstring", cell2.getTransformationIdentifier());
		
	}
	
	private static Alignment loadAlignment(URI sourceSchemaLocation, 
			URI targetSchemaLocation, final URI alignmentLocation) throws IOProviderConfigurationException, IOException, MarshalException, ValidationException, MappingException, URISyntaxException {
		
		// load source schema
		Schema source = readXMLSchema(new DefaultInputSupplier(sourceSchemaLocation));
		
		// load target schema
		Schema target = readXMLSchema(new DefaultInputSupplier(targetSchemaLocation));


		OmlReader reader = new OmlReader();
		
		reader.setSourceSchema(source);
		reader.setTargetSchema(target);
		reader.setSource(new DefaultInputSupplier(alignmentLocation));
		
		reader.validate();
		
		IOReport report = reader.execute(null);
		
		assertTrue(report.isSuccess());
		
		return reader.getAlignment();
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
	private static Schema readXMLSchema(LocatableInputSupplier<? extends InputStream> input) throws IOProviderConfigurationException, IOException {
		XmlSchemaReader reader = new XmlSchemaReader();
		reader.setSharedTypes(new DefaultTypeIndex());
		reader.setSource(input);
		
		reader.validate();
		IOReport report = reader.execute(null);
		
		assertTrue(report.isSuccess());
		assertTrue("Errors are contained in the report", report.getErrors().isEmpty());
		
		return reader.getSchema();
	}
}
