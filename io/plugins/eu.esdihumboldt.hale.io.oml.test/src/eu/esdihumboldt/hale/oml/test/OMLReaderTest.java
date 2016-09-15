/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.oml.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
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
@Ignore
public class OMLReaderTest {

	private static Alignment alignment = null;
	private static Alignment alignment2 = null;
	private static Alignment alignment3 = null;
	private static Alignment alignment4 = null;
	private static Alignment alignment5 = null;
	private static Alignment alignment6 = null;
	private static Alignment alignment7 = null;

	/**
	 * Load the test alignment.
	 * 
	 * @throws Exception if an error occurs
	 */
	@BeforeClass
	public static void load() throws Exception {
		alignment = loadAlignment(
				OMLReaderTest.class.getResource("/testdata/testOML/t2.xsd").toURI(),
				OMLReaderTest.class.getResource("/testdata/testOML/t2.xsd").toURI(),
				OMLReaderTest.class.getResource("/testdata/testOML/testOMLmapping.goml").toURI());

		alignment2 = loadAlignment(
				OMLReaderTest.class.getResource("/testdata/sample_wva/wfs_va.xsd").toURI(),
				URI.create("http://hale-test/inspire3/HydroPhysicalWaters.xsd"), OMLReaderTest.class
						.getResource("/testdata/sample_wva/watercourse_va.xml.goml").toURI());

		alignment3 = loadAlignment(URI.create("http://hale-test/NAS_6.0.1/schema/aaa.xsd"),
				URI.create("http://hale-test/inspire3/CadastralParcels.xsd"), OMLReaderTest.class
						.getResource("/testdata/aaa2inspire_cp/aaa2inspire_cp.xml.goml").toURI());

		alignment4 = loadAlignment(
				OMLReaderTest.class.getResource("/testdata/watrcrsl/ERM_Watercourse_FME.xsd")
						.toURI(),
				URI.create("http://hale-test/inspire3/HydroPhysicalWaters.xsd"), OMLReaderTest.class
						.getResource("/testdata/watrcrsl/_watrcrsl_inspire.xml.goml").toURI());

		alignment5 = loadAlignment(
				OMLReaderTest.class.getResource("/testdata/dkm_inspire/KA_14168_EPSG25833.xsd")
						.toURI(),
				URI.create("http://hale-test/inspire3/CadastralParcels.xsd"), OMLReaderTest.class
						.getResource("/testdata/dkm_inspire/mapping_dkm_inspire.xml.goml").toURI());

		alignment6 = loadAlignment(
				OMLReaderTest.class.getResource("/testdata/testOML/hydroEx.xsd").toURI(),
				URI.create("http://hale-test/inspire3/HydroPhysicalWaters.xsd"),
				OMLReaderTest.class.getResource("/testdata/testOML/test3.xml.goml").toURI());

		alignment7 = loadAlignment(
				OMLReaderTest.class.getResource("/testdata/testOML/hydroEx.xsd").toURI(),
				URI.create("http://hale-test/inspire3/HydroPhysicalWaters.xsd"),
				OMLReaderTest.class.getResource("/testdata/testOML/test.xml.goml").toURI());
	}

