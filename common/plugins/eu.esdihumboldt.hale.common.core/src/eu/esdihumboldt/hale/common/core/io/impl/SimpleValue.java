/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.common.core.io.impl;

import org.springframework.core.convert.ConversionService;
import org.w3c.dom.Element;

import eu.esdihumboldt.hale.common.core.HalePlatform;
import eu.esdihumboldt.hale.common.core.io.Value;

/**
 * Simple value represented as string.
 * 
 * @author Simon Templer
 */
public class SimpleValue extends Value {

	private static final long serialVersionUID = 6992194329380349404L;

	private final Object value;

	private final String strValue;

	/**
	 * Create a new simple value.
	 * 
	 * @param value the value
	 */
	public SimpleValue(Object value) {
		super();
		this.value = value;

		if (value == null) {
			strValue = null;
		}
		else {
			String tmpValue = null;
			ConversionService cs = HalePlatform.getService(ConversionService.class);
			if (cs != null) {
				try {
					tmpValue = cs.convert(value, String.class);
				} catch (Exception e) {
					// ignore
				}
			}

			if (tmpValue == null) {
				tmpValue = value.toString();
			}

			strValue = tmpValue;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T as(Class<T> expectedType) {
		if (value == null) {
			return null;
		}

		if (expectedType.isAssignableFrom(value.getClass())) {
			return (T) value;
		}

		if (expectedType.isAssignableFrom(String.class)) {
			return (T) strValue;
		}

		// conversion using service
		ConversionService cs = HalePlatform.getService(ConversionService.class);
		if (cs != null) {
			try {
				return cs.convert(value, expectedType);
			} catch (Exception e) {
				// ignore
			}
		}

		return null;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public boolean isRepresentedAsDOM() {
		return false;
	}

	@Override
	public Element getDOMRepresentation() {
		return null;
	}

	@Override
	public String getStringRepresentation() {
		return strValue;
	}

	@Override
	public boolean isEmpty() {
		return value == null || strValue == null || strValue.isEmpty();
	}

	@Override
	public String toString() {
		return getStringRepresentation();
	}

	@Override
	public <T> T as(Class<T> expectedType, T defValue) {
		T value = as(expectedType);
		if (value == null)
			return defValue;
		return value;
	}

}
