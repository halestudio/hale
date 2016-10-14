package eu.esdihumboldt.hale.common.instance.graph.test

import static org.junit.Assert.*

import org.junit.BeforeClass
import org.junit.Test

import eu.esdihumboldt.hale.common.instance.graph.reference.ReferenceGraph
import eu.esdihumboldt.hale.common.instance.graph.reference.impl.XMLInspector
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder
import eu.esdihumboldt.hale.common.instance.model.Instance
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.common.test.TestUtil

class ReferenceGraphTest {

	private static Schema INSPIRE_ADDRESSES_SCHEMA

	@BeforeClass
	static void init() {
		TestUtil.startConversionService()
		def inspireAddressesUri = URI.create("http://inspire.ec.europa.eu/schemas/au/3.0/AdministrativeUnits.xsd")
		INSPIRE_ADDRESSES_SCHEMA = TestUtil.loadSchema(inspireAddressesUri)
	}

	/**
	 * Test the reference graph partitioning with independent GML instances.
	 */
	@Test
	void testGmlIndependent() {
		Set<String> ids = new HashSet<>()

		InstanceCollection instances = new InstanceBuilder(types: INSPIRE_ADDRESSES_SCHEMA).createCollection {
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

	/**
	 * Test the reference graph partitioning with dependent GML instances.
	 */
	@Test
	void testGmlReferences() {
		InstanceCollection instances = new InstanceBuilder(types: INSPIRE_ADDRESSES_SCHEMA).createCollection {
			AdministrativeBoundaryType {
				id "AB_1"
				for (i in 1..20) {
					admUnit { href URI.create("#AB_1_$i") }
				}
			}

			for (i in 1..20) {
				AdministrativeUnitType {
					id "AB_1_$i"
					boundary { href URI.create("#AB_1") }
				}
			}
		}

		assertEquals('Incorrect number of instances in the original instance collection', 21, instances.size())

		ReferenceGraph<String> rg = new ReferenceGraph<String>(new XMLInspector(), instances)
		List<InstanceCollection> collections = rg.partition(5).toList()

		assertEquals('Unexpected number of parts', 1, collections.size())
		for (InstanceCollection collection : collections) {
			assertEquals('Unexpected number of instances in part', 21, collection.size())
		}
	}

	/**
	 * Test the reference graph partitioning with dependent GML instances.
	 */
	@Test
	void testGmlOverflow() {
		InstanceCollection instances = new InstanceBuilder(types: INSPIRE_ADDRESSES_SCHEMA).createCollection {
			// some packages of 10
			for (k in 1..5) {
				AdministrativeBoundaryType {
					id "AB_$k"
					for (i in 1..9) {
						admUnit { href URI.create("#AB_${k}_$i") }
					}
				}

				for (i in 1..9) {
					AdministrativeUnitType {
						id "AB_${k}_$i"
						boundary { href URI.create("#AB_$k") }
					}
				}
			}

			// some packages of 2 (which cannot fit in parts of 11 together with the packages of 10)
			for (k in 1..3) {
				AdministrativeBoundaryType {
					id "CD_$k"
					for (i in 1..1) {
						admUnit { href URI.create("#CD_${k}_$i") }
					}
				}

				for (i in 1..1) {
					AdministrativeUnitType {
						id "CD_${k}_$i"
						boundary { href URI.create("#CD_$k") }
					}
				}
			}

			// some packages of 1
			for (k in 1..6) {
				AdministrativeBoundaryType { id "EF_$k" }
			}
		}

		assertEquals('Incorrect number of instances in the original instance collection', 62, instances.size())

		ReferenceGraph<String> rg = new ReferenceGraph<String>(new XMLInspector(), instances)
		List<InstanceCollection> collections = rg.partition(11).toList()

		assertEquals('Unexpected number of parts', 6, collections.size())
	}

	/**
	 * Test the reference graph partitioning with dependent GML instances.
	 */
	@Test
	void testGmlOverflow2() {
		InstanceCollection instances = new InstanceBuilder(types: INSPIRE_ADDRESSES_SCHEMA).createCollection {
			// some packages of 10
			for (k in 1..1) {
				AdministrativeBoundaryType {
					id "AB_$k"
					for (i in 1..9) {
						admUnit { href URI.create("#AB_${k}_$i") }
					}
				}

				for (i in 1..9) {
					AdministrativeUnitType {
						id "AB_${k}_$i"
						boundary { href URI.create("#AB_$k") }
					}
				}
			}

			// some packages of 2 (which cannot fit in parts of 11 together with the packages of 10)
			for (k in 1..3) {
				AdministrativeBoundaryType {
					id "CD_$k"
					for (i in 1..1) {
						admUnit { href URI.create("#CD_${k}_$i") }
					}
				}

				for (i in 1..1) {
					AdministrativeUnitType {
						id "CD_${k}_$i"
						boundary { href URI.create("#CD_$k") }
					}
				}
			}

			// some more packages of 10
			for (k in 2..5) {
				AdministrativeBoundaryType {
					id "AB_$k"
					for (i in 1..9) {
						admUnit { href URI.create("#AB_${k}_$i") }
					}
				}

				for (i in 1..9) {
					AdministrativeUnitType {
						id "AB_${k}_$i"
						boundary { href URI.create("#AB_$k") }
					}
				}
			}

			// some packages of 1
			for (k in 1..6) {
				AdministrativeBoundaryType { id "EF_$k" }
			}
		}

		assertEquals('Incorrect number of instances in the original instance collection', 62, instances.size())

		ReferenceGraph<String> rg = new ReferenceGraph<String>(new XMLInspector(), instances)
		List<InstanceCollection> collections = rg.partition(11).toList()

		assertEquals('Unexpected number of parts', 6, collections.size())
	}

	/**
	 * Test the reference graph partitioning with dependent GML instances.
	 */
	@Test
	void testGmlOverflow3() {
		InstanceCollection instances = new InstanceBuilder(types: INSPIRE_ADDRESSES_SCHEMA).createCollection {
			// some packages of 10
			for (k in 1..5) {
				AdministrativeBoundaryType {
					id "AB_$k"
					for (i in 1..9) {
						admUnit { href URI.create("#AB_${k}_$i") }
					}
				}

				for (i in 1..9) {
					AdministrativeUnitType {
						id "AB_${k}_$i"
						boundary { href URI.create("#AB_$k") }
					}
				}
			}

			// some packages of 2 (which cannot fit in parts of 11 together with the packages of 10)
			for (k in 1..3) {
				AdministrativeBoundaryType {
					id "CD_$k"
					for (i in 1..1) {
						admUnit { href URI.create("#CD_${k}_$i") }
					}
				}

				for (i in 1..1) {
					AdministrativeUnitType {
						id "CD_${k}_$i"
						boundary { href URI.create("#CD_$k") }
					}
				}
			}

			// some packages of 1
			for (k in 1..3) {
				AdministrativeBoundaryType { id "EF_$k" }
			}
		}

		assertEquals('Incorrect number of instances in the original instance collection', 59, instances.size())

		ReferenceGraph<String> rg = new ReferenceGraph<String>(new XMLInspector(), instances)
		List<InstanceCollection> collections = rg.partition(11).toList()

		assertEquals('Unexpected number of parts', 6, collections.size())
	}

	/**
	 * Test the reference graph partitioning with dependent GML instances.
	 */
	@Test
	void testGmlUnresolvableReferences() {
		InstanceCollection instances = new InstanceBuilder(types: INSPIRE_ADDRESSES_SCHEMA).createCollection {
			AdministrativeBoundaryType {
				id "AB_1"
				for (i in 1..20) {
					admUnit { href URI.create("#AB_1_$i") }
				}
			}

			for (i in 1..15) {
				AdministrativeUnitType {
					id "AB_1_$i"
					boundary { href URI.create("#AB_1") }
				}
			}
		}

		assertEquals('Incorrect number of instances in the original instance collection', 16, instances.size())

		ReferenceGraph<String> rg = new ReferenceGraph<String>(new XMLInspector(), instances)
		List<InstanceCollection> collections = rg.partition(5).toList()

		assertEquals('Unexpected number of parts', 1, collections.size())
		for (InstanceCollection collection : collections) {
			assertEquals('Unexpected number of instances in part', 16, collection.size())
		}
	}

	/**
	 * Test the reference graph partitioning with dependent GML instances and an object w/o identifier.
	 */
	@Test
	void testGmlNoId() {
		InstanceCollection instances = new InstanceBuilder(types: INSPIRE_ADDRESSES_SCHEMA).createCollection {
			AdministrativeBoundaryType {
				for (i in 1..20) {
					admUnit { href URI.create("#AB_1_$i") }
				}
			}

			for (i in 1..20) {
				AdministrativeUnitType { id "AB_1_$i" }
			}
		}

		assertEquals('Incorrect number of instances in the original instance collection', 21, instances.size())

		ReferenceGraph<String> rg = new ReferenceGraph<String>(new XMLInspector(), instances)
		List<InstanceCollection> collections = rg.partition(5).toList()

		assertEquals('Unexpected number of parts', 1, collections.size())
		for (InstanceCollection collection : collections) {
			assertEquals('Unexpected number of instances in part', 21, collection.size())
		}
	}

	/**
	 * Test the reference graph partitioning with dependent GML instances.
	 */
	@Test
	void testGmlReferencesMultiPart() {
		InstanceCollection instances = new InstanceBuilder(types: INSPIRE_ADDRESSES_SCHEMA).createCollection {
			AdministrativeBoundaryType { id "AB_1" }

			for (i in 1..20) {
				AdministrativeUnitType {
					id "AB_1_$i"
					boundary { href URI.create("#AB_1") }
				}
			}

			AdministrativeBoundaryType { id "AB_2" }

			for (i in 1..10) {
				AdministrativeUnitType {
					id "AB_2_$i"
					boundary { href URI.create("#AB_2") }
				}
			}
		}

		assertEquals('Incorrect number of instances in the original instance collection', 32, instances.size())

		ReferenceGraph<String> rg = new ReferenceGraph<String>(new XMLInspector(), instances)
		List<InstanceCollection> collections = rg.partition(15).toList()

		assertEquals('Unexpected number of parts', 2, collections.size())
		assertEquals('Unexpected combined part size', 32, collections[0].size() + collections[1].size())
		assertTrue('Unexpected number of instances in part', collections[0].size() == 11 || collections[0].size() == 21)
	}

	/**
	 * Test the reference graph partitioning with dependent GML instances.
	 */
	@Test
	void testGmlReferencesMultiPartFill() {
		InstanceCollection instances = new InstanceBuilder(types: INSPIRE_ADDRESSES_SCHEMA).createCollection {
			AdministrativeBoundaryType { id "AB_1" }

			for (i in 1..20) {
				AdministrativeUnitType {
					id "AB_1_$i"
					boundary { href URI.create("#AB_1") }
				}
			}

			AdministrativeBoundaryType { id "AB_2" }

			for (i in 1..10) {
				AdministrativeUnitType {
					id "AB_2_$i"
					boundary { href URI.create("#AB_2") }
				}
			}

			for (i in 1..18) {
				CondominiumType { id "CD_$i" }
			}
		}

		assertEquals('Incorrect number of instances in the original instance collection', 50, instances.size())

		ReferenceGraph<String> rg = new ReferenceGraph<String>(new XMLInspector(), instances)
		List<InstanceCollection> collections = rg.partition(15).toList()

		assertEquals('Unexpected number of parts', 3, collections.size())
		assertEquals('Unexpected combined part size', 50, collections[0].size() + collections[1].size() + collections[2].size())
		assertTrue('Part with 21 instances not found', collections[0].size() == 21 || collections[1].size() == 21  || collections[2].size() == 21)
	}

}
