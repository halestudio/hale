/*
 * Copyright (c) 2016 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.align.model.impl.mdexpl;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameterDefinition;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.core.parameter.ParameterValueDescriptor;
import groovy.lang.GroovyObjectSupport;

/**
 * Parameter binding for cell explanations.
 * 
 * @author Simon Templer
 */
public class ParameterBinding extends GroovyObjectSupport {

	private final Cell cell;
	private final FunctionDefinition<?> function;

	/**
	 * Create a parameter binding for the given cell.
	 * 
	 * @param cell the cell
	 * @param function the function definition for the cell
	 */
	public ParameterBinding(Cell cell, FunctionDefinition<?> function) {
		super();
		this.cell = cell;
		this.function = function;
	}

	/**
	 * Extract the value of a {@link ParameterValue}.
	 * 
	 * @param value the parameter value
	 * @param paramDef the parameter definition
	 * @return the extracted value
	 */
	protected Object extractParameterValue(ParameterValue value,
			Optional<FunctionParameterDefinition> paramDef) {
		// TODO handle scripted parameters?

		if (paramDef.isPresent()) {
			Class<?> binding = paramDef.get().getBinding();

			if (binding != null) {
				// handle simple binding case
				return value.as(binding);
			}
		}

		// as fall-back yield the internal value
		return value.getValue();
	}

	@Override
	public Object getProperty(String property) {
		boolean getAsList = true;

		final Optional<FunctionParameterDefinition> paramDef;
		if (function != null) {
			paramDef = function.getDefinedParameters().stream()
					.filter(param -> Objects.equals(property, param.getName())).findFirst();
		}
		else {
			paramDef = Optional.empty();
		}

		if (paramDef.isPresent()) {
			if (paramDef.get().getMaxOccurrence() == 1) {
				getAsList = false;
			}
		}

		List<ParameterValue> values;
		if (cell != null && cell.getTransformationParameters() != null) {
			values = cell.getTransformationParameters().get(property);
		}
		else {
			values = Collections.emptyList();
		}

		if (getAsList) {
			// yield parameters as list
			return values.stream().map(value -> extractParameterValue(value, paramDef))
					.collect(Collectors.toList());
		}
		else {
			// yield parameter value or null if there is none
			if (values.isEmpty()) {
				if (paramDef.isPresent()) {
					ParameterValueDescriptor descriptor = paramDef.get().getValueDescriptor();
					if (descriptor != null && descriptor.getDefaultValue() != null) {
						// use default value as parameter value
						return extractParameterValue(
								new ParameterValue(descriptor.getDefaultValue()), paramDef);
					}
				}
				return null;
			}
			else {
				return extractParameterValue(values.get(0), paramDef);
			}
		}
	}

	@Override
	public void setProperty(String property, Object newValue) {
		throw new UnsupportedOperationException();
	}

}
