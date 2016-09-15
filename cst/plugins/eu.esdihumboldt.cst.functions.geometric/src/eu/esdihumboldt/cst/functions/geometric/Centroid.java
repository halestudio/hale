/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.cst.functions.geometric;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ListMultimap;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

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
 * Centroid function.
 * 
 * @author Kevin Mais
 */
public class Centroid extends AbstractSingleTargetPropertyTransformation<TransformationEngine>
		implements CentroidFunction {

	@Override
	protected Object evaluate(String transformationIdentifier, TransformationEngine engine,
			ListMultimap<String, PropertyValue> variables, String resultName,
			PropertyEntityDefinition resultProperty, Map<String, String> executionParameters,
			TransformationLog log) throws TransformationException, NoResultException {
		// get input geometry
		PropertyValue input = variables.get(null).get(0);
		Object inputValue = input.getValue();

		GeometryProperty<?> result = calculateCentroid(inputValue);

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
	 * Calculate the centroid for a given geometry or object holding a geometry.
	 * 
	 * @param geometryHolder {@link Geometry}, {@link GeometryProperty} or
	 *            {@link Instance} holding a geometry
	 * @return the centroid of the geometry
	 * @throws TransformationException if the no geometry could be extracted
	 *             from the input
	 */
	public static GeometryProperty<?> calculateCentroid(Object geometryHolder)
			throws TransformationException {
		// depth first traverser that on cancel continues traversal but w/o the
		// children of the current object
		InstanceTraverser traverser = new DepthFirstInstanceTraverser(true);

		GeometryFinder geoFind = new GeometryFinder(null);

		traverser.traverse(geometryHolder, geoFind);

		List<GeometryProperty<?>> geoms = geoFind.getGeometries();

		Geometry result;
		CRSDefinition oldCRS = null;

		if (geoms.size() > 1) {
			// multiple geometries -> create a multi point
			// XXX is this the desired behavior?
			Point[] centroids = new Point[geoms.size()];

			GeometryFactory geomFactory = new GeometryFactory();
			for (int i = 0; i < geoms.size(); i++) {
				GeometryProperty<?> prop = geoms.get(i);
				centroids[i] = prop.getGeometry().getCentroid();
				if (oldCRS == null) {
					// assume the same CRS for all points
					oldCRS = prop.getCRSDefinition();
				}
			}

			result = geomFactory.createMultiPoint(centroids);
		}
		else {
			Geometry geom = geoms.get(0).getGeometry();
			oldCRS = geoms.get(0).getCRSDefinition();
			if (geom != null) {
				result = geom.getCentroid();
			}
			else {
				throw new TransformationException("Geometry for centroid could not be retrieved.");
			}
		}

		return new DefaultGeometryProperty<Geometry>(oldCRS, result);
	}

}
