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

import java.util.Arrays;
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

	/**
	 * Number of Geometries to be processed at once by either extent option.
	 * Especially the "union" process run time depends on this value. A too
	 * large value causes a @code{java.lang.OutOfMemoryError}. The chosen value
	 * results in fairly good processing time of 30-60 seconds per 10,000
	 * geometries.
	 */
	private static final short SIMULTAN_PROCESS_GEOMS = 768;

	@Override
	protected Object evaluate(String transformationIdentifier, TransformationEngine engine,
			ListMultimap<String, PropertyValue> variables, String resultName,
			PropertyEntityDefinition resultProperty, Map<String, String> executionParameters,
			TransformationLog log) throws TransformationException, NoResultException {

		InstanceTraverser traverser = new DepthFirstInstanceTraverser(true);
		GeometryFinder geoFind = new GeometryFinder(null);

		GeometryFactory fact = new GeometryFactory();

		CRSDefinition commonCrs = null;
		Geometry[] geomsCollectingArray = new Geometry[SIMULTAN_PROCESS_GEOMS];
		short geomsCollectedIdx = 0;

//		int count = 0;

		String paramType = getOptionalParameter(PARAM_TYPE, new ParameterValue(PARAM_BOUNDING_BOX))
				.as(String.class);

		// System.err.println("Geoms Currently Processed;TotalMem;FreeMem");

		for (PropertyValue pv : variables.get(null)) {

			// find contained geometries
			Object value = pv.getValue();
			traverser.traverse(value, geoFind);
//			count += geoFind.getGeometries().size();

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

				Geometry g = geom.getGeometry();

				// If geometry collecting array not filled.
				if (geomsCollectedIdx < SIMULTAN_PROCESS_GEOMS - 1) {
					geomsCollectingArray[geomsCollectedIdx++] = g;
				}

				// Geometry collecting array filled.
				else {
					geomsCollectingArray[geomsCollectedIdx] = g; // add last
																	// geometry
					GeometryCollection gc = new GeometryCollection(geomsCollectingArray, fact);
					geomsCollectingArray[0] = resolveParam(gc, paramType);
					geomsCollectedIdx = 1;

				}

			}

			geoFind.reset();
		}

		Geometry extent = resolveParam(
				new GeometryCollection(Arrays.copyOfRange(geomsCollectingArray, 0,
						geomsCollectedIdx), fact), paramType);

		if (extent != null) {
			return new DefaultGeometryProperty<Geometry>(commonCrs, extent);
		}
		throw new NoResultException();
	}

	/**
	 * Function resolves the extent option parameter and computes the specified
	 * extent type.
	 * 
	 * @param gc GeometryCollection, the extent function is processed on
	 * @param paramType extent option parameter
	 * @return Geometry representing extent of input geometries
	 */
	private Geometry resolveParam(GeometryCollection gc, String paramType) {

		Geometry extent = null;

		// Compute bounding box.
		if (paramType.equalsIgnoreCase(PARAM_BOUNDING_BOX)) {
			extent = gc.getEnvelope();
		}

		// Compute convex hull.
		else if (paramType.equalsIgnoreCase(PARAM_CONVEX_HULL)) {
			extent = gc.convexHull();
		}

		// Compute union.
		else if (paramType.equalsIgnoreCase(PARAM_UNION)) {

			// Alternative function <extent.union()> is slower and results in
			// inconsistent topology errors within the jts-lib.
			// lib-intern errors due to topology inconsistency errros.
			extent = gc.buffer(0);

		}

		// Ensure extent is not null. Default case is bounding box.
		if (extent == null) {
			extent = gc.getEnvelope();
		}

		return extent;
	}

}
