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

package eu.esdihumboldt.hale.common.align.tgraph.internal

import com.tinkerpop.blueprints.Direction
import com.tinkerpop.blueprints.Edge
import com.tinkerpop.blueprints.Graph
import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.util.ElementHelper

import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree
import eu.esdihumboldt.hale.common.align.tgraph.TGraph
import eu.esdihumboldt.hale.common.align.tgraph.TGraphConstants.NodeType



/**
 * Transformation graph.
 * 
 * @author Simon Templer
 */
class TGraphImpl implements TGraph {

	/** The internal graph */
	private Graph graph

	/**
	 * Create a transformation graph from a transformation tree.
	 * 
	 * @param ttree the transformation tree
	 */
	TGraphImpl(TransformationTree ttree) {
		graph = TGraphFactory.create(ttree)
	}

	/**
	 * Create a transformation graph from a given graph.
	 * 
	 * @param g the graph, the caller is responsible for it to be a valid
	 *   transformation graph
	 */
	TGraphImpl(Graph g) {
		graph = g
	}

	@Override
	Graph getGraph() {
		graph
	}

	@Override
	Vertex getTarget() {
		// get the target node that has no outgoing edges
		graph.V(P_TYPE, NodeType.Target).filter{!it.outE.hasNext()}.next()
	}

	@Override
	TGraph proxyMultiResultNodes() {
		graph.V(P_TYPE, NodeType.Target) // find target nodes
				.filter{
					it.inE(EDGE_RESULT).count() > 1} // with more than one cell attached
				.inE(EDGE_RESULT) // and get the incoming result edges
				.sideEffect{
					// for each of those edges
					// create a bypass

					Vertex proxy = graph.addVertex()
					proxy.setProperty P_TYPE, NodeType.Proxy

					Edge newResult = graph.addEdge(null,
							it.getVertex(Direction.IN), proxy, EDGE_RESULT)
					// copy properties from original edge
					ElementHelper.copyProperties(it, newResult)

					graph.addEdge(null,
							proxy, it.getVertex(Direction.OUT), EDGE_PROXY)

					// remove the original edge
					graph.removeEdge(it)
				}.iterate() // actually traverse the graph

		this
	}
}
