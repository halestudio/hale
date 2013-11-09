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

package eu.esdihumboldt.hale.ui.util.groovy.view

import org.eclipse.jface.viewers.StyledString


/**
 * AST node utilities.
 * 
 * @author Simon Templer
 */
class ASTNodeUtil {

	/**
	 * Properties that should be ignored for {@link #addProperties(def, StyledString)},
	 * either because they hold no relevant value or they are displayed in the tree
	 * already.
	 */
	private static final def IGNORE_PROPS = [
		'class',
		'metaclass',
		'expression',
		'expressions',
		'statements',
		'text',
		'name',
		'code',
		'accessedVariable',
		'leftExpression',
		'rightExpression',
		'lineNumber',
		'lastLineNumber',
		'columnNumber',
		'lastColumnNumber'
	]

	/**
	 * Append information on an AST nodes properties to the given styled string
	 * @param node the node
	 * @param text the styled string
	 */
	static void addProperties(def node, StyledString text) {
		int index = 0
		node.properties.each { prop, val ->
			if (val == null) {
				return
			}

			if (IGNORE_PROPS.find{ it == prop }) {
				return
			}

			if (index > 0) {
				text.append(', ')
			}
			text.append(prop, StyledString.DECORATIONS_STYLER)
			text.append(' = ')
			text.append(val as String)

			index++
		}
	}
}
