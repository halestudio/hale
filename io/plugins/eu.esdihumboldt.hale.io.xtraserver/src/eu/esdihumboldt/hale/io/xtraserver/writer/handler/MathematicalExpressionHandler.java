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

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.ListMultimap;

import de.interactive_instruments.xtraserver.config.api.MappingValue;
import de.interactive_instruments.xtraserver.config.api.MappingValueBuilder;
import eu.esdihumboldt.cst.functions.numeric.MathematicalExpressionFunction;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.Property;

/**
 * Transforms the {@link MathematicalExpressionFunction} to a
 * {@link MappingValue}
 * 
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
class MathematicalExpressionHandler extends AbstractPropertyTransformationHandler {

	MathematicalExpressionHandler(final MappingContext mappingContext) {
		super(mappingContext);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.xtraserver.writer.handler.TransformationHandler#handle(eu.esdihumboldt.hale.common.align.model.Cell)
	 */
	@Override
	public Optional<MappingValue> doHandle(final Cell propertyCell, final Property targetProperty) {

		// Get mathematical expression from parameters
		final ListMultimap<String, ParameterValue> parameters = propertyCell
				.getTransformationParameters();
		final List<ParameterValue> expressions = parameters.get("expression");
		if (expressions.isEmpty()) {
			throw new IllegalArgumentException("Expression not set");
		}
		else if (expressions.size() > 1) {
			throw new IllegalArgumentException("Only one expression is supported");
		}
		String expression = expressions.get(0).getStringRepresentation();
		// Replace variables in the expression with our expression syntax
		final Collection<? extends Entity> variables = propertyCell.getSource().asMap().get("var");
		for (Entity var : variables) {
			final String varName = var.getDefinition().getDefinition().getName().getLocalPart();
			expression = expression.replaceAll(varName, "\\$T\\$." + varName);
		}

		final MappingValue mappingValue = new MappingValueBuilder().expression()
				.qualifiedTargetPath(buildPath(targetProperty.getDefinition().getPropertyPath()))
				.value(expression).build();

		return Optional.of(mappingValue);
	}

}
