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

import com.vividsolutions.jts.geom.Envelope
import com.vividsolutions.jts.geom.Point

import groovy.transform.CompileStatic

/**
 * Quadtree implementation where contained Point geometries all lie within a 
 * pre-determined envelope.
 * 
 * @author Florian Esser
 */
@CompileStatic
class FixedBoundaryQuadtree<T> implements Quadtree<Point, T> {

	/**
	 * Maximum number of nodes this tree can hold before it is split up
	 */
	final int maxNodes

	final Set<QuadtreeNode<Point, T>> nodes = []

	/**
	 * Corner coordinates of this tree
	 */
	final double minX, minY, maxX, maxY

	/**
	 * The sub-trees or <code>null</code> if this tree was not yet split up
	 */
	private FixedBoundaryQuadtree northWest, northEast, southWest, southEast

	/**
	 * Create a quadtree with the given envelope
	 * 
	 * @param envelope Envelope of this quadtree
	 * @param maxNodes Maximum number of nodes that can be added before it is split up
	 */
	FixedBoundaryQuadtree(Envelope envelope, int maxNodes) {
		this(envelope.minX, envelope.minY, envelope.maxX, envelope.maxY, maxNodes)
	}

	/**
	 * Create a quadtree with the given envelope
	 * 
	 * @param minX Minimum x-coordinate of the envelope
	 * @param minY Minimum y-coordinate of the envelope
	 * @param maxX Maximum x-coordinate of the envelope
	 * @param maxY Maximum y-coordinate of the envelope
	 * @param maxNodes
	 */
	FixedBoundaryQuadtree(double minX, double minY, double maxX, double maxY, int maxNodes) {
		this.maxNodes = maxNodes
		this.minX = minX
		this.minY = minY
		this.maxX = maxX
		this.maxY = maxY
	}

	@Override
	String toString() {
		if (nodes.empty) {
			if (hasSubtree()) {
				def nw = northWest.toString()
				def ne = northEast.toString()
				def sw = southWest.toString()
				def se = southEast.toString()

				"NE[$ne], NW[$nw], SW[$sw], SE[$se]"
			}
			else {
				"empty"
			}
		}
		else {
			int size = nodes.size()
			"has $size leafs"
		}
	}

	@Override
	void add(QuadtreeNode<Point, T> p) {
		if (hasSubtree()) {
			addToSubtree(p)
		}
		else {
			if (nodes.size() == maxNodes) {
				double distX = maxX - minX
				double distY = maxY - minY

				northWest = new FixedBoundaryQuadtree(minX, minY + distY / 2d, minX + distX / 2d, maxY, maxNodes)
				northEast = new FixedBoundaryQuadtree(minX + distX / 2d, minY + distY / 2d, maxX, maxY, maxNodes)
				southWest = new FixedBoundaryQuadtree(minX, minY, minX + distX / 2d, minY + distY / 2d, maxNodes)
				southEast = new FixedBoundaryQuadtree(minX + distX / 2d, minY, maxX, minY + distY / 2d, maxNodes)

				addToSubtree(p)
				nodes.each { n -> addToSubtree(n) }
				nodes.clear()
			}
			else {
				nodes << p
			}
		}
	}

	@Override
	boolean hasSubtree() {
		northWest
	}

	@Override
	FixedBoundaryQuadtree<T> getSubtree(String treeKey) {
		if (treeKey.length() % 3 != 0) {
			throw new IllegalArgumentException("Invalid tree key")
		}

		def current = this
		def levels = treeKey.length() / 3
		for (int level = 1; level <= levels; level++) {
			def nextQuadrant = treeKey.getAt(level * 3 - 1)
			switch (nextQuadrant) {
				case '1':
					current = current.northEast
					break
				case '2':
					current = current.northWest
					break
				case '3':
					current = current.getSouthWest()
					break
				case '4':
					current = current.getSouthEast()
					break
				default:
					throw new IllegalArgumentException("Invalid tree key")
			}
			if (current == null) {
				throw new IllegalArgumentException("Invalid tree key")
			}
		}

		current
	}

	private void addToSubtree(QuadtreeNode<Point, T> n) {
		double distX = maxX - minX
		double distY = maxY - minY

		if (n.geom.y < minY + distY / 2) {
			// south
			if (n.geom.x < minX + distX / 2) {
				// west
				southWest.add(n)
			}
			else {
				southEast.add(n)
			}
		}
		else {
			// north
			if (n.geom.x < minX + distX / 2) {
				// west
				northWest.add(n)
			}
			else {
				northEast.add(n)
			}
		}
	}

	int depth() {
		if (hasSubtree()) {
			def result = 1
			result = Math.max(result, result + northEast.depth())
			result = Math.max(result, result + northWest.depth())
			result = Math.max(result, result + southWest.depth())
			result = Math.max(result, result + southEast.depth())
		}
		else {
			0
		}
	}

	FixedBoundaryQuadtree<T> getNorthEast() {
		northEast
	}

	FixedBoundaryQuadtree<T> getNorthWest() {
		northWest
	}

	FixedBoundaryQuadtree<T> getSouthEast() {
		southEast
	}

	FixedBoundaryQuadtree<T> getSouthWest() {
		southWest
	}

	@Override
	void traverse(QuadtreeNodeVisitor<T> visitor) {
		traverse0(visitor, 0, '')
	}

	private void traverse0(QuadtreeNodeVisitor<T> visitor, int level, String key) {
		if (hasSubtree()) {
			def levelStr = String.format("%02d", ++level)
			northEast.traverse0(visitor, level, "${key}${levelStr}1")
			northWest.traverse0(visitor, level, "${key}${levelStr}2")
			southWest.traverse0(visitor, level, "${key}${levelStr}3")
			southEast.traverse0(visitor, level, "${key}${levelStr}4")
		}
		else {
			nodes.each { visitor.visit(it.geom, it.data, key) }
		}
	}
}
