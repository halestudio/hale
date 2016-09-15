/*
 * Copyright (c) 2013 Fraunhofer IGD
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
 *     Fraunhofer IGD
 */

package eu.esdihumboldt.hale.io.xslt.transformations.base

import eu.esdihumboldt.cst.test.TransformationExample
import eu.esdihumboldt.cst.test.TransformationExamples
import eu.esdihumboldt.hale.common.align.model.Alignment
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree
import eu.esdihumboldt.hale.common.align.model.transformation.tree.impl.TransformationTreeImpl
import eu.esdihumboldt.hale.common.align.model.transformation.tree.visitor.TreeToGraphVisitor
import eu.esdihumboldt.hale.common.align.service.impl.AlignmentFunctionService
import eu.esdihumboldt.hale.common.align.tgraph.TGraph
import eu.esdihumboldt.hale.common.align.tgraph.impl.TGraphImpl


/**
 * Tests on the {@link AbstractTransformationTraverser} class.
 * 
 * @author Simon Templer
 */
class TransformationTraverserTest extends GroovyTestCase {

	/**
	 * Simple test for a mapping with a Retype and four Renames, checking if
	 * the traversion order is correct.
	 */
	void testOrderSimpleRename() {
		TGraph tg = createGraph(TransformationExamples.SIMPLE_RENAME)

		OrderTransformationTraverser t = new OrderTransformationTraverser()
		t.traverse(tg)

		checkOrder(t.nodes, ['id', 'a2', 'b2', 'c2'])
	}

	/**
	 * Test the traversion order for the
	 * {@link TransformationExamples#CM_NESTED_1} example.
	 */
	void testOrderCMNested1() {
		TGraph tg = createGraph(TransformationExamples.CM_NESTED_1)

		OrderTransformationTraverser t = new OrderTransformationTraverser()
		t.traverse(tg)

		checkOrder(t.nodes, ['b', 'b1', 'b2'])
	}

	/**
	 * Test the traversion order for the
	 * {@link TransformationExamples#CM_NESTED_4} example.
	 */
	void testOrderCMNested4() {
		TGraph tg = createGraph(TransformationExamples.CM_NESTED_4)

		OrderTransformationTraverser t = new OrderTransformationTraverser()
		t.traverse(tg)

		checkOrder(t.nodes, [
			'element',
			'b',
			'b1',
			'b2',
			'd',
			'd1',
			'd2'
		])
	}

	private void checkOrder(def nodes, def names) {
		assertEquals(nodes.size(), names.size())

		for (i in 0..nodes.size() - 1) {
			def targetId = TreeToGraphVisitor.TARGET_PREFIX + names[i]
			assertEquals(targetId, nodes[i].id)
		}
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
