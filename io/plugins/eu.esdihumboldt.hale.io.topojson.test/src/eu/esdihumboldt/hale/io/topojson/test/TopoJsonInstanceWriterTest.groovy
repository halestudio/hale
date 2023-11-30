/*
 * Copyright (c) 2022 wetransform GmbH
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

package eu.esdihumboldt.hale.io.topojson.test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNull
import static org.junit.Assert.assertTrue

import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDate
import java.util.function.Consumer

import org.junit.Test
import org.locationtech.jts.geom.Geometry

import eu.esdihumboldt.cst.functions.geometric.GeometryHelperFunctions
import eu.esdihumboldt.hale.common.core.io.impl.LogProgressIndicator
import eu.esdihumboldt.hale.common.core.io.report.IOReport
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty
import eu.esdihumboldt.hale.common.schema.groovy.SchemaBuilder
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchemaSpace
import eu.esdihumboldt.hale.io.topojson.TopoJsonInstanceWriter
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic


/**
 * Tests for the TopoJsonInstanceWriter
 * 
 * @author Florian Esser
 */
class TopoJsonInstanceWriterTest {

	static GeometryProperty<? extends Geometry> createGeometry(String wkt, Object crs) {
		return GeometryHelperFunctions._with([geometry: wkt, crs: crs])
	}

	// schema with types for testing
	Schema schema = new SchemaBuilder().schema {
		SimpleType {
			id(Integer)
			name(String)
			label(String)
			date(LocalDate)
			fiin(Integer)
			fido(Double)
			fibo(Boolean)
			geometry(GeometryProperty)
		}
	}

	/**
	 * Write an instance collection to a TopoJSON file.
	 */
	@CompileStatic
	static IOReport writeInstances(File file, Schema schema, InstanceCollection instances,
			Consumer<TopoJsonInstanceWriter> configurator = null) {

		def writer = new TopoJsonInstanceWriter()

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
	static void withNewTopoJson(Schema schema, InstanceCollection instances, Consumer<File> handler,
			Consumer<TopoJsonInstanceWriter> configurator = null) {

		Path tmpFile = Files.createTempFile('new', '.json')
		try {
			println "Temporary file is $tmpFile"
			writeInstances(tmpFile.toFile(), schema, instances, configurator)
			handler.accept(tmpFile.toFile())
		} finally {
			Files.delete(tmpFile)
		}
	}

	@Test
	public void testWriteTopoJson() {
		def aDate = LocalDate.of(2023, 11, 28)
		def badDate = null
		int i1 = 1;

		InstanceCollection instances = new InstanceBuilder(types: schema).createCollection {
			SimpleType {
				id(1)
				name 'Area 1'
				label 'Label 1'
				date(aDate)
				fiin(1)
				fido(1.2)
				fibo(true)
				geometry(createGeometry('POLYGON ((10 10, 20 10, 20 20, 10 20, 10 10))', 4326))
			}

			SimpleType {
				id(2)
				name '\u0000\u0000\u0000'
				label null
				date null
				fiin(-1)
				fido(-1.2)
				fibo(false)
				geometry(createGeometry('POLYGON ((20 10, 40 10, 40 20, 20 20, 20 10))', 4326))
			}

			SimpleType {
				id(3)
				name 'Area 3\u0000\u0000\u0000'
				label '\u0000\u0000\u0000'
				date null
				fiin(123)
				fido(12.3)
				fibo(null)
				geometry(createGeometry('POLYGON ((20 10, 40 10, 40 20, 20 20, 20 10))', 4326))
			}

			SimpleType {
				id(4)
				name 'Area 4\u0000'
				label 'null'
				date badDate
				fiin(null)
				fido(null)
				fibo()
				geometry(createGeometry('POLYGON ((20 10, 40 10, 40 20, 20 20, 20 10))', 4326))
			}
		}

		withNewTopoJson(schema, instances) { file ->
			def jsonStr = file.text
			println jsonStr

			def json = new JsonSlurper().parseText(jsonStr)

			assertEquals('Topology', json.type)
			assertEquals(1, json.objects.size())
			assertEquals(4, json.arcs.size())

			assertEquals(4, json.objects.Topology.geometries.size())

			assertEquals(0, json.objects.Topology.geometries[0].id)
			assertEquals('Polygon', json.objects.Topology.geometries[0].type)
			assertEquals('Area 1', json.objects.Topology.geometries[0].'properties'.name)
			assertEquals(1, json.objects.Topology.geometries[0].'properties'.id)
			assertEquals('Label 1', json.objects.Topology.geometries[0].'properties'.label)
			assertEquals("2023-11-28", json.objects.Topology.geometries[0].'properties'.date)
			assertEquals(1, json.objects.Topology.geometries[0].'properties'.fiin)
			assertEquals(1.2, json.objects.Topology.geometries[0].'properties'.fido)
			assertEquals(true, json.objects.Topology.geometries[0].'properties'.fibo)
			assertEquals(1, json.objects.Topology.geometries[0].arcs.size())
			assertEquals(2, json.objects.Topology.geometries[0].arcs[0].size())
			assertEquals([0, 1], json.objects.Topology.geometries[0].arcs[0])

			assertEquals(1, json.objects.Topology.geometries[1].id)
			assertEquals('Polygon', json.objects.Topology.geometries[1].type)
			assertNull(json.objects.Topology.geometries[1].'properties'.name)
			assertEquals(2, json.objects.Topology.geometries[1].'properties'.id)
			assertNull(json.objects.Topology.geometries[1].'properties'.label)
			assertNull(json.objects.Topology.geometries[1].'properties'.date)
			assertEquals(-1, json.objects.Topology.geometries[1].'properties'.fiin)
			assertEquals(-1.2, json.objects.Topology.geometries[1].'properties'.fido)
			assertEquals(false, json.objects.Topology.geometries[1].'properties'.fibo)
			assertEquals(1, json.objects.Topology.geometries[1].arcs.size())
			assertEquals(3, json.objects.Topology.geometries[1].arcs[0].size())
			assertEquals([-1, 2, 3], json.objects.Topology.geometries[1].arcs[0])

			assertEquals('Area 3', json.objects.Topology.geometries[2].'properties'.name)
			assertNull(json.objects.Topology.geometries[2].'properties'.label)
			assertNull(json.objects.Topology.geometries[2].'properties'.date)
			assertEquals(123, json.objects.Topology.geometries[2].'properties'.fiin)
			assertEquals(12.3, json.objects.Topology.geometries[2].'properties'.fido)
			assertNull(json.objects.Topology.geometries[2].'properties'.fibo)

			assertNull(json.objects.Topology.geometries[3].'properties'.label)
			assertNull(json.objects.Topology.geometries[3].'properties'.date)
			assertNull(json.objects.Topology.geometries[3].'properties'.fiin)
			assertNull(json.objects.Topology.geometries[3].'properties'.fido)
			assertNull(json.objects.Topology.geometries[3].'properties'.fibo)
		}
	}
}
