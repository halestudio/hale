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

import com.tinkerpop.blueprints.Graph
import com.tinkerpop.gremlin.groovy.Gremlin

import eu.esdihumboldt.cst.test.TransformationExample
import eu.esdihumboldt.cst.test.TransformationExamples
import eu.esdihumboldt.hale.common.align.model.Alignment
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree
import eu.esdihumboldt.hale.common.align.model.transformation.tree.impl.TransformationTreeImpl
import eu.esdihumboldt.hale.common.align.tgraph.TransformationGraphConstants.NodeType


/**
 * {@link TransformationGraph} tests.<br>
 * <br>
 * Not using JUnit 4 features, as this doesn't seem to work with Eclipse.
 * 
 * @author Simon Templer
 */
class TransformationGraphTest extends GroovyTestCase {

	static {
		Gremlin.load();
	}

	void testSimpleRename() {
		TransformationExample sample = TransformationExamples
				.getExample(TransformationExamples.SIMPLE_RENAME);
		Alignment alignment = sample.getAlignment();

		// get the target type
		def type = alignment.getTypeCells().iterator().next().getTarget().values().iterator().next()
				.getDefinition().getDefinition();

		// create the transformation tree
		TransformationTree tree = new TransformationTreeImpl(type, alignment);

		// create the transformation graph
		Graph g = TransformationGraph.create(tree);

		assertEquals(4, g.V.filter{it.type == NodeType.Cell}.count());
		assertEquals(5, g.V.filter{it.type == NodeType.Source}.count());
		assertEquals(5, g.V.filter{it.type == NodeType.Target}.count());
	}

}
