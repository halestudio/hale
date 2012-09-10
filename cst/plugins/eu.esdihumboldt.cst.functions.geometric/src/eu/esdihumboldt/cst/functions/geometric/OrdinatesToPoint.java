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

import java.util.Map;

import net.jcip.annotations.Immutable;

import com.google.common.collect.ListMultimap;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractSingleTargetPropertyTransformation;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.instance.geometry.DefaultGeometryProperty;
import eu.esdihumboldt.hale.common.instance.geometry.impl.CodeDefinition;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;

/**
 * Ordinates to point function.
 * 
 * @author Kai Schwierczek
 */
@Immutable
public class OrdinatesToPoint extends
		AbstractSingleTargetPropertyTransformation<TransformationEngine> implements
		OrdinatesToPointFunction {

	/**
	 * @see eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractPropertyTransformation#evaluate(java.lang.String,
	 *      eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine,
	 *      com.google.common.collect.ListMultimap,
	 *      com.google.common.collect.ListMultimap, java.util.Map,
	 *      eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog)
	 */
	@Override
	protected Object evaluate(String transformationIdentifier, TransformationEngine engine,
			ListMultimap<String, PropertyValue> variables, String resultName,
			PropertyEntityDefinition resultProperty, Map<String, String> executionParameters,
			TransformationLog log) throws TransformationException {
		// get x, y and z properties
		PropertyValue x = variables.get("x").get(0);
		PropertyValue y = variables.get("y").get(0);
		PropertyValue z = null;
		if (!variables.get("z").isEmpty())
			z = variables.get("z").get(0);

		// get crs definition if srs is specified
		CRSDefinition crsDef = null;
		String srs = null;
		if (getParameters() != null && !getParameters().get(PARAMETER_REFERENCE_SYSTEM).isEmpty())
			srs = getParameters().get(PARAMETER_REFERENCE_SYSTEM).get(0);
		if (srs != null)
			crsDef = new CodeDefinition(srs, null);

		// convert values to double and create a point
		double xValue = x.getValueAs(Double.class);
		double yValue = y.getValueAs(Double.class);
		Point resultPoint;
		GeometryFactory geomFactory = new GeometryFactory();
		if (z == null)
			resultPoint = geomFactory.createPoint(new Coordinate(xValue, yValue));
		else
			resultPoint = geomFactory.createPoint(new Coordinate(xValue, yValue, z
					.getValueAs(Double.class)));

		// pack result into geometry property and return it
		GeometryProperty<Point> result = new DefaultGeometryProperty<Point>(crsDef, resultPoint);
		return result;
	}
}
