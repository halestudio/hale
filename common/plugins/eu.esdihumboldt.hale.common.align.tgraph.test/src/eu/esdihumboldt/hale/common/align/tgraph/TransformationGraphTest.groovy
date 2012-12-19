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

import com.tinkerpop.gremlin.groovy.Gremlin

import eu.esdihumboldt.cst.test.TransformationExample
import eu.esdihumboldt.cst.test.TransformationExamples
import eu.esdihumboldt.hale.common.align.model.Alignment
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree
import eu.esdihumboldt.hale.common.align.model.transformation.tree.impl.TransformationTreeImpl
import eu.esdihumboldt.hale.common.align.tgraph.TransformationGraphConstants.NodeType
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition


/**
 * {@link TransformationGraph} tests.<br>
 * <br>
 * Not using JUnit 4 features, as this doesn't seem to work with Eclipse
 * (at least out of the box).
 * 
 * @author Simon Templer
 */
class TransformationGraphTest extends GroovyTestCase implements TransformationGraphConstants {

	static {
		Gremlin.load()
	}

	/**
	 * Simple test for a mapping with a Retype and four Renames, checking if
	 * the vertices count is OK.  
	 */
	void testCountSimpleRename() {
		TransformationGraph tg = createGraph(TransformationExamples.SIMPLE_RENAME)

		// check vertices count of the different types in different ways
		assertEquals(4, tg.graph.V.filter{it.type == NodeType.Cell}.count())
		assertEquals(5, tg.graph.V('type', NodeType.Source).count())
		assertEquals(5, tg.graph.V(P_TYPE, NodeType.Target).count())
	}

	void testProxyMultiResultNodes() {

	}

	private TransformationGraph createGraph(String exampleId) {
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
		new TransformationGraph(tree)
	}

}
