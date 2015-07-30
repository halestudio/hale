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

import static eu.esdihumboldt.hale.common.align.model.functions.ClassificationMappingFunction.PARAMETER_NOT_CLASSIFIED_ACTION;
import static eu.esdihumboldt.hale.common.align.model.functions.ClassificationMappingFunction.USE_FIXED_VALUE_ACTION_PREFIX;
import static eu.esdihumboldt.hale.common.align.model.functions.ClassificationMappingFunction.USE_NULL_ACTION;
import static eu.esdihumboldt.hale.common.align.model.functions.ClassificationMappingFunction.USE_SOURCE_ACTION;
import static eu.esdihumboldt.hale.io.appschema.writer.internal.AppSchemaMappingUtils.asCqlLiteral;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.collect.ListMultimap;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.Property;
import eu.esdihumboldt.hale.common.align.model.functions.ClassificationMappingUtil;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.service.ServiceManager;
import eu.esdihumboldt.hale.common.lookup.LookupTable;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;

/**
 * TODO Type description
 * 
 * @author stefano
 */
public class ClassificationHandler extends AbstractPropertyTransformationHandler {

	private static final ALogger log = ALoggerFactory.getLogger(ClassificationHandler.class);

	/**
	 * @see eu.esdihumboldt.hale.io.appschema.writer.internal.AbstractPropertyTransformationHandler#getSourceExpressionAsCQL()
	 */
	@Override
	protected String getSourceExpressionAsCQL() {
		Property source = AppSchemaMappingUtils.getSourceProperty(propertyCell);
		PropertyDefinition sourceDef = source.getDefinition().getDefinition();
		Property target = AppSchemaMappingUtils.getTargetProperty(propertyCell);
		PropertyDefinition targetDef = target.getDefinition().getDefinition();

		String sourceName = source.getDefinition().getDefinition().getName().getLocalPart();

		ListMultimap<String, ParameterValue> parameters = propertyCell
				.getTransformationParameters();

		LookupTable lookup = ClassificationMappingUtil.getClassificationLookup(parameters,
				new ServiceManager(ServiceManager.SCOPE_PROJECT));
		if (lookup == null) {
			log.warn("No classification specified");
			return "''";
		}
		else {
			String cqlTemplate = "if_then_else(in(%s), Recode(%s,%s), %s)";

			// build args to Recode function
			StringBuilder recodeArgsBuilder = new StringBuilder();
			Map<Value, Value> valueMap = lookup.asMap();
			int counter = 0;
			for (Value sourceValue : valueMap.keySet()) {
				Value targetValue = valueMap.get(sourceValue);

				String sourceLiteral = asCqlLiteral(sourceDef, sourceValue.as(String.class));
				String targetLiteral = asCqlLiteral(targetDef, targetValue.as(String.class));
				recodeArgsBuilder.append(sourceLiteral).append(",").append(targetLiteral);
				if (counter < valueMap.size() - 1) {
					recodeArgsBuilder.append(",");
				}
				counter++;
			}
			String recodeArgs = recodeArgsBuilder.toString();

			// build args for in function
			List<String> values = new ArrayList<String>();
			for (Value v : valueMap.keySet()) {
				String valueLiteral = asCqlLiteral(sourceDef, v.as(String.class));
				values.add(valueLiteral);
			}
			values.add(0, sourceName);
			String inArgs = Joiner.on(",").join(values);

			// determine what to put in the "else" branch, based on
			// transformation parameters
			String elsePart = null;
			List<ParameterValue> notClassifiedParam = parameters
					.get(PARAMETER_NOT_CLASSIFIED_ACTION);
			String notClassifiedAction = null;
			if (notClassifiedParam != null && notClassifiedParam.size() > 0) {
				notClassifiedAction = notClassifiedParam.get(0).as(String.class);
			}
			else {
				notClassifiedAction = USE_NULL_ACTION;
			}
			if (USE_SOURCE_ACTION.equals(notClassifiedAction))
				elsePart = sourceName;
			else if (notClassifiedAction.startsWith(USE_FIXED_VALUE_ACTION_PREFIX))
				elsePart = asCqlLiteral(targetDef,
						notClassifiedAction.substring(notClassifiedAction.indexOf(':') + 1));
			else if (USE_NULL_ACTION.equals(notClassifiedAction))
				elsePart = "Expression.NIL";

			return String.format(cqlTemplate, inArgs, sourceName, recodeArgs, elsePart);
		}
	}
}
