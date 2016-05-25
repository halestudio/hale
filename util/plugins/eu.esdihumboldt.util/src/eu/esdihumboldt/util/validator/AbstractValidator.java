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

package eu.esdihumboldt.util.validator;

import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

/**
 * Abstract validator implementation containing helper methods for conversion.
 * 
 * @author Kai Schwierczek
 */
public abstract class AbstractValidator implements Validator {

	/**
	 * Converts the given object to the given class if possible.
	 * 
	 * @param o the object to convert
	 * @param type the target type
	 * @return the converted object
	 * @throws ConversionException if the object couldn't be converted to the
	 *             target type
	 */
	@SuppressWarnings("unchecked")
	public <T> T getObjectAs(Object o, Class<T> type) throws ConversionException {
		if (o == null) {
			return null;
		}

		if (type.isAssignableFrom(o.getClass())) {
			return (T) o;
		}

		ConversionService cs = new DefaultConversionService();
		return cs.convert(o, type);
	}

	/**
	 * @see eu.esdihumboldt.util.validator.Validator#isAlwaysTrue()
	 */
	@Override
	public boolean isAlwaysTrue() {
		return false;
	}
}
