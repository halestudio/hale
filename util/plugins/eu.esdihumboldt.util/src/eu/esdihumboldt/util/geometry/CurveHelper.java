/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;

import eu.esdihumboldt.util.DependencyOrderedList;

/**
 * Helper methods for curves.
 * 
 * @author Simon Templer
 */
public class CurveHelper {

	/**
	 * Convert the given {@link MultiLineString} to a curve.
	 * 
	 * @param lineStrings the source multi line stirng
	 * @param fact a geometry factory
	 * @param strict if it should be checked if the geometry fulfills the strict
	 *            requirements of a curve
	 * @return the combined {@link MultiLineString} or <code>null</code> if the
	 *         geometry did not meet the requirements of the strict mode
	 */
	@Nullable
	public static MultiLineString combineCurve(MultiLineString lineStrings, GeometryFactory fact,
			boolean strict) {
		List<LineString> list = new ArrayList<>();
		for (int i = 0; i < lineStrings.getNumGeometries(); i++) {
			list.add((LineString) lineStrings.getGeometryN(i));
		}
		return combineCurve(list, fact, strict);
	}

	/**
	 * Combine the given {@link LineString}s to a curve /
	 * {@link MultiLineString}.
	 * 
	 * @param lineStrings the line strings
	 * @param fact a geometry factory
	 * @param strict if it should be checked if the geometry fulfills the strict
	 *            requirements of a curve
	 * @return the combined {@link MultiLineString} or <code>null</code> if the
	 *         geometry did not meet the requirements of the strict mode
	 */
	@Nullable
	public static MultiLineString combineCurve(List<? extends LineString> lineStrings,
			GeometryFactory fact, boolean strict) {
		// try to order by start/end point (e.g. for composite curves)
		Map<Coordinate, LineString> endPoints = new HashMap<>();

		for (LineString element : lineStrings) {
			endPoints.put(element.getEndPoint().getCoordinate(), element);
		}

		Map<LineString, Set<LineString>> dependencies = new HashMap<>();
		for (LineString element : lineStrings) {
			// check if there is another line that ends at this line's start
			// and build the dependency map accordingly
			LineString dependsOn = endPoints.get(element.getStartPoint().getCoordinate());
			@SuppressWarnings("unchecked")
			Set<LineString> deps = (Set<LineString>) ((dependsOn == null) ? Collections.emptySet()
					: Collections.singleton(dependsOn));
			dependencies.put(element, deps);
		}

		// use dependency ordered list to achieve sorting
		// will only yield a perfect result if all lines can be combined into
		// one
		DependencyOrderedList<LineString> ordered = new DependencyOrderedList<>(dependencies);

		if (strict) {
			Coordinate lastEndPoint = null;
			for (LineString lineString : ordered.getInternalList()) {
				if (lastEndPoint != null) {
					// start point must be equal to last end point
					if (!lineString.getStartPoint().getCoordinate().equals(lastEndPoint)) {
						// not a strict curve
						return null;
					}
				}
				lastEndPoint = lineString.getEndPoint().getCoordinate();
			}
		}
		else {
			// "best effort"
		}

		return fact.createMultiLineString(ordered.getInternalList().toArray(
				new LineString[ordered.getInternalList().size()]));
	}

}
