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

import org.locationtech.jts.geom.Geometry;

/**
 * Visitor interface for QuadtreeNodes
 * 
 * @param <T> Payload type
 * 
 * @author Florian Esser
 */
public interface QuadtreeNodeVisitor<T> {

	/**
	 * Callback for a traverser
	 * 
	 * @param geometry Geometry associated with the node
	 * @param data Data associated with the node
	 * @param treeKey the node's tree key
	 */
	void visit(Geometry geometry, T data, String treeKey);
}
