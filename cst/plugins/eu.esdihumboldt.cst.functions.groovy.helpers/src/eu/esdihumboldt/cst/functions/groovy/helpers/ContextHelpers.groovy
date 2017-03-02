/*
 * Copyright (c) 2016 Data Harmonisation Panel
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

package eu.esdihumboldt.cst.functions.groovy.helpers

import javax.annotation.Nullable

import eu.esdihumboldt.cst.functions.groovy.helper.HelperContext
import eu.esdihumboldt.cst.functions.groovy.helper.spec.SpecBuilder
import eu.esdihumboldt.cst.functions.groovy.helpers.util.Collector

/**
 * Helper functions for interacting with a context map. 
 * 
 * @author Simon Templer
 */
class ContextHelpers {

	public static final String KEY_COLLECTOR = '___COLLECTOR___';

	/**
	 * Specification for the collector function
	 */
	public static final eu.esdihumboldt.cst.functions.groovy.helper.spec.Specification _collector_spec = SpecBuilder.newSpec( //
	description: 'Get the Collector associated to a context map.', //
	result: 'The Collector instance. Use it to collect values within a transformation') { //
		context('The context map, if omitted uses the overall transformation context.') }

	@Nullable
	static Collector _collector(def context, HelperContext hc) {
		if (context == null) {
			// default to transformation context
			context = hc?.executionContext?.transformationContext
		}

		if (context instanceof Map) {
			synchronized (context) {
				def collector = context[KEY_COLLECTOR]
				if (collector == null) {
					collector = new Collector()
					context[KEY_COLLECTOR] = collector
				}
				return collector
			}
		}

		throw new IllegalArgumentException('You need to provide a context map as argument')
	}

}
