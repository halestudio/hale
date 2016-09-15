/*
 * Copyright (c) 2016 Fraunhofer IGD
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
 *     Fraunhofer IGD <http://www.igd.fraunhofer.de/>
 */

package de.fhg.igd.mapviewer.geom.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fhg.igd.mapviewer.geom.Point3D;

/**
 * Triangulates FaceSets using a 3D version of the ear cutting algorithm. This
 * class is also able to triangulate faces which are not flat.
 * 
 * @author Michel Kraemer
 */
public class FaceTriangulation {

	private static final int CONVEX_NOTCHECKED = 0;
	private static final int CONVEX_TRUE = 1;
	private static final int CONVEX_FALSE = 2;

	/**
	 * An array that saves, if a point is convex or not (see constants above)
	 */
	private int[] _convexCache;

	/**
	 * Default constructor
	 */
	public FaceTriangulation() {
		super();
	}

	/**
	 * Checks if the vertex p2 is convex by calculating the determinant of
	 * p1,p2,p3 which is negative if p2 convex and positive if it is not.
	 * 
	 * @param p1 the point prior to p2
	 * @param p2 the point to check
	 * @param p3 the point next to p2
	 * @return true if p2 is convex, false otherwise
	 */
	private boolean isConvex(Point3D p1, Point3D p2, Point3D p3) {
		// get vectors P2P3 and P2P1
		double a1x = p3.getX() - p2.getX();
		double a1y = p3.getY() - p2.getY();
		double a1z = p3.getZ() - p2.getZ();

		double a2x = p1.getX() - p2.getX();
		double a2y = p1.getY() - p2.getY();
		double a2z = p1.getZ() - p2.getZ();

		// calculate determinant of the cross product
		double cx = a1y * a2z - a1z * a2y;
		double cy = a1z * a2x - a1x * a2z;
		double cz = a1x * a2y - a1y * a2x;
		double det = cx + cy + cz;

		// if the determinant is negative, then p2 is convex
		return (det < 0.0);
	}

