/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.core.io.impl;

import org.w3c.dom.Element;

import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.extension.ComplexValueExtension;

/**
 * Complex value represented as DOM {@link Element} as defined by the
 * {@link ComplexValueExtension}.
 * 
 * @author Simon Templer
 */
public class ComplexValue extends Value {

	private static final long serialVersionUID = -8570342467474565043L;

	private final Object value;

	/**
	 * Create a complex value.
	 * 
	 * @param value the value, must be of a type registered with the
	 *            {@link ComplexValueExtension} (though this is not checked in
	 *            the constructor)
	 */
	public ComplexValue(Object value) {
		super();
		this.value = value;
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

		// TODO if expected is string try legacy conversion?

		// TODO try conversion service?

		return null;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public boolean isEmpty() {
		return value == null;
	}

	@Override
	public boolean isRepresentedAsDOM() {
		return true;
	}

	@Override
	public Element getDOMRepresentation() {
		return HaleIO.getComplexElement(value);
	}

	@Override
	public String getStringRepresentation() {
		return as(String.class);
	}

	@Override
	public <T> T as(Class<T> expectedType, T defValue) {
		T value = as(expectedType);
		if (value == null)
			return defValue;
		return value;
	}

}
