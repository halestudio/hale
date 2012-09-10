/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.align.extension.function.validator;

import java.math.BigDecimal;

import com.google.common.collect.ListMultimap;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.extension.function.Validator;

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
	 * @see eu.esdihumboldt.hale.common.align.extension.function.Validator#validate(java.lang.String)
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
	 * @see eu.esdihumboldt.hale.common.align.extension.function.Validator#setParameters(com.google.common.collect.ListMultimap)
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
