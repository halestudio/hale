/*
 * Copyright (c) 2026 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.ui.util.graph;

import java.io.IOException;
import java.io.Writer;
import java.util.IdentityHashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphContainer;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.GridLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

/**
 * Writes a Zest {@link Graph} in the DOT language of Graphviz.
 *
 * Nodes are written with their text as label, connections with their text as
 * label and their line style. The Graphviz layout engine is chosen to match
 * the layout algorithm of the graph.
 */
class DotWriter {

	private final Writer writer;

	private final Map<GraphNode, String> nodeIds = new IdentityHashMap<>();

	/**
	 * @param writer the writer to write the DOT graph to
	 */
	DotWriter(Writer writer) {
		this.writer = writer;
	}

	/**
	 * Write a graph.
	 *
	 * @param graph the graph
	 * @throws IOException if writing fails
	 */
	void write(Graph graph) throws IOException {
		boolean directed = graph.getConnectionStyle() == ZestStyles.CONNECTIONS_DIRECTED;
		LayoutAlgorithm algorithm = CenteredLayoutAlgorithm.unwrap(graph.getLayoutAlgorithm());

		writer.write(directed ? "digraph" : "graph");
		writer.write(" {\n");
		writer.write("\tgraph [layout=" + layoutEngine(algorithm, graph.getNodes().size())
				+ "];\n");
		// boxes look like the default Zest nodes
		writer.write("\tnode [shape=box];\n");
		writer.write("\trankdir=" + rankDirection(algorithm) + ";\n");

		for (GraphNode node : graph.getNodes()) {
			if (node instanceof GraphContainer) {
				for (GraphNode child : ((GraphContainer) node).getNodes()) {
					writeNode(child);
				}
			}
			else {
				writeNode(node);
			}
		}

		String edgeOperator = directed ? " -> " : " -- ";
		for (GraphConnection connection : graph.getConnections()) {
			writer.write("\t" + nodeId(connection.getSource()) + edgeOperator
					+ nodeId(connection.getDestination()) + " [style="
					+ lineStyle(connection.getLineStyle()) + " label="
					+ quote(connection.getText()) + "];\n");
		}

		writer.write("}\n");
	}

	private void writeNode(GraphNode node) throws IOException {
		writer.write("\t" + nodeId(node) + " [label=" + quote(node.getText()) + "];\n");
	}

	private String nodeId(GraphNode node) {
		return nodeIds.computeIfAbsent(node, n -> "n" + nodeIds.size());
	}

	private static String layoutEngine(LayoutAlgorithm algorithm, int nodeCount) {
		if (algorithm instanceof RadialLayoutAlgorithm) {
			return "twopi";
		}
		if (algorithm instanceof GridLayoutAlgorithm) {
			return "osage";
		}
		if (algorithm instanceof SpringLayoutAlgorithm) {
			return nodeCount < 100 ? "fdp" : "sfdp";
		}
		return "dot";
	}

	private static String rankDirection(LayoutAlgorithm algorithm) {
		if (algorithm instanceof TreeLayoutAlgorithm
				&& ((TreeLayoutAlgorithm) algorithm).getDirection() == TreeLayoutAlgorithm.LEFT_RIGHT) {
			return "LR";
		}
		return "TB";
	}

	private static String lineStyle(int lineStyle) {
		switch (lineStyle) {
		case SWT.LINE_DASH:
			return "dashed";
		case SWT.LINE_DOT:
			return "dotted";
		default:
			return "solid";
		}
	}

	/**
	 * Create a quoted DOT string.
	 *
	 * @param text the text, may be <code>null</code>
	 * @return the quoted string
	 */
	static String quote(String text) {
		if (text == null) {
			return "\"\"";
		}
		StringBuilder result = new StringBuilder(text.length() + 2);
		result.append('"');
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			switch (c) {
			case '"':
				result.append("\\\"");
				break;
			case '\\':
				result.append("\\\\");
				break;
			case '\n':
				result.append("\\n");
				break;
			case '\r':
				break;
			default:
				result.append(c);
			}
		}
		result.append('"');
		return result.toString();
	}

}
