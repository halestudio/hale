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

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ListMultimap;

import de.interactive_instruments.xtraserver.config.util.api.MappingValue;
import eu.esdihumboldt.cst.functions.string.RegexAnalysisFunction;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.Property;

/**
 * Transforms the {@link RegexAnalysisFunction} to a {@link MappingValue}
 * 
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
class RegexHandler extends AbstractPropertyTransformationHandler {

	RegexHandler(final MappingContext mappingContext) {
		super(mappingContext);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.xtraserver.writer.handler.AbstractPropertyTransformationHandler#doHandle(eu.esdihumboldt.hale.common.align.model.Cell,
	 *      eu.esdihumboldt.hale.common.align.model.Property,
	 *      de.interactive_instruments.xtraserver.config.util.api.MappingValue)
	 */
	@Override
	protected void doHandle(final Cell propertyCell, final Property targetProperty,
			final MappingValue mappingValue) {

		mappingValue.setTarget(buildPath(targetProperty.getDefinition().getPropertyPath()));

		final ListMultimap<String, ParameterValue> parameters = propertyCell
				.getTransformationParameters();
		final List<ParameterValue> regexParam = parameters.get("regexPattern");
		if (regexParam.isEmpty()) {
			throw new IllegalArgumentException("Regular expression not set");
		}
		final String regex = regexParam.get(0).as(String.class);

		final List<ParameterValue> outputFormatParam = parameters.get("outputFormat");
		if (outputFormatParam.isEmpty()) {
			throw new IllegalArgumentException("Output format for regular expression not set");
		}
		final String outputFormat = outputFormatParam.get(0).as(String.class);

		final Iterator<ChildContext> it = targetProperty.getDefinition().getPropertyPath()
				.iterator();
		ChildContext lastItem = null;
		while (it.hasNext()) {
			lastItem = it.next();
		}
		if (lastItem == null) {
			throw new IllegalArgumentException("Invalid target for regular expression");
		}

		final String regexpTargetProperty = lastItem.getChild().getName().getLocalPart();
		// Replace {number} with escaped-escaped-escaped \\number
		mappingValue.setValue("regexp_replace(" + regexpTargetProperty + ", '" + regex + "', '"
				+ outputFormat.replaceAll("\\{(\\d)\\}", "\\\\\\\\$1") + "', 'g')");
	}

}
