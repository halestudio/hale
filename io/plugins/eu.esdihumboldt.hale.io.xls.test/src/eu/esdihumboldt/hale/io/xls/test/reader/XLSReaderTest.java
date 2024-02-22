/*
 * Copyright (c) 2014 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.xls.test.reader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import javax.xml.namespace.QName;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.ValueList;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.test.TestUtil;
import eu.esdihumboldt.hale.io.csv.InstanceTableIOConstants;
import eu.esdihumboldt.hale.io.csv.reader.CSVConstants;
import eu.esdihumboldt.hale.io.csv.reader.CommonSchemaConstants;
import eu.esdihumboldt.hale.io.csv.reader.internal.AbstractTableSchemaReader;
import eu.esdihumboldt.hale.io.xls.reader.ReaderSettings;
import eu.esdihumboldt.hale.io.xls.reader.SheetSettings;
import eu.esdihumboldt.hale.io.xls.reader.XLSInstanceReader;
import eu.esdihumboldt.hale.io.xls.reader.XLSSchemaReader;

/**
 * Test class for {@link XLSSchemaReader} and {@link XLSInstanceReader}
 * 
 * @author Yasmina Kammeyer
 */
public class XLSReaderTest {

	private final String typeName = "item";
	private final String[] properties = { "number", "name", "desc" };
	private final String[] dataFirstColumn = { "1234", "Glasses", "Pair of" };
	private final int numberOfInstances = 3;

	/**
	 * Wait for needed services to be running
	 */
	@BeforeClass
	public static void waitForServices() {
		TestUtil.startConversionService();
	}

	/**
	 * Test - read a sample xls schema and data from same file and sheet (simple
	 * io test). Check the type, check the properties, check the values of the
	 * properties, check the datatype of the properties
	 * 
	 * @throws Exception , if an error occurs
	 */
	@Test
	public void testReadSimple() throws Exception {

		// read Schema ###
		Schema schema = readXLSSchema("/data/simpleOneSheet.xls", 0, typeName,
				"java.lang.String,java.lang.String,java.lang.String");
		// Test properties and their datatype
		TypeDefinition schemaType = schema.getType(QName.valueOf(typeName));
		Binding binding;
		for (ChildDefinition<?> child : schemaType.getChildren()) {
			binding = child.asProperty().getPropertyType().getConstraint(Binding.class);
			assertTrue(binding.getBinding().equals(String.class));
		}
		// Check every property for their existence
		for (String propertyName : properties) {
			assertEquals(propertyName,
					schemaType.getChild(QName.valueOf(propertyName)).getDisplayName());
		}

		// read Instances - not header ###
		InstanceCollection instances = readXLSInstances("/data/simpleOneSheet.xls", 0, typeName, 1,
				schema);
		assertTrue(instances.hasSize());
		assertEquals(numberOfInstances, instances.size());

		// read and skip N instances ###
		int numberOfLines = 4; // no. of lines = header + 3 instances
		for (int i = 0; i <= numberOfLines; i++) {
			InstanceCollection remainingInstances = readXLSInstances("/data/simpleOneSheet.xls", 0,
					typeName, i, schema);
			assertEquals(numberOfLines - i, remainingInstances.size());
		}

		// get Type to check property definition (schema and instance
		// combination)
		TypeDefinition type = instances.iterator().next().getDefinition();
		ChildDefinition<?> child = null;
		assertEquals(typeName, type.getDisplayName());
		for (int i = 0; i < properties.length; i++) {
			child = type.getChild(QName.valueOf(properties[i]));
			assertEquals(properties[i], child.getDisplayName());
		}

		// Check the values of the first (type) instance
		Instance instance = instances.iterator().next();
		Object[] value;
		for (int i = 0; i < dataFirstColumn.length; i++) {
			value = instance.getProperty(QName.valueOf(properties[i]));
			assertEquals(dataFirstColumn[i], value[0]);
			assertTrue(value[0] instanceof String);
		}
	}

