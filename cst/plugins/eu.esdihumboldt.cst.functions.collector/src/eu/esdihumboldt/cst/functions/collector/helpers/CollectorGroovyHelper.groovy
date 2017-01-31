/*
 * Copyright (c) 2017 wetransform GmbH
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

package eu.esdihumboldt.cst.functions.collector.helpers

import eu.esdihumboldt.cst.functions.groovy.helpers.util.Collector
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder
import eu.esdihumboldt.hale.common.instance.model.Instance
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import groovy.transform.CompileStatic

/**
 * Helper methods for Collector-related functions
 * 
 * @author Florian Esser
 */
@CompileStatic
class CollectorGroovyHelper {

	private final InstanceBuilder builder;

	public CollectorGroovyHelper() {
		builder = new InstanceBuilder(strictBinding: false)
	}

	/**
	 * Create an instance of a given type and assign a value to a child property.
	 * 
	 * @param type Type to instantiate
	 * @param child Child property of the type
	 * @param reference value to assign
	 * @return the created instance
	 */
	public Instance createInstance(TypeDefinition type, PropertyDefinition child, Object value) {
		builder.createInstance(type) {
			builder.createProperty(child.name.localPart, child.name.namespaceURI, value);
		}
	}

	/**
	 * Extract values from a {@link Collector}
	 * 
	 * @param collector Collector to extract from
	 * @return collected values
	 */
	public List<Object> extractCollectedValues(Collector collector) {
		List<Object> result = [];
		collector.each { value -> result.add(value) }

		return result;
	}
}
