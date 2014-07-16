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

package eu.esdihumboldt.hale.common.align.extension.function.validator;

import java.math.BigDecimal;

import com.google.common.collect.ListMultimap;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.parameter.Validator;

/**
 * Validator that checks whether the value is a number.<br>
 * Parameters: <br>
 * <table border="1">
 * <tr>
 * <th>name</th>
 * <th>value</th>
 * <th>description</th>
 * <th>default</th>
 * </tr>
 * <tr>
 * <td>type</td>
 * <td>"integer"|"float"</td>
 * <td>whether the number should be an integer or a floating point number</td>
 * <td>integer</td>
 * </tr>
 * <tr>
 * <td>min</td>
 * <td>float</td>
 * <td>minimum value of the number</td>
 * <td>no bounds</td>
 * </tr>
 * <tr>
 * <td>max</td>
 * <td>float</td>
 * <td>maximum value of the number</td>
 * <td>no bounds</td>
 * </tr>
 * </table>
 * 
 * @author Kai Schwierczek
 */
public class NumberValidator implements Validator {

	private static final ALogger log = ALoggerFactory.getLogger(LengthValidator.class);

	private BigDecimal minValue = null;
	private BigDecimal maxValue = null;
	private boolean isInt = true;

	/**
	 * @see eu.esdihumboldt.hale.common.core.parameter.Validator#validate(java.lang.String)
	 */
	@Override
	public String validate(String value) {
		try {
			BigDecimal number = new BigDecimal(value);
			if (isInt && number.scale() > 0)
				throw new NumberFormatException();
			if (minValue != null && number.compareTo(minValue) < 0)
				return "parameter must be at least " + minValue;
			if (maxValue != null && number.compareTo(maxValue) > 0)
				return "parameter must be at most " + maxValue;
		} catch (NumberFormatException nfe) {
			return "parameter must be a valid " + (isInt ? "integer" : "floating point number");
		}
		return null;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.parameter.Validator#setParameters(com.google.common.collect.ListMultimap)
	 */
	@Override
	public void setParameters(ListMultimap<String, String> parameters) {
		if (parameters.containsKey("type")) {
			String type = parameters.get("type").get(0);
			if (type.equals("integer"))
				isInt = true;
			else if (type.equals("float"))
				isInt = false;
			else
				log.error("type has to be integer or float.");
		}
		if (parameters.containsKey("min")) {
			String min = parameters.get("min").get(0);
			try {
				minValue = new BigDecimal(min);
			} catch (NumberFormatException nfe) {
				log.error("min is no valid floating point number.", nfe);
			}
		}
		if (parameters.containsKey("max")) {
			String max = parameters.get("max").get(0);
			try {
				maxValue = new BigDecimal(max);
			} catch (NumberFormatException nfe) {
				log.error("max is no valid floating point number.", nfe);
			}
		}
	}

}
