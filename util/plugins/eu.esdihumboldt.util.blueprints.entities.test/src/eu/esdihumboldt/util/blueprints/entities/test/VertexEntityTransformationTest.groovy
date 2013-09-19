/*
 * Copyright (c) 2013 Simon Templer
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
 *     Simon Templer - initial version
 */

package eu.esdihumboldt.util.blueprints.entities.test;

import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.impls.tg.TinkerGraph
import com.tinkerpop.blueprints.impls.tg.TinkerGraphFactory


/**
 * VertexEntityTransformation tests on sample entities.
 * 
 * @author Simon Templer
 */
class VertexEntityTransformationTest extends GroovyTestCase {

	/**
	 * Test category entity w/ {@link TinkerGraph}.
	 */
	void testCategoryTinker() {
		TinkerGraph graph = TinkerGraphFactory.createTinkerGraph()

		// create Category
		Category cat = Category.create(graph)
		assertNotNull cat

		// inner vertex
		Vertex vertex = cat.v
		assertNotNull vertex

		// inner graph
		assertEquals graph, cat.g

		// simple properties
		cat.name = 'Name'
		assertEquals 'Name', cat.v.getProperty('name')

		cat.v.setProperty('description', 'Test')
		assertEquals 'Test', cat.getDescription()
	}
}
