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

package eu.esdihumboldt.hale.io.xslt.transformations.base;

import java.util.ArrayList;
import java.util.List;

import org.apache.tinkerpop.gremlin.structure.Vertex;

import eu.esdihumboldt.hale.common.align.tgraph.TGraph;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;

/**
 * Traverser that simply collects nodes in the traversal order.
 * 
 * @author Simon Templer
 */
public class OrderTransformationTraverser extends AbstractTransformationTraverser {

	private final List<Vertex> nodes = new ArrayList<Vertex>();

	@Override
	public void traverse(TGraph graph) {
		nodes.clear();
		super.traverse(graph);
	}

	@Override
	protected void handleUnmappedProperty(ChildDefinition<?> child) {
		// XXX no node present!
	}

	@Override
	protected void leaveProperty(Vertex node) {
		// do nothing
	}

	@Override
	protected void visitProperty(Vertex node) {
		nodes.add(node);
	}

	/**
	 * @return the collected nodes in traversal order
	 */
	public List<Vertex> getNodes() {
		return nodes;
	}

}
