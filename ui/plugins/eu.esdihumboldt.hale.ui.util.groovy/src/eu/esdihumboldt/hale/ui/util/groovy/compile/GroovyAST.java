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

package eu.esdihumboldt.hale.ui.util.groovy.compile;

import java.util.List;

import org.codehaus.groovy.ast.ASTNode;

/**
 * Represents the Groovy AST of a source document.
 * 
 * @author Simon Templer
 */
public class GroovyAST {

	private final List<ASTNode> nodes;

	/**
	 * Constructor.
	 * 
	 * @param nodes the list with root AST nodes
	 */
	public GroovyAST(List<ASTNode> nodes) {
		super();
		this.nodes = nodes;
	}

	/**
	 * @return the AST nodes
	 */
	public List<ASTNode> getNodes() {
		return nodes;
	}

}
