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

import java.text.MessageFormat;
import java.util.Collections;

import javax.annotation.Nullable;

import eu.esdihumboldt.cst.functions.groovy.helper.HelperFunctionArgument;
import eu.esdihumboldt.cst.functions.groovy.helper.HelperFunctionSpecification;
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

	public static final HelperFunctionSpecification _union_spec = new HelperFunctionSpecification(
			"Union of the geometries or instances containing geometries", "union",
			new HelperFunctionArgument("geometry holder",
					"Geometry or geometry property or instance holding a geometry"));

	@Nullable
	public GeometryProperty<?> _convexHull(Object geometryHolders) throws TransformationException,
			NoResultException {
		return computeExtent(geometryHolders, ExtentType.CONVEX_HULL);
	}

	public static final HelperFunctionSpecification _convexHull_spec = new HelperFunctionSpecification(
			"calculates the convex hull of the geometry", "convexhull", new HelperFunctionArgument(
					"geometry holder",
					"Geometry or geometry property or instance holding a geometry"));

//	public HelperFunctionSpecification _convexHull_spec() {
//		return new HelperFunctionSpecification("description", "result", new HelperFunctionArgument("name", "descr"));
//	}

	@Nullable
	public GeometryProperty<?> _bbox(Object geometryHolders) throws TransformationException,
			NoResultException {
		return computeExtent(geometryHolders, ExtentType.BBOX);
	}

	public HelperFunctionSpecification _bbox_spec(String name) {
		return new HelperFunctionSpecification(MessageFormat.format(
				"The {0} function calculates the bounding box of the geometry", name),
				"boundingbox", new HelperFunctionArgument("geometry holder",
						"Geometry or geometry property or instance holding a geometry"));
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
