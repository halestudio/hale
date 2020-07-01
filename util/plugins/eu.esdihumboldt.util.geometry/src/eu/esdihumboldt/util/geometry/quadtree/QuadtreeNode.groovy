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

package eu.esdihumboldt.util.geometry.quadtree

import org.locationtech.jts.geom.Geometry

/**
 * Node in a Quadtree
 * 
 * @param <G> Geometry type
 * @param <T> Payload type
 * 
 * @author Florian Esser
 */
class QuadtreeNode<G extends Geometry, T> {
	private final G geom
	T data

	QuadtreeNode(G geom, T data) {
		this.geom = geom
		this.data = data
	}

	G getGeom() {
		geom
	}
}
