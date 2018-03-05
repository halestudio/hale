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

package eu.esdihumboldt.hale.io.xtraserver.writer.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ListMultimap;

import de.interactive_instruments.xtraserver.config.api.MappingValue;
import de.interactive_instruments.xtraserver.config.api.MappingValueBuilder;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.Property;
import eu.esdihumboldt.hale.common.align.model.functions.FormattedStringFunction;

/**
 * Transforms the {@link FormattedStringFunction} to a {@link MappingValue}
 * 
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
class FormattedStringHandler extends AbstractPropertyTransformationHandler {

	private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{(.+?)\\}");
	private static final String XS_VAR_PREFIX = "$T$.";
	private static final String XS_CONCAT = " || ";
	private static final String XS_CONCAT_LEFT_STR = "' || ";
	private static final String XS_CONCAT_RIGHT_STR = " || '";

	FormattedStringHandler(final MappingContext mappingContext) {
		super(mappingContext);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.xtraserver.writer.handler.TransformationHandler#handle(eu.esdihumboldt.hale.common.align.model.Cell)
	 */
	@Override
	public Optional<MappingValue> doHandle(final Cell propertyCell, final Property targetProperty) {

		// Get the formatted string from parameters
		final ListMultimap<String, ParameterValue> parameters = propertyCell
				.getTransformationParameters();
		final List<ParameterValue> patterns = parameters.get("pattern");
		if (patterns == null || patterns.isEmpty() || patterns.get(0).isEmpty()) {
			throw new IllegalArgumentException("Formatted string not set");
		}
		final String pattern = patterns.get(0).as(String.class);
		final StringBuilder formattedStr = new StringBuilder(
				mappingContext.resolveProjectVars(pattern));

		if (propertyCell.getSource() != null
				&& !propertyCell.getSource().asMap().get("var").isEmpty()) {
			final Collection<? extends Entity> variables = propertyCell.getSource().asMap()
					.get("var");
			final List<int[]> startEndList = new ArrayList<int[]>();
			final List<String> varList = new ArrayList<String>();
			final Matcher m = VARIABLE_PATTERN.matcher(pattern);
			while (m.find()) {
				int[] startEnd = new int[2];
				startEnd[0] = m.start(); // index of '{' character
				startEnd[1] = m.end(); // index of '}' character
				startEndList.add(startEnd);
				varList.add(m.group(1)); // the variable name, without curly braces
			}
			if (varList.size() != variables.size()
					&& varList.size() == new HashSet<>(varList).size()) {
				throw new IllegalArgumentException(
						"Found " + varList.size() + " variables in the format pattern, but "
								+ variables.size() + " provided variables should be replaced");
			}

			final StringBuilder varBuilder = new StringBuilder();
			int idxOffset = 0;

			// Add ' if string does not start with a variable definition
			if (startEndList.get(0)[0] != 0) {
				formattedStr.insert(0, '\'');
				varBuilder.append(XS_CONCAT_LEFT_STR);
				idxOffset++;
			}
			int firstStartIdx = startEndList.get(0)[0] + idxOffset;
			int firstEndIdx = startEndList.get(0)[1] + idxOffset;
			varBuilder.append(XS_VAR_PREFIX);
			varBuilder.append(varList.get(0));
			if (firstEndIdx - idxOffset != pattern.length()) {
				rightConcat(varBuilder, 0, startEndList);
				if (varList.size() == 1) {
					formattedStr.append('\'');
				}
			}
			formattedStr.replace(firstStartIdx, firstEndIdx, varBuilder.toString());
			idxOffset += varBuilder.length() - firstEndIdx + firstStartIdx;
			varBuilder.setLength(0);

			if (varList.size() > 1) {
				for (int i = 1; i < startEndList.size(); i++) {
					final int startIdx = startEndList.get(i)[0] + idxOffset;
					final int endIdx = startEndList.get(i)[1] + idxOffset;
					leftConcat(varBuilder, i, startEndList);
					varBuilder.append(XS_VAR_PREFIX);
					varBuilder.append(varList.get(i));
					rightConcat(varBuilder, i, startEndList);
					formattedStr.replace(startIdx, endIdx, varBuilder.toString());
					idxOffset += varBuilder.length() - endIdx + startIdx;
					varBuilder.setLength(0);
				}
				// Remove || ' after the last variable or append ' after the last string
				if (startEndList.get(startEndList.size() - 1)[1] == pattern.length()) {
					formattedStr.setLength(formattedStr.length() - XS_CONCAT_RIGHT_STR.length());
				}
				else {
					formattedStr.append(XS_CONCAT_LEFT_STR);
				}
			}
		}
		else {
			formattedStr.insert(0, '\'');
			// Simple string without formatting
			formattedStr.append('\'');
		}

		final MappingValue mappingValue = new MappingValueBuilder().expression()
				.qualifiedTargetPath(buildPath(targetProperty.getDefinition().getPropertyPath()))
				.value(formattedStr.toString()).build();

		return Optional.of(mappingValue);
	}

	private static void leftConcat(final StringBuilder builder, final int currentIdx,
			final List<int[]> startEndList) {
		if (startEndList.get(currentIdx)[0] == startEndList.get(currentIdx - 1)[1]) {
			builder.append(XS_CONCAT);
		}
		else {
			builder.append(XS_CONCAT_LEFT_STR);
		}
	}

	private static void rightConcat(final StringBuilder builder, final int currentIdx,
			final List<int[]> startEndList) {
		if (currentIdx + 1 < startEndList.size()) {
			if (startEndList.get(currentIdx)[1] != startEndList.get(currentIdx + 1)[0]) {
				builder.append(XS_CONCAT_RIGHT_STR);
			}
		}
		else {
			builder.append(XS_CONCAT_RIGHT_STR);
		}
	}

}
