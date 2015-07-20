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

package eu.esdihumboldt.cst.functions.geometric.extent;

import java.util.Collections;

import javax.annotation.Nullable;

import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.NoResultException;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;

/**
 * Helper functions for use in Groovy scripts related to extent calculation.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("javadoc")
public class ExtentHelperFunctions {

//	public static final String PARAM_GEOMETRIES = "geometries";

	@Nullable
	public GeometryProperty<?> _union(Object geometryHolders) throws TransformationException,
			NoResultException {
		return computeExtent(geometryHolders, ExtentType.UNION);
	}

	@Nullable
	public GeometryProperty<?> _convexHull(Object geometryHolders) throws TransformationException,
			NoResultException {
		return computeExtent(geometryHolders, ExtentType.CONVEX_HULL);
	}

	@Nullable
	public GeometryProperty<?> _bbox(Object geometryHolders) throws TransformationException,
			NoResultException {
		return computeExtent(geometryHolders, ExtentType.BBOX);
	}

	@Nullable
	private GeometryProperty<?> computeExtent(Object geometryHolders, ExtentType type)
			throws TransformationException, NoResultException {
//		Object geometryHolders = params.get(PARAM_GEOMETRIES);
		Iterable<?> geoms;
		if (geometryHolders == null) {
			return null;
		}
		else if (geometryHolders instanceof Iterable) {
			geoms = (Iterable<?>) geometryHolders;
		}
		else {
			geoms = Collections.singleton(geometryHolders);
		}
		return ExtentTransformation.calculateExtent(geoms, type);
	}

}
