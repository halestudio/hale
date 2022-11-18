/*
 * Copyright (c) 2020 wetransform GmbH
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

package eu.esdihumboldt.hale.io.geopackage

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

import java.nio.file.Files
import java.nio.file.Path
import java.time.Instant
import java.time.LocalDate
import java.util.function.Consumer

import javax.xml.namespace.QName

import org.junit.Test
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.Point

import eu.esdihumboldt.cst.functions.geometric.GeometryHelperFunctions
import eu.esdihumboldt.hale.common.core.io.impl.LogProgressIndicator
import eu.esdihumboldt.hale.common.core.io.report.IOReport
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier
import eu.esdihumboldt.hale.common.instance.geometry.impl.CodeDefinition
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder
import eu.esdihumboldt.hale.common.instance.model.Instance
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty
import eu.esdihumboldt.hale.common.schema.groovy.SchemaBuilder
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding
import eu.esdihumboldt.hale.common.schema.model.constraint.type.PrimaryKey
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchemaSpace
import groovy.transform.CompileStatic
import mil.nga.geopackage.manager.GeoPackageManager

/**
 * Tests for writing instances to GeoPackage.
 * 
 * @author Simon Templer
 */
class GeopackageInstanceWriterTest {

	static GeometryProperty<? extends Geometry> createGeometry(String wkt, Object crs) {
		return GeometryHelperFunctions._with([geometry: wkt, crs: crs])
	}

	/**
	 * Write an instance collection to a GeoPackage file.
	 */
	@CompileStatic
	static IOReport writeInstances(File file, Schema schema, InstanceCollection instances,
			Consumer<GeopackageInstanceWriter> configurator = null) {

		GeopackageInstanceWriter writer = new GeopackageInstanceWriter()

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

	@CompileStatic
	static void withNewGeopackage(Schema schema, InstanceCollection instances, Consumer<File> handler,
			Consumer<GeopackageInstanceWriter> configurator = null) {

		Path tmpFile = Files.createTempFile('new', '.gpkg')
		try {
			println "Temporary file is $tmpFile"
			writeInstances(tmpFile.toFile(), schema, instances, configurator)
			handler.accept(tmpFile.toFile())
		} finally {
			Files.delete(tmpFile)
		}
	}

	@Test
	void testWriteAttributes() {
		Schema schema = new SchemaBuilder().schema {
			attr1 {
				col1()
				col2()
			}
			abc {
				a(String)
				b(Double)
				c(Long)
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
			}

			abc {
				a('More text')
				b(1.52)
				c(23)
			}
		}

		withNewGeopackage(schema, instances) { file ->
			// load instances again and test
			def loaded = GeopackageInstanceReaderTest.loadInstances(file)

			// expect 3 instances
			assertEquals(3, loaded.size())

			int num = 0
			int abcCount = 0
			int attrCount = 0
			loaded.iterator().withCloseable {
				while (it.hasNext()) {
					Instance inst = it.next()
					num++

					// test instance
					def typeName = inst.getDefinition().getName().getLocalPart()
					switch (typeName) {
						case 'attr1':
							attrCount++
							assert inst.p.col1.value() == 'Value 1'
							assert inst.p.col2.value() == 'Value 2'
							break
						case 'abc':
							abcCount++
							assert inst.p.a.value() instanceof String
							assert inst.p.b.value() instanceof Double
							assert inst.p.c.value() instanceof Long
							break
						default:
							throw new IllegalStateException("Unexpected type $typeName")
					}
				}
			}

			// three instances were loaded
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
			}
		}

		InstanceCollection instances = new InstanceBuilder(types: schema).createCollection {
			abc {
				a(new BigInteger('123'))
				b(new BigDecimal('1.23'))
			}

			abc {
				// it used to work with ('1' + Long.MAX_VALUE and it does no longer after java17 migration for an unknown reason
				a(new BigInteger('' + Long.MAX_VALUE))
				b(new BigDecimal('1098491071975459529.6201509049614540479'))
			}
		}

		withNewGeopackage(schema, instances) { file ->
			// load instances again and test
			def loaded = GeopackageInstanceReaderTest.loadInstances(file)

			// expect 2 instances
			assertEquals(2, loaded.size())

			int num = 0
			loaded.iterator().withCloseable {
				while (it.hasNext()) {
					Instance inst = it.next()
					num++

					// test instance
					def typeName = inst.getDefinition().getName().getLocalPart()
					switch (typeName) {
						case 'abc':
							assert inst.p.a.value() instanceof Long
							assert inst.p.b.value() instanceof String
							break
						default:
							throw new IllegalStateException("Unexpected type $typeName")
					}
				}
			}

			// two instances were loaded
			assertEquals(2, num)
		}
	}

