/*
 * Copyright (c) 2016 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.gml.writer.internal

import static org.junit.Assert.*

import java.util.function.Consumer

import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.MultiLineString

import eu.esdihumboldt.hale.common.core.io.Value
import eu.esdihumboldt.hale.common.core.io.report.IOReport
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier
import eu.esdihumboldt.hale.common.core.io.supplier.Locatable
import eu.esdihumboldt.hale.common.instance.geometry.GeometryUtil
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder
import eu.esdihumboldt.hale.common.instance.io.GeoInstanceWriter
import eu.esdihumboldt.hale.common.instance.io.InstanceWriter
import eu.esdihumboldt.hale.common.instance.model.Instance
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstanceCollection
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchemaSpace
import eu.esdihumboldt.hale.common.test.TestUtil
import eu.esdihumboldt.hale.io.gml.writer.GmlInstanceWriter
import eu.esdihumboldt.hale.io.xsd.model.XmlIndex
import eu.esdihumboldt.hale.io.xsd.reader.XmlSchemaReader
import groovy.transform.CompileStatic

/**
 * GML writer tests with a focus on geometries.
 * 
 * @author Simon Templer
 */
class StreamGmlWriter2Test {

	/**
	 * If temporary files shall be deleted
	 */
	private static final boolean DEL_TEMP_FILES = true;

	/**
	 * Prepare the conversion service
	 */
	@BeforeClass
	public static void initAll() {
		TestUtil.startConversionService();
	}

	/**
	 * Test if a codespace attribute is automatically added to a GML identifier within an INSPIRE type.
	 * @throws Exception
	 */
	@Test
	public void testInspireCodespaceAutoAdd() throws Exception {
		// load schema
		Schema schema = loadSchema(URI.create("https://inspire.ec.europa.eu/schemas/us-govserv/4.0/GovernmentalServices.xsd"))
		// create instance
		Instance instance = new InstanceBuilder(types: schema).GovernmentalServiceType {
			identifier('myid')
		}

		// write file and load instance again
		Instance loaded = writeValidateAndLoad(instance, schema, false, false, { writer ->
			// enable adding code space automagically (Note: this is also the default!)
			writer.setParameter(StreamGmlWriter.PARAM_ADD_CODESPACE, Value.TRUE)
		})

		def codeSpace = loaded.p.identifier.codeSpace.value()
		assertEquals(StreamGmlWriter.INSPIRE_IDENTIFIER_CODESPACE, codeSpace?.toString())
	}

	/**
	 * Test if a codespace attribute is NOT added to a GML identifier when this is NOT within an INSPIRE type.
	 * @throws Exception
	 */
	@Test
	public void testNotInspireCodespaceNotAdd() throws Exception {
		// load non-INSPIRE schema
		Schema schema = loadSchema(URI.create("http://schemas.opengis.net/om/2.0/observation.xsd"))

		// create instance
		Instance instance = new InstanceBuilder(types: schema).OM_ObservationType {
			identifier('myid')
		}

		// write file and load instance again
		Instance loaded = writeValidateAndLoad(instance, schema, false, false, { writer ->
			// enable adding code space automagically (Note: this is also the default!)
			writer.setParameter(StreamGmlWriter.PARAM_ADD_CODESPACE, Value.TRUE)
		})


		def codeSpace = loaded.p.identifier.codeSpace.value()
		assertNull(codeSpace)
	}

	/**
	 * Test that the behavior to add a codespace attribute automatically to a GML identifier within an INSPIRE type is enabled by default.
	 * @throws Exception
	 */
	@Test
	public void testInspireCodespaceAutoAddDefault() throws Exception {
		// load schema
		Schema schema = loadSchema(URI.create("https://inspire.ec.europa.eu/schemas/us-govserv/4.0/GovernmentalServices.xsd"))
		System.out.print("Schema " + schema);

		// create instance
		Instance instance = new InstanceBuilder(types: schema).GovernmentalServiceType {
			identifier('myid')
		}

		// write file and load instance again
		Instance loaded = writeValidateAndLoad(instance, schema, false, false, null)

		def codeSpace = loaded.p.identifier.codeSpace.value()
		assertEquals(StreamGmlWriter.INSPIRE_IDENTIFIER_CODESPACE, codeSpace?.toString())
	}

	/**
	 * Test that a codespace attribute of a GML identifier within an INSPIRE type is not overridden.
	 * @throws Exception
	 */
	@Test
	public void testInspireCodespaceAutoAddNoOverride() throws Exception {
		// load schema
		Schema schema = loadSchema(URI.create("https://inspire.ec.europa.eu/schemas/us-govserv/4.0/GovernmentalServices.xsd"))

		// create instance
		def mycs = "iknowbetter"
		Instance instance = new InstanceBuilder(types: schema).GovernmentalServiceType {
			identifier('myid') {
				codeSpace(mycs)
			}
		}

		// write file and load instance again
		Instance loaded = writeValidateAndLoad(instance, schema, false, false, { writer ->
			// enable adding code space automagically (Note: this is also the default!)
			writer.setParameter(StreamGmlWriter.PARAM_ADD_CODESPACE, Value.TRUE)
		})

		def codeSpace = loaded.p.identifier.codeSpace.value()
		assertEquals(mycs, codeSpace?.toString())
	}

