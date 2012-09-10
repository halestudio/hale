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

package eu.esdihumboldt.hale.common.align.transformation.function;

import org.springframework.core.convert.ConversionException;

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;

/**
 * Represents a property value for use in an {@link PropertyTransformation}.
 * 
 * @author Simon Templer
 */
public interface PropertyValue {

	/**
	 * Get the property value.
	 * 
	 * @return the property value
	 */
	public abstract Object getValue();

	/**
	 * Get the property value converted to the given type.
	 * 
	 * @param type the type the value should be converted to
	 * @return the converted property value
	 * @throws ConversionException if the conversion service is not available or
	 *             the conversion fails or is not supported
	 */
	public abstract <T> T getValueAs(Class<T> type) throws ConversionException;

	/**
	 * Get the property.
	 * 
	 * @return the property entity definition
	 */
	public abstract PropertyEntityDefinition getProperty();

}