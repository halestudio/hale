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

import org.locationtech.jts.geom.Envelope
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.GeometryFactory

import groovy.transform.CompileStatic

/**
 * Helper class to build Quadtrees
 * 
 * @author Florian Esser
 */
@CompileStatic
class QuadtreeBuilder<G extends Geometry, T> {
	final List<QuadtreeNode<G, T>> contents = []

	QuadtreeBuilder<G, T> add(G geom, T data) {
		contents << new QuadtreeNode(geom, data)
		this
	}

	/**
	 * Build a FixedBoundaryQuadtree with an envelope determined by the 
	 * geometries added to this builder.
	 * 
	 * @param maxNodes Maximum number of nodes per tile
	 * @return the quadtree
	 */
	FixedBoundaryQuadtree<T> build(int maxNodes, Envelope envelope = null) {
		if (maxNodes < 1) {
			throw new IllegalArgumentException("Maximum number of nodes must be at least 1")
		}

		def geometries = contents.stream().map { it.geom }.collect() as Geometry[]
		def gc = new GeometryFactory().createGeometryCollection(geometries)
		def env = gc.getEnvelopeInternal()

		if (envelope) {
			if (!envelope.covers(env)) {
				throw new IllegalArgumentException("Provided envelope does not cover added geometries.")
			}
			env = envelope
		}

		def qt = new FixedBoundaryQuadtree(env, maxNodes)
		contents.each { qt.add(it) }

		qt
	}
}
