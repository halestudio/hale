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

import static eu.esdihumboldt.cst.functions.string.DateExtractionFunction.PARAMETER_DATE_FORMAT;
import eu.esdihumboldt.cst.functions.string.DateExtraction;
import eu.esdihumboldt.hale.common.align.model.Property;

/**
 * Translates a property cell specifying a {@link DateExtraction} transformation
 * function to an app-schema attribute mapping.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class DateExtractionHandler extends AbstractPropertyTransformationHandler {

	/**
	 * @see eu.esdihumboldt.hale.io.appschema.writer.internal.AbstractPropertyTransformationHandler#getSourceExpressionAsCQL()
	 */
	@Override
	protected String getSourceExpressionAsCQL() {
		Property source = AppSchemaMappingUtils.getSourceProperty(propertyCell);
		String dateFormat = propertyCell.getTransformationParameters().get(PARAMETER_DATE_FORMAT)
				.get(0).getStringRepresentation();

		String dateStrProperty = source.getDefinition().getDefinition().getName().getLocalPart();
		String cqlExpression = String.format("dateParse(%s, '%s')", dateStrProperty, dateFormat);

		return getConditionalExpression(source.getDefinition(), cqlExpression);
	}
}
