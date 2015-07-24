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

package eu.esdihumboldt.cst.functions.geometric;

import javax.annotation.Nullable

import com.vividsolutions.jts.geom.Geometry

import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty
import groovy.transform.CompileStatic

/**
 * Geometry helper functions for Groovy scripts.
 * 
 * @author Simon Templer
 */
@CompileStatic
class GeometryHelperFunctions {

	/**
	 * Calculate the centroid of a given geometry.
	 * 
	 * @param geometryHolder the {@link Geometry}, {@link GeometryProperty} or
	 *            {@link Instance} holding a geometry
	 * @return the centroid of the geometry or <code>null</code>
	 */
	@Nullable
	static GeometryProperty<? extends Geometry> _centroid(def geometryHolder) {
		GeometryProperty<?> result;
		try {
			result = Centroid.calculateCentroid(geometryHolder);
		} catch (TransformationException e) {
			// XXX what should the behavior be?
			return null;
		}

		if (!result.geometry || result.geometry.isEmpty()) {
			return null;
		}

		return result;
	}

	/**
	 * Calculate a buffer from an existing geometry.
	 *
	 * @param args the function arguments
	 * @return the buffer geometry or <code>null</code>
	 */
	@Nullable
	static GeometryProperty<? extends Geometry> _buffer(Map args) {
		GeometryProperty<?> result = NetworkExpansion.calculateBuffer(
				args.geometry, // the geometry holder
				args.distance as double, // buffer distance
				null)

		if (result && result.geometry && !result.geometry.isEmpty()) {
			result
		}
		else {
			null
		}
	}
}
