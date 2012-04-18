package eu.esdihumboldt.hale.oml.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.junit.Test;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeIndex;
import eu.esdihumboldt.hale.io.oml.OmlReader;
import eu.esdihumboldt.hale.io.xsd.reader.XmlSchemaReader;

public class OMLReaderTest {
	
	@Test
	public void testOML() throws Exception {
		
		Alignment alignment = loadAlignment(
				getClass().getResource("/testdata/testOML/t2.xsd").toURI(), 
				getClass().getResource("/testdata/testOML/t2.xsd").toURI(), 
				getClass().getResource("/testdata/testOML/testOMLmapping.goml").toURI());
		
		assertNotNull(alignment);
	}
	
	private Alignment loadAlignment(URI sourceSchemaLocation, 
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
	private Schema readXMLSchema(LocatableInputSupplier<? extends InputStream> input) throws IOProviderConfigurationException, IOException {
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