	/**
	 * Test that no codespace attribute is added automatically to a GML identifier with an INSPIRE type when this is not enabled.
	 * @throws Exception
	 */
	@Test
	public void testInspireCodespaceNoAutoAdd() throws Exception {
		// load schema
		Schema schema = loadSchema(URI.create("https://inspire.ec.europa.eu/schemas/us-govserv/4.0/GovernmentalServices.xsd"))

		// create instance
		Instance instance = new InstanceBuilder(types: schema).GovernmentalServiceType {
			identifier('myid')
		}

		// write file and load instance again
		Instance loaded = writeValidateAndLoad(instance, schema, false, false, { writer ->
			// disable adding code space automagically
			writer.setParameter(StreamGmlWriter.PARAM_ADD_CODESPACE, Value.FALSE)
		})

		def codeSpace = loaded.p.identifier.codeSpace.value()
		assertNull(codeSpace)
	}

	/**
	 * Test writing a {@link LineString} to a GML 3.2 Curve
	 *
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testGeometryPrimitive_32_Curve_LineString() throws Exception {
		// create the geometry
		LineString geom = StreamGmlWriterTest.createLineString(0.0);

		// load the target schema
		Schema schema = loadSchema(getClass().getResource("/data/geom_schema/geom-gml32.xsd").toURI())
		// create the instance
		Instance instance = new InstanceBuilder(types: schema).PrimitiveTestType {
			geometry { Curve( geom ) }
		}

		// write file, validate and load it
		Instance loaded = writeValidateAndLoad(instance, schema)

		// assert there is a geometry present in the loaded instance
		assertGeometry(loaded)

		// compare geometries
		compareGeometries(geom, loaded)
	}

	/**
	 * Test writing a {@link LineString} to a GML 3.2 Curve
	 *
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testGeometryPrimitive_32_CompositeCurve_LineString() throws Exception {
		// create the geometry
		LineString geom = StreamGmlWriterTest.createLineString(0.0);

		// load the target schema
		Schema schema = loadSchema(getClass().getResource("/data/geom_schema/geom-gml32.xsd").toURI())
		// create the instance
		Instance instance = new InstanceBuilder(types: schema).PrimitiveTestType {
			geometry { CompositeCurve( geom ) }
		}

		// write file, validate and load it
		Instance loaded = writeValidateAndLoad(instance, schema)

		// assert there is a geometry present in the loaded instance
		assertGeometry(loaded)

		// compare geometries
		compareGeometries(geom, loaded)
	}

	/**
	 * Test writing a {@link LineString} to a GML 3.2 Curve
	 *
	 * @throws Exception if an error occurs
	 */
	@Ignore("MultiLineStrings are combined to a LineString for a Curve if possible")
	@Test
	public void testGeometryPrimitive_32_Curve_MultiLineString() throws Exception {
		// create the geometry
		MultiLineString geom = StreamGmlWriterTest.createCurve()

		// load the target schema
		Schema schema = loadSchema(getClass().getResource("/data/geom_schema/geom-gml32.xsd").toURI())
		// create the instance
		Instance instance = new InstanceBuilder(types: schema).PrimitiveTestType {
			geometry { Curve( geom ) }
		}

		// write file, validate and load it
		Instance loaded = writeValidateAndLoad(instance, schema)

		// assert there is a geometry present in the loaded instance
		assertGeometry(loaded)

		// compare geometries
		compareGeometries(geom, loaded)
	}

	/**
	 * Test writing a {@link LineString} to a GML 3.2 Curve
	 *
	 * @throws Exception if an error occurs
	 */
	@Ignore("MultiLineStrings are combined to a LineString for a CompositeCurve if possible")
	@Test
	public void testGeometryPrimitive_32_CompositeCurve_MultiLineString() throws Exception {
		// create the geometry
		MultiLineString geom = StreamGmlWriterTest.createCurve()

		// load the target schema
		Schema schema = loadSchema(getClass().getResource("/data/geom_schema/geom-gml32.xsd").toURI())
		// create the instance
		Instance instance = new InstanceBuilder(types: schema).PrimitiveTestType {
			geometry { CompositeCurve( geom ) }
		}

		// write file, validate and load it
		Instance loaded = writeValidateAndLoad(instance, schema)

		// assert there is a geometry present in the loaded instance
		assertGeometry(loaded)

		// compare geometries
		compareGeometries(geom, loaded)
	}

