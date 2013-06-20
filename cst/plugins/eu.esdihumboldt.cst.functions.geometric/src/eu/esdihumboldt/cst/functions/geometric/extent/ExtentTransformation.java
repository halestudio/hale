/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

import java.util.Map;

import com.google.common.collect.ListMultimap;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;

import eu.esdihumboldt.hale.common.align.model.ParameterValue;
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
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;

/**
 * Computes the extent of all input geometries.
 * 
 * @author Simon Templer
 */
public class ExtentTransformation extends
		AbstractSingleTargetPropertyTransformation<TransformationEngine> implements ExtentFunction {

	@Override
	protected Object evaluate(String transformationIdentifier, TransformationEngine engine,
			ListMultimap<String, PropertyValue> variables, String resultName,
			PropertyEntityDefinition resultProperty, Map<String, String> executionParameters,
			TransformationLog log) throws TransformationException, NoResultException {
		InstanceTraverser traverser = new DepthFirstInstanceTraverser(true);
		GeometryFinder geoFind = new GeometryFinder(null);

		GeometryFactory fact = new GeometryFactory();

		CRSDefinition commonCrs = null;
		Geometry extent = null;

		int count = 0;

		String paramType = getOptionalParameter(PARAM_TYPE, new ParameterValue(PARAM_BOUNDING_BOX))
				.as(String.class);

		boolean convexHull = PARAM_CONVEX_HULL.equalsIgnoreCase(paramType);

		for (PropertyValue pv : variables.get(null)) {
			// find contained geometries
			Object value = pv.getValue();
			traverser.traverse(value, geoFind);
			count += geoFind.getGeometries().size();

			for (GeometryProperty<?> geom : geoFind.getGeometries()) {
				// check CRS
				// no CRS or one common CRS is OK
				if (commonCrs == null) {
					commonCrs = geom.getCRSDefinition();
				}
				else {
					if (geom.getCRSDefinition() != null
							&& !geom.getCRSDefinition().equals(commonCrs)) {
						// CRS doesn't match
						throw new TransformationException(
								"Source geometries don't have a common CRS.");
					}
				}

				if (convexHull) {

					// compute Convex Hull
					Geometry g = geom.getGeometry();
					if (extent == null) {
						extent = g.convexHull();
					}
					else {
						GeometryCollection gc = new GeometryCollection(
								new Geometry[] { extent, g }, fact);
						extent = gc.convexHull();
					}
				}

				else {

					// compute Envelope
					Geometry g = geom.getGeometry();
					if (extent == null) {
						extent = g.getEnvelope();
					}
					else {

						GeometryCollection gc = new GeometryCollection(
								new Geometry[] { extent, g }, fact);
						extent = gc.getEnvelope();
					}
				}

				/*
				 * TODO alternatives for computing the extent through
				 * parameters? e.g. union
				 */
			}

			geoFind.reset();
		}

		System.err.println(count);

		if (extent != null) {
			return new DefaultGeometryProperty<Geometry>(commonCrs, extent);
		}
		throw new NoResultException();
	}
}
