/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.align.model.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Joiner;

import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;

/**
 * Formatted string function constants.
 * 
 * @author Simon Templer
 */
public interface FormattedStringFunction {

	/**
	 * Name of the parameter specifying the pattern for the string format. See
	 * the function definition in <code>eu.esdihumboldt.hale.common.align</code>
	 * .
	 */
	public static final String PARAMETER_PATTERN = "pattern";

	/**
	 * Entity name for variables. See the function definition in
	 * <code>eu.esdihumboldt.hale.common.align</code>.
	 */
	public static final String ENTITY_VARIABLE = "var";

	/**
	 * the formatted string function Id
	 */
	public static final String ID = "eu.esdihumboldt.hale.align.formattedstring";

	/**
	 * Add a value to the given map of values, with the variable names derived
	 * from the associated property definition.
	 * 
	 * @param values the map associating variable names to values
	 * @param value the value
	 * @param property the associated property
	 */
	public static void addValue(Map<String, Object> values, Object value,
			PropertyEntityDefinition property) {
		// determine the variable name
		String name = property.getDefinition().getName().getLocalPart();

		// add with short name, but ensure no variable with only a short
		// name is overridden
		if (!values.keySet().contains(name) || property.getPropertyPath().size() == 1) {
			values.put(name, value);
		}

		// add with long name if applicable
		if (property.getPropertyPath().size() > 1) {
			List<String> names = new ArrayList<String>();
			for (ChildContext context : property.getPropertyPath()) {
				names.add(context.getChild().getName().getLocalPart());
			}
			String longName = Joiner.on('.').join(names);
			values.put(longName, value);
		}
	}

}
