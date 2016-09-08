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

package eu.esdihumboldt.hale.io.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.impl.LogProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.test.TestUtil;
import eu.esdihumboldt.hale.io.csv.reader.CommonSchemaConstants;
import eu.esdihumboldt.hale.io.csv.reader.internal.CSVSchemaReader;

/**
 * Test Class for CSVSchemaReader
 * 
 * @author Kevin Mais
 */
public class CSVSchemaReaderTest {

//	/**
//	 * Wait for needed services to be running
//	 */
//	@BeforeClass
//	public static void waitForServices() {
//		assertTrue("Conversion service not available", OsgiUtils.waitUntil(new Condition() {
//
//			@Override
//			public boolean evaluate() {
//				return HalePlatform.getService(ConversionService.class) != null;
//			}
//		}, 30));
//	}

	/**
	 * Wait for needed services to be running
	 */
	@BeforeClass
	public static void waitForServices() {
		TestUtil.startConversionService();
	}

	/**
	 * Test for given property names and property types
	 * 
	 * @throws Exception the Exception thrown if the test fails
	 */
	@Test
	public void testRead() throws Exception {

		String props = "muh,kuh,bla,blub";

		CSVSchemaReader schemaReader = new CSVSchemaReader();
		schemaReader.setSource(
				new DefaultInputSupplier(getClass().getResource("/data/test1.csv").toURI()));
		schemaReader.setParameter(CommonSchemaConstants.PARAM_TYPENAME, Value.of("TestTyp"));
		schemaReader.setParameter(CSVSchemaReader.PARAM_PROPERTY, Value.of(props));
		schemaReader.setParameter(CSVSchemaReader.PARAM_PROPERTYTYPE,
				Value.of("java.lang.String,java.lang.String,java.lang.String,java.lang.String"));
		schemaReader.setParameter(CSVSchemaReader.PARAM_SEPARATOR, null);
		schemaReader.setParameter(CSVSchemaReader.PARAM_QUOTE, null);
		schemaReader.setParameter(CSVSchemaReader.PARAM_ESCAPE, null);

		IOReport report = schemaReader.execute(new LogProgressIndicator());
		assertTrue(report.isSuccess());

		Schema schema = schemaReader.getSchema();
		assertEquals(1, schema.getMappingRelevantTypes().size());
		TypeDefinition type = schema.getMappingRelevantTypes().iterator().next();
		assertTrue(type.getName().getLocalPart().equals("TestTyp"));
		Iterator<? extends ChildDefinition<?>> it = type.getChildren().iterator();

		while (it.hasNext()) {
			assertTrue(props.contains(it.next().getName().getLocalPart()));
		}
	}

	/**
	 * Test for no given property names and property types (using default
	 * settings)
	 * 
	 * @throws Exception the Exception thrown if the test fails
	 */
	@Test
	public void testRead2() throws Exception {

		String prop = "Name,Xcoord,Ycoord,id";

		CSVSchemaReader schemaReader = new CSVSchemaReader();
		schemaReader.setSource(
				new DefaultInputSupplier(getClass().getResource("/data/test1.csv").toURI()));
		schemaReader.setParameter(CommonSchemaConstants.PARAM_TYPENAME, Value.of("TestTyp"));
		schemaReader.setParameter(CSVSchemaReader.PARAM_PROPERTY, null);
		schemaReader.setParameter(CSVSchemaReader.PARAM_PROPERTYTYPE, null);
		schemaReader.setParameter(CSVSchemaReader.PARAM_SEPARATOR, null);
		schemaReader.setParameter(CSVSchemaReader.PARAM_QUOTE, null);
		schemaReader.setParameter(CSVSchemaReader.PARAM_ESCAPE, null);

		IOReport report = schemaReader.execute(new LogProgressIndicator());
		assertTrue(report.isSuccess());

		Schema schema = schemaReader.getSchema();
		assertEquals(1, schema.getMappingRelevantTypes().size());
		TypeDefinition type = schema.getMappingRelevantTypes().iterator().next();
		assertTrue(type.getName().getLocalPart().equals("TestTyp"));
		Iterator<? extends ChildDefinition<?>> it = type.getChildren().iterator();

		while (it.hasNext()) {
			assertTrue(prop.contains(it.next().getName().getLocalPart()));
		}
	}

