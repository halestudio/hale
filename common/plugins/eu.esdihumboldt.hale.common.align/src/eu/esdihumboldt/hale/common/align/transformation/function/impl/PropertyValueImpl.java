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

package eu.esdihumboldt.hale.common.align.transformation.function.impl;

import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConversionService;

import de.fhg.igd.osgi.util.OsgiUtils;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.convert.ConversionServiceNotAvailableException;

/**
 * Default {@link PropertyValue} implementation.
 * 
 * @author Simon Templer
 */
public class PropertyValueImpl implements PropertyValue {

	private final Object value;

	private final PropertyEntityDefinition property;

	/**
	 * Create a property value associated with its definition
	 * 
	 * @param value the property value
	 * @param property the property entity definition
	 */
	public PropertyValueImpl(Object value, PropertyEntityDefinition property) {
		super();
		this.value = value;
		this.property = property;
	}

	/**
	 * @see PropertyValue#getValue()
	 */
	@Override
	public Object getValue() {
		return value;
	}

	/**
	 * @see PropertyValue#getValueAs(Class)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getValueAs(Class<T> type) throws ConversionException {
		if (value == null) {
			return null;
		}

		if (type.isAssignableFrom(value.getClass())) {
			return (T) value;
		}

		ConversionService cs = OsgiUtils.getService(ConversionService.class);
		if (cs == null) {
			throw new ConversionServiceNotAvailableException();
		}
		else {
			return cs.convert(value, type);
		}
	}

	/**
	 * @see PropertyValue#getProperty()
	 */
	@Override
	public PropertyEntityDefinition getProperty() {
		return property;
	}

}