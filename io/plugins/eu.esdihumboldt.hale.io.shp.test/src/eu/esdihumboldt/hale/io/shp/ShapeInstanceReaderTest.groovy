/*
 * Copyright (c) 2024 wetransform GmbH
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

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.function.Consumer

import org.apache.commons.io.IOUtils
import org.junit.Test
import org.locationtech.jts.geom.Geometry

import eu.esdihumboldt.hale.common.core.io.impl.LogProgressIndicator
import eu.esdihumboldt.hale.common.core.io.report.IOReport
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier
import eu.esdihumboldt.hale.common.instance.groovy.InstanceAccessor
import eu.esdihumboldt.hale.common.instance.model.Instance
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.common.test.TestUtil
import eu.esdihumboldt.hale.io.shp.reader.internal.ShapeInstanceReader
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode

/**
 * Tests for reading Shapefiles.
 * 
 * @author Simon Templer
 */
@CompileStatic
class ShapeInstanceReaderTest {

	/**
	 * Test reading Shapefile instances using the Shapefile as schema.
	 */
	@Test
	void testReadShapeInstances() {
		Schema xmlSchema = TestUtil.loadSchema(getClass().getClassLoader().getResource("testdata/arokfnp/ikg.shp").toURI())

		InstanceCollection instances = loadInstances(xmlSchema, getClass().getClassLoader().getResource("testdata/arokfnp/ikg.shp").toURI())

		assertNotNull(instances)
		List<Instance> list = instances.toList()

		// test count
		assertThat(list).hasSize(14)

		// instance validation
		validateArokFnpIkg(list, 'the_geom')
	}

	/**
	 * Test reading a single Shapefile from a folder.
	 */
	@Test
	void testReadFromFolder() {
		Schema xmlSchema = TestUtil.loadSchema(getClass().getClassLoader().getResource("testdata/arokfnp/ikg.shp").toURI())

		File tempDir = Files.createTempDirectory("read-shape").toFile()
		try {
			def ext = ['shp', 'dbf', 'prj', 'shx']
			ext.each {
				IOUtils.copy(getClass().getClassLoader().getResource("testdata/arokfnp/ikg.$it"), new File(tempDir, "ikg.$it"))
			}

			InstanceCollection instances = loadInstances(xmlSchema, tempDir.toURI())

			assertNotNull(instances)
			List<Instance> list = instances.toList()

			// test count
			assertThat(list).hasSize(14)

			// instance validation
			validateArokFnpIkg(list, 'the_geom')
		} finally {
			tempDir.deleteDir()
		}
	}

	/**
	 * Test reading Shapefile instances using an XML schema.
	 */
	@Test
	void testReadXsdInstances() {
		Schema xmlSchema = TestUtil.loadSchema(getClass().getClassLoader().getResource("testdata/arokfnp/arok-fnp.xsd").toURI())

		InstanceCollection instances = loadInstances(xmlSchema, getClass().getClassLoader().getResource("testdata/arokfnp/ikg.shp").toURI())

		assertNotNull(instances)
		List<Instance> list = instances.toList()

		// test count
		assertThat(list).hasSize(14)

		// instance validation
		validateArokFnpIkg(list, 'geometrie')
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	private void validateArokFnpIkg(List<Instance> instances, String geometryPropertyName) {
		Map<String, List<Instance>> instancesByType = [:]

		instances.iterator().with {
			while(it.hasNext()) {
				Instance instance = it.next()

				String typeName = instance.getDefinition().displayName
				instancesByType.computeIfAbsent(typeName) { []}
				instancesByType[typeName] << instance
			}
		}

		// check count of feature types
		assertThat(instancesByType)
				.hasSize(1)
				.containsOnlyKeys('ikg')

		// check counts per type

		assertEquals 14, instancesByType.ikg.size()

		// check ikg
		def ikg =  { new InstanceAccessor(instancesByType.ikg) }
		assertThat(ikg().aktennr.values().findAll())
				.hasSize(2)

		assertThat(ikg().bezeich.values())
				.containsExactlyInAnyOrder('Dußlingen-Gomaringen-Nehren, Musburg-Hönisch', 'Engstingen-Hohenstein-Trochtelfingen, Haidt', 'FNP Meersburg (GVV)', 'IKG Berg', 'Immenstaad-Friedrichshafen, Steigwiesen', 'Kirchberg-Weihungstal (Staig), Gassenäcker', 'Laichinger Alb', 'Meßkirch, Industriepark Nördlicher Bodensee', 'Munderkingen', 'Ostrach, Königsegg', 'Reutlingen-Kirchentellinsfurt, Mahden', 'Reutlingen-Kusterdingen, Mark West', 'Wangen-Amtzell, Geiselharz-Schauwies', 'Winterlingen-Straßberg, Vogelherd/Längenfeld')

		assertThat(ikg()."$geometryPropertyName".values().findAll())
				.as('ikg geometries')
				.hasSize(14)
				.allSatisfy({ value ->
					assertThat(value)
							.isInstanceOf(GeometryProperty)
					assertThat(value.geometry)
							.isNotNull()
							.isInstanceOf(Geometry)
					assertThat(value.CRSDefinition)
							.isNotNull()
					assertThat(value.CRSDefinition.CRS)
							.isNotNull()
				} as Consumer)
	}

	// helpers

	/**
	 * Load an instance collection from a Shapefile.
	 *
	 * @param schema the schema to use
	 * @param resource the file to load
	 * @return the loaded instance collection
	 */
	static InstanceCollection loadInstances(Schema schema, URI resource) {
		ShapeInstanceReader reader = new ShapeInstanceReader()

		reader.setSource(new DefaultInputSupplier(resource))
		reader.setSourceSchema(schema)

		reader.setCharset(StandardCharsets.UTF_8)

		IOReport report = reader.execute(new LogProgressIndicator())

		assertTrue(report.isSuccess())
		assertTrue(report.getErrors().isEmpty())

		return reader.getInstances();
	}
}
