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

import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree
import eu.esdihumboldt.hale.common.align.tgraph.TransformationGraphConstants.NodeType
import eu.esdihumboldt.hale.common.align.tgraph.internal.TransformationGraphFactory


/**
 * Transformation graph.
 * 
 * @author Simon Templer
 */
class TransformationGraph implements TransformationGraphConstants {

	/** The internal graph */
	private Graph graph

	/**
	 * Create a transformation graph from a transformation tree.
	 * 
	 * @param ttree the transformation tree
	 */
	TransformationGraph(TransformationTree ttree) {
		graph = TransformationGraphFactory.create(ttree)
	}

	/**
	 * Create a transformation graph from a given graph.
	 * 
	 * @param g the graph, the caller is responsible for it to be a valid
	 *   transformation graph
	 */
	TransformationGraph(Graph g) {
		graph = g
	}

	/**
	 * Get the graph
	 * @return
	 */
	Graph getGraph() {
		graph
	}

	/**
	 * Create proxy nodes for target nodes that have multiple cells assigning
	 * results to it - for each incoming edge from a cell a proxy node is
	 * created.
	 * 
	 * @return this transformation graph
	 */
	TransformationGraph proxyMultiResultNodes() {
		def vertices =
				g.V(P_TYPE, NodeType.Target) // target nodes
				.filter{it.inE(EDGE_RESULT).count() > 1} // with more than one cell attached

		//TODO insert proxies

		this
	}
}
