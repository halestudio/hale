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
	 * 
	 * Get the property value converted to the given type or if the property
	 * value is an instance, the instance value converted to the given type
	 * 
	 * @param type the type the value should be converted to
	 * @return the converted property value or the converted instance value
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
