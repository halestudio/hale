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

package eu.esdihumboldt.hale.common.convert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConversionService;

import de.fhg.igd.osgi.util.OsgiUtils;

/**
 * Conversion utilities.
 * 
 * @author Simon Templer
 */
public abstract class ConversionUtil {

	/**
	 * Convert a given value to the given target type.
	 * 
	 * @param value the value to convert
	 * @param targetType the target type
	 * @return the converted value
	 * @throws ConversionException if the conversion failed
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getAs(Object value, Class<T> targetType) throws ConversionException {
		if (value == null) {
			return null;
		}

		if (targetType.isInstance(value)) {
			return (T) value;
		}

		ConversionService cs = OsgiUtils.getService(ConversionService.class);
		if (cs == null) {
			throw new ConversionServiceNotAvailableException();
		}

		return cs.convert(value, targetType);
	}

	/**
	 * Convert a given value to a collection of target type values.
	 * 
	 * @param value the value to convert
	 * @param targetType the target element type
	 * @param flatten if the collection shall be flattened, i.e. if it in turn
	 *            contains a collection instead the converted values are added
	 * @return the collection of converted values, an empty list if the value
	 *         was <code>null</code> or an empty collection
	 * @throws ConversionException if the conversion fails
	 */
	public static <T> List<T> getAsList(Object value, Class<T> targetType, boolean flatten)
			throws ConversionException {
		List<T> result = new ArrayList<T>();

		if (value instanceof Collection<?>) {
			// convert each entry and return the list of converted elements
			for (Object element : (Collection<?>) value) {
				if (flatten && element instanceof Collection<?>) {
					// collect all values from elements
					result.addAll(getAsList(element, targetType, flatten));
				}
				else {
					// try to convert the collection element
					result.add(getAs(element, targetType));
				}
			}
		}
		else if (value != null) {
			// convert the value and add it to the list
			result.add(getAs(value, targetType));
		}

		return result;
	}

}
