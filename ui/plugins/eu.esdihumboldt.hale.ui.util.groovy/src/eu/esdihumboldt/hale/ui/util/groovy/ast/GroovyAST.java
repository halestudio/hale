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

import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;

/**
 * Represents the Groovy AST of a source document.
 * 
 * @author Simon Templer
 */
public class GroovyAST {

	private static final ALogger log = ALoggerFactory.getLogger(GroovyAST.class);

	private final List<ASTNode> nodes;

	private final Graph graph;

	private final List<Vertex> rootVertices;

	/**
	 * Constructor.
	 * 
	 * @param nodes the list with root AST nodes
	 */
	public GroovyAST(List<ASTNode> nodes) {
		super();
		this.nodes = nodes;

		ASTToGraphVisitor visitor = new ASTToGraphVisitor();
		for (ASTNode node : nodes) {
			if (node instanceof ClassNode)
				visitor.visitClass((ClassNode) node);
			else
				log.error("unexpected node type " + node.getClass());
		}

		graph = visitor.getGraph();
		rootVertices = visitor.getRootVertices();
	}

	/**
	 * @return the root AST nodes
	 */
	public List<ASTNode> getNodes() {
		return nodes;
	}

	/**
	 * @return the graph representing the AST
	 */
	public Graph getGraph() {
		return graph;
	}

	/**
	 * @return the root vertices of the AST graph
	 */
	public List<Vertex> getRootVertices() {
		return rootVertices;
	}

}
