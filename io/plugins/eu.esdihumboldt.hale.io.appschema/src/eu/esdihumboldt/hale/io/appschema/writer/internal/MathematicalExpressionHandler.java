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

import static eu.esdihumboldt.cst.functions.numeric.MathematicalExpressionFunction.PARAMETER_EXPRESSION;

import java.util.List;

import eu.esdihumboldt.cst.functions.numeric.MathematicalExpression;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;

/**
 * Translates a property cell specifying a {@link MathematicalExpression}
 * transformation function to an app-schema attribute mapping.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class MathematicalExpressionHandler extends AbstractPropertyTransformationHandler {

	/**
	 * @see eu.esdihumboldt.hale.io.appschema.writer.internal.AbstractPropertyTransformationHandler#getSourceExpressionAsCQL()
	 */
	@Override
	protected String getSourceExpressionAsCQL() {
		// TODO: verify math expressions work as-is in CQL
		String mathExpression = propertyCell.getTransformationParameters()
				.get(PARAMETER_EXPRESSION).get(0).getStringRepresentation();

		// if properties used in the expression have conditions defined on them,
		// expression should be evaluated only if all conditions are met
		if (propertyCell.getSource() != null) {
			List<? extends Entity> sourceEntities = propertyCell.getSource().get(
					MathematicalExpression.ENTITY_VARIABLE);

			if (sourceEntities != null) {
				for (Entity source : sourceEntities) {
					PropertyEntityDefinition propEntityDef = (PropertyEntityDefinition) source
							.getDefinition();
					mathExpression = getConditionalExpression(propEntityDef, mathExpression);
				}
			}
		}

		return mathExpression;
	}
}