	/**
	 * Test - read a sample xls schema and data where skip is a boolean. It
	 * simulates the previous version where the first line was either skipped
	 * (true) or not (false)
	 * 
	 * @throws Exception , if an error occurs
	 */
	@Test
	public void testBackwardCompatibilityRead() throws Exception {
		// read Schema ###
		Schema schema = readXLSSchema("/data/simpleOneSheet.xls", 0, typeName,
				"java.lang.String,java.lang.String,java.lang.String");
		int numberOfLines = 4; // no. of lines = header + 3 instances

		// test backwards compatibility for simple read with skip first line as
		// boolean
		InstanceCollection instancesNotSkip = readXLSInstances("/data/simpleOneSheet.xls", 0,
				typeName, false, schema);
		InstanceCollection instancesSkipFirst = readXLSInstances("/data/simpleOneSheet.xls", 0,
				typeName, true, schema);

		assertTrue(instancesNotSkip.hasSize());
		assertTrue(instancesSkipFirst.hasSize());
		assertEquals(numberOfLines, instancesNotSkip.size());
		assertEquals(numberOfLines - 1, instancesSkipFirst.size());

		// test backwards compatibility for read empty sheet
		InstanceCollection instancesSkipFirstEmptySheet = readXLSInstances("/data/blankEntries.xls",
				0, typeName, true, schema);

		assertTrue(instancesSkipFirstEmptySheet.hasSize());
		assertEquals(numberOfLines - 1, instancesSkipFirstEmptySheet.size());

		// test backwards compatibility for read multiple values
		InstanceCollection instancesSkipFirstMultValues = readXLSInstances(
				"/data/cmplxSheetMultipleValues.xls", 0, typeName, true, schema);

		assertTrue(instancesSkipFirstMultValues.hasSize());
		assertEquals(numberOfLines - 1, instancesSkipFirstMultValues.size());

	}

	/**
	 * Test - Check empty table
	 * 
	 * @throws Exception , if an error occurs
	 */
	@Test
	public void testReadEmptySheet() throws Exception {
		int sheetIndex = 0;

		XLSSchemaReader schemaReader = new XLSSchemaReader();
		schemaReader.setSource(new DefaultInputSupplier(
				getClass().getResource("/data/emptyAndNormalSheet.xls").toURI()));
		schemaReader.setParameter(InstanceTableIOConstants.SHEET_INDEX, Value.of(sheetIndex));
		schemaReader.setParameter(CommonSchemaConstants.PARAM_TYPENAME, Value.of(typeName));

		// Try read the empty sheet
		IOReport report = schemaReader.execute(null);
		assertFalse(report.isSuccess());

		// Read the correct sheet
		sheetIndex = 1;
		schemaReader.setParameter(InstanceTableIOConstants.SHEET_INDEX, Value.of(sheetIndex));
		report = schemaReader.execute(null);
		assertTrue(report.isSuccess());
	}

	/**
	 * Test - read a sample xls schema and data from same file and different
	 * sheets. Check skip first
	 * 
	 * @throws Exception , if an error occurs
	 */
	@Test
	public void testReadSameFileDiffSheet() throws Exception {

		String sourceLocation = "/data/simpleTwoSheet.xls";

		Schema schema = readXLSSchema(sourceLocation, 0, typeName,
				"java.lang.String,java.lang.String,java.lang.String");

		TypeDefinition schemaType = schema.getType(QName.valueOf(typeName));
		// Check every property for their existence
		for (String propertyName : properties) {
			assertEquals(propertyName,
					schemaType.getChild(QName.valueOf(propertyName)).getDisplayName());
		}

		// Instance Read ###
		InstanceCollection instances = readXLSInstances(sourceLocation, 1, typeName, 0, schema);
		assertTrue(instances.hasSize());
		assertEquals(numberOfInstances, instances.size());

		// get Type to check property definition (schema and instance
		// combination)
		TypeDefinition type = instances.iterator().next().getDefinition();
		ChildDefinition<?> child = null;
		assertEquals(typeName, type.getDisplayName());
		for (int i = 0; i < properties.length; i++) {
			child = type.getChild(QName.valueOf(properties[i]));
			assertEquals(properties[i], child.getDisplayName());
		}

		// Check the values of the first (type) instance
		Instance instance = instances.iterator().next();
		Object[] value;
		for (int i = 0; i < dataFirstColumn.length; i++) {
			value = instance.getProperty(QName.valueOf(properties[i]));
			assertEquals(dataFirstColumn[i], value[0]);
			assertTrue(value[0] instanceof String);
		}
	}

