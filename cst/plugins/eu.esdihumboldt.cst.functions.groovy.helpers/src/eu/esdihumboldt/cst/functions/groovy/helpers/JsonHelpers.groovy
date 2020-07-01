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

package eu.esdihumboldt.cst.functions.groovy.helpers

import eu.esdihumboldt.cst.functions.groovy.helper.spec.SpecBuilder
import groovy.json.*

/**
 * Helper functions for using JSON data.
 * @author Johanna Ott
 */
class JsonHelpers {

	/**
	 * Specification for the parseJson function
	 */
	public static final eu.esdihumboldt.cst.functions.groovy.helper.spec.Specification _parseJson_spec = SpecBuilder.newSpec( //
	description: 'Parse a text representation of a JSON data structure and returns a data structure of lists and maps', //
	result: 'A data structure of lists and maps.') {
		//
		json('The JSON data structure that should be parsed.')
	}

	static Object _parseJson(String json) {
		def slurper = new JsonSlurper()
		slurper.parseText(json)
	}
}
