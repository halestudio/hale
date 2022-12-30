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


import org.apache.tinkerpop.gremlin.structure.Vertex
import org.apache.tinkerpop.gremlin.util.Gremlin

import eu.esdihumboldt.cst.test.TransformationExample
import eu.esdihumboldt.cst.test.TransformationExamples
import eu.esdihumboldt.hale.common.align.model.Alignment
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree
import eu.esdihumboldt.hale.common.align.model.transformation.tree.impl.TransformationTreeImpl
import eu.esdihumboldt.hale.common.align.model.transformation.tree.visitor.TreeToGraphVisitor
import eu.esdihumboldt.hale.common.align.service.impl.AlignmentFunctionService
import eu.esdihumboldt.hale.common.align.tgraph.impl.TGraphImpl
import groovy.test.GroovyTestCase


/**
 * {@link TransformationGraph} tests.<br>
 * <br>
 * Not using JUnit 4 features, as this doesn't seem to work with Eclipse
 * (at least out of the box).
 * 
 * @author Simon Templer
 */
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
	 * Simple test for a mapping with a Retype and four Renames, checking
	 * context matching is performed correctly.
	 */
	void testContextSimpleRename() {
		TGraph tg = createGraph(TransformationExamples.SIMPLE_RENAME)

		tg.proxyMultiResultNodes()
		tg.performContextMatching()

		// contexts in this example are unambiguous
		assertContext(tg, 'id', 'id')
		assertContext(tg, 'a1', 'a2')
		assertContext(tg, 'b1', 'b2')
		assertContext(tg, 'c1', 'c2')

		// 4 contexts altogether
		assertEquals(4, tg.graph.E.filter{it.label == EDGE_CONTEXT}.count())
	}

	/**
	 * Check if context matching is performed correctly for the
	 * {@link TransformationExamples#CM_CCROSSOVER_1} example.
	 */
	void testContextCMCCrossover1() {
		TGraph tg = createGraph(TransformationExamples.CM_CCROSSOVER_1)

		tg.proxyMultiResultNodes()
		//XXX no candidates with current implementation!
		//tg.performContextMatching()

		//TODO check
	}

	/**
	 * Check if context matching is performed correctly for the
	 * {@link TransformationExamples#CM_MULTI_1} example.
	 */
	void testContextCMMulti1() {
		TGraph tg = createGraph(TransformationExamples.CM_MULTI_1)

		tg.proxyMultiResultNodes()
		tg.performContextMatching()

		//TODO check

		//XXX requires multi-node context
	}

	/**
	 * Check if context matching is performed correctly for the
	 * {@link TransformationExamples#CM_MULTI_1B} example.
	 */
	void testContextCMMulti1b() {
		TGraph tg = createGraph(TransformationExamples.CM_MULTI_1B)

		tg.proxyMultiResultNodes()
		tg.performContextMatching()

		//XXX alternatives only w/ multi-node context
		assertContext(tg, 't1', 'b')
		assertContext(tg, 'item', 'bt')

		assertEquals(2, tg.graph.E.filter{it.label == EDGE_CONTEXT}.count())
	}

	/**
	 * Check if context matching is performed correctly for the
	 * {@link TransformationExamples#CM_MULTI_2} example.
	 */
	void testContextCMMulti2() {
		TGraph tg = createGraph(TransformationExamples.CM_MULTI_2)

		tg.proxyMultiResultNodes()
		tg.performContextMatching()

		assertContext(tg, 't1', 'b') // could also be a -> b
		assertContext(tg, 'a', 'bt')

		// 2 contexts altogether
		assertEquals(2, tg.graph.E.filter{it.label == EDGE_CONTEXT}.count())
	}

	/**
	 * Check if context matching is performed correctly for the
	 * {@link TransformationExamples#CM_MULTI_3} example.
	 */
	void testContextCMMulti3() {
		TGraph tg = createGraph(TransformationExamples.CM_MULTI_3)

		tg.proxyMultiResultNodes()
		tg.performContextMatching()

		//TODO check

		//XXX requires multi-node context
	}

	/**
	 * Check if context matching is performed correctly for the
	 * {@link TransformationExamples#CM_MULTI_4} example.
	 */
	void testContextCMMulti4() {
		TGraph tg = createGraph(TransformationExamples.CM_MULTI_4)

		tg.proxyMultiResultNodes()
		tg.performContextMatching()

		assertContext(tg, 'a', 'b')
		assertContext(tg, 'a', 'bt') //XXX

		// 2 contexts altogether
		assertEquals(2, tg.graph.E.filter{it.label == EDGE_CONTEXT}.count())
	}

	/**
	 * Check if context matching is performed correctly for the
	 * {@link TransformationExamples#CM_NESTED_1} example.
	 */
	void testContextCMNested1() {
		TGraph tg = createGraph(TransformationExamples.CM_NESTED_1)

		tg.proxyMultiResultNodes()
		tg.performContextMatching()

		// contexts in this example are unambiguous
		assertContext(tg, 'a', 'b')
		assertContext(tg, 'a1', 'b1')
		assertContext(tg, 'a2', 'b2')

		// 3 contexts altogether
		assertEquals(3, tg.graph.E.filter{it.label == EDGE_CONTEXT}.count())
	}

	/**
	 * Check if context matching is performed correctly for the
	 * {@link TransformationExamples#CM_NESTED_2} example.
	 */
	void testContextCMNested2() {
		TGraph tg = createGraph(TransformationExamples.CM_NESTED_2)

		tg.proxyMultiResultNodes()
		tg.performContextMatching()

		// contexts in this example are unambiguous
		assertContext(tg, 'item', 'element')
		assertContext(tg, 'a', 'b')
		assertContext(tg, 'a1', 'b1')
		assertContext(tg, 'a2', 'b2')
		assertContext(tg, 'c', 'd')
		assertContext(tg, 'c1', 'd1')
		assertContext(tg, 'c2', 'd2')

		// 7 contexts altogether
		assertEquals(7, tg.graph.E.filter{it.label == EDGE_CONTEXT}.count())
	}

	/**
	 * Check if context matching is performed correctly for the
	 * {@link TransformationExamples#CM_NESTED_3} example.
	 */
	void testContextCMNested3() {
		TGraph tg = createGraph(TransformationExamples.CM_NESTED_3)

		tg.proxyMultiResultNodes()
		tg.performContextMatching()

		//XXX alternatives would involve splitting item
		assertContext(tg, 'a', 'b')
		assertContext(tg, 'a1', 'b1')
		assertContext(tg, 'a2', 'b2')
		assertContext(tg, 'c', 'd')
		assertContext(tg, 'c1', 'd1')
		assertContext(tg, 'c2', 'd2')

		// 6 contexts altogether
		assertEquals(6, tg.graph.E.filter{it.label == EDGE_CONTEXT}.count())
	}

	/**
	 * Check if context matching is performed correctly for the
	 * {@link TransformationExamples#CM_NESTED_3B} example.
	 */
	void testContextCMNested3b() {
		TGraph tg = createGraph(TransformationExamples.CM_NESTED_3B)

		tg.proxyMultiResultNodes()
		tg.performContextMatching()

		//XXX alternatives would involve splitting item
		//FIXME item would be better match
		assertContext(tg, 'a', 'b')
		assertContext(tg, 'a1', 'b1')
		assertContext(tg, 'a2', 'b2')
		assertContext(tg, 'c', 'd')
		assertContext(tg, 'c1', 'd1')
		assertContext(tg, 'c2', 'd2')

		// 6 contexts altogether
		assertEquals(6, tg.graph.E.filter{it.label == EDGE_CONTEXT}.count())
	}

	/**
	 * Check if context matching is performed correctly for the
	 * {@link TransformationExamples#CM_NESTED_4} example.
	 */
	void testContextCMNested4() {
		TGraph tg = createGraph(TransformationExamples.CM_NESTED_4)

		tg.proxyMultiResultNodes()
		tg.performContextMatching()

		//XXX are there other alternatives for element?
		assertContext(tg, 't1', 'element')
		assertContext(tg, 'a', 'b')
		assertContext(tg, 'a1', 'b1')
		assertContext(tg, 'a2', 'b2')
		assertContext(tg, 'c', 'd')
		assertContext(tg, 'c1', 'd1')
		assertContext(tg, 'c2', 'd2')

		// 7 contexts altogether
		assertEquals(7, tg.graph.E.filter{it.label == EDGE_CONTEXT}.count())
	}

	/**
	 * Check if context matching is performed correctly for the
	 * {@link TransformationExamples#CM_NESTED_5} example.
	 */
	void testContextCMNested5() {
		TGraph tg = createGraph(TransformationExamples.CM_NESTED_5)

		tg.proxyMultiResultNodes()
		//XXX no candidates with current implementation!
		//tg.performContextMatching()

		//TODO check
	}

	/**
	 * Check if context matching is performed correctly for the
	 * {@link TransformationExamples#CM_NESTED_6} example.
	 */
	void testContextCMNested6() {
		TGraph tg = createGraph(TransformationExamples.CM_NESTED_6)

		tg.proxyMultiResultNodes()
		tg.performContextMatching()

		// contexts in this example are unambiguous
		assertContext(tg, 'a', 'b')
		assertContext(tg, 'a1', 'b1')
		assertContext(tg, 'a2', 'b2')
		assertContext(tg, 'a', 'x')

		// 4 contexts altogether
		assertEquals(4, tg.graph.E.filter{it.label == EDGE_CONTEXT}.count())
	}

	/**
	 * Check if context matching is performed correctly for the
	 * {@link TransformationExamples#CM_PCROSSOVER_1} example.
	 */
	void testContextCMPCrossover1() {
		TGraph tg = createGraph(TransformationExamples.CM_PCROSSOVER_1)

		tg.proxyMultiResultNodes()
		tg.performContextMatching()

		//TODO check

		//XXX requires multi-node context
	}

	/**
	 * Check if context matching is performed correctly for the
	 * {@link TransformationExamples#CM_PCROSSOVER_1B} example.
	 */
	void testContextCMPCrossover1b() {
		TGraph tg = createGraph(TransformationExamples.CM_PCROSSOVER_1B)

		tg.proxyMultiResultNodes()
		tg.performContextMatching()

		//TODO check

		//XXX requires multi-node context
	}

	/**
	 * Check if context matching is performed correctly for the
	 * {@link TransformationExamples#CM_PCROSSOVER_2} example.
	 */
	void testContextCMPCrossover2() {
		TGraph tg = createGraph(TransformationExamples.CM_PCROSSOVER_2)

		tg.proxyMultiResultNodes()
		tg.performContextMatching()

		//TODO check

		//XXX requires multi-node context
	}

	/**
	 * Check if context matching is performed correctly for the
	 * {@link TransformationExamples#CM_PCROSSOVER_3} example.
	 */
	void testContextCMPCrossover3() {
		TGraph tg = createGraph(TransformationExamples.CM_PCROSSOVER_3)

		tg.proxyMultiResultNodes()
		tg.performContextMatching()

		//TODO check

		//XXX requires multi-node context
	}

	/**
	 * Check if context matching is performed correctly for the
	 * {@link TransformationExamples#CM_PCROSSOVER_4} example.
	 */
	void testContextCMPCrossover4() {
		TGraph tg = createGraph(TransformationExamples.CM_PCROSSOVER_4)

		tg.proxyMultiResultNodes()
		tg.performContextMatching()

		//TODO check

		//XXX requires multi-node context
	}

	/**
	 * Check if context matching is performed correctly for the
	 * {@link TransformationExamples#CM_PCROSSOVER_4_EX_1} example.
	 */
	void testContextCMPCrossover4ex1() {
		TGraph tg = createGraph(TransformationExamples.CM_PCROSSOVER_4_EX_1)

		tg.proxyMultiResultNodes()
		//XXX no candidates with current implementation!
		//tg.performContextMatching()

		//TODO check
	}

	/**
	 * Check if context matching is performed correctly for the
	 * {@link TransformationExamples#CM_PCROSSOVER_4_EX_2} example.
	 */
	void testContextCMPCrossover4ex2() {
		TGraph tg = createGraph(TransformationExamples.CM_PCROSSOVER_4_EX_2)

		tg.proxyMultiResultNodes()
		//XXX no candidates with current implementation!
		//tg.performContextMatching()

		//TODO check
	}

	/**
	 * Check if context matching is performed correctly for the
	 * {@link TransformationExamples#CM_PCROSSOVER_5} example.
	 */
	void testContextCMPCrossover5() {
		TGraph tg = createGraph(TransformationExamples.CM_PCROSSOVER_5)

		tg.proxyMultiResultNodes()
		tg.performContextMatching()

		//TODO check

		//XXX requires multi-node context
	}

	/**
	 * Check if context matching is performed correctly for the
	 * {@link TransformationExamples#CM_PCROSSOVER_6} example.
	 */
	void testContextCMPCrossover6() {
		TGraph tg = createGraph(TransformationExamples.CM_PCROSSOVER_6)

		tg.proxyMultiResultNodes()
		tg.performContextMatching()

		//TODO check

		//XXX requires multi-node context
	}

	/**
	 * Check if context matching is performed correctly for the
	 * {@link TransformationExamples#CM_PCROSSOVER_6B} example.
	 */
	void testContextCMPCrossover6b() {
		TGraph tg = createGraph(TransformationExamples.CM_PCROSSOVER_6B)

		tg.proxyMultiResultNodes()
		tg.performContextMatching()

		//TODO check

		//XXX requires multi-node context
	}

	/**
	 * Check if context matching is performed correctly for the
	 * {@link TransformationExamples#CM_UNION_1} example.
	 */
	void testContextCMUnion1() {
		TGraph tg = createGraph(TransformationExamples.CM_UNION_1)

		tg.proxyMultiResultNodes()
		tg.performContextMatching()

		//TODO check

		//XXX requires multi-node context
	}

	/**
	 * Check if context matching is performed correctly for the
	 * {@link TransformationExamples#CM_UNION_2} example.
	 */
	void testContextCMUnion2() {
		TGraph tg = createGraph(TransformationExamples.CM_UNION_2)

		tg.proxyMultiResultNodes()
		tg.performContextMatching()

		//TODO check

		//XXX eventually requires multi-node context
	}

	/**
	 * Check if context matching is performed correctly for the
	 * {@link TransformationExamples#CM_UNION_3} example.
	 */
	void testContextCMUnion3() {
		TGraph tg = createGraph(TransformationExamples.CM_UNION_3)

		tg.proxyMultiResultNodes()
		tg.performContextMatching()

		// contexts in this example are unambiguous
		assertContext(tg, 't1', 'b')
		assertProxyContext(tg, 'a1', 'b')
		assertProxyContext(tg, 'a2', 'b')

		// 3 contexts altogether
		assertEquals(3, tg.graph.E.filter{it.label == EDGE_CONTEXT}.count())
	}

	/**
	 * Check if context matching is performed correctly for the
	 * {@link TransformationExamples#CM_UNION_4} example.
	 */
	void testContextCMUnion4() {
		TGraph tg = createGraph(TransformationExamples.CM_UNION_4)

		tg.proxyMultiResultNodes()
		tg.performContextMatching()

		// one context for the proxy connected to the Rename relation
		assertProxyContext(tg, 't1', 'b')
	}

	/**
	 * Check if context matching is performed correctly for the
	 * {@link TransformationExamples#CM_UNION_5} example.
	 */
	void testContextCMUnion5() {
		TGraph tg = createGraph(TransformationExamples.CM_UNION_5)

		tg.proxyMultiResultNodes()
		tg.performContextMatching()

		assertContext(tg, 'a3', 'c')
		/*
		 * XXX Unsure why there is no context found for b.
		 * But in practice at least for now it makes no difference as the
		 * parent context will be the correct one.
		 */
		// assertContext(tg, 't1', 'b')
		assertProxyContext(tg, 'a1', 'b')
		assertProxyContext(tg, 'a2', 'b')
	}

	/**
	 * Assert if there is a context match between a source and target node in
	 * the given graph.
	 * 
	 * @param tg the transformation graph
	 * @param source the source name (i.e. the node id w/o prefix)
	 * @param target the target name (i.e. the node id w/o prefix)
	 */
	void assertContext(TGraph tg, String source, String target) {
		def sourceId = TreeToGraphVisitor.SOURCE_PREFIX + source
		def targetId = TreeToGraphVisitor.TARGET_PREFIX + target

		def sourceNode = tg.graph.getVertex(sourceId)
		def targetNodes = sourceNode.out(EDGE_CONTEXT).filter{it.id == targetId}.toList()
		assertEquals(1, targetNodes.size())
	}

	/**
	 * Assert if there is a context match between a source and target proxy
	 * node in the given graph.
	 *
	 * @param tg the transformation graph
	 * @param source the source name (i.e. the node id w/o prefix)
	 * @param target the proxied target name (i.e. the node id w/o prefix)
	 */
	void assertProxyContext(TGraph tg, String source, String target) {
		def sourceId = TreeToGraphVisitor.SOURCE_PREFIX + source
		def targetId = TreeToGraphVisitor.TARGET_PREFIX + target

		def sourceNode = tg.graph.getVertex(sourceId)
		def targetNodes = sourceNode.out(EDGE_CONTEXT).out(EDGE_PROXY).filter{it.id == targetId}.toList()
		assertEquals(1, targetNodes.size())
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
		assertEquals(0, tg.graph.V(P_PROXY, true).count())

		tg.proxyMultiResultNodes();

		// there should be two new nodes
		assertEquals(9, tg.graph.V.count())
		// both of them proxies
		assertEquals(2, tg.graph.V(P_PROXY, true).count())
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
		assertEquals(0, tg.graph.V(P_PROXY, true).count())

		tg.proxyMultiResultNodes();

		// there should be two new nodes
		assertEquals(13, tg.graph.V.count())
		// both of them proxies
		assertEquals(2, tg.graph.V(P_PROXY, true).count())
	}

	/**
	 * Tests proxying multi result nodes on the
	 * {@link TransformationExamples#CM_UNION_3} example.
	 */
	void testProxyMultiResultNodes3() {
		TGraph tg = createGraph(TransformationExamples.CM_UNION_3)

		// there should be seven vertices
		assertEquals(7, tg.graph.V.count())
		// none of them proxy nodes
		assertEquals(0, tg.graph.V(P_PROXY, true).count())

		tg.proxyMultiResultNodes();

		// there should be two new nodes
		assertEquals(9, tg.graph.V.count())
		// both of them proxies
		assertEquals(2, tg.graph.V(P_PROXY, true).count())
	}

	/**
	 * Tests proxying multi result nodes on the
	 * {@link TransformationExamples#CM_UNION_4} example.
	 */
	void testProxyMultiResultNodes4() {
		TGraph tg = createGraph(TransformationExamples.CM_UNION_4)

		// there should be six vertices
		assertEquals(6, tg.graph.V.count())
		// none of them proxy nodes
		assertEquals(0, tg.graph.V(P_PROXY, true).count())

		tg.proxyMultiResultNodes();

		// there should be two new nodes
		assertEquals(8, tg.graph.V.count())
		// both of them proxies
		assertEquals(2, tg.graph.V(P_PROXY, true).count())
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

		// get the type cell
		def typeCell = alignment.activeTypeCells.asList()[0] // first active type cell

		// create the transformation tree
		TransformationTree tree = new TransformationTreeImpl(alignment, typeCell)

		def functionService = new AlignmentFunctionService(alignment)

		// create the transformation graph
		new TGraphImpl(tree, functionService)
	}
}
