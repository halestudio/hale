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

package eu.esdihumboldt.hale.ui.util.tree;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jface.viewers.TreeNode;

/**
 * Tree node that associates children with a key, children are sorted by the key
 * 
 * @param <T> the key type
 * @param <N> the node type
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class SortedMapTreeNode<T extends Comparable<?>, N extends TreeNode> extends
		MapTreeNode<T, N> {

	/**
	 * @see MapTreeNode#MapTreeNode(Object...)
	 */
	public SortedMapTreeNode(Object... values) {
		super(values);
	}

	/**
	 * @see MapTreeNode#createMap()
	 */
	@Override
	protected Map<T, N> createMap() {
		return new TreeMap<T, N>();
	}

}
