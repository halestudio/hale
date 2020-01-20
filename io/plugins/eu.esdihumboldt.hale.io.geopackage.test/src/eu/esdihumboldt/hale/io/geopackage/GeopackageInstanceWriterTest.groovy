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
import java.util.function.Consumer

import org.junit.Test

import com.vividsolutions.jts.geom.Geometry
import com.vividsolutions.jts.geom.Point

import eu.esdihumboldt.cst.functions.geometric.GeometryHelperFunctions
import eu.esdihumboldt.hale.common.core.io.impl.LogProgressIndicator
import eu.esdihumboldt.hale.common.core.io.report.IOReport
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder
import eu.esdihumboldt.hale.common.instance.model.Instance
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty
import eu.esdihumboldt.hale.common.schema.groovy.SchemaBuilder
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchemaSpace
import groovy.transform.CompileStatic

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
	static IOReport writeInstances(File file, Schema schema, InstanceCollection instances) {
		GeopackageInstanceWriter writer = new GeopackageInstanceWriter()

		writer.setTarget(new FileIOSupplier(file))
		def ss = new DefaultSchemaSpace()
		ss.addSchema(schema)
		writer.setTargetSchema(ss)
		writer.setInstances(instances)

		IOReport report = writer.execute(new LogProgressIndicator())

		assertTrue(report.isSuccess())
		assertTrue(report.getErrors().isEmpty())

		return report;
	}

	@CompileStatic
	static void withNewGeopackage(Schema schema, InstanceCollection instances, Consumer<File> handler) {
		Path tmpFile = Files.createTempFile('new', '.gpkg')
		try {
			println "Temporary file is $tmpFile"
			writeInstances(tmpFile.toFile(), schema, instances)
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
}