	// helpers

	@CompileStatic
	private void compareGeometries(Geometry expected, Instance other) {
		Collection<GeometryProperty<?>> geomsOther = GeometryUtil.getAllGeometries(other);
		assertEquals(1, geomsOther.size())

		StreamGmlWriterTest.matchGeometries(expected, geomsOther[0].getGeometry()) // using getter instead of property circumvents type inferring problems of Groovy 2.3.11
	}

	@CompileStatic
	private void assertGeometry(Instance instance) {
		Collection<GeometryProperty<?>> geoms = GeometryUtil.getAllGeometries(instance);
		assertEquals(1, geoms.size())
	}

	@CompileStatic
	private Schema loadSchema(URI targetSchema) {
		// load the sample schema
		XmlSchemaReader reader = new XmlSchemaReader();
		reader.setSharedTypes(null);
		reader.setSource(new DefaultInputSupplier(targetSchema));
		IOReport schemaReport = reader.execute(null);
		assertTrue(schemaReport.isSuccess());
		XmlIndex schema = reader.getSchema();

		return schema;
	}

	@CompileStatic
	private Instance writeValidateAndLoad(Instance instance, Schema schema, boolean expectWriteFail = false,
			boolean mustValidate = true, Consumer<InstanceWriter> adaptWriter = null) {
		// write to file
		InstanceWriter writer = new GmlInstanceWriter()
		writer.setParameter(GmlInstanceWriter.PARAM_PRETTY_PRINT, Value.of((Boolean)true))
		writer.setParameter(GeoInstanceWriter.PARAM_UNIFY_WINDING_ORDER, Value.simple('noChanges'))
		if (adaptWriter != null) {
			adaptWriter.accept(writer);
		}
		writer.setInstances(new DefaultInstanceCollection([instance]))
		DefaultSchemaSpace schemaSpace = new DefaultSchemaSpace()
		schemaSpace.addSchema(schema)
		writer.setTargetSchema(schemaSpace)
		File outFile = File.createTempFile('gml-writer', '.gml')
		println outFile.absolutePath
		writer.setTarget(new FileIOSupplier(outFile))

		IOReport report = writer.execute(null); // new LogProgressIndicator());
		if (expectWriteFail) {
			assertFalse("Writing the GML output should not be successful", report.isSuccess())
			return null;
		}
		else {
			assertTrue("Writing the GML output not successful", report.isSuccess())
		}

		List<? extends Locatable> validationSchemas = writer.getValidationSchemas()

		System.out.println(outFile.getAbsolutePath());

		IOReport valReport = StreamGmlWriterTest.validate(outFile.toURI(), validationSchemas)
		if (mustValidate) {
			assertTrue("Expected GML output to be valid", valReport.isSuccess())
		}

		// load file
		InstanceCollection loaded = StreamGmlWriterTest.loadGML(outFile.toURI(), schema)

		def iterator = loaded.iterator()
		Instance result
		try {
			assertTrue('No source instance found', iterator.hasNext())
			result = iterator.next()
		} finally {
			iterator.close()
			// delete temporary file
			if (DEL_TEMP_FILES) {
				outFile.delete()
			}
		}

		result
	}

