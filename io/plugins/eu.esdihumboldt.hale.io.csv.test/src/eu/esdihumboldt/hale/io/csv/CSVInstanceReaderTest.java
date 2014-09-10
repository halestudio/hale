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

package eu.esdihumboldt.hale.io.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.xml.namespace.QName;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.impl.LogProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.test.TestUtil;
import eu.esdihumboldt.hale.io.csv.reader.CommonSchemaConstants;
import eu.esdihumboldt.hale.io.csv.reader.internal.CSVInstanceReader;
import eu.esdihumboldt.hale.io.csv.reader.internal.CSVSchemaReader;

/**
 * Test class for {@link CSVInstanceReader}
 * 
 * @author Yasmina Kammeyer
 */
public class CSVInstanceReaderTest {

	/**
	 * Wait for needed services to be running
	 */
	@BeforeClass
	public static void waitForServices() {
		TestUtil.startConversionService();
	}

	/**
	 * Test - read a sample csv schema and data.
	 * 
	 * @throws Exception , if an error occurs
	 */
	@Test
	public void testReadSimple() throws Exception {

		String typeName = "location";
		String[] properties = { "Name", "Xcoord", "Ycoord", "id" };
		String[] dataFirstColumn = { "test", "12", "16", "1" };
		int numberOfInstances = 2;
		// read Schema ###
		Schema schema = readCSVSchema("/data/test1.csv", typeName,
				"java.lang.String,java.lang.String,java.lang.String,java.lang.String",
				"Name,Xcoord,Ycoord,id", null, null, null);
		// Test properties
		TypeDefinition schemaType = schema.getType(QName.valueOf(typeName));
		// Check every property for their existence
		for (String propertyName : properties) {
			assertEquals(propertyName, schemaType.getChild(QName.valueOf(propertyName))
					.getDisplayName());
		}

		// read Instances ###
		InstanceCollection instances = readCSVInstances("/data/test1.csv", typeName, true, schema,
				null, null, null);
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

	private Schema readCSVSchema(String sourceLocation, String typeName, String paramPropertyType,
			String propertyNames, String seperator, String quote, String escape) throws Exception {

		CSVSchemaReader schemaReader = new CSVSchemaReader();
		schemaReader.setSource(new DefaultInputSupplier(getClass().getResource(sourceLocation)
				.toURI()));
		schemaReader.setParameter(CommonSchemaConstants.PARAM_TYPENAME, Value.of(typeName));
		schemaReader.setParameter(CSVSchemaReader.PARAM_PROPERTY, Value.of(propertyNames));
		schemaReader.setParameter(CSVSchemaReader.PARAM_PROPERTYTYPE, Value.of(paramPropertyType));
		schemaReader.setParameter(CSVSchemaReader.PARAM_SEPARATOR, Value.of(seperator));
		schemaReader.setParameter(CSVSchemaReader.PARAM_QUOTE, Value.of(quote));
		schemaReader.setParameter(CSVSchemaReader.PARAM_ESCAPE, Value.of(escape));

		IOReport report = schemaReader.execute(new LogProgressIndicator());
		assertTrue(report.isSuccess());

		return schemaReader.getSchema();
	}

	private InstanceCollection readCSVInstances(String sourceLocation, String typeName,
			boolean skipFirst, Schema sourceSchema, String seperator, String quote, String escape)
			throws Exception {

		InstanceReader instanceReader = new CSVInstanceReader();
		instanceReader.setSource(new DefaultInputSupplier(getClass().getResource(sourceLocation)
				.toURI()));
		instanceReader.setParameter(CommonSchemaConstants.PARAM_TYPENAME, Value.of(typeName));
		instanceReader.setParameter(CommonSchemaConstants.PARAM_SKIP_FIRST_LINE,
				Value.of(skipFirst));
		instanceReader.setParameter(CSVSchemaReader.PARAM_SEPARATOR, Value.of(seperator));
		instanceReader.setParameter(CSVSchemaReader.PARAM_QUOTE, Value.of(quote));
		instanceReader.setParameter(CSVSchemaReader.PARAM_ESCAPE, Value.of(escape));
		instanceReader.setSourceSchema(sourceSchema);

		// Test instances
		IOReport report = instanceReader.execute(null);
		assertTrue("Data import was not successfull.", report.isSuccess());

		return instanceReader.getInstances();
	}

}
