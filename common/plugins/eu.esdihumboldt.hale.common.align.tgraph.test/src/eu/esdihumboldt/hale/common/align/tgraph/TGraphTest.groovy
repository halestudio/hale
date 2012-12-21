/*
 * Copyright (c) 2012 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.align.tgraph

import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.gremlin.groovy.Gremlin

import eu.esdihumboldt.cst.test.TransformationExample
import eu.esdihumboldt.cst.test.TransformationExamples
import eu.esdihumboldt.hale.common.align.model.Alignment
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree
import eu.esdihumboldt.hale.common.align.model.transformation.tree.impl.TransformationTreeImpl
import eu.esdihumboldt.hale.common.align.tgraph.internal.TGraphImpl
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition


/**
 * {@link TransformationGraph} tests.<br>
 * <br>
 * Not using JUnit 4 features, as this doesn't seem to work with Eclipse
 * (at least out of the box).
 * 
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
class TGraphTest extends GroovyTestCase implements TGraphConstants {

	static {
		Gremlin.load()
	}

	/**
	 * Simple test for a mapping with a Retype and four Renames, checking if
	 * the vertices count is OK.  
	 */
	void testCountSimpleRename() {
		TGraph tg = createGraph(TransformationExamples.SIMPLE_RENAME)

		// check vertices count of the different types in different ways
		assertEquals(4, tg.graph.V.filter{it.type == NodeType.Cell}.count())
		assertEquals(5, tg.graph.V('type', NodeType.Source).count())
		assertEquals(5, tg.graph.V(P_TYPE, NodeType.Target).count())
	}

	/**
	 * Simple test for a mapping with a Retype and four Renames, checking the
	 * correct target type node is retrieved.
	 */
	void testTargetSimpleRename() {
		TGraph tg = createGraph(TransformationExamples.SIMPLE_RENAME)

		Vertex targetType = tg.getTarget();
		assertNotNull(targetType)

		TypeEntityDefinition ted = targetType.getProperty(P_ENTITY)
		assertNotNull(ted)

		assertEquals('T2', ted.definition.name.localPart)
	}

	/**
	 * Tests proxying multi result nodes on the
	 * {@link TransformationExamples#CM_UNION_1} example.
	 */
	void testProxyMultiResultNodes1() {
		TGraph tg = createGraph(TransformationExamples.CM_UNION_1)

		// there should be seven vertices
		assertEquals(7, tg.graph.V.count())
		// none of them proxy nodes
		assertEquals(0, tg.graph.V(P_TYPE, NodeType.Proxy).count())

		tg.proxyMultiResultNodes();

		// there should be two new nodes
		assertEquals(9, tg.graph.V.count())
		// both of them proxies
		assertEquals(2, tg.graph.V(P_TYPE, NodeType.Proxy).count())
	}

	/**
	 * Tests proxying multi result nodes on the
	 * {@link TransformationExamples#CM_UNION_2} example.
	 */
	void testProxyMultiResultNodes2() {
		TGraph tg = createGraph(TransformationExamples.CM_UNION_2)

		// there should be seven vertices
		assertEquals(11, tg.graph.V.count())
		// none of them proxy nodes
		assertEquals(0, tg.graph.V(P_TYPE, NodeType.Proxy).count())

		tg.proxyMultiResultNodes();

		// there should be two new nodes
		assertEquals(13, tg.graph.V.count())
		// both of them proxies
		assertEquals(2, tg.graph.V(P_TYPE, NodeType.Proxy).count())
	}

	/**
	 * Create the transformation graph for the given example ID.
	 * @param exampleId the example ID as defined by
	 *   {@link TransformationExamples}
	 * @return the transformation graph
	 */
	private TGraph createGraph(String exampleId) {
		TransformationExample sample = TransformationExamples.getExample(exampleId)
		Alignment alignment = sample.getAlignment()

		// get the target type
		TypeDefinition type =
				alignment.typeCells.asList()[0] // first type cell
				.target.values().asList()[0] // first target type
				.definition.definition // its type definition

		// create the transformation tree
		TransformationTree tree = new TransformationTreeImpl(type, alignment)

		// create the transformation graph
		new TGraphImpl(tree)
	}

}