	@Test
	void testNumbers() throws Exception {
		/**test DOUBLE numbers*/
		testNumber("doubleNumber", 12345.123456789, null)
		testNumber("doubleNumber", 12345.123456789, "12345.123456789")
		double doubleNumber = 123456789.123456789
		double expected = java.lang.Double.parseDouble(doubleNumber.toString())
		testNumber("doubleSimpleNumber", doubleNumber, expected)
		testNumber("doubleSimpleNumber", doubleNumber, expected.toString())
		testFloatingPointNumber(doubleNumber, "doubleNumber")

		/**test INT, SHORT numbers*/
		testNumber("integerNumber", 12345, null)
		testNumber("integerNumber", 12345, "12345")
		testNumber("shortNumber", 12345 as short, null)
		testNumber("shortNumber", 12345 as short, "12345")

		float smallDecimal = 0.12345F
		float largeDecimal = 12345.6789F
		float verySmall = 0.0000001F
		float veryLarge = 12345678.9F
		float negative = -98765.4321F
		float scientific = 1.23E4F
		float maxFloat = Float.MAX_VALUE
		float minFloat = Float.MIN_VALUE

		// for Float and Double we are expecting to use using scientific notation
		testFloatingPointNumber(smallDecimal, "floatNumber")
		testNumber("floatNumber", smallDecimal, "0.12345")
		testFloatingPointNumber(largeDecimal, "floatNumber")
		testNumber("floatNumber", largeDecimal, "12345.679")
		testFloatingPointNumber(verySmall, "floatNumber")
		testNumber("floatNumber", verySmall, "1.0E-7")
		testFloatingPointNumber(veryLarge, "floatNumber")
		testNumber("floatNumber", veryLarge, "1.2345679E7")
		testFloatingPointNumber(negative, "floatNumber")
		testFloatingPointNumber(scientific, "floatNumber")
		testFloatingPointNumber(maxFloat, "floatNumber")
		testFloatingPointNumber(minFloat, "floatNumber")

		/**test LONG numbers*/
		long smallPositive = 12345L
		long largePositive = 1234567890123456789L
		long smallNegative = -12345L
		long largeNegative = -1234567890123456789L
		long maxLong = Long.MAX_VALUE
		long minLong = Long.MIN_VALUE
		long zero = 0L
		long powerOfTwo = 1024L
		long negativePowerOfTwo = -1024L
		long nearMaxLong = 9223372036854775806L
		long nearMinLong = -9223372036854775807L

		testNumber("longNumber", smallPositive, "12345")
		testNumber("longNumber", largePositive, "1234567890123456789")
		testNumber("longNumber", smallNegative, null)
		testNumber("longNumber", largeNegative, null)
		testNumber("longNumber", maxLong, null)
		testNumber("longNumber", minLong, null)
		testNumber("longNumber", zero, null)
		testNumber("longNumber", powerOfTwo, null)
		testNumber("longNumber", negativePowerOfTwo, null)
		testNumber("longNumber", nearMaxLong, null)
		testNumber("longNumber", nearMinLong, null)

		/**test DECIMAL numbers*/
		testNumber("decimalNumber", new BigDecimal("1234567890.123456789"), null)
		testNumber("decimalNumber", new BigDecimal(doubleNumber), null)

		doubleNumber = 1.23456789123456789E8
		testNumber("decimalNumber", new BigDecimal(doubleNumber), null)

		String numberAsString = "1.23456789123456789E8"
		testNumber("decimalNumber", new BigDecimal(numberAsString), null)
		testNumber("decimalNumber", new BigDecimal(numberAsString), "123456789.123456789")
	}

	void testNumber(String elementName, number, expected) throws Exception {
		def schema = loadSchema(getClass().getResource("/data/numbers/numbers.xsd").toURI())

		def instance = new InstanceBuilder(types: schema).PrimitiveTestType {
			"$elementName"(number)
		}

		def writer = new GmlInstanceWriter()
		File outFile = writeFile(instance, schema, writer)
		def xmlFile = outFile.getAbsolutePath()
		def xml = new XmlSlurper().parse(xmlFile)

		String actualText = xml.featureMember.PrimitiveTest."$elementName".text()
		if (expected != null) {
			if (expected instanceof String) {
				assertEquals(expected.toString(), xml.featureMember.PrimitiveTest."$elementName".text())
			} else {
				compareValues(expected, actualText)
			}
		} else {
			compareValues(number, actualText)
		}


		if (DEL_TEMP_FILES) {
			outFile.delete()
		}
	}

	@CompileStatic
	private File writeFile(Instance instance, Schema schema, InstanceWriter writer) {
		writer.setParameter(GmlInstanceWriter.PARAM_PRETTY_PRINT, Value.of((Boolean)true))
		writer.setParameter(GeoInstanceWriter.PARAM_UNIFY_WINDING_ORDER, Value.simple('noChanges'))
		writer.setInstances(new DefaultInstanceCollection([instance]))
		DefaultSchemaSpace schemaSpace = new DefaultSchemaSpace()
		schemaSpace.addSchema(schema)
		writer.setTargetSchema(schemaSpace)
		File outFile = File.createTempFile('gml-writer', '.gml')
		writer.setTarget(new FileIOSupplier(outFile))

		IOReport report = writer.execute(null);
		List<? extends Locatable> validationSchemas = writer.getValidationSchemas()

		return outFile;
	}

	void testFloatingPointNumber(floatNumber, type) {
		def expected = java.lang.Double.parseDouble(floatNumber.toString())
		println expected
		testNumber(type, floatNumber, expected)
	}

	void compareValues(expected, actualText) {
		if (expected instanceof BigDecimal) {
			assertEquals(((BigDecimal) expected), new BigDecimal(actualText))
		} else if (expected instanceof Double || expected instanceof Float) {
			assertEquals(expected.doubleValue(), new Double(actualText).doubleValue(), 0.0000001)
		} else if (expected instanceof Long) {
			assertEquals(expected.longValue(), new Long(actualText).longValue())
		} else if (expected instanceof Integer) {
			assertEquals(expected.intValue(), (new Integer(actualText)).intValue())
		} else if (expected instanceof BigInteger) {
			assertEquals(expected, new BigInteger(actualText))
		} else if (expected instanceof Short) {
			assertEquals(expected.shortValue(), (new Short(actualText)).shortValue())
		} else {
			assertEquals(expected, actual)
		}
	}
}
