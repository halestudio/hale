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

package eu.esdihumboldt.cst.functions.geometric.interiorpoint;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ListMultimap;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.TopologyException;

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractSingleTargetPropertyTransformation;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.NoResultException;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.instance.geometry.DefaultGeometryProperty;
import eu.esdihumboldt.hale.common.instance.geometry.GeometryFinder;
import eu.esdihumboldt.hale.common.instance.helper.DepthFirstInstanceTraverser;
import eu.esdihumboldt.hale.common.instance.helper.InstanceTraverser;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;

/**
 * Interior Point function.
 * 
 * @author Simon Templer
 */
public class InteriorPoint extends AbstractSingleTargetPropertyTransformation<TransformationEngine>
		implements InteriorPointFunction {

	@Override
	protected Object evaluate(String transformationIdentifier, TransformationEngine engine,
			ListMultimap<String, PropertyValue> variables, String resultName,
			PropertyEntityDefinition resultProperty, Map<String, String> executionParameters,
			TransformationLog log) throws TransformationException, NoResultException {
		// get input geometry
		PropertyValue input = variables.get(null).get(0);
		Object inputValue = input.getValue();

		GeometryProperty<?> result = calculateInteriorPoint(inputValue);

		// try to yield a result compatible to the target
		TypeDefinition targetType = resultProperty.getDefinition().getPropertyType();
		// TODO check element type?
		Class<?> binding = targetType.getConstraint(Binding.class).getBinding();
		if (Geometry.class.isAssignableFrom(binding) && binding.isAssignableFrom(result.getClass())) {
			return result.getGeometry();
		}
		return result;
	}

	/**
	 * Calculate an interior point for a given geometry or object holding a
	 * geometry.
	 * 
	 * @param geometryHolder {@link Geometry}, {@link GeometryProperty} or
	 *            {@link Instance} holding a geometry
	 * @return an interior point of the geometry
	 * @throws TransformationException if the interior point could not be
	 *             calculated
	 */
	public static GeometryProperty<?> calculateInteriorPoint(Object geometryHolder)
			throws TransformationException {
		// depth first traverser that on cancel continues traversal but w/o the
		// children of the current object
		InstanceTraverser traverser = new DepthFirstInstanceTraverser(true);

		GeometryFinder geoFind = new GeometryFinder(null);

		traverser.traverse(geometryHolder, geoFind);

		List<GeometryProperty<?>> geoms = geoFind.getGeometries();

		Geometry result;
		CRSDefinition oldCRS = null;

		// use the first geometry encountered
		int index = 0;
		Geometry geom = null;
		while (geom == null && index < geoms.size()) {
			geom = geoms.get(index).getGeometry();
			oldCRS = geoms.get(index).getCRSDefinition();
			index++;
		}

		if (geom != null) {
			try {
				result = geom.getInteriorPoint();
			} catch (TopologyException e) {
				// calculate the point for a geometry with a small buffer to
				// avoid error with polygons that have overlapping lines
				result = geom.buffer(0.000001).getInteriorPoint();
				if (!result.within(geom)) {
					// fail if the point does not actually lie within the
					// geometry
					throw new TransformationException(
							"Could not determine interior point for geometry");
				}
			}
		}
		else {
			return null;
		}

		return new DefaultGeometryProperty<Geometry>(oldCRS, result);
	}

}
