/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.html.svg

import eu.esdihumboldt.cst.test.TransformationExample
import eu.esdihumboldt.cst.test.TransformationExamples
import eu.esdihumboldt.hale.io.html.svg.mapping.MappingDocumentation
import groovy.json.JsonSlurper


/**
 * Tests for {@link MappingDocumentation}.
 * 
 * @author Simon Templer
 */
class MappingDocumentationTest extends GroovyTestCase {

	/**
	 * Test creating the mapping documentation template binding based on
	 * {@link TransformationExamples#PROPERTY_JOIN}.
	 */
	void testCreateBindingExPropertyJoin() {
		// load example project
		TransformationExample ex = TransformationExamples.getExample(TransformationExamples.PROPERTY_JOIN)

		// create template binding from alignment
		//TODO also retrieve project information?
		Map binding = MappingDocumentation.createBinding(null, ex.alignment)

		// basic checks
		assertNotNull binding.alignment
		assertNotNull binding.alignment.cells
		assertTrue binding.alignment.cells instanceof Map

		Map cells = binding.alignment.cells

		// cell count
		assertEquals 'Mapping has not the expected count of cells', 8, cells.entrySet().size()

		// pick one cell
		String cellJson = cells.values().first()
		def c = new JsonSlurper().parseText(cellJson)

		// do a basic structure test

		// function name is always there
		assertNotNull c.functionName

		// targets as well
		assertNotNull c.targets

		// property path must be there as well
		def target = c.targets[0]
		assertNotNull target.propertyPath
	}
}
