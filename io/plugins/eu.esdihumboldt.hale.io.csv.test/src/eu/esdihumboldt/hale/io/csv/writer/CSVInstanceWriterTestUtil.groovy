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

package eu.esdihumboldt.hale.io.csv.writer

import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection
import eu.esdihumboldt.hale.common.schema.groovy.SchemaBuilder
import eu.esdihumboldt.hale.common.schema.model.Schema

/**
 * Create a test schema and an associated instance with a missing value
 * type's name: city
 * properties: name, population, country
 * instance's type: city
 * @author Flaminia Catalli
 */
class CSVInstanceWriterTestUtil {

	static Schema createExampleSchema() {
		new SchemaBuilder().schema {
			city {
				name(String)
				population(Integer, cardinality: '0..1')
				country(String)
			}
		}
	}

	static InstanceCollection createExampleInstancesNoPopulation(Schema schema) {
		new InstanceBuilder(types: schema).createCollection {
			city {
				name 'Darmstadt'
			}

			city {
				name 'München'
			}
		}
	}
}
