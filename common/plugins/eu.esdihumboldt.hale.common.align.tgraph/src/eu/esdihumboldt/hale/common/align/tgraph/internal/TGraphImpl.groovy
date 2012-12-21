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
					proxy.setProperty P_TYPE, NodeType.Target
					proxy.setProperty P_PROXY, true

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

	@Override
	TGraph performContextMatching() {
		/*
		 * XXX First experiments with context matching.
		 * For now looking only at simple examples
		 * w/o a context hierarchy (e.g. simple rename, cm_multi_2/4) 
		 */

		// get the target type node
		def root = getTarget();

		// get all its direct children
		def children = root.in.has(P_TYPE, NodeType.Target)

		for (Vertex node : children) {
			// for each child (which is a target node)

			// determine all paths leading to this node
			/*
			 * This is only working because there are no cycles in the graph
			 * and all incoming connections are acceptable. 
			 */
			def paths = node.in.loop(1){
				it.object.inE.hasNext() // loop while there is an incoming edge
			}.path.toList()

			// reverse paths so they start with source (type) nodes
			paths = paths.collect{it.reverse()}

			//XXX debug
			println "Paths: $paths"

			// find candidates for context match

			/*
			 * All paths have to have at least the one item in common -
			 * otherwise there is no point on the source side where they
			 * converge.
			 */
			// find the elements equal in all paths
			def common = findCommonPaths(paths)

			//XXX debug
			println "Common: $common"

			/*
			 * TODO Analyze common elements
			 * Are there any outgoing connections other than the determined paths?
			 * Are they candidates for the context match?
			 * Changes to the graph may be necessary to use an element as context.  
			 */
			/*
			 * TODO Check for alternative contexts by grouping child nodes that
			 * occur in the paths. 
			 */

			/*
			 * XXX Now what about the children of node?
			 * XXX How to pass the node context as parent context?
			 */
		}
	}

	/**
	 * Find the common elements in a list of paths.
	 * 
	 * @param paths the paths, a list of lists
	 * @return the list of common leading elements
	 */
	private def findCommonPaths(def paths) {
		assert paths
		assert paths.size() > 0

		def common
		for (path in paths) {
			if (common == null) {
				common = path
			} else {
				for (int i = 0; i < Math.min(common.size(), path.size()); i++) {
					if (common[i] != path[i]) {
						common = common.take(i)
						break;
					}
				}
			}
		}
		return common
	}

}
