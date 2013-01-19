/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.cst.internal.tgraph;

import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.impl.TransformationTreeImpl;
import eu.esdihumboldt.hale.common.align.tgraph.TGraph;
import eu.esdihumboldt.hale.common.align.tgraph.impl.TGraphImpl;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Pool for transformation graphs.
 * 
 * @author Simon Templer
 */
public class TGraphPool {

	private final Alignment alignment;

	private final ListMultimap<TypeDefinition, TGraph> graphs;

	/**
	 * Create a transformation graph pool.
	 * 
	 * @param alignment the associated alignment
	 */
	public TGraphPool(Alignment alignment) {
		this.alignment = alignment;

		graphs = ArrayListMultimap.create();
	}

	/**
	 * Get a transformation graph from the pool.
	 * 
	 * @param targetType the target type for the transformation graph
	 * @return the transformation graph
	 */
	public TGraph getGraph(TypeDefinition targetType) {
		synchronized (graphs) {
			List<TGraph> graphList = graphs.get(targetType);
			if (graphList.isEmpty()) {
				// create a new graph
				TransformationTree tree = new TransformationTreeImpl(targetType, alignment);
				TGraph graph = new TGraphImpl(tree);
				// do preprocessing on the graph
				graph.proxyMultiResultNodes();
				return graph;
			}
			else {
				// use an existing one
				TGraph graph = graphList.remove(0);
				return graph;
			}
		}
	}

	/**
	 * Release a transformation graph to the pool.
	 * 
	 * @param graph the transformation graph that is no longer needed
	 */
	public void releaseGraph(TGraph graph) {
		/*
		 * XXX For now just discard graph as there is no yet a functionality to
		 * reset it.
		 */
//		tree.accept(resetVisitor); // remove all annotations
//		synchronized (graphs) {
//			graphs.put(tree.getType(), tree);
//		}
	}

}
