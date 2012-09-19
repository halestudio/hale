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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.align.model.transformation.tree;

import eu.esdihumboldt.util.IdentityWrapper;

/**
 * Transformation tree utilities.
 * 
 * @author Simon Templer
 */
public abstract class TransformationTreeUtil {

	/**
	 * Extract the definition or cell contained in a transformation node.
	 * 
	 * @param node the node or other kind of object
	 * @return the contained definition, cell or the node/object itself
	 */
	public static Object extractObject(Object node) {
		if (node instanceof IdentityWrapper<?>) {
			node = ((IdentityWrapper<?>) node).getValue();
		}

		if (node instanceof TransformationTree) {
			return ((TransformationTree) node).getType();
		}
		if (node instanceof TargetNode) {
			return ((TargetNode) node).getEntityDefinition();
		}
		if (node instanceof CellNode) {
			return ((CellNode) node).getCell();
		}
		if (node instanceof SourceNode) {
			return ((SourceNode) node).getEntityDefinition();
		}

		return node;
	}

}