	/**
	 * Test for given property names and property types with point as a decimal
	 * divisor
	 * 
	 * @throws Exception the Exception thrown if the test fails
	 */
	@Test
	public void testRead3() throws Exception {
		String props = "A,B,C,D,E";

		CSVSchemaReader schemaReader = new CSVSchemaReader();
		schemaReader.setSource(new DefaultInputSupplier(
				getClass().getResource("/data/test3-pointdecimal.csv").toURI()));
		schemaReader.setParameter(CommonSchemaConstants.PARAM_TYPENAME, Value.of("TestTyp"));
		schemaReader.setParameter(CSVSchemaReader.PARAM_PROPERTY, Value.of(props));
		schemaReader.setParameter(CSVSchemaReader.PARAM_PROPERTYTYPE, Value.of(
				"java.lang.Integer,java.lang.String,java.lang.Float,java.lang.Float,java.lang.String"));
		schemaReader.setParameter(CSVSchemaReader.PARAM_SEPARATOR, Value.of(";"));
		schemaReader.setParameter(CSVSchemaReader.PARAM_QUOTE, null);
		schemaReader.setParameter(CSVSchemaReader.PARAM_ESCAPE, null);
		schemaReader.setParameter(CSVSchemaReader.PARAM_DECIMAL, Value.of("."));

		IOReport report = schemaReader.execute(new LogProgressIndicator());
		assertTrue(report.isSuccess());

		Schema schema = schemaReader.getSchema();
		assertEquals(1, schema.getMappingRelevantTypes().size());
		TypeDefinition type = schema.getMappingRelevantTypes().iterator().next();
		assertTrue(type.getName().getLocalPart().equals("TestTyp"));
		Iterator<? extends ChildDefinition<?>> it = type.getChildren().iterator();

		while (it.hasNext()) {
			assertTrue(props.contains(it.next().getName().getLocalPart()));
		}

	}

	/**
	 * Test for given property names and property types with point as a decimal
	 * divisor
	 * 
	 * @throws Exception the Exception thrown if the test fails
	 */
	@Test
	public void testRead4() throws Exception {
		String props = "A,B,C,D,E";

		CSVSchemaReader schemaReader = new CSVSchemaReader();
		schemaReader.setSource(new DefaultInputSupplier(
				getClass().getResource("/data/test4-commadecimal.csv").toURI()));
		schemaReader.setParameter(CommonSchemaConstants.PARAM_TYPENAME, Value.of("TestTyp"));
		schemaReader.setParameter(CSVSchemaReader.PARAM_PROPERTY, Value.of(props));
		schemaReader.setParameter(CSVSchemaReader.PARAM_PROPERTYTYPE, Value.of(
				"java.lang.Integer,java.lang.String,java.lang.Float,java.lang.Float,java.lang.String"));
		schemaReader.setParameter(CSVSchemaReader.PARAM_SEPARATOR, Value.of(";"));
		schemaReader.setParameter(CSVSchemaReader.PARAM_QUOTE, null);
		schemaReader.setParameter(CSVSchemaReader.PARAM_ESCAPE, null);
		schemaReader.setParameter(CSVSchemaReader.PARAM_DECIMAL, Value.of(","));

		IOReport report = schemaReader.execute(new LogProgressIndicator());
		assertTrue(report.isSuccess());

		Schema schema = schemaReader.getSchema();
		assertEquals(1, schema.getMappingRelevantTypes().size());
		TypeDefinition type = schema.getMappingRelevantTypes().iterator().next();
		assertTrue(type.getName().getLocalPart().equals("TestTyp"));
		Iterator<? extends ChildDefinition<?>> it = type.getChildren().iterator();

		while (it.hasNext()) {
			assertTrue(props.contains(it.next().getName().getLocalPart()));
		}

	}

	/**
	 * Test for no given property names and only 2 (of 4) given property types
	 * (if there are not given 0 or maximum, in this case 4, property types we
	 * expect an error)
	 * 
	 * @throws Exception the Exception thrown if the test fails
	 */
	public void failTest() throws Exception {

		CSVSchemaReader schemaReader = new CSVSchemaReader();
		schemaReader.setSource(
				new DefaultInputSupplier(getClass().getResource("/data/test1.csv").toURI()));
		schemaReader.setParameter(CommonSchemaConstants.PARAM_TYPENAME, Value.of("TestTyp"));
		schemaReader.setParameter(CSVSchemaReader.PARAM_PROPERTY, null);
		schemaReader.setParameter(CSVSchemaReader.PARAM_PROPERTYTYPE,
				Value.of("java.lang.String,java.lang.String"));
		schemaReader.setParameter(CSVSchemaReader.PARAM_SEPARATOR, null);
		schemaReader.setParameter(CSVSchemaReader.PARAM_QUOTE, null);
		schemaReader.setParameter(CSVSchemaReader.PARAM_ESCAPE, null);

		IOReport report = schemaReader.execute(new LogProgressIndicator());

		assertFalse(report.getErrors().isEmpty());
		assertFalse(report.isSuccess());

	}

	/**
	 * Test for no given type name. So we expect the reporter not to be
	 * successful.
	 * 
	 * @throws Exception the Exception thrown if the test fails
	 */
	@Test
	public void failTest2() throws Exception {

		CSVSchemaReader schemaReader2 = new CSVSchemaReader();
		schemaReader2.setSource(
				new DefaultInputSupplier(getClass().getResource("/data/test1.csv").toURI()));
		schemaReader2.setParameter(CommonSchemaConstants.PARAM_TYPENAME, null);

		IOReport report = schemaReader2.execute(new LogProgressIndicator());

		assertFalse(report.isSuccess());
	}
}
