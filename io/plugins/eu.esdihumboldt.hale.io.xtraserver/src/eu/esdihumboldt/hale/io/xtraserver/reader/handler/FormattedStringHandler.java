/*
 * Copyright (c) 2017 interactive instruments GmbH
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
 *     interactive instruments GmbH <http://www.interactive-instruments.de>
 */

package eu.esdihumboldt.hale.io.xtraserver.reader.handler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.interactive_instruments.xtraserver.config.util.api.MappingValue;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.functions.FormattedStringFunction;

/**
 * Transforms a {@link MappingValue} to a {@link FormattedStringFunction}
 * 
 * @author zahnen
 */
class FormattedStringHandler extends AbstractPropertyTransformationHandler {

	private static final Pattern EXPRESSION_PATTERN = Pattern.compile(
			"(?:(?:'(?<text>[^']*?)')|(?:(?:\\$T\\$\\.)(?<column>[\\S]+)))(?:\\s*\\|\\|\\s*)?");

	FormattedStringHandler(final TransformationContext transformationContext) {
		super(transformationContext);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.xtraserver.reader.handler.AbstractPropertyTransformationHandler#doHandle(de.interactive_instruments.xtraserver.config.util.api.MappingValue)
	 */
	@Override
	public String doHandle(final MappingValue mappingValue) {

		final Matcher matcher = EXPRESSION_PATTERN.matcher(mappingValue.getValue());
		final StringBuilder pattern = new StringBuilder();
		String column = null;

		while (matcher.find()) {
			String text = matcher.group("text");
			column = matcher.group("column");

			if (text != null) {
				pattern.append(text);
			}
			else if (column != null) {
				pattern.append('{').append(column).append('}');
			}
		}

		if (column == null) {
			throw new IllegalArgumentException(
					"Expression could not be parsed: " + mappingValue.getValue());
		}
		// TODO: multiple columns
		transformationContext.nextPropertyTransformation(mappingValue.getTable(), column, "var",
				mappingValue.getTargetQNameList());

		transformationContext.getCurrentPropertyParameters().put("pattern",
				new ParameterValue(pattern.toString()));

		return FormattedStringFunction.ID;
	}

	static boolean isFormattedStringExpression(String expression) {
		Matcher matcher = EXPRESSION_PATTERN.matcher(expression);
		boolean matches = false;

		do {
			matches = matcher.find();
		} while (matches && !matcher.hitEnd());

		return matches;
	}

}
