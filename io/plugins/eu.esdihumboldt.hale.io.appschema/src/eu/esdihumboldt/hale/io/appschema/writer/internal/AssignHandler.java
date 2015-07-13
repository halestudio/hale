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

import static eu.esdihumboldt.hale.common.align.model.functions.AssignFunction.ENTITY_ANCHOR;
import static eu.esdihumboldt.hale.common.align.model.functions.AssignFunction.PARAMETER_VALUE;

import java.util.List;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.cst.functions.core.Assign;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.Property;

/**
 * Translates a property cell specifying an {@link Assign} transformation
 * function to an app-schema attribute mapping.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class AssignHandler extends AbstractPropertyTransformationHandler {

	/**
	 * @see eu.esdihumboldt.hale.io.appschema.writer.internal.AbstractPropertyTransformationHandler#getSourceExpressionAsCQL()
	 */
	@Override
	protected String getSourceExpressionAsCQL() {
		ListMultimap<String, ? extends Entity> sourceEntities = propertyCell.getSource();
		Property anchor = null;
		if (sourceEntities != null && sourceEntities.containsKey(ENTITY_ANCHOR)) {
			anchor = (Property) sourceEntities.get(ENTITY_ANCHOR).get(0);
		}

		ListMultimap<String, ParameterValue> parameters = propertyCell
				.getTransformationParameters();
		List<ParameterValue> valueParams = parameters.get(PARAMETER_VALUE);
		String value = valueParams.get(0).getStringRepresentation();

		String ocql = null;
		if (anchor != null) {
			// TODO: generalize this code
			String anchorAttr = anchor.getDefinition().getDefinition().getName().getLocalPart();
			ocql = "if_then_else(isNull(" + anchorAttr + "), Expression.NIL, '" + value + "')";
			ocql = getConditionalExpression(anchor.getDefinition(), ocql);
		}
		else {
			ocql = "'" + value + "'";
		}

		return ocql;
	}

}
