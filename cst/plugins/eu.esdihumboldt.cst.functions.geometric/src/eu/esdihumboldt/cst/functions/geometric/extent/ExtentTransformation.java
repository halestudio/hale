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

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;

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
		String paramType = getOptionalParameter(PARAM_TYPE, new ParameterValue(PARAM_BOUNDING_BOX))
				.as(String.class);
		Iterable<Object> geometries = Iterables.transform(variables.get(null),
				new Function<PropertyValue, Object>() {

					@Override
					public Object apply(PropertyValue input) {
						return input.getValue();
					}
				});
		return calculateExtent(geometries, ExtentType.forId(paramType));
	}

	/**
	 * Calculate the extent of a set of geometries.
	 * 
	 * @param geometries the geometries or instances containing geometries
	 * @param type the type of extent to calculate
	 * @return the calculated extent
	 * @throws TransformationException if source geometries don't have a common
	 *             CRS
	 * @throws NoResultException if the result extent would be <code>null</code>
	 */
	public static GeometryProperty<?> calculateExtent(Iterable<?> geometries, ExtentType type)
			throws TransformationException, NoResultException {

		InstanceTraverser traverser = new DepthFirstInstanceTraverser(true);
		GeometryFinder geoFind = new GeometryFinder(null);

		GeometryFactory fact = new GeometryFactory();

		CRSDefinition commonCrs = null;
		Geometry[] geomsCollectingArray = new Geometry[SIMULTAN_PROCESS_GEOMS];
		short geomsCollectedIdx = 0;

//		int count = 0;

		// System.err.println("Geoms Currently Processed;TotalMem;FreeMem");

		for (Object value : geometries) {
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
					geomsCollectingArray[0] = resolveParam(gc, type);
					geomsCollectedIdx = 1;

				}

			}

			geoFind.reset();
		}

		Geometry extent = resolveParam(
				new GeometryCollection(Arrays.copyOfRange(geomsCollectingArray, 0,
						geomsCollectedIdx), fact), type);

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
	 * @param type extent option parameter
	 * @return Geometry representing extent of input geometries
	 */
	private static Geometry resolveParam(GeometryCollection gc, ExtentType type) {

		Geometry extent = null;

		switch (type) {
		case CONVEX_HULL:
			// Compute convex hull.
			extent = gc.convexHull();
			break;
		case UNION:
			// Compute union.

			// Alternative function <extent.union()> is slower and results in
			// inconsistent topology errors within the jts-lib.
			// lib-intern errors due to topology inconsistency errros.
			extent = gc.buffer(0);
			break;
		case BBOX:
			// Compute bounding box.
		default:
			// Ensure extent is not null. Default case is bounding box.
			extent = gc.getEnvelope();
		}

		return extent;
	}

}
