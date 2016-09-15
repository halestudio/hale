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

package eu.esdihumboldt.hale.common.align.tgraph.impl

import com.tinkerpop.blueprints.Direction
import com.tinkerpop.blueprints.Edge
import com.tinkerpop.blueprints.Graph
import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.util.ElementHelper

import eu.esdihumboldt.hale.common.align.model.EntityDefinition
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree
import eu.esdihumboldt.hale.common.align.service.FunctionService
import eu.esdihumboldt.hale.common.align.tgraph.TGraph
import eu.esdihumboldt.hale.common.align.tgraph.TGraphHelpers
import eu.esdihumboldt.hale.common.align.tgraph.TGraphConstants.NodeType
import eu.esdihumboldt.hale.common.align.tgraph.impl.internal.TGraphFactory
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality



/**
 * Transformation graph.
 * 
 * @author Simon Templer
 */
class TGraphImpl implements TGraph {

	static {
		TGraphHelpers.load()
	}

	/** The internal graph */
	private Graph graph

	/**
	 * Create a transformation graph from a transformation tree.
	 * 
	 * @param ttree the transformation tree
	 */
	TGraphImpl(TransformationTree ttree, FunctionService functions) {
		graph = TGraphFactory.create(ttree, functions)
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
		/*
		 * XXX Creating proxies for multi result nodes makes no sense if they
		 * can only share the same context.
		 * TODO Creating proxies could therefore be restricted to nodes that
		 * have no children where the cardinality can be moved to the proxy
		 * nodes.  
		 */

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
							it.getVertex(Direction.OUT), proxy, EDGE_RESULT)
					// copy properties from original edge
					ElementHelper.copyProperties(it, newResult)

					graph.addEdge(null,
							proxy, it.getVertex(Direction.IN), EDGE_PROXY)

					// remove the original edge
					graph.removeEdge(it)
					
					/*
					 * Proxy cardinalities
					 */
					
					/*
					 * First attempt was to set the entity cardinality on the
					 * proxies and an cardinality of 0..1 for the proxied node.
					 * This has the advantage that context matching can be
					 * performed separately for the proxies if the entity may
					 * occur multiple times.
					 * However, this poses a problem when the proxied node has
					 * children - they would be reduced to the new parent's
					 * cardinality.
					 * Moving the cardinality to the proxies must instead
					 * involve duplicating the whole associated subgraph with
					 * eventually also introducing proxies on the source side. 
					 */
					// set a single cardinality on the proxied vertex
//					Vertex proxied = it.getVertex(Direction.IN)
//					proxied.setProperty(P_CARDINALITY, Cardinality.CC_OPTIONAL);
					// and set the entity cardinality on the proxy
					//assert proxied.entity()
//					proxy.setProperty(P_CARDINALITY,
//						proxied.entity().getDefinition().getConstraint(Cardinality))
					
					/*
					 * The alternative used now is to keep the cardinality of the
					 * proxied node as is and use a cardinality of 0..1 for the
					 * proxies. This means the context is defined by the proxied
					 * node and children can be handled normally.
					 * Through this the possibilities in context matching are
					 * more limited than with the first approach, but can be made
					 * to work much easier for the cases it covers.
					 */
					proxy.setProperty(P_CARDINALITY, Cardinality.CC_OPTIONAL)
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

		for (Vertex target : children) {
			// for each child (which is a target node)
			// determine the context match
			contextMatching(target, null)
		}

