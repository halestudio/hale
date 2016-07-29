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

package de.fhg.igd.mapviewer.geom.shape;

import java.util.Arrays;

import de.fhg.igd.mapviewer.geom.BoundingBox;
import de.fhg.igd.mapviewer.geom.Point2D;
import de.fhg.igd.mapviewer.geom.util.BlochHashCode;

/**
 * This class is equal to the OGC simple feature spec's Surface class, that is,
 * it describes a 2D feature that has one exterior ring and n interior rings
 * (holes). Each ring is a Polygon.
 * 
 * @author Thorsten Reitz
 */
public class Surface extends Shape {

	/**
	 * The class' serial version UID
	 */
	private static final long serialVersionUID = 4468837090533482032L;

	/**
	 * A Polygon which defines the outer boundary of this Surface.
	 */
	private Polygon exterior_boundary;

	/**
	 * An Array of Polygons which defines the holes in this Surface. Hole
	 * Polygons may neither intersect the outer boundary nor touch it.
	 */
	private Polygon[] interior_boundaries;

	/**
	 * Def. Constructor.
	 */
	public Surface() {
		super();
	}

	/**
	 * Constructs a surface that has only a exterior boundary
	 * 
	 * @param exterior_boundary the exterior boundary of the new surface
	 */
	public Surface(Polygon exterior_boundary) {
		super();
		this.exterior_boundary = exterior_boundary;
	}

	/**
	 * Full constructor. You are encouraged to use this one.
	 * 
	 * @param exterior_boundary the exterior boundary polygon
	 * @param interior_boundaries the interior polygons (holes)
	 */
	public Surface(Polygon exterior_boundary, Polygon... interior_boundaries) {
		super();
		this.exterior_boundary = exterior_boundary;
		this.interior_boundaries = interior_boundaries;
	}

	// functional methods ......................................................

	/**
	 * This method will return this Surface as an array of java.AWT.Polygons, so
	 * that they can be printed on images.
	 * 
	 * @param scale_x the scale factor in x direction
	 * @param scale_y the scale factor in y direction
	 * @param offset the offset to add to the points
	 * @return an AWT polygon
	 */
	public java.awt.Polygon[] toAWTPolygons(double scale_x, double scale_y, Point2D offset) {
		if (this.exterior_boundary != null) {
			int length = 1;
			if (this.interior_boundaries != null) {
				length += this.interior_boundaries.length;
			}
			java.awt.Polygon[] result = new java.awt.Polygon[length];
			result[0] = this.exterior_boundary.toAWTPolygon(scale_x, scale_y, offset);
			if (this.interior_boundaries != null) {
				for (int n = 0; n < this.interior_boundaries.length; n++) {
					result[n + 1] = this.interior_boundaries[n].toAWTPolygon(scale_x, scale_y,
							offset);
				}
			}
			return result;
		}
		return null;
	}

	@Override
	public BoundingBox getBoundingBox() {
		return this.exterior_boundary.getBoundingBox();
	}

	// canonical java methods ..................................................

	/**
	 * @see Object#toString() super.toString() is included.
	 */
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("Surface[");
		buffer.append(super.toString());
		buffer.append("exterior_boundary = ").append(exterior_boundary);
		if (interior_boundaries == null) {
			buffer.append(" interior_boundaries = ").append("null");
		}
		else {
			buffer.append(" interior_boundaries = ")
					.append(Arrays.asList(interior_boundaries).toString());
		}
		buffer.append("]");
		return buffer.toString();
	}

	/**
	 * This equals method will test the following cases to determine equality:
	 * <ol>
	 * <li>runtime environment equivalence</li>
	 * <li>if IDs have been set, ID equivalence</li>
	 * <li>if no IDs have been set, check if all Polygons are equal.</li>
	 * </ol>
	 * 
	 * @param o the object to check
	 * @return true if the tests pass, false otherwise
	 */
	@Override
	public boolean equals(Object o) {
		if (!super.equals(o) || !(o instanceof Surface))
			return false;

		Surface s = (Surface) o;

		// check exterior polygon
		if (this.exterior_boundary.equals(s.getExterior_boundary())) {
			if (this.interior_boundaries != null && s.getInterior_boundaries() != null) {
				// find an equal polygon for every interior polygon
				for (int ti = 0; ti < this.interior_boundaries.length; ti++) {
					boolean found = false;
					for (int si = 0; si < s.getInterior_boundaries().length; si++) {
						if (this.interior_boundaries[ti].equals(s.getInterior_boundaries()[si])) {
							found = true;
							break;
						}
					}
					if (!found) {
						// there was no matching polygon for the
						// current interior polygon
						return false;
					}
				}
			}
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hash = BlochHashCode.HASH_CONSTANT;
		hash = BlochHashCode.addFieldToHash(hash, this.exterior_boundary);
		if (this.interior_boundaries != null) {
			for (Polygon i : this.interior_boundaries) {
				hash = BlochHashCode.addFieldToHash(hash, i);
			}
		}
		return hash;
	}

	// getter / setter methods .................................................

	/**
	 * @return Returns the exterior_boundary.
	 */
	public Polygon getExterior_boundary() {
		return exterior_boundary;
	}

	/**
	 * @param exterior_boundary The exterior_boundary to set.
	 */
	public void setExterior_boundary(Polygon exterior_boundary) {
		this.exterior_boundary = exterior_boundary;
	}

	/**
	 * @return Returns the interior_boundaries.
	 */
	public Polygon[] getInterior_boundaries() {
		return interior_boundaries;
	}

	/**
	 * @param interior_boundaries The interior_boundaries to set.
	 */
	public void setInterior_boundaries(Polygon[] interior_boundaries) {
		this.interior_boundaries = interior_boundaries;
	}

}
