/*
 * Copyright (c) 2016 Data Harmonisation Panel
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

package eu.esdihumboldt.util.geometry;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.geotools.referencing.CRS;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

/**
 * TODO Type description
 * 
 * @author Arun
 */
public class WindingOrderTest {

	private static Polygon clockWise1, clockWise2, clockWise2WOHoles;
	private static MultiPolygon clockWise3;

	private static GeometryCollection clockWise4;

	private static LinearRing r1, r2, h1, h2;

	private static final String code1 = "EPSG:4026";
	private static final String code2 = "EPSG:25832";
	private static CoordinateReferenceSystem crs1, crs2;

	private static final LoadingCache<String, CoordinateReferenceSystem> CRS_CACHE = CacheBuilder
			.newBuilder().maximumSize(100).expireAfterAccess(1, TimeUnit.HOURS)
			.build(new CacheLoader<String, CoordinateReferenceSystem>() {

				@Override
				public CoordinateReferenceSystem load(String code) throws Exception {
					return CRS.decode(code);
				}

			});

	/**
	 * Setup for different tests
	 */
	@BeforeClass
	public static void setUp() {
		GeometryFactory factory = new GeometryFactory();

		r1 = factory.createLinearRing(new Coordinate[] { new Coordinate(10, 30),
				new Coordinate(20, 0), new Coordinate(0, 0), new Coordinate(10, 30) });

		r2 = factory.createLinearRing(new Coordinate[] { new Coordinate(49.87445, 8.64729),
				new Coordinate(49.87582, 8.65441), new Coordinate(49.87095, 8.65694),
				new Coordinate(49.86978, 8.65032), new Coordinate(49.87197, 8.64758),
				new Coordinate(49.87341, 8.64688), new Coordinate(49.87445, 8.64729) });

		h1 = factory.createLinearRing(new Coordinate[] { new Coordinate(49.87327, 8.64991),
				new Coordinate(49.8735, 8.6521), new Coordinate(49.87253, 8.65239),
				new Coordinate(49.8723, 8.65045), new Coordinate(49.87327, 8.64991) });

		h2 = factory.createLinearRing(new Coordinate[] { new Coordinate(49.87203, 8.65208),
				new Coordinate(49.87209, 8.6531), new Coordinate(49.87156, 8.65312),
				new Coordinate(49.87145, 8.65227), new Coordinate(49.87203, 8.65208) });

		clockWise1 = factory.createPolygon(r1);

		clockWise2 = factory.createPolygon(r2, new LinearRing[] { h1, h2 });

		clockWise2WOHoles = factory.createPolygon(r2);

		clockWise3 = factory.createMultiPolygon(new Polygon[] { clockWise1, clockWise2 });

		clockWise4 = factory.createGeometryCollection(
				new Geometry[] { clockWise2, clockWise2WOHoles, clockWise3, r2 });

		if (crs1 == null) {
			try {
				crs1 = CRS_CACHE.get(code1);
			} catch (Exception e) {
				throw new IllegalStateException("Invalid CRS code", e);
			}
		}

		if (crs2 == null) {
			try {
				crs2 = CRS_CACHE.get(code2);
			} catch (Exception e) {
				throw new IllegalStateException("Invalid CRS code", e);
			}
		}
	}

	/**
	 * Test of Flip state of CRS
	 */
	@Test
	public void testCRSFlipped() {
		// Flipped CRS
		assertTrue(WindingOrder.isCRSFlip(crs1));

		// Normal CRS
		assertFalse(WindingOrder.isCRSFlip(crs2));
	}

	/**
	 * test geometry on normal CRS
	 */
	@Test
	public void testGeometryNormalOnCRS() {
		assertTrue(WindingOrder.isCounterClockwise(clockWise2.getExteriorRing()));
		assertFalse(WindingOrder.isCRSFlip(crs2));
		Geometry result = WindingOrder.unifyWindingOrder(clockWise2, true, crs2);
		assertTrue(result instanceof Polygon);
		assertFalse(clockWise2.equalsExact(result));
		assertTrue(WindingOrder.isCounterClockwise(((Polygon) result).getExteriorRing()));
	}

	/**
	 * test geometry on Flipped CRS
	 */
	@Test
	public void testGeometryFlippedOnCRS() {
		assertTrue(WindingOrder.isCounterClockwise(clockWise2.getExteriorRing()));
		assertTrue(WindingOrder.isCRSFlip(crs1));
		Geometry result = WindingOrder.unifyWindingOrder(clockWise2, true, crs1);
		assertTrue(result instanceof Polygon);
		assertFalse(clockWise2.equalsExact(result));
		assertFalse(WindingOrder.isCounterClockwise(((Polygon) result).getExteriorRing()));
	}

