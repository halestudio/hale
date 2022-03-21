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

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue

import java.util.function.Consumer

import org.junit.Test
import org.locationtech.jts.geom.Geometry

import eu.esdihumboldt.hale.common.core.io.impl.LogProgressIndicator
import eu.esdihumboldt.hale.common.core.io.report.IOReport
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier
import eu.esdihumboldt.hale.common.instance.groovy.InstanceAccessor
import eu.esdihumboldt.hale.common.instance.model.Instance
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.common.test.TestUtil
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode

/**
 * Tests loading GeoPackage instances.
 * 
 * @author Simon Templer
 */
@CompileStatic
class GeopackageInstanceReaderTest {

	/**
	 * Load an instance collection from a Geopackage file.
	 * 
	 * @param file the file
	 * @return the loaded instance collection
	 */
	static InstanceCollection loadInstances(File file) {
		Schema schema = GeopackageSchemaReaderTest.loadSchema(file)
		return loadInstances(schema, file)
	}

	/**
	 * Load an instance collection from a Geopackage file.
	 *
	 * @param schema the schema to use
	 * @param file the file
	 * @return the loaded instance collection
	 */
	static InstanceCollection loadInstances(Schema schema, File file) {
		GeopackageInstanceReader reader = new GeopackageInstanceReader()

		reader.setSource(new FileIOSupplier(file))
		reader.setSourceSchema(schema)

		IOReport report = reader.execute(new LogProgressIndicator())

		assertTrue(report.isSuccess())
		assertTrue(report.getErrors().isEmpty())

		return reader.getInstances();
	}

