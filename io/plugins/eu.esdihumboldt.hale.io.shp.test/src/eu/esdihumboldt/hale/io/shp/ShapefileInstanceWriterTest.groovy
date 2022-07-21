/*
 * Copyright (c) 2021 wetransform GmbH
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

package eu.esdihumboldt.hale.io.shp

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Instant
import java.time.LocalDate
import java.util.function.Consumer

import org.junit.Test
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.MultiPolygon
import org.locationtech.jts.geom.Point

import eu.esdihumboldt.cst.functions.geometric.GeometryHelperFunctions
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException
import eu.esdihumboldt.hale.common.core.io.impl.LogProgressIndicator
import eu.esdihumboldt.hale.common.core.io.report.IOReport
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier
import eu.esdihumboldt.hale.common.instance.geometry.DefaultGeometryProperty
import eu.esdihumboldt.hale.common.instance.geometry.impl.CodeDefinition
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder
import eu.esdihumboldt.hale.common.instance.model.Instance
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty
import eu.esdihumboldt.hale.common.schema.groovy.SchemaBuilder
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchemaSpace
import eu.esdihumboldt.hale.io.shp.reader.internal.ShapeInstanceReader
import eu.esdihumboldt.hale.io.shp.reader.internal.ShapeSchemaReader
import eu.esdihumboldt.hale.io.shp.writer.ShapefileInstanceWriter
import groovy.transform.CompileStatic

class ShapefileInstanceWriterTest {

	static GeometryProperty<? extends Geometry> createGeometry(String wkt, Object crs) {
		return GeometryHelperFunctions._with([geometry: wkt, crs: crs])
	}


	@CompileStatic
	public static Schema loadSchema(File file)
	throws IOProviderConfigurationException, IOException {
		ShapeSchemaReader reader = new ShapeSchemaReader();

		reader.setSchemaSpace(SchemaSpaceID.SOURCE);
		reader.setSource(new FileIOSupplier(file));

		IOReport report = reader.execute(new LogProgressIndicator());

		assertTrue(report.isSuccess());
		assertTrue(report.getErrors().isEmpty());

		return reader.getSchema();
	}

	@CompileStatic
	static InstanceCollection loadInstances(File file) {
		Schema schema = loadSchema(file)

		ShapeInstanceReader reader = new ShapeInstanceReader()

		reader.setSource(new FileIOSupplier(file))
		reader.setSourceSchema(schema)

		IOReport report = reader.execute(new LogProgressIndicator())

		assertTrue(report.isSuccess())
		assertTrue(report.getErrors().isEmpty())

		return reader.getInstances();
	}

	@CompileStatic
	static InstanceCollection loadInstances(File file, String additionalName) {
		URI location = file.toURI()
		String filePath = Paths.get(location).getParent().toString();
		String filenameOnly = Paths.get(location).getFileName().toString();

		filenameOnly = filenameOnly.substring(0, filenameOnly.lastIndexOf("."));
		String filename = filePath + "/" + filenameOnly + "_" + additionalName + ".shp";
		file = new File(filename)
		Schema schema = loadSchema(file)

		ShapeInstanceReader reader = new ShapeInstanceReader()

		reader.setSource(new FileIOSupplier(file))
		reader.setSourceSchema(schema)

		IOReport report = reader.execute(new LogProgressIndicator())

		assertTrue(report.isSuccess())
		assertTrue(report.getErrors().isEmpty())

		return reader.getInstances();
	}


	/**
	 * Write an instance collection to a Shapefile.
	 */
	@CompileStatic
	static IOReport writeInstances(File file, Schema schema, InstanceCollection instances,
			Consumer<ShapefileInstanceWriter> configurator = null) {

		ShapefileInstanceWriter writer = new ShapefileInstanceWriter();

		writer.setTarget(new FileIOSupplier(file))
		def ss = new DefaultSchemaSpace()
		ss.addSchema(schema)
		writer.setTargetSchema(ss)
		writer.setInstances(instances)

		if (configurator != null) {
			configurator.accept(writer)
		}

		IOReport report = writer.execute(new LogProgressIndicator())

		assertTrue(report.isSuccess())
		assertTrue(report.getErrors().isEmpty())

		return report;
	}

	/**
	 * Write an instance collection to a Shapefile.
	 */
	@CompileStatic
	static IOReport writeInstancesWithReporterErrors(File file, Schema schema, InstanceCollection instances,
			Consumer<ShapefileInstanceWriter> configurator = null) {

		ShapefileInstanceWriter writer = new ShapefileInstanceWriter();

		writer.setTarget(new FileIOSupplier(file))
		def ss = new DefaultSchemaSpace()
		ss.addSchema(schema)
		writer.setTargetSchema(ss)
		writer.setInstances(instances)

		if (configurator != null) {
			configurator.accept(writer)
		}

		IOReport report = writer.execute(new LogProgressIndicator())

		assertTrue(report.isSuccess())
		assertFalse(report.getErrors().isEmpty())

		return report;
	}



	@CompileStatic
	static void withNewShapefile(Schema schema, InstanceCollection instances, Consumer<File> handler,
			Consumer<ShapefileInstanceWriter> configurator = null) {
		Path tmpDir = Files.createTempDirectory("ShapefileTest")
		Path tmpFile = Files.createTempFile(tmpDir,'new', '.shp')

		try {
			println "Temporary file is $tmpFile"
			writeInstances(tmpFile.toFile(), schema, instances, configurator)
			handler.accept(tmpFile.toFile())
		} finally {
			tmpDir.deleteDir()
		}
	}

	@CompileStatic
	static void withNewShapefileWithReporterErrors(Schema schema, InstanceCollection instances, Consumer<File> handler,
			Consumer<ShapefileInstanceWriter> configurator = null) {
		Path tmpDir = Files.createTempDirectory("ShapefileTest")
		Path tmpFile = Files.createTempFile(tmpDir,'new', '.shp')

		try {
			println "Temporary file is $tmpFile"
			writeInstancesWithReporterErrors(tmpFile.toFile(), schema, instances, configurator)
			handler.accept(tmpFile.toFile())
		} finally {
			tmpDir.deleteDir()
		}
	}

	@Test
	public void simpleSchemaWriteTest() throws Exception {
		ShapeSchemaReader schemaReader = new ShapeSchemaReader();
		schemaReader.setSource(new DefaultInputSupplier(getClass().getResource(
				"/testdata/GN_Point/GN_Point.shp").toURI()));

		schemaReader.validate();
		IOReport report = schemaReader.execute(null);
		assertTrue(report.isSuccess());

		Schema schema = schemaReader.getSchema();

		ShapeInstanceReader instanceReader = new ShapeInstanceReader();
		instanceReader.setSource(new DefaultInputSupplier(getClass().getResource(
				"/testdata/GN_Point/GN_Point.shp").toURI()));
		instanceReader.setSourceSchema(schema);

		instanceReader.validate();
		report = instanceReader.execute(null);
		assertTrue(report.isSuccess());

		InstanceCollection instances = instanceReader.getInstances();

		assertFalse(instances.isEmpty());

		withNewShapefile(schema, instances) { file ->
			// load instances again and test
			def loaded = loadInstances(file)

			int num = 0
			loaded.iterator().withCloseable {
				while (it.hasNext()) {
					Instance inst = it.next()
					num++
				}
			}
			// 593 instances were loaded
			assertEquals(593, num)
		}
	}



	@Test
	void testSingleGeometry() {

		Schema schema = new SchemaBuilder().schema {
			city {
				name(String)
				population(Integer)
				location(GeometryProperty)
			}
		}

		InstanceCollection instances = new InstanceBuilder(types: schema).createCollection {
			city {
				name 'Darmstadt'
				population 158254
				location( createGeometry('POINT(49.872833 8.651222)', 4326) )
			}

			city {
				name 'München'
				population 1471508
				location( createGeometry('POINT(48.137222 11.575556)', 4326) )
			}
		}

		withNewShapefile(schema, instances) { file ->
			// load instances again and test

			def loaded = loadInstances(file)

			int num = 0
			loaded.iterator().withCloseable {
				while (it.hasNext()) {
					Instance inst = it.next()
					num++

					// test instance
					def typeName = inst.getDefinition().getName().getLocalPart()
					def the_geom = inst.p.the_geom.value()
					assert the_geom
					assert the_geom instanceof GeometryProperty
					def crs = the_geom.getCRSDefinition()
					assert crs
					def jts = the_geom.geometry
					assert jts instanceof Point
					def name = inst.p.name.value()
					assert name
					switch (name) {
						case 'Darmstadt':
							assert inst.p.population.value() == 158254
							break
						case 'München':
							assert inst.p.population.value() == 1471508
							break
						default:
							throw new IllegalStateException("Unexpected type $typeName")
					}
				}
			}
			assertEquals(2, num)
		}

	}

	@Test
	void testSinglePolyGeometry() {

		GeometryFactory gf = new GeometryFactory()
		def poly = gf.createPolygon([
			new Coordinate(0, 0),
			new Coordinate(1, 0),
			new Coordinate(1, 1),
			new Coordinate(0, 1),
			new Coordinate(0, 0)] as Coordinate[])
		def polyGeom = new DefaultGeometryProperty<Geometry>(new CodeDefinition("EPSG:4326", null), poly)

		Schema schema = new SchemaBuilder().schema {
			city {
				name(String)
				population(Integer)
				location(GeometryProperty)
			}
		}

		InstanceCollection instances = new InstanceBuilder(types: schema).createCollection {
			city {
				name 'Darmstadt'
				population 158254
				location(polyGeom)
			}

			city {
				name 'München'
				population 1471508
				location( polyGeom)
			}
		}

		withNewShapefile(schema, instances) { file ->
			// load instances again and test

			def loaded = loadInstances(file)

			int num = 0
			loaded.iterator().withCloseable {
				while (it.hasNext()) {
					Instance inst = it.next()
					num++

					// test instance
					def typeName = inst.getDefinition().getName().getLocalPart()

					def the_geom = inst.p.the_geom.value()
					assert the_geom
					assert the_geom instanceof GeometryProperty
					def crs = the_geom.getCRSDefinition()
					assert crs
					def jts = the_geom.geometry

					assert jts instanceof MultiPolygon
					def name = inst.p.name.value()
					assert name
					switch (name) {
						case 'Darmstadt':
							assert inst.p.population.value() == 158254
							break
						case 'München':
							assert inst.p.population.value() == 1471508
							break
						default:
							throw new IllegalStateException("Unexpected type $typeName")
					}
				}
			}
			assertEquals(2, num)
		}

	}

	@Test
	void testWriteMultipleGeometries() {
		GeometryFactory gf = new GeometryFactory()
		def poly = gf.createPolygon([
			new Coordinate(0, 0),
			new Coordinate(1, 0),
			new Coordinate(1, 1),
			new Coordinate(0, 1),
			new Coordinate(0, 0)] as Coordinate[])
		def polyGeom = new DefaultGeometryProperty<Geometry>(new CodeDefinition("EPSG:4326", null), poly)

		Schema schema = new SchemaBuilder().schema {
			city {
				name(String)
				population(Integer)
				location(GeometryProperty)
				loc(GeometryProperty)
			}
		}

		InstanceCollection instances = new InstanceBuilder(types: schema).createCollection {
			city {
				name 'Darmstadt'
				population 158254
				location( createGeometry('POINT(49.872833 8.651222)', 4326) )
				loc(polyGeom )
			}

		}

		withNewShapefile(schema, instances) { file ->
			// load instances again and test
			def geomNames = ["Point", "Polygon"]
			//				def loaded = loadInstances(file)
			int num = 0
			for (geom in geomNames) {
				def loaded = loadInstances(file, geom)

				loaded.iterator().withCloseable {
					while (it.hasNext()) {
						Instance inst = it.next()
						num++

						// test instance
						def typeName = inst.getDefinition().getName().getLocalPart()

						def the_geom = inst.p.the_geom.value()
						assert the_geom
						assert the_geom instanceof GeometryProperty
						def crs = the_geom.getCRSDefinition()
						assert crs
						def jts = the_geom.geometry
						assert jts
						def name = inst.p.name.value()
						assert name
						switch (name) {
							case 'Darmstadt':
								assert inst.p.population.value() == 158254
								break
							case 'München':
								assert inst.p.population.value() == 1471508
								break
							default:
								throw new IllegalStateException("Unexpected type $typeName")
						}
					}
				}
			}
			assertEquals(2, num)
		}

	}

	@Test
	void testWriteMultiSchemas() {
		Schema schema = new SchemaBuilder().schema {
			attr1 {
				col1()
				col2()
				location(GeometryProperty)
			}
			abc {
				a(String)
				b(Double)
				c(Long)
				location(GeometryProperty)
			}
		}

		InstanceCollection instances = new InstanceBuilder(types: schema).createCollection {
			attr1 {
				col1('Value 1')
				col2('Value 2')
				location( createGeometry('POINT(49.872833 8.651222)', 4326) )
			}

			abc {
				a('Text')
				b(3.14)
				c(42)
				location( createGeometry('POINT(49.872833 8.651222)', 4326))
			}

			abc {
				a('More text')
				b(1.52)
				c(23)
				location(createGeometry('POINT(48.137222 11.575556)', 4326))
			}
		}

		withNewShapefile(schema, instances) { file ->
			// load instances again and test
			def schemaNames = ["attr1", "abc"]
			int num = 0
			int abcCount = 0
			int attrCount = 0
			for (schemaName in schemaNames) {
				def loaded = loadInstances(file, schemaName)
				loaded.iterator().withCloseable {
					while (it.hasNext()) {
						Instance inst = it.next()
						num++

						// test instance
						def typeName = inst.getDefinition().getName().getLocalPart()

						if(typeName.contains("attr1")) {
							attrCount++
							assert inst.p.col1.value() == 'Value 1'
							assert inst.p.col2.value() == 'Value 2'
							def the_geom = inst.p.the_geom.value()
							assert the_geom
							assert the_geom instanceof GeometryProperty
						}
						else if(typeName.contains("abc")) {
							abcCount++
							assert inst.p.a.value() instanceof String
							assert inst.p.b.value() instanceof Double
							assert inst.p.c.value() instanceof Long
							def the_geom = inst.p.the_geom.value()
							assert the_geom
							assert the_geom instanceof GeometryProperty
						}
					}
				}


			}
			assertEquals(3, num)
			assertEquals(2, abcCount)
			assertEquals(1, attrCount)
		}
	}

	@Test
	void testWriteBigNumbers() {
		Schema schema = new SchemaBuilder().schema {
			abc {
				a(BigInteger)
				b(BigDecimal)
				location(GeometryProperty)
			}
		}

		InstanceCollection instances = new InstanceBuilder(types: schema).createCollection {
			abc {
				a(new BigInteger('123'))
				b(new BigDecimal('1.23'))
				location( createGeometry('POINT(49.872833 8.651222)', 4326))
			}

			abc {
				// Expectation: 776627963145224191
				a(new BigInteger('1' + Long.MAX_VALUE))
				// Expectation in Double: 1.09849107197545958E18
				b(new BigDecimal('1098491071975459529.6201509049614540479'))
				location( createGeometry('POINT(49.872833 8.651222)', 4326))
			}
		}

		withNewShapefile(schema, instances) { file ->
			// load instances again and test
			def loaded = loadInstances(file)
			int num = 0
			loaded.iterator().withCloseable {
				while (it.hasNext()) {
					Instance inst = it.next()
					num++

					// test instance
					assert inst.p.a.value() instanceof Long
					assert inst.p.b.value() instanceof Double
					def the_geom = inst.p.the_geom.value()
					assert the_geom
					assert the_geom instanceof GeometryProperty
				}
			}

			// two instances were loaded
			assertEquals(2, num)
		}
	}

	//test case for multiple schema for multiple geometries where all geometries are present in all the instances.
	@Test
	void testWriteMultiSchemasWithMultiGeom() {
		GeometryFactory gf = new GeometryFactory()
		def poly = gf.createPolygon([
			new Coordinate(0, 0),
			new Coordinate(1, 0),
			new Coordinate(1, 1),
			new Coordinate(0, 1),
			new Coordinate(0, 0)] as Coordinate[])
		def polyGeom = new DefaultGeometryProperty<Geometry>(new CodeDefinition("EPSG:4326", null), poly)

		Schema schema = new SchemaBuilder().schema {
			attr1 {
				col1()
				col2()
				location(GeometryProperty)
				loc(GeometryProperty)
			}
			abc {
				a(String)
				b(Double)
				c(Long)
				location(GeometryProperty)
				locat(GeometryProperty)
			}
		}

		InstanceCollection instances = new InstanceBuilder(types: schema).createCollection {
			attr1 {
				col1('Value 1')
				col2('Value 2')
				location( createGeometry('POINT(49.872833 8.651222)', 4326) )
				loc(polyGeom)
			}

			abc {
				a('Text')
				b(3.14)
				c(42)
				location( createGeometry('POINT(52.872833 9.651222)', 4326))
				locat(polyGeom)
			}

			abc {
				a('More text')
				b(1.52)
				c(23)
				location(createGeometry('POINT(55.137222 10.575556)', 4326))
				locat(polyGeom)
			}
		}

		withNewShapefile(schema, instances) { file ->
			// load instances again and test
			def schemaNames = [
				"attr1_Point",
				"attr1_Polygon",
				"abc_Point",
				"abc_Polygon"
			]
			int num = 0
			int abcCount = 0
			int attrCount = 0
			for (schemaName in schemaNames) {
				def loaded = loadInstances(file, schemaName)
				loaded.iterator().withCloseable {
					while (it.hasNext()) {
						Instance inst = it.next()
						num++

						// test instance
						def typeName = inst.getDefinition().getName().getLocalPart()
						if(typeName.contains("attr1")) {
							attrCount++
							assert inst.p.col1.value() == 'Value 1'
							assert inst.p.col2.value() == 'Value 2'
							def the_geom = inst.p.the_geom.value()
							assert the_geom
							assert the_geom instanceof GeometryProperty
						}
						else if(typeName.contains("abc")) {
							abcCount++
							assert inst.p.a.value() instanceof String
							assert inst.p.b.value() instanceof Double
							assert inst.p.c.value() instanceof Long
							def the_geom = inst.p.the_geom.value()
							assert the_geom
							assert the_geom instanceof GeometryProperty
						}
					}
				}


			}
			assertEquals(6, num)
			assertEquals(4, abcCount)
			assertEquals(2, attrCount)
		}
	}

	//test case for multiple schema with where each instance of source data has only one geometry but different geometry types.
	@Test
	void testWriteMultiSchemasWithDifferentGeometryTypes() {
		GeometryFactory gf = new GeometryFactory()
		def poly = gf.createPolygon([
			new Coordinate(0, 0),
			new Coordinate(1, 0),
			new Coordinate(1, 1),
			new Coordinate(0, 1),
			new Coordinate(0, 0)] as Coordinate[])
		def polyGeom = new DefaultGeometryProperty<Geometry>(new CodeDefinition("EPSG:4326", null), poly)

		def lineString= gf.createLineString([
			new Coordinate(0, 0),
			new Coordinate(1, 0),
			new Coordinate(1, 1),
			new Coordinate(0, 1),
			new Coordinate(0, 0)] as Coordinate[])
		def lineStringGeom = new DefaultGeometryProperty<Geometry>(new CodeDefinition("EPSG:4326", null), lineString)

		def lineString2= gf.createLineString([
			new Coordinate(0, 1),
			new Coordinate(1, 0),
			new Coordinate(1, 1),
			new Coordinate(0, 1),
			new Coordinate(0, 1)] as Coordinate[])
		def lineStringGeom2 = new DefaultGeometryProperty<Geometry>(new CodeDefinition("EPSG:4326", null), lineString)


		Schema schema = new SchemaBuilder().schema {
			attr1 {
				col1()
				col2()
				locationPoint(GeometryProperty)
				geomMultiPolygon(GeometryProperty)
			}
			abc {
				a(String)
				b(Double)
				c(Long)
				locationPoint(GeometryProperty)
				geomMultiPolygon(GeometryProperty)
				geomMultiLineString(GeometryProperty)
			}
		}

		InstanceCollection instances = new InstanceBuilder(types: schema).createCollection {
			attr1 {
				col1('Value 1 with point')
				col2('Value 2 with point')
				locationPoint( createGeometry('POINT(49.872833 8.651222)', 4326) )
			}

			attr1 {
				col1('Value 1 with Multipolygon')
				col2('Value 2 with Multipolygon')
				geomMultiPolygon(polyGeom)
			}

			abc {
				a('Text with point geometry')
				b(11.14)
				c(11)
				locationPoint( createGeometry('POINT(52.872833 9.651222)', 4326))
			}

			abc {
				a('More text with multipolygon geometry')
				b(22.52)
				c(22)
				geomMultiPolygon(polyGeom)
			}

			abc {
				a('More text with MultiLineString geometry')
				b(333.52)
				c(333)
				geomMultiLineString(lineStringGeom)
			}

			abc {
				a('More text with MultiLineString geometry')
				b(333.52)
				c(333)
				geomMultiLineString(lineStringGeom2)
			}
		}

		withNewShapefile(schema, instances) { file ->
			// load instances again and test
			def schemaNames = [
				"attr1_Point",
				"attr1_Polygon",
				"abc_Point",
				"abc_Polygon",
				"abc_LineString"
			]
			int num = 0
			int abcCount = 0
			int attrCount = 0
			int attr1PointCount = 0
			int attr1PolygonCount = 0
			int abcPointCount = 0
			int abcMultiPolygonCount = 0
			int abcMultiLineStringCount = 0

			for (schemaName in schemaNames) {
				def loaded = loadInstances(file, schemaName)
				loaded.iterator().withCloseable {
					while (it.hasNext()) {
						Instance inst = it.next()
						num++

						// test instance
						def typeName = inst.getDefinition().getName().getLocalPart()
						if(typeName.contains("attr1")) {
							attrCount++
							def the_geom = inst.p.the_geom.value()
							assert the_geom
							assert the_geom instanceof GeometryProperty

							if(schemaName.contains("attr1_Point")) {
								attr1PointCount++
								assert inst.p.col1.value() == 'Value 1 with point'
								assert inst.p.col2.value() == 'Value 2 with point'
								assert ((GeometryProperty) the_geom).getGeometry().geometryType.equalsIgnoreCase("POINT")
							} else {
								attr1PolygonCount++
								assert inst.p.col1.value() == 'Value 1 with Multipolygon'
								assert inst.p.col2.value() == 'Value 2 with Multipolygon'
								assert ((GeometryProperty) the_geom).getGeometry().geometryType.equalsIgnoreCase("MultiPolygon")
							}
						}
						else if(typeName.contains("abc")) {
							abcCount++
							def the_geom = inst.p.the_geom.value()
							assert the_geom
							assert the_geom instanceof GeometryProperty
							if(schemaName.contains("abc_Point")) {
								abcPointCount++
								assert inst.p.a.value() == 'Text with point geometry'
								assert inst.p.b.value() == 11.14
								assert inst.p.c.value() == 11
								assert ((GeometryProperty) the_geom).getGeometry().geometryType.equalsIgnoreCase("Point")
							} else if(schemaName.contains("abc_Polygon")) {
								abcMultiPolygonCount++
								assert inst.p.a.value() == 'More text with multipolygon geometry'
								assert inst.p.b.value() == 22.52
								assert inst.p.c.value() == 22
								assert ((GeometryProperty) the_geom).getGeometry().geometryType.equalsIgnoreCase("MultiPolygon")
							} else if(schemaName.contains("abc_LineString")) {
								abcMultiLineStringCount++
								assert inst.p.a.value() == 'More text with MultiLineString geometry'
								assert inst.p.b.value() == 333.52
								assert inst.p.c.value() == 333
								assert ((GeometryProperty) the_geom).getGeometry().geometryType.equalsIgnoreCase("MultiLineString")
							}
						}
					}
				}
			}
			assertEquals(6, num)
			assertEquals(4, abcCount)
			assertEquals(2, attrCount)
			assertEquals(1, attr1PointCount)
			assertEquals(1, attr1PolygonCount)
			assertEquals(1, abcPointCount)
			assertEquals(1, abcMultiPolygonCount)
			assertEquals(2, abcMultiLineStringCount)
		}
	}



	//test case for multiple schema for multiple geometries with one schema missing the geometry property.
	@Test
	void testWriteSingleCorrectSchemasWithOneMissingGeom() {
		GeometryFactory gf = new GeometryFactory()
		def poly = gf.createPolygon([
			new Coordinate(0, 0),
			new Coordinate(1, 0),
			new Coordinate(1, 1),
			new Coordinate(0, 1),
			new Coordinate(0, 0)] as Coordinate[])
		def polyGeom = new DefaultGeometryProperty<Geometry>(new CodeDefinition("EPSG:4326", null), poly)


		Schema schema = new SchemaBuilder().schema {
			attr1 {
				col1()
				col2()
			}
			abc {
				a(String)
				b(Double)
				c(Long)
				location(GeometryProperty)
				locat(GeometryProperty)
			}
		}

		InstanceCollection instances = new InstanceBuilder(types: schema).createCollection {
			attr1 {
				col1('Value 1')
				col2('Value 2')
			}

			abc {
				a('Text')
				b(3.14)
				c(42)
				location( createGeometry('POINT(49.872833 8.651222)', 4326))
				locat(polyGeom)
			}

			abc {
				a('More text')
				b(1.52)
				c(23)
				location(createGeometry('POINT(48.137222 11.575556)', 4326))
				locat(polyGeom)
			}
		}

		withNewShapefileWithReporterErrors(schema, instances) { file ->
			// load instances again and test
			def schemaNames = ["Point", "Polygon"]
			int num = 0
			int abcCount = 0
			int attrCount = 0
			for (schemaName in schemaNames) {
				def loaded = loadInstances(file, schemaName)
				loaded.iterator().withCloseable {
					while (it.hasNext()) {
						Instance inst = it.next()
						num++

						// test instance
						def typeName = inst.getDefinition().getName().getLocalPart()
						abcCount++
						assert inst.p.a.value() instanceof String
						assert inst.p.b.value() instanceof Double
						assert inst.p.c.value() instanceof Long
						def the_geom = inst.p.the_geom.value()
						assert the_geom
						assert the_geom instanceof GeometryProperty
					}
				}


			}
			assertEquals(4, num)
			assertEquals(4, abcCount)
			//			assertEquals(2, attrCount)
		}
	}

	@Test
	void testWriteMultiSchemasWithOneMissingGeom() {
		GeometryFactory gf = new GeometryFactory()
		def poly = gf.createPolygon([
			new Coordinate(0, 0),
			new Coordinate(1, 0),
			new Coordinate(1, 1),
			new Coordinate(0, 1),
			new Coordinate(0, 0)] as Coordinate[])
		def polyGeom = new DefaultGeometryProperty<Geometry>(new CodeDefinition("EPSG:4326", null), poly)

		Schema schema = new SchemaBuilder().schema {
			attr1 {
				col1()
				col2()
			}
			abc {
				a(String)
				b(Double)
				c(Long)
				location(GeometryProperty)
				locat(GeometryProperty)
			}
			xyz {
				a(String)
				place(GeometryProperty)
				geom(GeometryProperty)
			}
		}

		InstanceCollection instances = new InstanceBuilder(types: schema).createCollection {
			attr1 {
				col1('Value 1')
				col2('Value 2')
			}

			attr1 {
				col1('Value 1')
				col2('Value 2')
			}

			abc {
				a('Text')
				b(3.14)
				c(42)
				location( createGeometry('POINT(49.872833 8.651222)', 4326))
				locat(polyGeom)
			}

			abc {
				a('More text')
				b(1.52)
				c(23)
				location(createGeometry('POINT(48.137222 11.575556)', 4326))
				locat(polyGeom)
			}

			xyz {
				a('Text1')
				place( createGeometry('POINT(49.872833 8.651222)', 4326))
				geom(polyGeom)
			}

			xyz {
				a('Text1')
				place(createGeometry('POINT(48.137222 11.575556)', 4326))
				geom(polyGeom)
			}
		}

		withNewShapefileWithReporterErrors(schema, instances) { file ->
			// load instances again and test
			def schemaNames = [
				"abc_Point",
				"abc_Polygon",
				"xyz_Point",
				"xyz_Polygon"
			]
			int num = 0
			int abcCount = 0
			int xyzCount = 0
			for (schemaName in schemaNames) {
				def loaded = loadInstances(file, schemaName)
				loaded.iterator().withCloseable {
					while (it.hasNext()) {
						Instance inst = it.next()
						num++

						// test instance
						def typeName = inst.getDefinition().getName().getLocalPart()
						if(typeName.contains("xyz")) {
							xyzCount++
							assert inst.p.a.value() == 'Text1'
							assert inst.p.a.value() instanceof String
							def the_geom = inst.p.the_geom.value()
							assert the_geom
							assert the_geom instanceof GeometryProperty
						}
						else if(typeName.contains("abc")) {
							abcCount++
							assert inst.p.a.value() instanceof String
							assert inst.p.b.value() instanceof Double
							assert inst.p.c.value() instanceof Long
							def the_geom = inst.p.the_geom.value()
							assert the_geom
							assert the_geom instanceof GeometryProperty
						}
					}
				}


			}
			assertEquals(8, num)
			assertEquals(4, xyzCount)
			assertEquals(4, abcCount)
			//			assertEquals(2, attrCount)
		}
	}


	@Test
	void testWriteReadDateTime() {
		Schema schema = new SchemaBuilder().schema {
			city {
				//				id(Integer)
				name(String)
				a_date(LocalDate)
				a_timestamp(Instant)
				legacy_date(Date)
				location(GeometryProperty)
			}
		}

		def aDate = LocalDate.of(2017, 10, 3)

		def aTimestamp = Instant.parse('2042-04-02T00:00:42Z')

		InstanceCollection instances = new InstanceBuilder(types: schema).createCollection {
			city {
				name 'Darmstadt'
				a_date(aDate)
				a_timestamp(aTimestamp)
				legacy_date(Date.from(aTimestamp))
				location( createGeometry('POINT(49.872833 8.651222)', 4326) )
			}
		}

		withNewShapefile(schema, instances) { file ->

			// load instances again and test
			def loaded = loadInstances(file)


			int num = 0
			loaded.iterator().withCloseable {
				while (it.hasNext()) {
					Instance inst = it.next()
					num++

					def name = inst.p.name.value()
					assert name

					def a_date = inst.p.a_date.value()
					assert a_date instanceof String
					assert a_date == aDate.toString()
					def a_timestamp = inst.p.a_timestam.value()
					assert a_timestamp == null
					def ati = inst.p.ati.value()
					assert ati != null
					def legacy_date = inst.p.legacy_dat.value()
					assert legacy_date == null

					def leda = inst.p.leda.value()
					assert leda != null


					def the_geom = inst.p.the_geom.value()
					assert the_geom
					assert the_geom instanceof GeometryProperty
				}
			}

			// instances were loaded
			assertEquals(1, num)
		}
	}

	@Test
	void testWriteFeaturesTargetCRS() {
		Schema schema = new SchemaBuilder().schema {
			city {
				name(String)
				population(Integer)
				location(GeometryProperty)
			}
		}

		InstanceCollection instances = new InstanceBuilder(types: schema).createCollection {
			city {
				name 'Darmstadt'
				population 158254
				location( createGeometry('POINT(49.872833 8.651222)', 4326) )
			}

			city {
				name 'München'
				population 1471508
				location( createGeometry('POINT(48.137222 11.575556)', 4326) )
			}
		}

		withNewShapefile(schema, instances) { file ->
			// load instances again and test
			def loaded = loadInstances(file)

			int num = 0
			loaded.iterator().withCloseable {
				while (it.hasNext()) {
					Instance inst = it.next()
					num++

					// test instance
					def typeName = inst.getDefinition().getName().getLocalPart()
					//					assert typeName == 'city'
					def geom = inst.p.the_geom.value()
					assert geom
					assert geom instanceof GeometryProperty
					def crs = geom.getCRSDefinition()
					assert crs
					assert crs instanceof CodeDefinition
					assert crs.code == 'EPSG:25832'
					def jts = geom.geometry
					assert jts instanceof Point
					def name = inst.p.name.value()
					assert name
					switch (name) {
						case 'Darmstadt':
							assert inst.p.population.value() == 158254
							break
						case 'München':
							assert inst.p.population.value() == 1471508
							break
						default:
							throw new IllegalStateException("Unexpected type $typeName")
					}
				}
			}

			// instances were loaded
			assertEquals(2, num)
		} { ShapefileInstanceWriter writer ->
			writer.setTargetCRS(new CodeDefinition('EPSG:25832'))
		}
	}


	@Test
	void testSinglePolyGeometryWithLongAttributeNamesWithSingleInstance() {

		GeometryFactory gf = new GeometryFactory()
		def poly = gf.createPolygon([
			new Coordinate(0, 0),
			new Coordinate(1, 0),
			new Coordinate(1, 1),
			new Coordinate(0, 1),
			new Coordinate(0, 0)] as Coordinate[])
		def polyGeom = new DefaultGeometryProperty<Geometry>(new CodeDefinition("EPSG:4326", null), poly)

		Schema schema = new SchemaBuilder().schema {
			city {
				name(String)
				nameInCamelCase(String)
				name_In_snake_CamelCase(String)
				name_in_snake_case(String)
				name1234567(String)
				population(Integer)
				population123456789(Integer)
				snake_camelCase1234(String)
				location(GeometryProperty)
				myprop(String)
			}
		}

		InstanceCollection instances = new InstanceBuilder(types: schema).createCollection {
			city {
				name 'Darms'
				nameInCamelCase 'DarmstadtnameInCamelCase'
				name_In_snake_CamelCase 'Darmstadt name_In_snake_CamelCase'
				name_in_snake_case 'Darmstadt name_in_snake_case'
				name1234567 'Darmstadt name1234567'
				population123456789 158254
				population 269745
				snake_camelCase1234 'snake_camelCase1234'
				location(polyGeom)
				myprop 'myprop'
			}

		}

		withNewShapefile(schema, instances) { file ->
			// load instances again and test

			def loaded = loadInstances(file)

			int num = 0
			loaded.iterator().withCloseable {
				while (it.hasNext()) {
					Instance inst = it.next()
					num++

					// test instance
					def typeName = inst.getDefinition().getName().getLocalPart()

					def the_geom = inst.p.the_geom.value()
					assert the_geom
					assert the_geom instanceof GeometryProperty
					def crs = the_geom.getCRSDefinition()
					assert crs
					def jts = the_geom.geometry

					assert jts instanceof MultiPolygon

					def name = inst.p.name.value()
					assert name == 'Darms'
					def naInCaCa = inst.p.naInCaCa.value()
					assert naInCaCa == 'DarmstadtnameInCamelCase'
					assertEquals('DarmstadtnameInCamelCase',naInCaCa)

					//truncated after 8 so 'Ca' is not in the name.
					def naInsnCa = inst.p.naInsnCa.value()
					assertEquals('Darmstadt name_In_snake_CamelCase',naInsnCa)

					def nainsnca = inst.p.nainsnca.value()
					assertEquals('Darmstadt name_in_snake_case',nainsnca)

					def na12 = inst.p.na12.value()
					assertEquals('Darmstadt name1234567',na12)

					def po12 = inst.p.po12.value()
					assertEquals(158254,po12)

					def population = inst.p.population.value()
					assertEquals(269745, population)

					def myprop = inst.p.myprop.value()
					assertEquals('myprop',myprop)

					def sncaCa12 = inst.p.sncaCa12.value()
					assertEquals('snake_camelCase1234',sncaCa12)
				}
			}
			assertEquals(1, num)
		}
	}


	@Test
	void testSinglePolyGeometryWithLongAlphanumericNamesWithMultipleInstances() {

		GeometryFactory gf = new GeometryFactory()
		def poly = gf.createPolygon([
			new Coordinate(0, 0),
			new Coordinate(1, 0),
			new Coordinate(1, 1),
			new Coordinate(0, 1),
			new Coordinate(0, 0)] as Coordinate[])
		def polyGeom = new DefaultGeometryProperty<Geometry>(new CodeDefinition("EPSG:4326", null), poly)

		Schema schema = new SchemaBuilder().schema {
			city {
				name(String)
				population123(Integer)
				location(GeometryProperty)
			}
		}

		InstanceCollection instances = new InstanceBuilder(types: schema).createCollection {
			city {
				name 'Darmstadt'
				population123 158254
				location(polyGeom)
			}

			city {
				name 'München'
				population123 1471508
				location( polyGeom)
			}
		}

		withNewShapefile(schema, instances) { file ->
			// load instances again and test

			def loaded = loadInstances(file)

			int num = 0
			loaded.iterator().withCloseable {
				while (it.hasNext()) {
					Instance inst = it.next()
					num++

					// test instance
					def typeName = inst.getDefinition().getName().getLocalPart()

					def the_geom = inst.p.the_geom.value()
					assert the_geom
					assert the_geom instanceof GeometryProperty
					def crs = the_geom.getCRSDefinition()
					assert crs
					def jts = the_geom.geometry

					assert jts instanceof MultiPolygon
					def name = inst.p.name.value()
					assert name
					switch (name) {
						case 'Darmstadt':
							assert inst.p.po12.value() == 158254
							break
						case 'München':
							assert inst.p.po12.value() == 1471508
							break
						default:
							throw new IllegalStateException("Unexpected type $typeName")
					}
				}
			}
			assertEquals(2, num)
		}

	}

	@Test
	void testSinglePolyGeometryWithLongAlphanumericNamesWithMultipleInstancesWithMissingData() {

		GeometryFactory gf = new GeometryFactory()
		def poly = gf.createPolygon([
			new Coordinate(0, 0),
			new Coordinate(1, 0),
			new Coordinate(1, 1),
			new Coordinate(0, 1),
			new Coordinate(0, 0)] as Coordinate[])
		def polyGeom = new DefaultGeometryProperty<Geometry>(new CodeDefinition("EPSG:4326", null), poly)

		Schema schema = new SchemaBuilder().schema {
			city {
				name(String)
				population123(Integer)
				location(GeometryProperty)
			}
		}

		InstanceCollection instances = new InstanceBuilder(types: schema).createCollection {
			city {
				name 'Darmstadt'
				location(polyGeom)
			}

			city {
				name 'München'
				population123 1471508
				location( polyGeom)
			}
		}

		withNewShapefile(schema, instances) { file ->
			// load instances again and test

			def loaded = loadInstances(file)

			int num = 0
			loaded.iterator().withCloseable {
				while (it.hasNext()) {
					Instance inst = it.next()
					num++

					// test instance
					def typeName = inst.getDefinition().getName().getLocalPart()

					def the_geom = inst.p.the_geom.value()
					assert the_geom
					assert the_geom instanceof GeometryProperty
					def crs = the_geom.getCRSDefinition()
					assert crs
					def jts = the_geom.geometry

					assert jts instanceof MultiPolygon
					def name = inst.p.name.value()
					assert name
					switch (name) {
						case 'München':
							assert inst.p.po12.value() == 1471508
							break
					}
				}
			}
			assertEquals(2, num)
		}

	}

}
