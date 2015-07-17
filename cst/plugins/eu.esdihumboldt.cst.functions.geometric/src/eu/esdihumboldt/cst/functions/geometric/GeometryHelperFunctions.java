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

import javax.annotation.Nullable;

import com.vividsolutions.jts.geom.Geometry;

import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;

/**
 * Geometry helper functions for Groovy scripts.
 * 
 * @author Simon Templer
 */
public class GeometryHelperFunctions {

	/**
	 * Calculate the centroid of a given geometry.
	 * 
	 * @param geometryHolder the {@link Geometry}, {@link GeometryProperty} or
	 *            {@link Instance} holding a geometry
	 * @return the centroid of the geometry or <code>null</code>
	 */
	@Nullable
	public static GeometryProperty<?> _centroid(Object geometryHolder) {
		GeometryProperty<?> result;
		try {
			result = Centroid.calculateCentroid(geometryHolder);
		} catch (TransformationException e) {
			// XXX what should the behavior be?
			return null;
		}

		if (result.getGeometry() == null || result.getGeometry().isEmpty()) {
			return null;
		}

		return result;
	}

}
