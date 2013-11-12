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

package eu.esdihumboldt.hale.ui.util.groovy.ast.viewer

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.ListMultimap

import eu.esdihumboldt.hale.ui.util.groovy.ast.AbstractASTTreeVisitor


/**
 * This AST visitor builds up a tree of nodes.
 *
 * @author Simon Templer
 */
public class ASTToTreeVisitor extends AbstractASTTreeVisitor<Object> {

	/**
	 * Multimap that collects the child associations during the visitor traversal.
	 */
	ListMultimap children;

	/**
	 * Creates the visitor.
	 */
	ASTToTreeVisitor() {
		children = ArrayListMultimap.create()
	}

	@Override
	public Object createNode(Object node) {
		return node;
	}

	@Override
	public void setParent(Object node, Object parent) {
		children.put(parent, node)
	}
}
