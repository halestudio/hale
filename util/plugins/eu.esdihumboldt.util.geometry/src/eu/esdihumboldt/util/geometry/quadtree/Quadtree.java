/*
 * Copyright (c) 2018 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.util.geometry.quadtree;

import java.util.Collection;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Interface for a quadtree implementations
 * 
 * @param <G> Geometry type
 * @param <T> Payload type
 * 
 * @author Florian Esser
 */
public interface Quadtree<G extends Geometry, T> {

	/**
	 * Add a node to the quadtree
	 * 
	 * @param node The node to add
	 */
	void add(QuadtreeNode<G, T> node);

	/**
	 * @return the nodes of the quadtree
	 */
	Collection<QuadtreeNode<G, T>> getNodes();

	/**
	 * @return true if this tree was split up
	 */
	boolean hasSubtree();

	/**
	 * Return the sub-tree with the given key
	 * 
	 * @param treeKey Key of the sub-tree
	 * @return the sub-tree or <code>null</code>
	 */
	Quadtree<G, T> getSubtree(String treeKey);

	/**
	 * Traverse all nodes of the quadtree
	 * 
	 * @param visitor Visitor callback
	 */
	void traverse(QuadtreeNodeVisitor<T> visitor);

	/**
	 * @return depth of the quadtree
	 */
	int depth();
}
