/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2012.
 */

package eu.esdihumboldt.hale.oml.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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

/**
 * Test for reading OML files.
 * 
 * @author Kevin Mais
 */
@SuppressWarnings("null")
public class OMLReaderTest {

	private static Alignment alignment = null;
	private static Alignment alignment2 = null;
	private static Alignment alignment3 = null;
	private static Alignment alignment4 = null;

	/**
	 * Load the test alignment.
	 * 
	 * @throws Exception
	 *             if an error occurs
	 */
	@BeforeClass
	public static void load() throws Exception {
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
				URI.create("http://hale-test/inspire3/HydroPhysicalWaters.xsd"),
				OMLReaderTest.class.getResource(
						"/testdata/sample_wva/watercourse_va.xml.goml").toURI());

		alignment3 = loadAlignment(
				URI.create("http://hale-test/NAS_6.0.1/schema/aaa.xsd"),
				URI.create("http://hale-test/inspire3/CadastralParcels.xsd"),
				OMLReaderTest.class.getResource(
						"/testdata/aaa2inspire_cp/aaa2inspire_cp.xml.goml")
						.toURI());

		alignment4 = loadAlignment(
				OMLReaderTest.class.getResource(
						"/testdata/watrcrsl/ERM_Watercourse_FME.xsd").toURI(),
				URI.create("http://hale-test/inspire3/HydroPhysicalWaters.xsd"),
				OMLReaderTest.class.getResource(
						"/testdata/watrcrsl/_watrcrsl_inspire.xml.goml")
						.toURI());
	}

	/**
	 * Test if all alignments were read correctly.
	 * 
	 * @throws Exception
	 *             if an error occurs
	 */
	@Test
	public void testOMLreader() throws Exception {
		assertNotNull(alignment);
		assertNotNull(alignment2);
		assertNotNull(alignment3);
		assertNotNull(alignment4);
	}

	/**
	 * Test if the cell count is correct.
	 */
	@Test
	public void testCellCount() {
		Collection<? extends Cell> cells = alignment.getCells();
		Collection<? extends Cell> cells2 = alignment2.getCells();
		Collection<? extends Cell> cells3 = alignment3.getCells();
		Collection<? extends Cell> cells4 = alignment4.getCells();

		assertEquals(2, cells.size());
		assertEquals(11, cells2.size());
		assertEquals(33, cells3.size());
		// TODO: test fails 'cause last cell can't be created -> 17 cells
		assertEquals(18, cells4.size());
	}

	/**
	 * Test for formatted string translation
	 */
	@Test
	public void testFormattedString1() {
		Collection<? extends Cell> cells = alignment.getCells();

		Iterator<? extends Cell> it = cells.iterator();

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

	}

	/**
	 * Extended test for formatted string function
	 */
	@Test
	public void testFormattedString2() {

		Collection<? extends Cell> cells = alignment3.getCells();

		Iterator<? extends Cell> it = cells.iterator();

		Cell cell = null;
		while (it.hasNext()) {
			Cell temp = it.next();

			if (temp.getTransformationIdentifier().equals(
					"eu.esdihumboldt.hale.align.formattedstring")) {
				cell = temp;
				break;
			}
		}
		assertNotNull(cell);

		ListMultimap<String, String> params = cell
				.getTransformationParameters();
		List<String> values = params.get("pattern");

		assertEquals(
				"{flurstuecksnummer.AX_Flurstuecksnummer.zaehler}/{flurstuecksnummer.AX_Flurstuecksnummer.nenner}",
				values.get(0));
	}

	/**
	 * Test for classification mapping function
	 */
	@Test
	public void testClassificationMapping() {

		Collection<? extends Cell> cells = alignment2.getCells();

		Iterator<? extends Cell> it = cells.iterator();

		Cell cell = null;
		while (it.hasNext()) {
			Cell temp = it.next();

			if (temp.getTransformationIdentifier().equals(
					"eu.esdihumboldt.hale.align.classification")) {
				cell = temp;
				break;
			}
		}

		ListMultimap<String, String> params = cell
				.getTransformationParameters();
		List<String> values = params.get("classificationMapping");

		for (int i = 0; i < values.size(); i++) {
			String temp = values.get(i);

			if (i == 0) {
				assertEquals("onGroundSurface 3", temp);
			}
			if (i == 1) {
				assertEquals("suspendedOrElevated 2", temp);
			}
			if (i == 2) {
				assertEquals("underground 1", temp);
			}
		}
	}

	private static Alignment loadAlignment(URI sourceSchemaLocation,
			URI targetSchemaLocation, final URI alignmentLocation)
			throws IOProviderConfigurationException, IOException {

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
