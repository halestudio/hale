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

package eu.esdihumboldt.hale.common.convert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConversionService;

import eu.esdihumboldt.hale.common.core.HalePlatform;

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

		ConversionService cs = HalePlatform.getService(ConversionService.class);
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
