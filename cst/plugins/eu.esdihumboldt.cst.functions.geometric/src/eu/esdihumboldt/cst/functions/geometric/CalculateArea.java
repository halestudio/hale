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

import org.locationtech.jts.geom.Geometry;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractSingleTargetPropertyTransformation;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.NoResultException;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.instance.geometry.GeometryFinder;
import eu.esdihumboldt.hale.common.instance.helper.DepthFirstInstanceTraverser;
import eu.esdihumboldt.hale.common.instance.helper.InstanceTraverser;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;

/**
 * Calculate area function
 * 
 * @author Kevin Mais
 */
public class CalculateArea extends AbstractSingleTargetPropertyTransformation<TransformationEngine>
		implements CalculateAreaFunction {

	/**
	 * @see eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractSingleTargetPropertyTransformation#evaluate(java.lang.String,
	 *      eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine,
	 *      com.google.common.collect.ListMultimap, java.lang.String,
	 *      eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition,
	 *      java.util.Map,
	 *      eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog)
	 */
	@Override
	protected Object evaluate(String transformationIdentifier, TransformationEngine engine,
			ListMultimap<String, PropertyValue> variables, String resultName,
			PropertyEntityDefinition resultProperty, Map<String, String> executionParameters,
			TransformationLog log) throws TransformationException, NoResultException {

		// get input geometry
		PropertyValue input = variables.get(null).get(0);
		Object inputValue = input.getValue();

		// depth first traverser that on cancel continues traversal but w/o the
		// children of the current object
		InstanceTraverser traverser = new DepthFirstInstanceTraverser(true);

		GeometryFinder geoFind = new GeometryFinder(null);

		traverser.traverse(inputValue, geoFind);

		List<GeometryProperty<?>> geoms = geoFind.getGeometries();

		Geometry geom = null;

		if (geoms.size() > 1) {

			double area = 0;

			for (GeometryProperty<?> geoProp : geoms) {
				area += geoProp.getGeometry().getArea();
			}

			// TODO: warn ?!

			return area;

		}
		else {
			geom = geoms.get(0).getGeometry();
		}

		if (geom != null) {
			return geom.getArea();
		}
		else {
			throw new TransformationException(
					"Geometry for calculate area could not be retrieved.");
		}

	}

}
