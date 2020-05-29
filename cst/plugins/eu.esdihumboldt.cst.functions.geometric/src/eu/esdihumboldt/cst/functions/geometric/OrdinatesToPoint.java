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

import java.util.Map;

import net.jcip.annotations.Immutable;

import com.google.common.collect.ListMultimap;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

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
		String srs = getOptionalParameter(PARAMETER_REFERENCE_SYSTEM, null).as(String.class);
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