	/**
	 * Checks if vertex p2 is an ear
	 * 
	 * @param p1 the point prior to p2
	 * @param p2 the point to check
	 * @param p3 the point next to p2
	 * @param points the array of all vertices
	 * @return true if p2 is an ear, false otherwise
	 */
	private boolean isEar(Point3D p1, Point3D p2, Point3D p3, List<Point3D> points) {
		for (int i = 0; i < points.size() - 1; ++i) {
			Point3D a1, a2, a3;
			if (i == 0) {
				a1 = points.get(points.size() - 1);
				a2 = points.get(0);
				a3 = points.get(1);
			}
			else {
				a1 = points.get(i - 1);
				a2 = points.get(i);
				a3 = points.get(i + 1);
			}

			// don't check triangle points
			if (a2 == p1 || a2 == p2 || a2 == p3) {
				continue;
			}

			if (_convexCache[i] == CONVEX_NOTCHECKED) {
				_convexCache[i] = (isConvex(a1, a2, a3) ? CONVEX_TRUE : CONVEX_FALSE);
			}
			if (_convexCache[i] == CONVEX_FALSE) {
				// if this point is concave and
				// the triangle p1,p2,p3 contains it
				// the p1,p2,p3 is no ear!
				boolean c1 = isConvex(p1, p2, a2);
				boolean c2 = isConvex(p2, p3, a2);
				boolean c3 = isConvex(p3, p1, a2);
				if ((c1 == c2) && (c2 == c3)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Cuts an ear from a face. The ear will be removed from the given list of
	 * points and the indices of the real vertices will also be truncated.
	 * 
	 * @param points all vertices of the face
	 * @param indices an array containing indices for the real vertices
	 * @return the indices of the ear
	 */
	private int[] earCutting(List<Point3D> points, int[] indices) {
		if (points.size() == 3) {
			points.clear();
			return indices;
		}

		// check all points (we don't need to check the last one, because
		// one triangle always remains)
		for (int i = 0; i < points.size() - 1; ++i) {
			Point3D v1, v2, v3;
			int i1, i2, i3;
			if (i == 0) {
				v1 = points.get(points.size() - 1);
				i1 = indices[points.size() - 1];
				v2 = points.get(0);
				i2 = indices[0];
				v3 = points.get(1);
				i3 = indices[1];
			}
			else {
				v1 = points.get(i - 1);
				i1 = indices[i - 1];
				v2 = points.get(i);
				i2 = indices[i];
				v3 = points.get(i + 1);
				i3 = indices[i + 1];
			}

			// is the vertex convex?
			if (_convexCache[i] == CONVEX_NOTCHECKED) {
				if (isConvex(v1, v2, v3)) {
					_convexCache[i] = CONVEX_TRUE;
				}
				else {
					_convexCache[i] = CONVEX_FALSE;
				}
			}

			// if the vertex is concave it cannot be an ear
			if (_convexCache[i] == CONVEX_FALSE) {
				continue;
			}

			boolean ear = isEar(v1, v2, v3, points);

			if (ear) {
				// cut ear:
				// create new Face
				int[] result = new int[] { i1, i2, i3 };

				// remove ear vertex from old Face
				if (i == 0) {
					System.arraycopy(indices, 1, indices, 0, indices.length - 1);
				}
				else {
					System.arraycopy(indices, 0, indices, 0, i);
					System.arraycopy(indices, i + 1, indices, i, indices.length - i - 1);
				}

				points.remove(i);

				return result;
			}
		}

		// should never happen, because a polygon always
		// contains at least one ear
		return null;
	}

	/**
	 * This method calculates the signed 2D area of a Polygon. If the result is
	 * < 0.0 the Polygon is oriented clockwise, otherwise it's counterclockwise.
	 * 
	 * @param points the Polygon, whereas the first point MUST NOT equal the
	 *            last one
	 * @return true if the Polygon is clockwise, false otherwise
	 */
	private static boolean isClockwise(List<Point3D> points) {
		double d = 0.0;
		int n = points.size();
		for (int i = 0; i < n; ++i) {
			d += points.get(i).getX()
					* (points.get((i + 1) % n).getY() - points.get((i - 1 + n) % n).getY());
		}

		return (d < 0.0);
	}

	/**
	 * Reverses a list of points
	 * 
	 * @param a the list
	 * @param indices the index array that connects the points with the vertices
	 *            in the corresponding face
	 */
	private static void reversePoints(List<Point3D> a, int[] indices) {
		for (int i = 0; i < a.size() / 2; ++i) {
			int j = a.size() - 1 - i;
			Point3D p1 = a.get(i);
			Point3D p2 = a.get(j);
			a.set(i, p2);
			a.set(j, p1);
			int i1 = indices[i];
			int i2 = indices[j];
			indices[i] = i2;
			indices[j] = i1;
		}
	}

	/**
	 * Normalizes a vector
	 * 
	 * @param normal the vector to normalize
	 */
	private static void normalize(Point3D normal) {
		double x = normal.getX();
		double y = normal.getY();
		double z = normal.getZ();

		double length = Math.sqrt(x * x + y * y + z * z);
		normal.setX(x / length);
		normal.setY(y / length);
		normal.setZ(z / length);
	}

	/**
	 * Projects a Face onto a 2D plane. Also compacts it (removes consecutive
	 * duplicate points).
	 * 
	 * @param f the Face
	 * @param points a valid list that will receive the new projected points
	 * @return the index array that connects the new projected points with the
	 *         vertex array in the given Face
	 * @throws IllegalArgumentException if the list of points is null
	 */
	private static int[] projectAndCompactFace(List<Point3D> f, List<Point3D> points) {
		// calculate average normal of the face
		Point3D k = Point3D.calcNormal(f);
		return projectAndCompactFace(f, points, k);
	}

	/**
	 * Projects a Face onto a 2D plane using a given normal. Also compacts the
	 * face (removes consecutive duplicate points).
	 * 
	 * @param f the Face
	 * @param k the normal
	 * @param points a valid list that will receive the new projected points
	 * @return the index array that connects the new projected points with the
	 *         vertex array in the given Face
	 * @throws IllegalArgumentException if the list of points is null
	 */
	private static int[] projectAndCompactFace(List<Point3D> f, List<Point3D> points, Point3D k) {
		if (points == null) {
			throw new IllegalArgumentException("points must not be null");
		}

		// handle degenerated faces
		if (f.size() == 0) {
			return new int[0];
		}
		else if (f.size() == 1) {
			points.add(f.get(0));
			return new int[] { 0 };
		}
		else if (f.size() == 2) {
			points.add(f.get(0));
			points.add(f.get(1));
			return new int[] { 0, 1 };
		}

		// calculate projected coordinate system
		Point3D i = new Point3D();
		Point3D j = new Point3D();
		if ((Math.abs(k.getX()) > 0.1) || (Math.abs(k.getY()) > 0.1)) {
			i.setX(-k.getY());
			i.setY(k.getX());
			i.setZ(k.getZ());
		}
		else {
			i.setX(k.getZ());
			i.setZ(-k.getX());
			i.setY(k.getY());
		}
		normalize(i);

		j.setX(i.getY() * k.getZ() - i.getZ() * k.getY());
		j.setY(i.getZ() * k.getX() - i.getX() * k.getZ());
		j.setZ(i.getX() * k.getY() - i.getY() * k.getX());
		normalize(j);

		// project face onto a plane:
		// transform points and create index array
		int len = f.size();
		if (f.get(0).equals(f.get(len - 1))) {
			// create a triangle from a face with 4 points, if the
			// first one and the last one are equal
			--len;
		}

		List<Integer> indexList = new ArrayList<Integer>();
		for (int v = 0; v < len; ++v) {
			if (f.get((len + v - 1) % len).equals(f.get(v))) {
				// skip consecutive duplicate points
				continue;
			}

			double vx = f.get(v).getX();
			double vy = f.get(v).getY();
			double vz = f.get(v).getZ();
			double x = vx * i.getX() + vy * i.getY() + vz * i.getZ();
			double y = vx * j.getX() + vy * j.getY() + vz * j.getZ();
			double z = vx * k.getX() + vy * k.getY() + vz * k.getZ();

			points.add(new Point3D(x, y, z));

			// add the original index, so we can refer to it later
			indexList.add(v);
		}

		// copy indexes
		int[] indices = new int[indexList.size()];
		int p = 0;
		for (Integer index : indexList) {
			indices[p++] = index;
		}

		return indices;
	}

	/**
	 * Triangulates a face and returns a list of triangles
	 * 
	 * @param f the face to triangulate
	 * @return a list of triangulated faces containing the original vertices
	 */
	public List<List<Point3D>> triangulateFace(List<Point3D> f) {
		List<List<Point3D>> result = new ArrayList<>();

		// don't triangulate triangles
		if (f.size() == 3) {
			result.add(f);
			return result;
		}

		// prepare cache for convex vertices
		if (_convexCache == null || _convexCache.length < f.size()) {
			_convexCache = new int[f.size()];
		}
		else {
			Arrays.fill(_convexCache, CONVEX_NOTCHECKED);
		}

		// project face onto a plane
		List<Point3D> points = new ArrayList<Point3D>();
		int[] indices = projectAndCompactFace(f, points);

		// the projected face must be clockwise in order to
		// let the ear cutting algorithm work correctly
		boolean reversed = false;
		if (!isClockwise(points)) {
			reversed = true;
			reversePoints(points, indices);
		}

		// cut ears and create new faces
		while (points.size() > 0) {
			// cut an ear
			int[] newindices = earCutting(points, indices);
			if (newindices == null) {
				// should never happen, because a polygon always
				// contains at least one ear
				break;
			}

			// create new face
			List<Point3D> newface = new ArrayList<>();
			if (!reversed) {
				newface.add(f.get(newindices[0]));
				newface.add(f.get(newindices[1]));
				newface.add(f.get(newindices[2]));
			}
			else {
				// preserve vertex order. The list of points
				// has been reversed before, so reverse it again
				newface.add(f.get(newindices[2]));
				newface.add(f.get(newindices[1]));
				newface.add(f.get(newindices[0]));
			}
			result.add(newface);

			// clear convex cache
			Arrays.fill(_convexCache, 0, points.size(), CONVEX_NOTCHECKED);
		}

		return result;
	}

}
