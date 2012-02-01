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

package eu.esdihumboldt.util.validator;

import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConversionService;

import de.fhg.igd.osgi.util.OsgiUtils;

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
	 * @throws ConversionException if the object couldn't be converted to the target type
	 */
	@SuppressWarnings("unchecked")
	public <T> T getObjectAs(Object o, Class<T> type) throws ConversionException {
		if (o == null) {
			return null;
		}
		
		if (type.isAssignableFrom(o.getClass())) {
			return (T) o;
		}
		
		ConversionService cs = OsgiUtils.getService(ConversionService.class);
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
