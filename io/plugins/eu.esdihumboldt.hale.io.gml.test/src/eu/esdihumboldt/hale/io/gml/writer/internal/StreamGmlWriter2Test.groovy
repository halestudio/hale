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

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue

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
	private Instance writeValidateAndLoad(Instance instance, Schema schema, boolean expectWriteFail = false) {
		// write to file
		InstanceWriter writer = new GmlInstanceWriter()
		writer.setParameter(GmlInstanceWriter.PARAM_PRETTY_PRINT, Value.of((Boolean)true))
		writer.setParameter(GeoInstanceWriter.PARAM_UNIFY_WINDING_ORDER, Value.simple('noChanges'))
		writer.setInstances(new DefaultInstanceCollection([instance]))
		DefaultSchemaSpace schemaSpace = new DefaultSchemaSpace()
		schemaSpace.addSchema(schema)
		writer.setTargetSchema(schemaSpace)
		File outFile = File.createTempFile('gml-writer', '.gml')
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
		assertTrue("Expected GML output to be valid", valReport.isSuccess())

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
}