	/**
	 * Test - read xls file and data. Check handled blank cells.
	 * 
	 * @throws Exception , if an error occurs
	 */
	@Test
	public void testReadBlankCells() throws Exception {

		// read schema ###
		Schema schema = readXLSSchema("/data/blankEntries.xls", 0, typeName,
				"java.lang.String,java.lang.String,java.lang.String");

		// read instances ###
		InstanceCollection instances = readXLSInstances("/data/blankEntries.xls", 0, typeName, 1,
				schema);
		// Number of instances should be the same
		assertTrue(instances.hasSize());
		assertEquals(numberOfInstances, instances.size());

		// Check blank property of first type
		Instance instance = instances.iterator().next();
		Object[] value;
		value = instance.getProperty(QName.valueOf(properties[1]));
		// There should be no value, therefore the length of the value-array has
		// to be
		// 0
		assertEquals(0, value.length);

		// check other values to be correct
		value = instance.getProperty(QName.valueOf(properties[0]));
		assertEquals(dataFirstColumn[0], value[0]);
		value = instance.getProperty(QName.valueOf(properties[2]));
		assertEquals(dataFirstColumn[2], value[0]);
	}

	/**
	 * Test - read xls file and data. Check multiple values of one instance.
	 * Check formula cells
	 * 
	 * @throws Exception , if an error occurs
	 */
	@Test
	public void testReadMultipleValues() throws Exception {

		// read schema ###
		Schema schema = readXLSSchema("/data/cmplxSheetMultipleValues.xls", 0, typeName,
				"java.lang.String,java.lang.String,java.lang.String");

		// read instance ###
		InstanceCollection instances = readXLSInstances("/data/cmplxSheetMultipleValues.xls", 0,
				typeName, 1, schema);
		// Number of instances should be the same
		assertTrue(instances.hasSize());
		assertEquals(numberOfInstances, instances.size());

		// Check blank property of first type
		Instance instance = instances.iterator().next();
		Object[] value;
		value = instance.getProperty(QName.valueOf(properties[0]));
		assertEquals("1234", value[0]);
		value = instance.getProperty(QName.valueOf(properties[1]));
		assertEquals("Glasses", value[0]);
		value = instance.getProperty(QName.valueOf(properties[2]));
		assertEquals("Pair of Glasses", value[0]);
	}

	/**
	 * Test - solving nested properties - deprecated
	 */
	public void testReadNestedProperties() {
		// TODO deprecated
	}

	/**
	 * Test - check declaration of properties' datatype.
	 * 
	 * @throws Exception , if an error occurs
	 */
	@Test
	public void testPropertyType() throws Exception {

		Schema schema = readXLSSchema("/data/simpleOneSheet.xls", 0, typeName,
				"java.lang.Integer,java.lang.String,java.lang.String");

		// Test property datatype
		TypeDefinition schemaType = schema.getType(QName.valueOf(typeName));
		Binding binding = schemaType.getChildren().iterator().next().asProperty().getPropertyType()
				.getConstraint(Binding.class);
		assertTrue("The type is not an Integer.", binding.getBinding().equals(Integer.class));

		// ### Instance
		InstanceCollection instances = readXLSInstances("/data/simpleOneSheet.xls", 0, typeName, 1,
				schema);
		assertTrue(instances.hasSize());
		assertEquals(numberOfInstances, instances.size());

		// Check the values of the first (type) instance
		Instance instance = instances.iterator().next();
		Object[] value = instance.getProperty(QName.valueOf(properties[0]));
		assertEquals(Integer.valueOf(dataFirstColumn[0]), value[0]);
		assertTrue(value[0] instanceof Integer);
	}

