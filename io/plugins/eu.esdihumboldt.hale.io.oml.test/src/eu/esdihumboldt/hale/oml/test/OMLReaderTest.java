package eu.esdihumboldt.hale.oml.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.ListMultimap;

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
	static Alignment alignment2 = null;
	static Alignment alignment3 = null;

	@BeforeClass
	public static void load() throws MarshalException, ValidationException,
			IOProviderConfigurationException, IOException, MappingException,
			URISyntaxException {

		alignment = loadAlignment(
				OMLReaderTest.class.getResource("/testdata/testOML/t2.xsd")
						.toURI(),
				OMLReaderTest.class.getResource("/testdata/testOML/t2.xsd")
						.toURI(),
				OMLReaderTest.class.getResource(
						"/testdata/testOML/testOMLmapping.goml").toURI());

		alignment2 = loadAlignment(
				OMLReaderTest.class.getResource(
						"/testdata/sample_wva/wfs_va.xsd").toURI(),
				OMLReaderTest.class
						.getResource(
								"/testdata/sample_wva/inspire3/HydroPhysicalWaters.xsd")
						.toURI(),
				OMLReaderTest.class.getResource(
						"/testdata/sample_wva/watercourse_va.xml.goml").toURI());

		alignment3 = loadAlignment(
				OMLReaderTest.class.getResource(
						"/testdata/aaa2inspire_cp/NAS_6.0.1/schema/aaa.xsd")
						.toURI(),
				OMLReaderTest.class
						.getResource(
								"/testdata/aaa2inspire_cp/inspire3/CadastralParcels.xsd")
						.toURI(),
				OMLReaderTest.class.getResource(
						"/testdata/aaa2inspire_cp/aaa2inspire_cp.xml.goml")
						.toURI());

	}

	@Test
	public void testOMLreader() throws Exception {

		assertNotNull(alignment);
		assertNotNull(alignment2);
		assertNotNull(alignment3);
	}

	@Test
	public void testCellCount() {

		Collection<? extends Cell> cells = alignment.getCells();
		Collection<? extends Cell> cells2 = alignment2.getCells();
		Collection<? extends Cell> cells3 = alignment3.getCells();

		assertEquals(2, cells.size());
		assertEquals(11, cells2.size());
		assertEquals(33, cells3.size());
	}

	@Test
	public void testFunction() {

		Collection<? extends Cell> cells = alignment.getCells();
		Collection<? extends Cell> cells2 = alignment2.getCells();
		Collection<? extends Cell> cells3 = alignment3.getCells();

		Iterator<? extends Cell> it = cells.iterator();
		Iterator<? extends Cell> it2 = cells2.iterator();
		Iterator<? extends Cell> it3 = cells3.iterator();

		// simple test for the formatted string translation
		
		Cell cell0 = it.next();
		Cell cell1 = it.next();

		assertEquals("eu.esdihumboldt.hale.align.retype",
				cell0.getTransformationIdentifier());
		assertEquals("eu.esdihumboldt.hale.align.formattedstring",
				cell1.getTransformationIdentifier());

		ListMultimap<String, String> params = cell1
				.getTransformationParameters();
		List<String> values = params.get("pattern");

		assertEquals("{id}-xxx-{details.address.street}", values.get(0));
		
		
		// simple test for the mapping classification translation
		
		Cell cell2 = null;
		
		while(it2.hasNext()) {
			Cell temp = it2.next();
			
			if(temp.getTransformationIdentifier().equals("eu.esdihumboldt.hale.align.classification")) {
				cell2 = temp;
				break;
			}
		}
		
		ListMultimap<String, String> params2 = cell2
				.getTransformationParameters();
		List<String> values2 = params2.get("classificationMapping");
		
		for(int i=0; i < values2.size(); i++) {
			String temp = values2.get(i);
			
			if(i==0) {
				assertEquals("onGroundSurface 3", temp);
			}
			if(i==1) {
				assertEquals("suspendedOrElevated 2", temp);
			}
			if(i==2) {
				assertEquals("underground 1", temp);
			}
			 
		}

		// extended test for the formated string translation
		
		Cell cell3 = null;
		
		while(it3.hasNext()) {
			Cell temp = it3.next();
			
			if(temp.getTransformationIdentifier().equals("eu.esdihumboldt.hale.align.formattedstring")) {
				cell3 = temp;
				break;
			}
		}
		
		ListMultimap<String, String> params3 = cell3
				.getTransformationParameters();
		List<String> values3 = params3.get("pattern");
		
		assertEquals("{flurstuecksnummer.AX_Flurstuecksnummer.zaehler}/{flurstuecksnummer.AX_Flurstuecksnummer.nenner}", values3.get(0));
		
	}

	private static Alignment loadAlignment(URI sourceSchemaLocation,
			URI targetSchemaLocation, final URI alignmentLocation)
			throws IOProviderConfigurationException, IOException,
			MarshalException, ValidationException, MappingException,
			URISyntaxException {

		// load source schema
		Schema source = readXMLSchema(new DefaultInputSupplier(
				sourceSchemaLocation));

		// load target schema
		Schema target = readXMLSchema(new DefaultInputSupplier(
				targetSchemaLocation));

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
	 * @param input
	 *            the input supplier
	 * @return the schema
	 * @throws IOProviderConfigurationException
	 *             if the configuration of the reader is invalid
	 * @throws IOException
	 *             if reading the schema fails
	 */
	private static Schema readXMLSchema(
			LocatableInputSupplier<? extends InputStream> input)
			throws IOProviderConfigurationException, IOException {
		XmlSchemaReader reader = new XmlSchemaReader();
		reader.setSharedTypes(new DefaultTypeIndex());
		reader.setSource(input);

		reader.validate();
		IOReport report = reader.execute(null);

		assertTrue(report.isSuccess());
		assertTrue("Errors are contained in the report", report.getErrors()
				.isEmpty());

		return reader.getSchema();
	}
}
