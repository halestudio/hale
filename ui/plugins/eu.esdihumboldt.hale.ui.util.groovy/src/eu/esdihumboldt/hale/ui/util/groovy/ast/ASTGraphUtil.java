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

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;

/**
 * Utilities for dealing with an AST graph.
 * 
 * @author Simon Templer
 */
public class ASTGraphUtil implements ASTGraphConstants {

	/**
	 * Find the AST node vertex with the deepest level possible that either
	 * contains or directly precedes the given position.
	 * 
	 * @param vertices the (root) vertices to start search from
	 * @param line the position line (1-based)
	 * @param col the position column (1-based)
	 * @return the vertex or <code>null</code> if none could be found
	 */
	public static Vertex findAt(Iterable<Vertex> vertices, final int line, final int col) {
		for (Vertex v : vertices) {
			final int vStartLine = v.getProperty(P_START_LINE);
			final int vStartCol = v.getProperty(P_START_COL);
			final int vEndLine = v.getProperty(P_END_LINE);
			final int vEndCol = v.getProperty(P_END_COL);

			if (vStartLine != -1 && vEndLine != -1) {
				// only check nodes with valid lines

				if ((line == vStartLine && col > vStartCol) || line > vStartLine) {
					// is after start
					if ((line == vEndLine && col <= vEndCol) || line < vEndLine) {
						// is after or at end

						/*
						 * Vertex is a valid candidate, but we prefer its
						 * children if possible.
						 */
						Vertex match = findAt(v.getVertices(Direction.OUT, E_CHILD), line, col);
						if (match != null) {
							return match;
						}
						else {
							return v;
						}
					}
				}
			}
		}

		return null;
	}

}
