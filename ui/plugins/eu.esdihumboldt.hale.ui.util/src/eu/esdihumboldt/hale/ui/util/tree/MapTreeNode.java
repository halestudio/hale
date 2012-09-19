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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.viewers.TreeNode;

/**
 * Tree node that associates children with a key
 * 
 * @param <T> the key type
 * @param <N> the node type
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id: MultiColumnTreeNode.java 2510 2010-01-21 08:49:00Z stempler $
 */
public class MapTreeNode<T, N extends TreeNode> extends AbstractMultiColumnTreeNode {

	private final Map<T, N> children;

	/**
	 * Create a new node
	 * 
	 * @param values the node values
	 */
	public MapTreeNode(Object... values) {
		super(values);

		children = createMap();
	}

	/**
	 * Create the map that is used to manage the children
	 * 
	 * @return the map to manage the children
	 */
	protected Map<T, N> createMap() {
		return new HashMap<T, N>();
	}

	/**
	 * Add a child to the node
	 * 
	 * @param key the key
	 * @param child the child node
	 */
	public void addChild(T key, N child) {
		children.put(key, child);
		child.setParent(this);
	}

	/**
	 * Get the child with the given key
	 * 
	 * @param key the key
	 * 
	 * @return the child or <code>null</code>
	 */
	public N getChild(T key) {
		return children.get(key);
	}

	/**
	 * Remove the child node with the given key
	 * 
	 * @param key the child node
	 */
	public void removeChild(T key) {
		children.remove(key);
	}

	/**
	 * @see TreeNode#setChildren(TreeNode[])
	 */
	@Override
	public void setChildren(TreeNode[] children) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see AbstractMultiColumnTreeNode#getChildNodes()
	 */
	@Override
	protected Collection<? extends TreeNode> getChildNodes() {
		return children.values();
	}

	/**
	 * Remove a child node
	 * 
	 * @param child the child node
	 */
	public void removeChildNode(N child) {
		Iterator<Entry<T, N>> it = children.entrySet().iterator();
		T key = null;
		while (key == null && it.hasNext()) {
			Entry<T, N> entry = it.next();
			if (entry.getValue().equals(child)) {
				key = entry.getKey();
			}
		}

		if (key != null) {
			children.remove(key);
		}
	}

}