	/**
	 * Read an Excel file with multiple sheets detecting the associated types
	 * based on the sheet name.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testReadMultiSheetDetectType() throws Exception {
		Schema schema = XLSReaderTestUtil.createMultiSheetExampleSchema();

		InstanceCollection instances = readXLSInstances("/data/multisheet.xlsx", schema, reader -> {
			// enable multi sheet loading
			reader.setParameter(ReaderSettings.PARAMETER_MULTI_SHEET, Value.of(true));
			// skip one line
			reader.setParameter(CommonSchemaConstants.PARAM_SKIP_N_LINES, Value.of(1));
		});

		XLSReaderTestUtil.verifyMultiSheetExample(instances, false);
	}

	/**
	 * Read an Excel file with multiple sheets setting the type mapping with the
	 * type names parameter based on index.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testReadMultiSheetTypeNames() throws Exception {
		Schema schema = XLSReaderTestUtil.createMultiSheetExampleSchema();

		InstanceCollection instances = readXLSInstances("/data/multisheet.xlsx", schema, reader -> {
			// enable multi sheet loading
			reader.setParameter(ReaderSettings.PARAMETER_MULTI_SHEET, Value.of(true));
			// skip one line
			reader.setParameter(CommonSchemaConstants.PARAM_SKIP_N_LINES, Value.of(1));

			// Note: we swap the tables to make sure auto-detection based on the
			// name is not used
			reader.setParameter(CommonSchemaConstants.PARAM_TYPENAME, Value.of("person,city"));
		});

		XLSReaderTestUtil.verifyMultiSheetExample(instances, true);
	}

	/**
	 * Read an Excel file with multiple sheets specifying types explicitly via
	 * sheet settings identified by sheet name.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testReadMultiSheetTypeSettingsNamed() throws Exception {
		Schema schema = XLSReaderTestUtil.createMultiSheetExampleSchema();

		ValueList settings = new ValueList();

		// Note: we swap the tables to make sure auto-detection based on the
		// name is not used

		SheetSettings personTypeSettings = new SheetSettings("city", null);
		personTypeSettings.setTypeName(QName.valueOf("person"));
		settings.add(personTypeSettings.toValue());

		SheetSettings cityTypeSettings = new SheetSettings("person", null);
		cityTypeSettings.setTypeName(QName.valueOf("city"));
		settings.add(cityTypeSettings.toValue());

		InstanceCollection instances = readXLSInstances("/data/multisheet.xlsx", schema, reader -> {
			// enable multi sheet loading
			reader.setParameter(ReaderSettings.PARAMETER_MULTI_SHEET, Value.of(true));
			// skip one line
			reader.setParameter(CommonSchemaConstants.PARAM_SKIP_N_LINES, Value.of(1));
			// settings
			reader.setParameter(ReaderSettings.PARAMETER_SHEET_SETTINGS, settings.toValue());
		});

		XLSReaderTestUtil.verifyMultiSheetExample(instances, true);
	}

	/**
	 * Read an Excel file with multiple sheets specifying types explicitly via
	 * sheet settings identified by sheet name. How many lines are to be skipped
	 * is configured individually per sheet.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testReadMultiSheetTypeSettingsNamedNoGeneralSkip() throws Exception {
		Schema schema = XLSReaderTestUtil.createMultiSheetExampleSchema();

		ValueList settings = new ValueList();

		// Note: we swap the tables to make sure auto-detection based on the
		// name is not used

		SheetSettings personTypeSettings = new SheetSettings("city", null);
		personTypeSettings.setTypeName(QName.valueOf("person"));
		personTypeSettings.setSkipLines(1);
		settings.add(personTypeSettings.toValue());

		SheetSettings cityTypeSettings = new SheetSettings("person", null);
		cityTypeSettings.setTypeName(QName.valueOf("city"));
		cityTypeSettings.setSkipLines(1);
		settings.add(cityTypeSettings.toValue());

		InstanceCollection instances = readXLSInstances("/data/multisheet.xlsx", schema, reader -> {
			// enable multi sheet loading
			reader.setParameter(ReaderSettings.PARAMETER_MULTI_SHEET, Value.of(true));
			// settings
			reader.setParameter(ReaderSettings.PARAMETER_SHEET_SETTINGS, settings.toValue());
		});

		XLSReaderTestUtil.verifyMultiSheetExample(instances, true);
	}

	/**
	 * Read an Excel file with multiple sheets specifying types explicitly via
	 * sheet settings identified by sheet index.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testReadMultiSheetTypeSettingsIndex() throws Exception {
		Schema schema = XLSReaderTestUtil.createMultiSheetExampleSchema();

		ValueList settings = new ValueList();

		// Note: we swap the tables to make sure auto-detection based on the
		// name is not used

		SheetSettings personTypeSettings = new SheetSettings(null, 0);
		personTypeSettings.setTypeName(QName.valueOf("person"));
		settings.add(personTypeSettings.toValue());

		SheetSettings cityTypeSettings = new SheetSettings(null, 1);
		cityTypeSettings.setTypeName(QName.valueOf("city"));
		settings.add(cityTypeSettings.toValue());

		InstanceCollection instances = readXLSInstances("/data/multisheet.xlsx", schema, reader -> {
			// enable multi sheet loading
			reader.setParameter(ReaderSettings.PARAMETER_MULTI_SHEET, Value.of(true));
			// skip one line
			reader.setParameter(CommonSchemaConstants.PARAM_SKIP_N_LINES, Value.of(1));
			// settings
			reader.setParameter(ReaderSettings.PARAMETER_SHEET_SETTINGS, settings.toValue());
		});

		XLSReaderTestUtil.verifyMultiSheetExample(instances, true);
	}

	/**
	 * Read an Excel file with multiple sheets specifying types explicitly via
	 * sheet settings identified by sheet index.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testReadMultiSheetTypeSkipSettings() throws Exception {
		Schema schema = XLSReaderTestUtil.createMultiSheetExampleSchema();

		ValueList settings = new ValueList();

		SheetSettings cityTypeSettings = new SheetSettings(null, 0);
		cityTypeSettings.setTypeName(QName.valueOf("city"));
		cityTypeSettings.setSkipSheet(true);
		settings.add(cityTypeSettings.toValue());

		SheetSettings personTypeSettings = new SheetSettings(null, 1);
		personTypeSettings.setTypeName(QName.valueOf("person"));
		personTypeSettings.setSkipLines(2);
		settings.add(personTypeSettings.toValue());

		InstanceCollection instances = readXLSInstances("/data/multisheet.xlsx", schema, reader -> {
			// enable multi sheet loading
			reader.setParameter(ReaderSettings.PARAMETER_MULTI_SHEET, Value.of(true));
			// skip one line
			reader.setParameter(CommonSchemaConstants.PARAM_SKIP_N_LINES, Value.of(1));
			// settings
			reader.setParameter(ReaderSettings.PARAMETER_SHEET_SETTINGS, settings.toValue());
		});

		List<Instance> persons = new ArrayList<>();

		try (ResourceIterator<Instance> it = instances.iterator()) {
			while (it.hasNext()) {
				persons.add(it.next());
			}
		}

		// only Yoda should be contained
		assertThat(persons).hasSize(1).allSatisfy(XLSReaderTestUtil::verifyYoda);
	}

	// helpers

	private Schema readXLSSchema(String sourceLocation, int sheetIndex, String typeName,
			String paramPropertyType) throws Exception {

		XLSSchemaReader schemaReader = new XLSSchemaReader();
		schemaReader.setSource(
				new DefaultInputSupplier(getClass().getResource(sourceLocation).toURI()));
		schemaReader.setParameter(InstanceTableIOConstants.SHEET_INDEX, Value.of(sheetIndex));
		schemaReader.setParameter(CommonSchemaConstants.PARAM_TYPENAME, Value.of(typeName));
		schemaReader.setParameter(AbstractTableSchemaReader.PARAM_PROPERTYTYPE,
				Value.of(paramPropertyType));

		IOReport report = schemaReader.execute(null);
		assertTrue("Schema import was not successfull.", report.isSuccess());

		return schemaReader.getSchema();
	}

	private InstanceCollection readXLSInstances(String sourceLocation, int sheetIndex,
			String typeName, boolean skipFirst, Schema sourceSchema) throws Exception {

		InstanceReader instanceReader = new XLSInstanceReader();
		instanceReader.setSource(
				new DefaultInputSupplier(getClass().getResource(sourceLocation).toURI()));
		instanceReader.setParameter(InstanceTableIOConstants.SHEET_INDEX, Value.of(sheetIndex));
		instanceReader.setParameter(CommonSchemaConstants.PARAM_TYPENAME, Value.of(typeName));
		instanceReader.setParameter(CommonSchemaConstants.PARAM_SKIP_N_LINES, Value.of(skipFirst));
		instanceReader.setSourceSchema(sourceSchema);

		// Test instances
		IOReport report = instanceReader.execute(null);
		assertTrue("Data import was not successfull.", report.isSuccess());

		return instanceReader.getInstances();
	}

	private InstanceCollection readXLSInstances(String sourceLocation, int sheetIndex,
			String typeName, int skipN, Schema sourceSchema) throws Exception {

		InstanceReader instanceReader = new XLSInstanceReader();
		instanceReader.setSource(
				new DefaultInputSupplier(getClass().getResource(sourceLocation).toURI()));
		instanceReader.setParameter(InstanceTableIOConstants.SHEET_INDEX, Value.of(sheetIndex));
		instanceReader.setParameter(CommonSchemaConstants.PARAM_TYPENAME, Value.of(typeName));
		instanceReader.setParameter(CommonSchemaConstants.PARAM_SKIP_N_LINES, Value.of(skipN));
		instanceReader.setSourceSchema(sourceSchema);

		// Test instances
		IOReport report = instanceReader.execute(null);
		assertTrue("Data import was not successfull.", report.isSuccess());

		return instanceReader.getInstances();
	}

	private InstanceCollection readXLSInstances(String sourceLocation, Schema sourceSchema,
			Consumer<XLSInstanceReader> configureReader) throws Exception {
		XLSInstanceReader instanceReader = new XLSInstanceReader();
		instanceReader.setSource(
				new DefaultInputSupplier(getClass().getResource(sourceLocation).toURI()));
		instanceReader.setParameter(CommonSchemaConstants.PARAM_TYPENAME, Value.of(typeName));
		instanceReader.setSourceSchema(sourceSchema);

		if (configureReader != null) {
			configureReader.accept(instanceReader);
		}

		// Test instances
		IOReport report = instanceReader.execute(null);
		assertTrue("Data import was not successfull.", report.isSuccess());

		return instanceReader.getInstances();
	}

	/**
	 * Test - read a sample xls schema and data from same file and sheet (simple
	 * io test). Check the type, check the properties, check the values of the
	 * properties, check the datatype of the properties
	 * 
	 * @throws Exception , if an error occurs
	 */
	@Test
	public void testReadSimpleWithDate() throws Exception {
		// Define test data
		String typeName = "item";
		String[] properties = { "number", "name", "desc", "date" };
		String[] dataFirstColumn = { "1234", "Glasses", "Pair of", "12.12.2023" };
		String dateFormatter = "dd.MM.yyyy";
		String sourceLocation = "/data/simpleOneSheetDate.xls";

		// Read Schema
		Schema schema = readXLSSchemaDate(sourceLocation, 0, typeName, dateFormatter,
				"java.lang.String,java.lang.String,java.lang.String,java.lang.String");

		// Read Instances (without header)
		InstanceCollection instances = readXLSInstances("/data/simpleOneSheetDate.xls", 0, typeName,
				1, schema);

		// Check the values of the date property in each instance
		Iterator<Instance> instanceIt = instances.iterator();
		while (instanceIt.hasNext()) {
			Instance instance = instanceIt.next();
			// Get the value of the date property
			Object[] value = instance.getProperty(QName.valueOf(properties[properties.length - 1]));

			// Ensure the value is not null
			assertNotNull("Date property value is null", value);

			// Ensure the value is an array with at least one element
			assertTrue("Date property value is not an array or is empty", value.length > 0);

			// Check the date string format
			String dateString = (String) value[0];
			assertTrue("Date string format is incorrect: " + dateString,
					isStringDate(dateString, dateFormatter));
		}

	}

