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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.align.model;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.common.core.io.LegacyComplexValue;

/**
 * Class to represent the value of a transformation parameter.
 * 
 * @author Kai Schwierczek
 */
@Immutable
public class ParameterValue {

	/**
	 * The name of the default type of value.
	 */
	public static final String DEFAULT_TYPE = "default";

	private final String type;
	private final Object value;

	/**
	 * Constructor specifying the type and the value.
	 * 
	 * @param type the type of the value
	 * @param value the value
	 */
	public ParameterValue(String type, Object value) {
		this.type = type;
		this.value = value;
	}

	/**
	 * Constructor specifying the value only. type will be default.
	 * 
	 * @param value the value
	 */
	public ParameterValue(String value) {
		this(DEFAULT_TYPE, value);
	}

	/**
	 * Returns the type of the value. Either "default", <code>null</code> or
	 * some script id.
	 * 
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Returns the value.
	 * 
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Get the complex parameter value.
	 * 
	 * @param valueType the value type
	 * @return the complex parameter value
	 * @throws IllegalStateException if the value is of the wrong type and
	 *             fall-back to a {@link LegacyComplexValue} does not succeed or
	 *             is not possible
	 */
	@SuppressWarnings("unchecked")
	public <T> T getComplexValue(Class<T> valueType) {
		if (value == null)
			return null;
		if (valueType.isAssignableFrom(value.getClass()))
			return (T) value;
		try {
			if (String.class.isAssignableFrom(value.getClass())
					&& LegacyComplexValue.class.isAssignableFrom(valueType)) {
				LegacyComplexValue lcv = (LegacyComplexValue) valueType.newInstance();
				lcv.loadFromString(getStringValue());
				return (T) lcv;
			}
		} catch (Exception e) {
			// ignore
		}
		throw new IllegalStateException("Could not load complex value parameter.");
	}

	/**
	 * Get the parameter value as string. Uses the toString method on
	 * {@link #getValue()}.
	 * 
	 * @return the string parameter value
	 */
	public String getStringValue() {
		if (value == null)
			return null;
		return value.toString();
	}

}
