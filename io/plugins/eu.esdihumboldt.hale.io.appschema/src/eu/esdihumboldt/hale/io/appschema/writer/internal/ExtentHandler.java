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

package eu.esdihumboldt.hale.io.appschema.writer.internal;

import static eu.esdihumboldt.hale.io.appschema.writer.AppSchemaMappingUtils.getSourceProperty;
import static eu.esdihumboldt.hale.io.appschema.writer.AppSchemaMappingUtils.getTransformationParameter;
import eu.esdihumboldt.cst.functions.geometric.extent.ExtentFunction;
import eu.esdihumboldt.cst.functions.geometric.extent.ExtentTransformation;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.Property;

/**
 * Translates a property cell specifying a {@link ExtentTransformation}
 * transformation function to an app-schema attribute mapping.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class ExtentHandler extends AbstractPropertyTransformationHandler {

	/**
	 * @see eu.esdihumboldt.hale.io.appschema.writer.internal.AbstractPropertyTransformationHandler#getSourceExpressionAsCQL()
	 */
	@Override
	protected String getSourceExpressionAsCQL() {
		Property source = getSourceProperty(propertyCell);

		ParameterValue extentTypeParam = getTransformationParameter(propertyCell,
				ExtentFunction.PARAM_TYPE);
		String extentType = (extentTypeParam != null) ? extentTypeParam.as(String.class,
				ExtentFunction.PARAM_BOUNDING_BOX) : ExtentFunction.PARAM_BOUNDING_BOX;
		String extentFunction = "";
		if (extentType.equals(ExtentFunction.PARAM_BOUNDING_BOX)) {
			extentFunction = "envelope";
		}
		else if (extentType.equals(ExtentFunction.PARAM_CONVEX_HULL)) {
			extentFunction = "convexHull";
		}
		else {
			throw new IllegalArgumentException("Extent type not supported: " + extentType);
		}

		String geomProperty = source.getDefinition().getDefinition().getName().getLocalPart();
		String cqlExpression = String.format("%s(%s)", extentFunction, geomProperty);

		return getConditionalExpression(source.getDefinition(), cqlExpression);
	}
}
