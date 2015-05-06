/*
 * Copyright (c) 2015 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.inspire.apps.templates

import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import groovy.transform.Immutable
import groovy.transform.TypeCheckingMode


/**
 * Configuration for generating INSPIRE template projects.
 * @author Simon Templer
 */
@Immutable
@CompileStatic
class GenerateTemplatesConfig {

	/**
	 * The schema combinations to generate projects for.
	 */
	Collection<CombinationConfig> combinations

	@CompileStatic(TypeCheckingMode.SKIP)
	static GenerateTemplatesConfig readConfig() {
		def config = GenerateTemplates.getResourceAsStream('config.json').withStream {
			new JsonSlurper().parse(it)
		}

		Collection<CombinationConfig> combinations = []
		config.combinations.each { combination ->
			List<String> schemaIds = []
			combination.schemas.each { schemaIds << it }
			combinations << new CombinationConfig(schemaIds)
		}

		new GenerateTemplatesConfig(combinations)
	}
}

@Immutable
@CompileStatic
class CombinationConfig {

	/**
	 * The list of short IDs for schemas that should be combined in a project. 
	 */
	List<String> schemaIds
}