	private Schema readXLSSchemaDate(String sourceLocation, int sheetIndex, String typeName,
			String dateFormatter, String paramPropertyType) throws Exception {

		XLSSchemaReader schemaReader = new XLSSchemaReader();
		schemaReader.setSource(
				new DefaultInputSupplier(getClass().getResource(sourceLocation).toURI()));
		schemaReader.setParameter(InstanceTableIOConstants.SHEET_INDEX, Value.of(sheetIndex));
		schemaReader.setParameter(CommonSchemaConstants.PARAM_TYPENAME, Value.of(typeName));
		schemaReader.setParameter(AbstractTableSchemaReader.PARAM_PROPERTYTYPE,
				Value.of(paramPropertyType));
		schemaReader.setParameter(CSVConstants.PARAMETER_DATE_FORMAT, Value.of(dateFormatter));

		IOReport report = schemaReader.execute(null);
		assertTrue("Schema import was not successfull.", report.isSuccess());

		return schemaReader.getSchema();
	}

	/**
	 * @param input String
	 * @param dateFormatter date formatter
	 * @return true is the input String is of type Date
	 */
	public boolean isStringDate(String input, String dateFormatter) {
		// Define the date format you expect
		SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatter);
		dateFormat.setLenient(false); // Disable lenient parsing

		try {
			// Try parsing the input string as a date
			Date parsedDate = dateFormat.parse(input);
			return true; // Parsing successful, input is a valid date
		} catch (ParseException e) {
			return false; // Parsing failed, input is not a valid date
		}
	}

}
