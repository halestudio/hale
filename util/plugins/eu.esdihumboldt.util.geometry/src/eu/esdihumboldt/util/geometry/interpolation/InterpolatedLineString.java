/*
 * Copyright (c) 2017 wetransform GmbH
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

package eu.esdihumboldt.util.geometry.interpolation;

import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;

import eu.esdihumboldt.util.geometry.interpolation.model.ComplexGeometry;

/**
 * Line string that was interpolated and holds the information on the original
 * geometry.
 * 
 * @author Simon Templer
 */
public class InterpolatedLineString extends LineString {

	private static final long serialVersionUID = -1656168975470997800L;

	private final ComplexGeometry original;

	/**
	 * Create a new line string that was interpolated from the given original
	 * geometry.
	 * 
	 * @param points the points forming the line string
	 * @param factory the geometry factory
	 * @param original the original geometry
	 */
	public InterpolatedLineString(CoordinateSequence points, GeometryFactory factory,
			ComplexGeometry original) {
		super(points, factory);
		this.original = original;
	}

	/**
	 * @return the original geometry
	 */
	public ComplexGeometry getOriginalGeometry() {
		return original;
	}

}
