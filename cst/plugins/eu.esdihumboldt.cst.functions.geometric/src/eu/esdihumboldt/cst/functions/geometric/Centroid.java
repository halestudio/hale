/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.cst.functions.geometric;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ListMultimap;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

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
 * Centroid function
 * 
 * @author Kevin Mais
 */
public class Centroid extends AbstractSingleTargetPropertyTransformation<TransformationEngine>
		implements CentroidFunction {

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

			List<Point> centroids = new ArrayList<Point>();

			for (GeometryProperty<?> geoProp : geoms) {
				centroids.add(geoProp.getGeometry().getCentroid());
			}

			// TODO: warn ?!

			// FIXME: list can only have one point so we don't need this case ?
			return centroids;

		}
		else {
			geom = geoms.get(0).getGeometry();
		}

		if (geom != null) {
			return geom.getCentroid();
		}
		else {
			throw new TransformationException("Geometry for centroid could not be retrieved.");
		}

	}

}
