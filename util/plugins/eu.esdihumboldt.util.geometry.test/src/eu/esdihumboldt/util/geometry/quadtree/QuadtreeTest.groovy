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

import static org.junit.Assert.*

import org.junit.Test

import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Envelope
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point

import groovy.transform.CompileStatic

/**
 * Tests for quadtree implementations
 * 
 * @author Florian Esser
 */
@CompileStatic
class QuadtreeTest {
	/**
	 * The geometry factory
	 */
	private static final GeometryFactory gfac = new GeometryFactory();

	/**
	 * Tests for the QuadtreeBuilder
	 * 
	 * @throws Exception
	 */
	@Test
	void testBuilder() throws Exception {
		def b = new QuadtreeBuilder<Point, String>()
		[
			c(0.1d, 0.1d),
			c(-0.1d, 0.1d),
			c(-0.1d, -0.1d),
			c(0.1d, -0.1d),
			c(0.02d, 0.02d),
			c(0.03d, 0.03d),
			c(0.04d, 0.04d)
		].each { b.add(gfac.createPoint(it), 'data') }

		def keys = [] as Set<String>
		def visitor = new QuadtreeNodeVisitor<String>() {
					void visit(Geometry geometry, String data, String key) {
						keys << key
					}
				}

		def qt = b.build(4)
		qt.traverse(visitor)
		assertEquals(4, keys.size())
		['011', '012', '013', '014'].each { assertTrue(keys.contains(it)) }

		qt = b.build(3)
		qt.traverse(visitor)
		assertEquals(6, keys.size())
		['011', '011021', '011023', '012', '013', '014'].each { assertTrue(keys.contains(it)) }
		assertEquals(0, qt.getSubtree('011').nodes.size())
		assertEquals(1, qt.getSubtree('011021').nodes.size())
		verifyPoint(qt.getSubtree('011021').nodes[0], 0.1d, 0.1d, 0.001d)
		assertEquals(3, qt.getSubtree('011023').nodes.size())
		assertEquals(1, qt.getSubtree('012').nodes.size())
		verifyPoint(qt.getSubtree('012').nodes[0], -0.1d, 0.1d, 0.001d)
		assertEquals(1, qt.getSubtree('013').nodes.size())
		verifyPoint(qt.getSubtree('013').nodes[0], -0.1d, -0.1d, 0.001d)
		assertEquals(1, qt.getSubtree('014').nodes.size())
		verifyPoint(qt.getSubtree('014').nodes[0], 0.1d, -0.1d, 0.001d)

		keys.clear()
		qt = b.build(1)
		qt.traverse(visitor)
		assertEquals(7, keys.size())

		keys.clear()
		qt = b.build(10)
		qt.traverse(visitor)
		assertEquals(1, keys.size())
	}

	@Test(expected = IllegalArgumentException.class)
	void testInvalidMaxNodes() throws Exception {
		def b = new QuadtreeBuilder<Point, String>()
		b.build(0)
	}

	@Test(expected = IllegalArgumentException.class)
	void testBadEnvelope() throws Exception {
		def b = new QuadtreeBuilder<Point, String>()
		[
			c(0.1d, 0.1d),
			c(-0.1d, 0.1d),
			c(-0.1d, -0.1d),
			c(0.1d, -0.1d),
			c(0.02d, 0.02d),
			c(0.03d, 0.03d),
			c(0.04d, 0.04d)
		].each { b.add(gfac.createPoint(it), 'data') }

		def qt = b.build(10, new Envelope(0d, 0.01d, 0d, 0.01d)) // must fail
	}

	private verifyPoint(QuadtreeNode node, double expectedX, double expectedY, double delta) {
		Point p = node.geom
		assertEquals(expectedX, p.x, delta)
		assertEquals(expectedY, p.y, delta)
	}

	private Coordinate c(double x, double y) {
		new Coordinate(x, y)
	}
}
