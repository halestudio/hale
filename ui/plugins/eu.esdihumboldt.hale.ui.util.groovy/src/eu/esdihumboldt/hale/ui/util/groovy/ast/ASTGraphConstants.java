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

import com.tinkerpop.blueprints.Graph;

/**
 * Constants related to {@link Graph} built from Groovy AST.
 * 
 * @author Simon Templer
 */
public interface ASTGraphConstants {

	/**
	 * Name of the property holding the AST node.
	 */
	public static final String P_AST_NODE = "ast-node";

	/**
	 * Name of the property holding the AST node type name.
	 */
	public static final String P_AST_TYPE = "ast-type";

	/**
	 * Name of the property holding the start line.
	 */
	public static final String P_START_LINE = "start-line";

	/**
	 * Name of the property holding the start column in the start line.
	 */
	public static final String P_START_COL = "start-col";

	/**
	 * Name of the property holding the end line.
	 */
	public static final String P_END_LINE = "end-line";

	/**
	 * Name of the property holding the end column in the end line.
	 */
	public static final String P_END_COL = "end-col";

	/**
	 * Name of the edge representing the child relation.
	 */
	public static final String E_CHILD = "child";

	/**
	 * Name of the edge representing the parent relation.
	 */
	public static final String E_PARENT = "parent";

	/**
	 * Name of the edge representing the first child.
	 */
	public static final String E_FIRST = "first";

	/**
	 * Name of the edge representing the last child.
	 */
	public static final String E_LAST = "last";

	/**
	 * Name of the edge representing the next sibling.
	 */
	public static final String E_NEXT = "next";

	/**
	 * Name of the edge representing the previous sibling.
	 */
	public static final String E_PREV = "previous";

}
