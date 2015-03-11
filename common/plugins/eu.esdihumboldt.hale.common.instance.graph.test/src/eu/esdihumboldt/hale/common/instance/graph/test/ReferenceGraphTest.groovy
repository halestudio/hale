package eu.esdihumboldt.hale.common.instance.graph.test

import static org.junit.Assert.*

import org.junit.BeforeClass
import org.junit.Test

import eu.esdihumboldt.hale.common.instance.graph.reference.ReferenceGraph
import eu.esdihumboldt.hale.common.instance.graph.reference.impl.XMLInspector
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder
import eu.esdihumboldt.hale.common.instance.model.Instance
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection
import eu.esdihumboldt.hale.common.test.TestUtil

class ReferenceGraphTest {

	private static final URI INSPIRE_ADDRESSES = URI.create("http://inspire.ec.europa.eu/schemas/au/3.0/AdministrativeUnits.xsd")

	@BeforeClass
	static void init() {
		TestUtil.startConversionService()
	}

	/**
	 * Test the reference graph partitioning with independent GML instances.
	 */
	@Test
	void testGmlIndependent() {
		def schema = TestUtil.loadSchema(INSPIRE_ADDRESSES)

		Set<String> ids = new HashSet<>()

		InstanceCollection instances = new InstanceBuilder(types: schema).createCollection {
			for (i in 1..20) {
				AdministrativeUnitType {
					String _id = "AU_$i"
					ids.add(_id)

					id(_id)
				}
			}
		}

		assertEquals('Instance collection should hold 20 instances', 20, instances.size())

		ReferenceGraph<String> rg = new ReferenceGraph<String>(new XMLInspector(), instances)
		List<InstanceCollection> collections = rg.partition(5).toList()

		assertEquals('Unexpected number of parts', 4, collections.size())
		for (InstanceCollection collection : collections) {
			assertEquals('Unexpected number of instances in part', 5, collection.size())

			// check individual IDs
			def itr = collection.iterator()
			while (itr.hasNext()) {
				Instance instance = itr.next()
				String id = instance.p.id.value()
				assertNotNull(id)
				ids.remove(id)
			}
			itr.close()
		}

		assertEquals("The following instances are missing: $ids.toListString()", 0, ids.size())
	}

}
