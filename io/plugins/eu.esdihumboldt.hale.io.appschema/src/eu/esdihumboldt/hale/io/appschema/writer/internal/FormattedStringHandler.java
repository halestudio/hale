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

import static eu.esdihumboldt.hale.common.align.model.functions.FormattedStringFunction.PARAMETER_PATTERN;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.cst.functions.core.FormattedString;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.functions.FormattedStringFunction;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;

/**
 * Translates a property cell specifying a {@link FormattedString}
 * transformation function to an app-schema attribute mapping.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class FormattedStringHandler extends AbstractPropertyTransformationHandler {

	private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{(.+?)\\}");

	/**
	 * @see eu.esdihumboldt.hale.io.appschema.writer.internal.AbstractPropertyTransformationHandler#getSourceExpressionAsCQL()
	 */
	@Override
	protected String getSourceExpressionAsCQL() {
		ListMultimap<String, ParameterValue> parameters = propertyCell
				.getTransformationParameters();
		String pattern = parameters.get(PARAMETER_PATTERN).get(0).as(String.class);

		List<int[]> startEndList = new ArrayList<int[]>();
		List<String> varList = new ArrayList<String>();
		Matcher m = VARIABLE_PATTERN.matcher(pattern);
		while (m.find()) {
			int[] startEnd = new int[2];
			startEnd[0] = m.start(); // index of '{' character
			startEnd[1] = m.end(); // index of '}' character
			startEndList.add(startEnd);
			varList.add(m.group(1)); // the variable name, without curly braces
		}

		// list of string to be concatenated, either string constants or
		// variable names
		String[] partsToConcat = new String[varList.size() * 2 + 1];
		int lastPos = 0;
		for (int i = 0; i < varList.size(); i++) {
			int[] startEnd = startEndList.get(i);
			String var = varList.get(i);

			String textBeforeVar = pattern.substring(lastPos, startEnd[0]);
			if (textBeforeVar != null && textBeforeVar.length() == 0) {
				textBeforeVar = null;
			}
			partsToConcat[i * 2] = (textBeforeVar != null) ? "'" + textBeforeVar + "'" : null;
			partsToConcat[i * 2 + 1] = var;

			lastPos = startEnd[1];
		}
		// add text after last variable
		String textAfterLastVar = pattern.substring(lastPos, pattern.length());
		if (textAfterLastVar != null && textAfterLastVar.length() == 0) {
			textAfterLastVar = null;
		}
		partsToConcat[partsToConcat.length - 1] = (textAfterLastVar != null) ? "'"
				+ textAfterLastVar + "'" : null;

		String strConcatExpr = "";
		int lastPartIdx = 0;
		while (lastPartIdx < partsToConcat.length) {
			if (lastPartIdx == 0) {
				// initialize strConcatExpr
				for (int notNullIdx = lastPartIdx; notNullIdx < partsToConcat.length; notNullIdx++) {
					if (partsToConcat[notNullIdx] != null) {
						strConcatExpr = partsToConcat[notNullIdx];
						lastPartIdx = notNullIdx;
						break;
					}
				}
			}

			String secondArgument = null;
			for (int notNullIdx = lastPartIdx + 1; notNullIdx < partsToConcat.length; notNullIdx++) {
				if (partsToConcat[notNullIdx] != null) {
					secondArgument = partsToConcat[notNullIdx];
					lastPartIdx = notNullIdx;
					break;
				}
			}

			if (secondArgument != null) {
				strConcatExpr = "strConcat(" + strConcatExpr + ", " + secondArgument + ")";
			}
			else {
				// no second argument could be found: should stop here
				break;
			}
		}

		// if properties used in the expression have conditions defined on them,
		// expression should be evaluated only if all conditions are met
		if (propertyCell.getSource() != null) {
			List<? extends Entity> sourceEntities = propertyCell.getSource().get(
					FormattedStringFunction.ENTITY_VARIABLE);

			if (sourceEntities != null) {
				for (Entity source : sourceEntities) {
					PropertyEntityDefinition propEntityDef = (PropertyEntityDefinition) source
							.getDefinition();
					strConcatExpr = getConditionalExpression(propEntityDef, strConcatExpr);
				}
			}
		}

		return strConcatExpr;
	}
}
