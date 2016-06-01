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

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunction;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import groovy.lang.GroovyObjectSupport;

/**
 * Parameter binding for cell explanations.
 * 
 * @author Simon Templer
 */
public class ParameterBinding extends GroovyObjectSupport {

	private final Cell cell;
	private final AbstractFunction<?> function;

	/**
	 * Create a parameter binding for the given cell.
	 * 
	 * @param cell the cell
	 * @param function the function definition for the cell
	 */
	public ParameterBinding(Cell cell, AbstractFunction<?> function) {
		super();
		this.cell = cell;
		this.function = function;
	}

	/**
	 * Extract the value of a {@link ParameterValue}.
	 * 
	 * @param value the parameter value
	 * @return the extracted value
	 */
	protected Object extractParameterValue(ParameterValue value) {
		// TODO handle scripted parameters?

		// XXX for now just retrieve the internal value
		return value.getValue();
	}

	@Override
	public Object getProperty(String property) {
		boolean getAsList = true;

		if (function != null) {
			boolean paramNotList = function.getDefinedParameters().stream().anyMatch(param -> {
				return Objects.equals(property, param.getName()) && param.getMaxOccurrence() == 1;
			});
			if (paramNotList) {
				getAsList = false;
			}
		}

		List<ParameterValue> values = cell.getTransformationParameters().get(property);
		if (getAsList) {
			// yield parameters as list
			return values.stream().map(this::extractParameterValue).collect(Collectors.toList());
		}
		else {
			// yield parameter value or null if there is none
			if (values.isEmpty()) {
				return null;
			}
			else {
				return extractParameterValue(values.get(0));
			}
		}
	}

	@Override
	public void setProperty(String property, Object newValue) {
		throw new UnsupportedOperationException();
	}

}