	/**
	 * Test reading the schema of an example file.
	 */
	@Test
	public void testReadInstances() {
		GeopackageApiTest.withWastewaterTestFile { File file ->
			InstanceCollection instances = loadInstances(file)

			assertNotNull(instances)
			assertTrue(instances.hasSize())

			// test count
			assertEquals(3193, instances.size())

			// test that count matches actual instances retrieved from collection
			ResourceIterator<Instance> iterator = instances.iterator()
			List<Instance> collected = []
			iterator.withCloseable {
				while (it.hasNext()) {
					Instance instance = it.next()
					collected.add(instance)
				}
			}
			assertEquals(3193, collected.size())

			validateInstances(collected)
		}
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	private void validateInstances(List<Instance> instances) {
		def fids = new HashSet<Integer>()
		Instance testInstance

		instances.each { Instance i ->
			def fid = i.properties.fid.value() as Integer
			fids.add(fid)

			// pick a specific instance
			if (fid == 2701) {
				testInstance = i
			}
		}

		// verify that there is one unique ID per instance
		assertEquals(3193, fids.size())

		// test a specific instance's content
		assertNotNull(testInstance)

		assert testInstance.p.fid.value() == 2701
		assert testInstance.p.item_id.value() == '67983-MONI0000000001'
		assert testInstance.p.descriptio.value() == 'Franconia Township 1 (J1-1442)'
		//TODO test geometry
	}

	/**
	 * Test reading Geopackage data based on an XML schema (Use case: Rewrite Geopackage as GML).
	 */
	@Test
	public void testReadInstancesXSD() {
		GeopackageApiTest.withTestFile("testdata/MajorAirports-StrategicNoiseMaps-AT-LOWW.gpkg") { File file ->
			Schema xmlSchema = TestUtil.loadSchema(getClass().getClassLoader().getResource("testdata/gpkg_majorairports_unqualified.xsd").toURI())

			InstanceCollection instances = loadInstances(xmlSchema, file)

			assertNotNull(instances)
			assertTrue(instances.hasSize())

			// test count
			assertEquals(59, instances.size())

			validateMajorAirports(instances);
		}
	}

	/**
	 * Test reading Geopackage data based on an XML schema (Use case: Rewrite Geopackage as GML).
	 * 
	 * The XML schema uses qualified names for the properties unlike the schema that would be loaded from the Geopackage schema reader.
	 */
	@Test
	public void testReadInstancesXSDQualified() {
		GeopackageApiTest.withTestFile("testdata/MajorAirports-StrategicNoiseMaps-AT-LOWW.gpkg") { File file ->
			Schema xmlSchema = TestUtil.loadSchema(getClass().getClassLoader().getResource("testdata/gpkg_majorairports_qualified.xsd").toURI())

			InstanceCollection instances = loadInstances(xmlSchema, file)

			assertNotNull(instances)
			assertTrue(instances.hasSize())

			// test count
			assertEquals(59, instances.size())

			validateMajorAirports(instances);
		}
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	private void validateMajorAirports(InstanceCollection instances) {
		Map<String, List<Instance>> instancesByType = [:]

		instances.iterator().withCloseable {
			while(it.hasNext()) {
				Instance instance = it.next()

				String typeName = instance.getDefinition().name.localPart
				instancesByType.computeIfAbsent(typeName) { []}
				instancesByType[typeName] << instance
			}
		}

		// check counts per type

		//		println instancesByType.collectEntries { k, v -> [k, v.size()]}

		assertEquals 9, instancesByType.CodelistProperties.size()
		assertEquals 12, instancesByType.DatasetDefaultProperties.size()
		assertEquals 1, instancesByType.ESTATUnitReference.size()
		assertEquals 1, instancesByType.ExposureMajorAirport.size()
		assertEquals 23, instancesByType.ExposureValue.size()
		assertEquals 5, instancesByType.NoiseContours_majorAirportsIncludingAgglomeration_Lden.size()
		assertEquals 7, instancesByType.NoiseContours_majorAirportsIncludingAgglomeration_Lnight.size()
		assertEquals 1, instancesByType.Voidables.size()

		// check CodelistProperties
		def clp =  { new InstanceAccessor(instancesByType.CodelistProperties) }
		assertThat(clp().codelist.values().findAll())
				.hasSize(9)

		assertThat(clp().propertyName.values())
				.containsExactlyInAnyOrder('category', 'category', 'category', 'category',
				'exposureType', 'noiseLevel', 'noiseLevel', 'source', 'source')

		assertThat(clp().tableName.values().findAll())
				.hasSize(9)

		assertThat(clp().id.values().collect { it.intValue() })
		.containsExactlyInAnyOrderElementsOf(1..9)

		// check DatasetDefaultProperties
		def ddp =  { new InstanceAccessor(instancesByType.DatasetDefaultProperties) }
		assertThat(ddp().id.values().collect { it.intValue() })
		.containsExactlyInAnyOrderElementsOf(1..12)

		assertThat(ddp().attribute.values().findAll())
				.hasSize(4)

		assertThat(ddp().defaultValue.values().findAll())
				.hasSize(12)

		assertThat(ddp().propertyName.values().findAll())
				.hasSize(12)

		assertThat(ddp().tableName.values().findAll())
				.hasSize(12)

		// check NoiseContours_majorAirportsIncludingAgglomeration_Lnight
		def lnight = { new InstanceAccessor(instancesByType.NoiseContours_majorAirportsIncludingAgglomeration_Lnight) }
		assertThat(lnight().id.values().collect { it.intValue() })
		.containsExactlyInAnyOrderElementsOf(2..8)

		assertThat(lnight().source.values().findAll())
				.containsOnly('majorAirportsIncludingAgglomeration')
				.hasSize(7)

		assertThat(lnight().category.values().findAll())
				.hasSize(7)

		assertThat(lnight().location.values().findAll())
				.as('Lnight locations')
				.hasSize(7)
				.allSatisfy({ value ->
					assertThat(value)
							.isInstanceOf(GeometryProperty)
					assertThat(value.geometry)
							.isNotNull()
							.isInstanceOf(Geometry)
					assertThat(value.CRSDefinition)
							.isNotNull()
				} as Consumer)
	}

}
