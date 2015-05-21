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

package eu.esdihumboldt.hale.ui.cst.debug.metadata.internal;

import java.util.Set;

import com.google.common.collect.Iterables;
import com.google.common.collect.SetMultimap;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.CellNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.SourceNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TargetNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.visitor.TreeToGraphVisitor;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;

/**
 * Provider for converting TransformationTrees into the graphML-format
 * 
 * @author SebastianReinhardt
 */
public class TreeGraphMLProvider implements TreeGraphProvider {

	TransformationTree tree;
	TreeToGraphVisitor graphVisitor;

	/**
	 * @param tree The Tree to get the graph from
	 */
	public TreeGraphMLProvider(TransformationTree tree) {
		this.tree = tree;
		graphVisitor = new TreeToGraphVisitor(null);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.cst.debug.metadata.internal.TreeGraphProvider#generateGraph()
	 */
	@Override
	public Graph generateGraph() {

		tree.accept(graphVisitor);

		SetMultimap<String, String> connections = graphVisitor.getAllConnections();
		Set<String> ids = graphVisitor.getAllIds();

		TinkerGraph graph = new TinkerGraph();

		// add nodes to the graph
		for (String key : ids) {
			TransformationNode node = graphVisitor.getNode(key);
			Vertex vertex = graph.addVertex(key);
			setVertexProperty(node, vertex);
		}

		for (String key : connections.keySet()) {
			for (String value : connections.get(key)) {
				graph.addEdge(null, graph.getVertex(key), graph.getVertex(value), " ");
			}
		}
		return graph;
	}

	/**
	 * sets the property of a [@link]Vertex from a [@link]TransformationNode
	 * 
	 * @param node the node-object to get the name from
	 * @param vertex the vertex to set the property
	 */
	private void setVertexProperty(TransformationNode node, Vertex vertex) {

		if (node instanceof TransformationTree) {
			vertex.setProperty("name", ((TransformationTree) node).getType().getDisplayName());
			vertex.setProperty("type", "root");
		}

		if (node instanceof TargetNode) {
			vertex.setProperty("name", ((TargetNode) node).getDefinition().getDisplayName());
			vertex.setProperty("type", "target");
		}

		if (node instanceof SourceNode) {
			SourceNode snode = (SourceNode) node;
			Object value = snode.getValue();
			String name = ((SourceNode) node).getDefinition().getDisplayName();

			if (value instanceof Group) {
				vertex.setProperty("name", name);
				vertex.setProperty("group", getChildrencountString(value));
				vertex.setProperty("type", "source");
			}
			if (value instanceof Instance) {
				if (((Instance) value).getValue() != null) {
					vertex.setProperty("group", getChildrencountString(value));
					vertex.setProperty("value", ((Instance) value).getValue().toString());
					vertex.setProperty("type", "source");
				}
			}
			else {
				vertex.setProperty("name", name);
				vertex.setProperty("type", "source");
				if (value instanceof String) {
					vertex.setProperty("value", value);
				}

			}

		}

		if (node instanceof CellNode) {
			vertex.setProperty(
					"name",
					FunctionUtil.getFunction(
							((CellNode) node).getCell().getTransformationIdentifier(), null)
							.getDisplayName());
			vertex.setProperty("type", "cell");
		}
	}

	/**
	 * method for gathering the string of a node value
	 * 
	 * @param value the node value
	 * @return the dot-format-string
	 */
	private String getChildrencountString(Object value) {

		if (value instanceof Instance) {
			String ivalue = "Instance(" + Iterables.size(((Instance) value).getPropertyNames())
					+ ")";
			return ivalue;

		}
		if (value instanceof Group) {
			return "Group(" + Iterables.size(((Group) value).getPropertyNames()) + ")";
		}

		return value.toString();

	}
}
