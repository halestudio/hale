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

import org.codehaus.groovy.ast.builder.AstBuilder

import com.google.common.collect.Iterables
import com.tinkerpop.blueprints.Graph
import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.impls.orient.OrientGraph
import com.tinkerpop.blueprints.impls.tg.TinkerGraph


/**
 * VertexEntityTransformation tests on sample entities.
 * 
 * @author Simon Templer
 */
class VertexEntityTransformationTest extends GroovyTestCase {

	public static void main(args) {
		def ast = new AstBuilder().buildFromCode {
			Iterable<Category> cat = new ArrayList<Category>()
		}
		println ast
	}

	/**
	 * Test category entity w/ {@link TinkerGraph}.
	 */
	public void testCategoryTinker() {
		TinkerGraph graph = new TinkerGraph()

		// create Category
		Category cat = Category.create(graph)
		assertNotNull cat

		assertEquals 'category', cat.v.getProperty('_type')

		commonCategoryTest(graph, cat)

		graph.shutdown()
	}

	/**
	 * Test category entity w/ {@link TinkerGraph}.
	 */
	public void testCategoryOrient() {
		OrientGraph graph = new OrientGraph('memory:test')

		// create Category
		Category cat = Category.create(graph)
		assertNotNull cat

		commonCategoryTest(graph, cat)

		graph.rollback()
	}

	private void commonCategoryTest(Graph graph, Category cat) {
		// identifier set
		Object id = cat.getId()
		assertNotNull id

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

		// deletion
		assertEquals 1, graph.vertices.toList().size()
		cat.delete()
		assertEquals 'Deleting the entity failed', 0, graph.vertices.toList().size()

		// create additional vertices
		Category cat2 = Category.create(graph)
		cat2.name = 'Foo'

		Category cat3 = Category.create(graph)
		cat3.setName('Bar')

		// find all
		Iterable<Category> cats = Category.findAll(graph)
		assertNotNull cats
		assertEquals 2, Iterables.size(cats)
	}
}
