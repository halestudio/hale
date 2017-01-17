/*
 * Copyright (c) 2016 wetransform GmbH
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;

/**
 * Interpolation of Arc String geometry
 * 
 * @author Arun
 */
@Deprecated
public class ArcStringInterpolation extends Interpolation<LineString> {

	private static final ALogger log = ALoggerFactory.getLogger(ArcStringInterpolation.class);

	private int numArc;

	/**
	 * Constructor for ArcString type
	 * 
	 * @param coordinates the arc string coordinates
	 * @param maxPositionalError maximum positional error
	 * @param keepOriginal keep original
	 */
	public ArcStringInterpolation(Coordinate[] coordinates, double maxPositionalError,
			boolean keepOriginal) {
		super(coordinates, maxPositionalError, keepOriginal);
	}

	/**
	 * @see eu.esdihumboldt.util.geometry.interpolation.Interpolation#validateRawCoordinates()
	 */
	@Override
	protected boolean validateRawCoordinates() {
		if ((rawGeometryCoordinates.length % 2) != 1) {
			log.error(
					"Invalid arc string geometry. ArcString must be represented by (2 * numArc) + 1 points.");
			return false;
		}
		return true;
	}

	/**
	 * @see eu.esdihumboldt.util.geometry.interpolation.Interpolation#getInterpolatedGeometry()
	 */
	@Override
	protected LineString getInterpolatedGeometry() {
		// get num of Arc
		numArc = getNumOfArcs();

		List<Coordinate> generatedCoordinates = new ArrayList<>();

		// loop for 3 coordinates and submit them to ArcInterpolation
		for (int i = 0; i < numArc; i++) {

			int from = i == 0 ? 0 : (i * 2);
			int to = from + 3;
			Coordinate[] arcCoordiantes = Arrays.copyOfRange(rawGeometryCoordinates, from, to);

			Interpolation<LineString> objArc = new ArcInterpolation(arcCoordiantes,
					this.MAX_POSITIONAL_ERROR, this.keepOriginal);

			LineString geometry = objArc.interpolateRawGeometry();

			List<Coordinate> coords = new ArrayList<Coordinate>(
					Arrays.asList(geometry.getCoordinates()));

			if (i != (numArc - 1)) {
				if (keepOriginal) {
					coords.remove(coords.size() - 1);
				}
				else {
					Coordinate coord = coords.get(coords.size() - 1);
					if (coord.equals(rawGeometryCoordinates[to - 1]))
						coords.remove(coords.size() - 1);
				}
			}

			generatedCoordinates.addAll(coords);
		}

		// now, we have all coordinates of line string. So then just create it.
		LineString lineString = null;
		try {
			lineString = new GeometryFactory().createLineString(
					generatedCoordinates.toArray(new Coordinate[generatedCoordinates.size()]));
		} catch (Exception ex) {
			log.error("Error creating LineString from interpolated coordinates of ArcString", ex);
		}

		return lineString;

	}

	private int getNumOfArcs() {
		return (rawGeometryCoordinates.length - 1) / 2;
	}

}
