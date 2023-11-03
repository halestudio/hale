/*
 * Copyright (c) 2023 wetransform GmbH
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

package eu.esdihumboldt.hale.io.xls.test.reader

import static org.assertj.core.api.Assertions.*;

import java.util.function.Consumer

import eu.esdihumboldt.hale.common.instance.model.Instance
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection
import eu.esdihumboldt.hale.common.schema.groovy.SchemaBuilder
import eu.esdihumboldt.hale.common.schema.model.Schema

/**
 * Utilities for XLSReaderTest. 
 * 
 * @author Simon Templer
 */
class XLSReaderTestUtil {

	static Schema createMultiSheetExampleSchema() {
		new SchemaBuilder().schema {
			city {
				name(String)
				planet(String)
			}
			person {
				name(String)
				planet(String)
			}
		}
	}

	/**
	 * @param instances
	 */
	static void verifyMultiSheetExample(InstanceCollection instances, boolean swapped) {
		List<Instance> inst = []
		instances.iterator().withCloseable {
			while (it.hasNext()) {
				inst << it.next()
			}
		}

		assertThat(inst).hasSize(4)

		def persons = inst.findAll { it.definition.name.localPart == 'person' }
		def cities = inst.findAll { it.definition.name.localPart == 'city' }

		if (swapped) {
			def tmp = persons
			persons = cities
			cities = tmp
		}

		assertThat(persons).hasSize(2)
		assertThat(cities).hasSize(2)

		def darmstadt = cities.find { it.p.name.value() == 'Darmstadt' }
		assertThat(darmstadt)
				.isNotNull()
				.satisfies({
					assertThat(it.p.planet.value()).isEqualTo('Earth')
				} as Consumer<Instance>)

		def coruscant = cities.find { it.p.name.value() == 'Coruscant' }
		assertThat(coruscant)
				.isNotNull()
				.satisfies({
					assertThat(it.p.planet.value()).isEqualTo('Coruscant')
				} as Consumer<Instance>)

		def yoda = persons.find { it.p.name.value() == 'Yoda' }
		verifyYoda(yoda)

		def doe = persons.find { it.p.name.value() == 'John Doe' }
		assertThat(doe)
				.isNotNull()
				.satisfies({
					assertThat(it.p.planet.value()).isEqualTo('Unknown')
				} as Consumer<Instance>)
	}

	static void verifyYoda(Instance yoda) {
		assertThat(yoda)
				.isNotNull()
				.satisfies({
					assertThat(it.p.name.value()).isEqualTo('Yoda')
					assertThat(it.p.planet.value()).isEqualTo('Dagobah')
				} as Consumer<Instance>)
	}
}