	/**
	 * test geometry flip on giving null CRS
	 */
	@Test
	public void testGeometryFlippedOnNULLCRS() {
		assertTrue(WindingOrder.isCounterClockwise(clockWise2.getExteriorRing()));

		Geometry result = WindingOrder.unifyWindingOrder(clockWise2, true, null);

		assertTrue(result instanceof Polygon);

		assertTrue(WindingOrder.isCounterClockwise(((Polygon) result).getExteriorRing()));
	}

	/**
	 * Testing of Polygon without holes.
	 */
	@Test
	public void testUnifyWOHoles() {
		Geometry result = WindingOrder.unifyWindingOrder(clockWise2WOHoles, true, null);
		assertTrue(result instanceof Polygon);
		assertTrue(clockWise2WOHoles.equalsExact(result));
	}

	/**
	 * Test of holes of Polygon as Counter ClockWise
	 */
	@Test
	public void testOrderHolesCCW() {
		assertTrue(WindingOrder.isCounterClockwise(h1));
		assertTrue(WindingOrder.isCounterClockwise(h2));
	}

	/**
	 * Test of winding order of hole which is already counter clockwise
	 */
	@Test
	public void testUnifyHoles() {
		assertTrue(WindingOrder.isCounterClockwise(h1));
		Geometry result = WindingOrder.unifyWindingOrder(h1, true, null);
		assertTrue(WindingOrder.isCounterClockwise(result));
		assertTrue(h1.equalsExact(result));
	}

	/**
	 * Test of winding order for an empty geometry.
	 */
	@Test
	public void testUnifyEmpty() {
		GeometryFactory fact = new GeometryFactory();
		Polygon geom = fact.createPolygon(new Coordinate[0]);
		Geometry result = WindingOrder.unifyWindingOrder(geom, true, null);
		assertTrue(WindingOrder.isCounterClockwise(result));
		assertTrue(geom.equalsExact(result));
	}

	/**
	 * Test winding order of polygon as clockwise
	 */
	@Test
	public void testOrderClockwise() {
		assertFalse(WindingOrder.isCounterClockwise(clockWise1.getExteriorRing()));

	}

	/**
	 * Test winding order of polygon as counter clockwise
	 */
	@Test
	public void testOrderCounterClockwise() {
		assertTrue(WindingOrder.isCounterClockwise(clockWise2.getExteriorRing()));
	}

	/**
	 * Test winding order of simple polygon as counter clockwise
	 */
	@Test
	public void testUnifyCCWSimple() {
		Geometry result = WindingOrder.unifyWindingOrder(clockWise1, true, null);
		assertTrue(result instanceof Polygon);
		assertFalse(clockWise1.equalsExact(result));
		assertTrue(WindingOrder.isCounterClockwise(((Polygon) result).getExteriorRing()));
	}

	/**
	 * Test winding order of polygon with holes as counter clockwise
	 * 
	 */
	@Test
	public void testUnifyCCWWithHoles() {
		Geometry result = WindingOrder.unifyWindingOrder(clockWise2, true, null);
		assertTrue(result instanceof Polygon);
		assertFalse(clockWise2.equalsExact(result));

		assertTrue(WindingOrder.isCounterClockwise(((Polygon) result).getExteriorRing()));
		assertFalse(WindingOrder.isCounterClockwise(((Polygon) result).getInteriorRingN(0)));

		assertTrue(WindingOrder.isCounterClockwise(clockWise2.getInteriorRingN(0)) != WindingOrder
				.isCounterClockwise(((Polygon) result).getInteriorRingN(0)));
		assertFalse(
				clockWise2.getInteriorRingN(0).equalsExact(((Polygon) result).getInteriorRingN(0)));

	}

	/**
	 * Test winding order of MultiPolygon
	 */
	@Test
	public void testUnifyMultiPolygon() {
		Geometry result = WindingOrder.unifyWindingOrder(clockWise3, false, null);
		assertTrue(result instanceof MultiPolygon);
		assertFalse(clockWise3.equalsExact(result));
	}

	/**
	 * Test winding order of GeometryCollection
	 */
	@Test
	public void testUnifyGeometryCollection() {
		Geometry result = WindingOrder.unifyWindingOrder(clockWise4, true, null);
		assertTrue(result instanceof GeometryCollection);
		assertFalse(clockWise4.equalsExact(result));
		assertTrue(
				((GeometryCollection) result).getNumGeometries() == clockWise4.getNumGeometries());
	}

}
