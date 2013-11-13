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

package eu.esdihumboldt.hale.ui.util.groovy.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.groovy.ast.ASTNode;

import com.google.common.collect.ImmutableList;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;

/**
 * Creates a Tinkerpop {@link Graph} from a Groovy AST.
 * 
 * @author Simon Templer
 */
public class ASTToGraphVisitor extends AbstractASTTreeVisitor<Vertex> implements ASTGraphConstants {

	private final Graph graph;

	private final List<Vertex> rootVertices = new ArrayList<>();

	/**
	 * Default constructor.
	 */
	public ASTToGraphVisitor() {
		super();

		graph = new TinkerGraph();
	}

	@Override
	public Vertex createNode(Object node) {
		if (node instanceof ASTNode) {
			ASTNode astn = (ASTNode) node;
			Vertex v = graph.addVertex(null);

			// fill vertex with info
			v.setProperty(P_AST_NODE, astn);
			v.setProperty(P_AST_TYPE, astn.getClass().getSimpleName());

			v.setProperty(P_START_LINE, astn.getLineNumber());
			v.setProperty(P_START_COL, astn.getColumnNumber());
			v.setProperty(P_END_LINE, astn.getLastLineNumber());
			v.setProperty(P_END_COL, astn.getLastColumnNumber());

			return v;
		}
		return null;
	}

	@Override
	protected void addRootNode(Vertex node) {
		super.addRootNode(node);

		// root node
		rootVertices.add(node);
	}

	@Override
	public void setParent(Vertex node, Vertex parent) {
		// add child/parent relation between nodes
		graph.addEdge(null, parent, node, E_CHILD);
		graph.addEdge(null, node, parent, E_PARENT);

		// set first edge if not set
		if (!parent.getEdges(Direction.OUT, E_FIRST).iterator().hasNext()) {
			graph.addEdge(null, parent, node, E_FIRST);
		}

		// set or update last
		Iterator<Vertex> it = parent.getVertices(Direction.OUT, E_LAST).iterator();
		if (it.hasNext()) {
			// last already set
			Vertex prev = it.next();

			// remove existing last edge
			graph.removeEdge(prev.getEdges(Direction.IN, E_LAST).iterator().next());

			// add next/previous edge
			graph.addEdge(null, prev, node, E_NEXT);
			graph.addEdge(null, node, prev, E_PREV);
		}
		graph.addEdge(null, parent, node, E_LAST);

		// verify/update parent positions, needed .e.g for BlockStatements

		// update start position
		int nodeLine = node.getProperty(P_START_LINE);
		int nodeCol = node.getProperty(P_START_COL);
		int parentLine = parent.getProperty(P_START_LINE);
		int parentCol = parent.getProperty(P_START_COL);

		if (nodeLine != -1) {
			if (parentLine == -1) {
				parentLine = nodeLine;
				parentCol = nodeCol;
			}
			else {
				if (nodeLine < parentLine) {
					parentLine = nodeLine;
					parentCol = nodeCol;
				}
				else if (nodeLine == parentLine && nodeCol < parentCol) {
					parentCol = nodeCol;
				}
			}

			parent.setProperty(P_START_LINE, parentLine);
			parent.setProperty(P_START_COL, parentCol);
		}

		// update end position

		int nodeLastLine = node.getProperty(P_END_LINE);
		int nodeLastCol = node.getProperty(P_END_COL);
		int parentLastLine = parent.getProperty(P_END_LINE);
		int parentLastCol = parent.getProperty(P_END_COL);

		if (nodeLastLine != -1) {
			if (parentLastLine == -1) {
				parentLastLine = nodeLastLine;
				parentLastCol = nodeLastCol;
			}
			else {
				if (nodeLastLine > parentLastLine) {
					parentLastLine = nodeLastLine;
					parentLastCol = nodeLastCol;
				}
				else if (nodeLastLine == parentLastLine && nodeLastCol > parentLastCol) {
					parentLastCol = nodeLastCol;
				}
			}

			parent.setProperty(P_END_LINE, parentLastLine);
			parent.setProperty(P_END_COL, parentLastCol);
		}
	}

	/**
	 * @return the graph
	 */
	public Graph getGraph() {
		return graph;
	}

	/**
	 * @return the rootVertices
	 */
	public List<Vertex> getRootVertices() {
		return ImmutableList.copyOf(rootVertices);
	}
}
