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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.xml.namespace.QName;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.test.TestUtil;
import eu.esdihumboldt.hale.io.csv.InstanceTableIOConstants;
import eu.esdihumboldt.hale.io.csv.reader.CommonSchemaConstants;
import eu.esdihumboldt.hale.io.csv.reader.internal.AbstractTableSchemaReader;
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

}
