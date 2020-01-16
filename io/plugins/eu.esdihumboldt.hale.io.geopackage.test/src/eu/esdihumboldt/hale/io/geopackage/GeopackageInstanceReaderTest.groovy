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
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue

import org.junit.Test

import eu.esdihumboldt.hale.common.core.io.impl.LogProgressIndicator
import eu.esdihumboldt.hale.common.core.io.report.IOReport
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier
import eu.esdihumboldt.hale.common.instance.model.Instance
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator
import eu.esdihumboldt.hale.common.schema.model.Schema
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

}