	/**
	 * Test if all alignments were read correctly.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testOMLreader() throws Exception {
		assertNotNull(alignment);
		assertNotNull(alignment2);
		assertNotNull(alignment3);
		assertNotNull(alignment4);
		assertNotNull(alignment5);
		assertNotNull(alignment6);
		assertNotNull(alignment7);
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
		Collection<? extends Cell> cells5 = alignment5.getCells();
		Collection<? extends Cell> cells6 = alignment6.getCells();
		Collection<? extends Cell> cells7 = alignment7.getCells();

		assertEquals(4, cells.size());
		assertEquals(11, cells2.size());
		assertEquals(33, cells3.size());
		assertEquals(18, cells4.size());
		assertEquals(51, cells5.size());
		assertEquals(2, cells6.size());
		assertEquals(2, cells7.size());
	}

	/**
	 * Test for formatted string translation in aligment
	 */
	@Test
	public void testFormattedString1() {
		Collection<? extends Cell> cells = alignment.getCells();

		Iterator<? extends Cell> it = cells.iterator();

		Cell cell = null;
		while (it.hasNext()) {
			Cell temp = it.next();

			if (temp.getTransformationIdentifier()
					.equals("eu.esdihumboldt.hale.align.formattedstring")) {
				cell = temp;
				break;
			}
		}

		ListMultimap<String, ParameterValue> params = cell.getTransformationParameters();
		List<ParameterValue> values = params.get("pattern");

		assertEquals(1, values.size());
		// size is 1, so "get(0)" works fine
		assertEquals("{id}-xxx-{details.address.street}", values.get(0).getValue());

	}

	/**
	 * Extended test for formatted string function in alignment3
	 */
	@Test
	public void testFormattedString2() {

		Collection<? extends Cell> cells = alignment3.getCells();

		Iterator<? extends Cell> it = cells.iterator();

		Cell cell = null;
		while (it.hasNext()) {
			Cell temp = it.next();

			if (temp.getTransformationIdentifier()
					.equals("eu.esdihumboldt.hale.align.formattedstring")) {
				cell = temp;
				break;
			}
		}
		assertNotNull(cell);

		ListMultimap<String, ParameterValue> params = cell.getTransformationParameters();
		List<ParameterValue> values = params.get("pattern");

		assertEquals(1, values.size());
		// size is 1
		assertEquals(
				"{flurstuecksnummer.AX_Flurstuecksnummer.zaehler}/{flurstuecksnummer.AX_Flurstuecksnummer.nenner}",
				values.get(0).getValue());
	}

	/**
	 * Test for formatted string function in alignment5
	 */
	@Test
	public void testFormattedString3() {

		Collection<? extends Cell> cells = alignment5.getCells();

		Iterator<? extends Cell> it = cells.iterator();

		Cell cell = null;
		while (it.hasNext()) {
			Cell temp = it.next();

			if (temp.getTransformationIdentifier()
					.equals("eu.esdihumboldt.hale.align.formattedstring")) {
				cell = temp;
				break;
			}
		}
		assertNotNull(cell);

		ListMultimap<String, ParameterValue> params = cell.getTransformationParameters();
		List<ParameterValue> values = params.get("pattern");

		assertEquals(1, values.size());
		// size is 1
		assertEquals("{Grundbuch}:{Nummer}:{Einlage}", values.get(0).getValue());
	}