		this
	}

	/**
	 * Perform context matching for the given target node.
	 * 
	 * @param target the target node
	 * @param parentContext the parent context, may be <code>null</code>
	 */
	private void contextMatching(Vertex target, def parentContext) {
		//XXX debug
		println "Context matching for $target"

		// determine all paths leading to this node
		/*
		 * This is only working because there are no cycles in the graph
		 * and all incoming connections are acceptable.
		 */
		def paths = target.in(EDGES_CORE).loop(1){
			it.object.inE(EDGES_CORE).hasNext() && // loop while there is an incoming edge
			it.object != parentContext // but don't continue once the parent context was reached
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

		// only retain source nodes
		common.retainAll{
			it.getProperty(P_TYPE) == NodeType.Source
		}

		//XXX debug
		println "Common: $common"

		/*
		 * TODO Candidate configured in mapping???
		 */

		/*
		 * TODO Analyze common elements
		 * Are there any outgoing connections other than the determined paths?
		 * Are they candidates for the context match?
		 * Changes to the graph may be necessary to use an element as context.
		 */
		def candidates = []
		def sourcePath = []
		for (source in common) {
			// determine if a source node is a valid candidate

			// add the source node to the source path to obtain its path
			sourcePath.add source

			/*
			 * Check if source and target form a self-contained sub-graph.
			 *
			 * This means that there may be no other routes outgoing from
			 * source through the graph than those in the paths list.
			 */
			boolean sc = checkSelfContained(sourcePath, paths)

			if (sc) {
				// is a valid candidate
				candidates.add source
			}
			else {
				/*
				 * This doesn't necessarily mean that this is not a valid
				 * candidate, but that a change to the graph may be needed
				 * to use it as candidate.
				 */
				//XXX for now, we stop here

				// get the node entity
				EntityDefinition entity = source.getProperty(P_ENTITY)
				//TODO
			}
		}

		/*
		 * TODO Check for alternative contexts by grouping child nodes that
		 * occur in the paths.
		 */

		//XXX debug
		println "Candidates: $candidates"

		/*
		 * TODO Determine an appropriate default candidate?!
		 */
		def context;
		if (candidates) {
			context = findBestCandidate(candidates, target)
		}
		else {
			// no context may be also valid, e.g. for assignments
			// the context then is the same as the parent context
			context = parentContext
			
			//XXX debug
			println "No context for node $target, using parent context"
		}
		
		//XXX debug
		println "Selected candidate: $context"

		// create the context edge
		// from source to target not to introduce any cycles into the graph
		if (context) {
			graph.addEdge(null, context, target, EDGE_CONTEXT)
		}

		/*
		 * Perform the context matching for the node children, passing the
		 * parent context.
		 */
		// get all its direct children
		def children = target.in.has(P_TYPE, NodeType.Target)

		for (child in children) {
			contextMatching(child, context)
		}
	}

	/**
	 * Find the best candidate for a context match from the given list of
	 * candidates.
	 * 
	 * @param candidates the list of context match candidates, these are source
	 *   nodes (XXX later maybe also sets of nodes)
	 * @param target the target node
	 * @return
	 */
	private def findBestCandidate(def candidates, def target) {
		assert candidates

		if (candidates.size() == 1) {
			return candidates[0]
		}

		// find the best candidates based on cardinality
		boolean targetMul = target.cardinality().mayOccurMultipleTimes()

		//XXX assuming that candidates are ordered by hierarchy

		if (targetMul) {
			/*
			 * Strategy: Find candidate highest in the hierarchy that also
			 * allows multiple values.
			 */
			for (candidate in candidates) {
				if (candidate.cardinality().mayOccurMultipleTimes()) {
					return candidate
				}
			}
		}
		else {
			/*
			 * Strategy: Find candidate highest in the hierarchy
			 */
			return candidates[0]
		}

		// fall back to the candidate highest in the hierarchy
		return candidates[0]
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

	/**
	 * Check if a source and target node form a self contained sub-graph.
	 * 
	 * @param sourcePath the path leading to the source node
	 * @param targetPaths the list of paths leading to the target node
	 * @return if the sub-graph is self contained
	 */
	private boolean checkSelfContained(def sourcePath, def targetPaths) {
		/*
		 * For the sub-graph to be self contained there may be no other routes
		 * outgoing from the source node through the graph than those in the
		 * target paths.
		 */

		// the source node
		def source = sourcePath.last()

		// cripple paths to represent path after source
		def paths = targetPaths.collect{
			// assure that target paths start with source path
			assert sourcePath == it.take(sourcePath.size())

			it.subList(sourcePath.size(), it.size())
		}

		// check if there are any other paths and
		// check the same for the source children
		checkOnlyPaths(source, paths)
	}

	/**
	 * Check if the given paths are the only paths leading from the given
	 * source node. If there are no paths given, the check will terminate
	 * successfully (as the paths target is reached).
	 *
	 * @param source the source node
	 * @param paths the list of paths
	 * @return if these paths are the only paths leading away from source
	 */
	private boolean checkOnlyPaths(def source, def paths) {
		// remove all empty paths
		paths.retainAll{
			!it.empty
		}

		// if no paths, terminate check
		if (paths.empty) {
			return true
		}

		// collect the first nodes in the paths in a set
		def targets = paths.collect{
			it[0]
		}.toSet()

		for (outgoing in source.out(EDGES_CORE)) { // ignore context edges
			if (!targets.contains(outgoing)) {
				/*
				 * If the outgoing node is not contained in the targets, there
				 * are additional paths 
				 */
				return false
			}
			// determine the paths that should be going out from this vertex
			def outPaths = paths.findResults{
				if (it[0] == outgoing) {
					it.subList(1, it.size())
				}
				else {
					null
				}
			}
			if (!checkOnlyPaths(outgoing, outPaths)) {
				// check paths from this node
				return false
			}
		}

		return true
	}

}