	@Test
	void testWriteFeatures() {
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

		withNewGeopackage(schema, instances) { file ->
			// load instances again and test
			def loaded = GeopackageInstanceReaderTest.loadInstances(file)

			// expect 2 instances
			assertEquals(2, loaded.size())

			int num = 0
			loaded.iterator().withCloseable {
				while (it.hasNext()) {
					Instance inst = it.next()
					num++

					// test instance
					def typeName = inst.getDefinition().getName().getLocalPart()
					assert typeName == 'city'
					def geom = inst.p.location.value()
					assert geom
					assert geom instanceof GeometryProperty
					def crs = geom.getCRSDefinition()
					assert crs
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
		}
	}

	@Test
	void testWriteFeaturesPrimaryKey() {
		Schema schema = new SchemaBuilder().schema {
			city([
				new PrimaryKey([new QName('id')])
			]) {
				id(Integer)
				name(String)
				population(Integer)
				location(GeometryProperty)
			}
		}

		InstanceCollection instances = new InstanceBuilder(types: schema).createCollection {
			city {
				// PK not set -> should be autogenerated
				name 'Darmstadt'
				population 158254
				location( createGeometry('POINT(49.872833 8.651222)', 4326) )
			}

			city {
				// PK set -> should not throw an error (regardless if it is ignored)
				id(12)
				name 'München'
				population 1471508
				location( createGeometry('POINT(48.137222 11.575556)', 4326) )
			}
		}

		withNewGeopackage(schema, instances) { file ->
			// load instances again and test
			def loaded = GeopackageInstanceReaderTest.loadInstances(file)

			// expect 2 instances
			assertEquals(2, loaded.size())

			int num = 0
			loaded.iterator().withCloseable {
				while (it.hasNext()) {
					Instance inst = it.next()
					num++

					// test instance
					def typeName = inst.getDefinition().getName().getLocalPart()
					assert typeName == 'city'

					def id = inst.p.id.value()
					// must be set
					assert id

					def geom = inst.p.location.value()
					assert geom
					assert geom instanceof GeometryProperty
					def crs = geom.getCRSDefinition()
					assert crs
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
		}
	}

	@Test
	void testWriteReadDateTime() {
		Schema schema = new SchemaBuilder().schema {
			city([
				new PrimaryKey([new QName('id')])
			]) {
				id(Integer)
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
				// PK not set -> should be autogenerated
				name 'Darmstadt'
				location( createGeometry('POINT(49.872833 8.651222)', 4326) )

				a_date(aDate)
				a_timestamp(aTimestamp)
				legacy_date(Date.from(aTimestamp))
			}
		}

		withNewGeopackage(schema, instances) { file ->
			// check schema
			def loadedSchema = GeopackageSchemaReaderTest.loadSchema(file)
			def type = loadedSchema.getType(new QName(GeopackageSchemaBuilder.DEFAULT_NAMESPACE, 'city'))

			def a_date_property = type.getChild(new QName('a_date')).asProperty()
			assert a_date_property
			assert a_date_property.getPropertyType().getConstraint(Binding.class).getBinding() == LocalDate.class

			def a_timestamp_property = type.getChild(new QName('a_timestamp')).asProperty()
			assert a_timestamp_property
			assert a_timestamp_property.getPropertyType().getConstraint(Binding.class).getBinding() == Instant.class

			def legacy_date_property = type.getChild(new QName('legacy_date')).asProperty()
			assert legacy_date_property
			assert legacy_date_property.getPropertyType().getConstraint(Binding.class).getBinding() == Instant.class

			// load instances again and test
			def loaded = GeopackageInstanceReaderTest.loadInstances(file)

			// expect 1 instance
			assertEquals(1, loaded.size())

			int num = 0
			loaded.iterator().withCloseable {
				while (it.hasNext()) {
					Instance inst = it.next()
					num++

					// test instance
					def typeName = inst.getDefinition().getName().getLocalPart()
					assert typeName == 'city'

					def id = inst.p.id.value()
					// must be set
					assert id

					def a_date = inst.p.a_date.value()
					def a_timestamp = inst.p.a_timestamp.value()
					def legacy_date = inst.p.legacy_date.value()

					assert a_date == aDate
					assert a_timestamp == aTimestamp
					assert legacy_date == aTimestamp
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

		withNewGeopackage(schema, instances) { file ->
			// load instances again and test
			def loaded = GeopackageInstanceReaderTest.loadInstances(file)

			// expect 2 instances
			assertEquals(2, loaded.size())

			int num = 0
			loaded.iterator().withCloseable {
				while (it.hasNext()) {
					Instance inst = it.next()
					num++

					// test instance
					def typeName = inst.getDefinition().getName().getLocalPart()
					assert typeName == 'city'
					def geom = inst.p.location.value()
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
		} { GeopackageInstanceWriter writer ->
			writer.setTargetCRS(new CodeDefinition('EPSG:25832'))
		}
	}

	@Test
	void testNgaSpatialIndexCreation() {
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

		withNewGeopackage(schema, instances) { file ->
			def loaded = GeopackageInstanceReaderTest.loadInstances(file)

			def gpkg = GeoPackageManager.open(file, true)
			assert gpkg.extensionsDao?.isTableExists()
			assert gpkg.extensionsDao?.queryForAll().any { it.extensionName == "nga_geometry_index" }
		} { GeopackageInstanceWriter writer ->
			writer.setSpatialIndexType("nga")
			writer.setTargetCRS(new CodeDefinition('EPSG:25832'))
		}
	}

	@Test
	void testRTreeSpatialIndexCreation() {
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

		withNewGeopackage(schema, instances) { file ->
			def loaded = GeopackageInstanceReaderTest.loadInstances(file)

			def gpkg = GeoPackageManager.open(file, true)
			assert gpkg.extensionsDao?.isTableExists()
			assert gpkg.extensionsDao?.queryForAll().any { it.extensionName == "gpkg_rtree_index" }
		} { GeopackageInstanceWriter writer ->
			writer.setSpatialIndexType("rtree")
			writer.setTargetCRS(new CodeDefinition('EPSG:25832'))
		}
	}

	@Test
	void testNoSpatialIndexCreation() {
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

		withNewGeopackage(schema, instances) { file ->
			def loaded = GeopackageInstanceReaderTest.loadInstances(file)

			def gpkg = GeoPackageManager.open(file, true)
			if (gpkg.extensionsDao?.isTableExists()) {
				// In case the extensions table was created by some other extension,
				// make sure that NGA and RTree extensions are not loaded
				assert !gpkg.extensionsDao.queryForAll().any {
					it.extensionName in [
						"gpkg_rtree_index",
						"nga_geometry_index"
					]
				}
			}
		} { GeopackageInstanceWriter writer ->
			writer.setSpatialIndexType("none")
			writer.setTargetCRS(new CodeDefinition('EPSG:25832'))
		}
	}
}