	/**
	 * Test for classification mapping function in alignment2
	 */
	@Test
	public void testClassificationMapping1() {

		Collection<? extends Cell> cells = alignment2.getCells();

		Iterator<? extends Cell> it = cells.iterator();

		Cell cell = null;
		while (it.hasNext()) {
			Cell temp = it.next();

			if (temp.getTransformationIdentifier()
					.equals("eu.esdihumboldt.hale.align.classification")) {
				cell = temp;
				break;
			}
		}

		ListMultimap<String, ParameterValue> params = cell.getTransformationParameters();
		List<ParameterValue> values = params.get("classificationMapping");

		for (int i = 0; i < values.size(); i++) {
			String temp = values.get(i).as(String.class);

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

		// check if all values were tested
		assertEquals(3, values.size());
	}

	/**
	 * Test for classification mapping function in alignment4
	 */
	@Test
	public void testClassificationMapping2() {

		Collection<? extends Cell> cells = alignment4.getCells();

		Iterator<? extends Cell> it = cells.iterator();

		List<Cell> classMapCells = new ArrayList<Cell>();

		while (it.hasNext()) {
			Cell temp = it.next();

			if (temp.getTransformationIdentifier()
					.equals("eu.esdihumboldt.hale.align.classification")) {
				classMapCells.add(temp);
			}
		}

		// test all cells that have a classification mapping function
		for (int i = 0; i < classMapCells.size(); i++) {
			Cell cell = classMapCells.get(i);

			ListMultimap<String, ParameterValue> params = cell.getTransformationParameters();
			List<ParameterValue> values = params.get("classificationMapping");

			// each cell can have more than one value, so iterate through them
			// for each cell too
			for (int j = 0; j < values.size(); j++) {
				String temp = values.get(j).as(String.class);

				// "i" is the index for the cell number
				// "j" stands for the indices of the values per cell
				// test cell #1
				if (i == 0 && j == 0) {
					assertEquals("underConstruction 5", temp);
				}
				// test cell #2
				if (i == 1 && j == 0) {
					assertEquals("manMade 4", temp);
				}
				if (i == 1 && j == 1) {
					assertEquals("natural 5", temp);
				}
				// test cell #3
				if (i == 2 && j == 0) {
					assertEquals("intermittent 6", temp);
				}
				if (i == 2 && j == 1) {
					assertEquals("perennial 8", temp);
				}
				// test cell #4
				if (i == 3 && j == 0) {
					assertEquals("false 1", temp);
				}
				if (i == 3 && j == 1) {
					assertEquals("true 2", temp);
				}

			}
		}

		// check if all cells with a classification mapping function were tested
		assertEquals(4, classMapCells.size());

	}

	/**
	 * Test for network expansion function in alignment4
	 */
	@Test
	public void testNetworkExpansion1() {

		Collection<? extends Cell> cells = alignment4.getCells();

		Iterator<? extends Cell> it = cells.iterator();

		Cell cell = null;
		while (it.hasNext()) {
			Cell temp = it.next();

			if (temp.getTransformationIdentifier()
					.equals("eu.esdihumboldt.cst.functions.geometric.networkexpansion")) {
				cell = temp;
				break;
			}
		}

		ListMultimap<String, ParameterValue> params = cell.getTransformationParameters();
		List<ParameterValue> values = params.get("bufferWidth");

		assertEquals(1, values.size());
		// size is always 1
		String temp = values.get(0).as(String.class);

		assertEquals("0.005", temp);
	}

	/**
	 * Test for network expansion function in alignment5
	 */
	@Test
	public void testNetworkExpansion2() {

		Collection<? extends Cell> cells = alignment5.getCells();

		Iterator<? extends Cell> it = cells.iterator();

		List<Cell> networkCells = new ArrayList<Cell>();

		while (it.hasNext()) {
			Cell temp = it.next();

			if (temp.getTransformationIdentifier()
					.equals("eu.esdihumboldt.cst.functions.geometric.networkexpansion")) {
				networkCells.add(temp);
			}
		}

		for (int i = 0; i < networkCells.size(); i++) {

			Cell cell = networkCells.get(i);

			ListMultimap<String, ParameterValue> params = cell.getTransformationParameters();
			List<ParameterValue> values = params.get("bufferWidth");

			String temp = values.get(0).as(String.class);

			if (i == 0) {
				assertEquals("50", temp);
			}
			if (i == 1) {
				assertEquals("5", temp);
			}
		}

		// check if all cells were tested
		assertEquals(2, networkCells.size());

	}

	/**
	 * Test assign function in alignment4
	 */
	@Test
	@Ignore
	// because now NilReasonFunction also produces assign cells
	public void testAssign1() {

		Collection<? extends Cell> cells = alignment4.getCells();

		Iterator<? extends Cell> it = cells.iterator();

		List<Cell> assignCells = new ArrayList<Cell>();

		while (it.hasNext()) {
			Cell temp = it.next();

			if (temp.getTransformationIdentifier().equals("eu.esdihumboldt.hale.align.assign")) {
				assignCells.add(temp);
			}
		}

		// test all cells that have an assign function
		for (int i = 0; i < assignCells.size(); i++) {
			Cell cell = assignCells.get(i);

			ListMultimap<String, ParameterValue> params = cell.getTransformationParameters();
			List<ParameterValue> values = params.get("value");

			assertEquals(1, values.size());
			// size is always 1
			String temp = values.get(0).as(String.class);

			// test cell #1
			if (i == 0) {
				assertEquals("FR", temp);
			}
			// test cell #2
			if (i == 1) {
				assertEquals("FR.IGN.ERM", temp);
			}
			// test cell #3
			if (i == 2) {
				assertEquals("250000", temp);
			}
		}

		// check if all cells with an assign function were tested
		assertEquals(3, assignCells.size());

	}

	/**
	 * Test for assign function in alignment2
	 */
	@Test
	@Ignore
	// because now NilReasonFunction also produces assign cells
	public void testAssign2() {

		Collection<? extends Cell> cells = alignment2.getCells();

		Iterator<? extends Cell> it = cells.iterator();

		List<Cell> assignCells = new ArrayList<Cell>();

		while (it.hasNext()) {
			Cell temp = it.next();

			if (temp.getTransformationIdentifier().equals("eu.esdihumboldt.hale.align.assign")) {
				assignCells.add(temp);
			}
		}

		// test all cells that have an assign function
		for (int i = 0; i < assignCells.size(); i++) {
			Cell cell = assignCells.get(i);

			ListMultimap<String, ParameterValue> params = cell.getTransformationParameters();
			List<ParameterValue> values = params.get("value");

			assertEquals(1, values.size());
			// size is always 1
			String temp = values.get(0).as(String.class);

			// test cell #1
			if (i == 0) {
				assertEquals("manMade", temp);
			}
			// test cell #2
			if (i == 1) {
				assertEquals("false", temp);
			}
			// test cell #3
			if (i == 2) {
				assertEquals("2009-12-23 12:13:14", temp);
			}
			// test cell #4
			if (i == 3) {
				assertEquals("m", temp);
			}
		}

		// check if all cells with an assign function were tested
		assertEquals(4, assignCells.size());
	}

	/**
	 * Test for assign function in alignment5
	 */
	@Test
	@Ignore
	// because now NilReasonFunction also produces assign cells
	public void testAssign3() {
		Collection<? extends Cell> cells = alignment5.getCells();

		Iterator<? extends Cell> it = cells.iterator();

		List<Cell> assignCells = new ArrayList<Cell>();

		while (it.hasNext()) {
			Cell temp = it.next();

			if (temp.getTransformationIdentifier().equals("eu.esdihumboldt.hale.align.assign")) {
				assignCells.add(temp);
			}
		}

		// test all cells that have an assign function
		for (int i = 0; i < assignCells.size(); i++) {
			Cell cell = assignCells.get(i);

			ListMultimap<String, ParameterValue> params = cell.getTransformationParameters();
			List<ParameterValue> values = params.get("value");

			assertEquals(1, values.size());
			// size is always 1
			String temp = values.get(0).as(String.class);

			// test cell #1
			if (i == 0) {
				assertEquals("2000", temp);
			}
			// test cell #2
			if (i == 1) {
				assertEquals("20", temp);
			}
			// test cell #3
			if (i == 2) {
				assertEquals("20", temp);
			}
			// test cell #4
			if (i == 3) {
				assertEquals("2", temp);
			}
			// test cell #5
			if (i == 4) {
				assertEquals("Katastralgemeinde", temp);
			}
			// test cell #6
			if (i == 5) {
				assertEquals("m2", temp);
			}
			// test cell #7
			if (i == 6) {
				assertEquals("m2", temp);
			}
			// test cell #8
			if (i == 7) {
				assertEquals("m", temp);
			}
			// test cell #9
			if (i == 8) {
				assertEquals("m", temp);
			}
		}

		// check if all cells with an assign function were tested
		assertEquals(9, assignCells.size());
	}

	/**
	 * Test for ordinates to point function in alignment5
	 */
	@Test
	public void testOrdinatesToPoint1() {

		Collection<? extends Cell> cells = alignment5.getCells();

		Iterator<? extends Cell> it = cells.iterator();

		Cell cell = null;

		while (it.hasNext()) {
			Cell temp = it.next();

			if (temp.getTransformationIdentifier()
					.equals("eu.esdihumboldt.cst.functions.geometric.ordinates_to_point")) {
				cell = temp;
				break;
			}
		}

		ListMultimap<String, ? extends Entity> src = cell.getSource();

		// the parameters were moved to the source entities with the appropriate
		// names so get the source entities with name "X"/"Y"
		Entity srcX = src.get("X").get(0);
		Entity srcY = src.get("Y").get(0);

		// check if the source entity has the correct value
		assertEquals("HOCHWERT", srcX.getDefinition().getDefinition().getDisplayName());
		assertEquals("RECHTSWERT", srcY.getDefinition().getDefinition().getDisplayName());

	}

	/**
	 * Test for centroid function in alignment3
	 */
	@Test
	public void testCentroid1() {

		Collection<? extends Cell> cells = alignment5.getCells();

		Iterator<? extends Cell> it = cells.iterator();

		Cell cell = null;

		while (it.hasNext()) {
			Cell temp = it.next();

			if (temp.getTransformationIdentifier()
					.equals("eu.esdihumboldt.cst.functions.geometric.centroid")) {
				cell = temp;
				break;
			}
		}

		// test if there is only one source and one target
		assertEquals(1, cell.getSource().size());
		assertEquals(1, cell.getTarget().size());

		List<? extends Entity> list = cell.getTarget().get(null);
		assertEquals(1, list.size());

		Entity ent = list.get(0);

		String name = ent.getDefinition().getDefinition().getDisplayName();

		assertEquals("referencePoint", name);
	}

	/**
	 * Test for date extraction function in alignment
	 */
	@Test
	public void testDateExtraction() {
		Collection<? extends Cell> cells = alignment.getCells();

		Iterator<? extends Cell> it = cells.iterator();

		Cell cell = null;
		while (it.hasNext()) {
			Cell temp = it.next();

			if (temp.getTransformationIdentifier()
					.equals("eu.esdihumboldt.cst.functions.string.dateextraction")) {
				cell = temp;
				break;
			}
		}

		ListMultimap<String, ParameterValue> params = cell.getTransformationParameters();
		List<ParameterValue> values = params.get("dateFormat");

		assertEquals(1, values.size());

		String date = values.get(0).as(String.class);

		assertEquals("yyyy-MM-dd HH:mm:ss", date);
	}

	/**
	 * Test for mathematical expression in alignment
	 */
	@Test
	public void testMathematicalExpression() {
		Collection<? extends Cell> cells = alignment.getCells();

		Iterator<? extends Cell> it = cells.iterator();

		Cell cell = null;
		while (it.hasNext()) {
			Cell temp = it.next();

			if (temp.getTransformationIdentifier()
					.equals("eu.esdihumboldt.cst.functions.numeric.mathexpression")) {
				cell = temp;
				break;
			}
		}

		ListMultimap<String, ParameterValue> params = cell.getTransformationParameters();
		List<ParameterValue> values = params.get("expression");

		// test the amount and the correctness of the parameter
		assertEquals(1, values.size());

		String date = values.get(0).as(String.class);

		assertEquals("income * age/10", date);

		// test the amount and the correctness of source properties
		ListMultimap<String, ? extends Entity> src = cell.getSource();

		// all source properties should be named "var" so we test if both lists
		// have the same size
		List<? extends Entity> srcCells = src.get("var");
		assertEquals(2, src.size());
		assertEquals(2, srcCells.size());

		// since we have now the right amount of source properties we can now
		// test the correctness of their names
		Entity srcCell1 = srcCells.get(0);
		Entity srcCell2 = srcCells.get(1);

		String name1 = srcCell1.getDefinition().getDefinition().getDisplayName();
		String name2 = srcCell2.getDefinition().getDefinition().getDisplayName();

		assertEquals("age", name1);
		assertEquals("income", name2);

	}

	/**
	 * test for the inspire identifier function in alignment5
	 */
	@Test
	public void testIdentifier() {
		Collection<? extends Cell> cells = alignment5.getCells();

		Iterator<? extends Cell> it = cells.iterator();

		Cell cell = null;
		while (it.hasNext()) {
			Cell temp = it.next();

			if (temp.getTransformationIdentifier()
					.equals("eu.esdihumboldt.cst.functions.inspire.identifier")) {
				cell = temp;
				break;
			}
		}

		ListMultimap<String, ParameterValue> params = cell.getTransformationParameters();

		List<ParameterValue> country = params.get("countryName");
		List<ParameterValue> provider = params.get("providerName");
		List<ParameterValue> product = params.get("productName");
		List<ParameterValue> version = params.get("version");
		List<ParameterValue> versionNilReason = params.get("versionNilReason");

		// check if all parameters were set once
		assertEquals(1, country.size());
		assertEquals(1, provider.size());
		assertEquals(1, product.size());
		assertEquals(1, version.size());
		assertEquals(1, versionNilReason.size());

		// now test if they have correct values
		assertEquals("at", country.get(0).getValue());
		assertEquals("BEV", provider.get(0).getValue());
		assertEquals("humboldt-sample-transformed-data-CadastralParcels",
				product.get(0).getValue());
		assertEquals("", version.get(0).getValue());
		assertEquals("unknown", versionNilReason.get(0).getValue());

		// check if all parameters were tested
		assertEquals(5, params.size());
	}

	/**
	 * test for the inspire geographical name function in alignment6
	 */
	@Test
	public void testGeographicalName1() {
		Collection<? extends Cell> cells = alignment6.getCells();

		Iterator<? extends Cell> it = cells.iterator();

		Cell cell = null;
		while (it.hasNext()) {
			Cell temp = it.next();

			if (temp.getTransformationIdentifier()
					.equals("eu.esdihumboldt.cst.functions.inspire.geographicalname")) {
				cell = temp;
				break;
			}
		}

		ListMultimap<String, ParameterValue> params = cell.getTransformationParameters();

		List<ParameterValue> gender = params.get("grammaticalGender");
		List<ParameterValue> number = params.get("grammaticalNumber");
		List<ParameterValue> lang = params.get("language");
		List<ParameterValue> nameStatus = params.get("nameStatus");
		List<ParameterValue> nativeness = params.get("nativeness");
		List<ParameterValue> ipa = params.get("pronunciationIPA");
		List<ParameterValue> sound = params.get("pronunciationSoundLink");
		List<ParameterValue> source = params.get("sourceOfName");
		List<ParameterValue> script = params.get("script");
		List<ParameterValue> text = params.get("text");
		List<ParameterValue> trans = params.get("transliterationScheme");

		// test if all parameters were set only once
		assertEquals(1, gender.size());
		assertEquals(1, number.size());
		assertEquals(1, lang.size());
		assertEquals(1, nameStatus.size());
		assertEquals(1, nativeness.size());
		assertEquals(1, ipa.size());
		// sound shouldn't be available because in older version we couldn't
		// enter a value
		assertEquals(0, sound.size());
		assertEquals(1, source.size());
		assertEquals(1, script.size());
		assertEquals(1, text.size());
		assertEquals(1, trans.size());

		// now test if they have the correct values
		assertEquals("common", gender.get(0).getValue());
		assertEquals("dual", number.get(0).getValue());
		assertEquals("deu", lang.get(0).getValue());
		assertEquals("historical", nameStatus.get(0).getValue());
		assertEquals("exonym", nativeness.get(0).getValue());
		assertEquals("IDipa", ipa.get(0).getValue());
		assertEquals("source", source.get(0).getValue());
		assertEquals("IDscript", script.get(0).getValue());
		assertEquals("identifier", text.get(0).getValue());
		assertEquals("IDtrans", trans.get(0).getValue());

		// check if all parameters were tested (size is 10 because "sound" is
		// not defined in params)
		assertEquals(10, params.size());
	}

	/**
	 * test for the inspire geographical name function in alignment7
	 */
	@Test
	public void testGeographicalName2() {
		Collection<? extends Cell> cells = alignment7.getCells();

		Iterator<? extends Cell> it = cells.iterator();

		Cell cell = null;
		while (it.hasNext()) {
			Cell temp = it.next();

			if (temp.getTransformationIdentifier()
					.equals("eu.esdihumboldt.cst.functions.inspire.geographicalname")) {
				cell = temp;
				break;
			}
		}

		ListMultimap<String, ParameterValue> params = cell.getTransformationParameters();

		List<ParameterValue> gender = params.get("grammaticalGender");
		List<ParameterValue> number = params.get("grammaticalNumber");
		List<ParameterValue> lang = params.get("language");
		List<ParameterValue> nameStatus = params.get("nameStatus");
		List<ParameterValue> nativeness = params.get("nativeness");
		List<ParameterValue> ipa = params.get("pronunciationIPA");
		List<ParameterValue> sound = params.get("pronunciationSoundLink");
		List<ParameterValue> source = params.get("sourceOfName");
		List<ParameterValue> script = params.get("script");
		List<ParameterValue> text = params.get("text");
		List<ParameterValue> trans = params.get("transliterationScheme");

		// test if all parameters (except the parameters for the spellings) were
		// set only once
		assertEquals(1, gender.size());
		assertEquals(1, number.size());
		assertEquals(1, lang.size());
		assertEquals(1, nameStatus.size());
		assertEquals(1, nativeness.size());
		assertEquals(1, ipa.size());
		// sound shouldn't be available because in older version we couldn't
		// enter a value
		assertEquals(0, sound.size());
		assertEquals(1, source.size());

		// spelling parameters
		assertEquals(2, script.size());
		assertEquals(2, text.size());
		assertEquals(2, trans.size());

		// now test if they have the correct values
		assertEquals("", gender.get(0).getValue());
		assertEquals("", number.get(0).getValue());
		assertEquals("esp", lang.get(0).getValue());
		assertEquals("official", nameStatus.get(0).getValue());
		assertEquals("endonym", nativeness.get(0).getValue());
		assertEquals("", ipa.get(0).getValue());
		assertEquals("unknown", source.get(0).getValue());

		for (int i = 0; i < text.size(); i++) {
			String spellText = text.get(i).as(String.class);
			String spellScript = script.get(i).as(String.class);
			String spellTrans = trans.get(i).as(String.class);
			if (i == 0) {
				assertEquals("identifier", spellText);
				assertEquals("idScript", spellScript);
				// no value set, initial value is "null"
				assertEquals(null, spellTrans);
			}
			if (i == 1) {
				assertEquals("name", spellText);
				// initial value is "eng", that was removed so we expect an
				// empty string
				assertEquals("", spellScript);
				assertEquals("nameTrans", spellTrans);
			}
		}

		// check if all parameters were tested (size is 13 because "sound" is
		// not defined in params and there are 2 spellings this time and 1
		// spelling has 3 parameters -> +3 parameters)
		assertEquals(13, params.size());

	}

	private static Alignment loadAlignment(URI sourceSchemaLocation, URI targetSchemaLocation,
			final URI alignmentLocation) throws IOProviderConfigurationException, IOException {

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
	 *             reader is invalid
	 * @throws IOException if reading the schema fails
	 */
	private static Schema readXMLSchema(LocatableInputSupplier<? extends InputStream> input)
			throws IOProviderConfigurationException, IOException {
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
