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

/**
 * Value based on a DOM {@link Element}.
 * 
 * @author Simon Templer
 */
public class ElementValue extends Value {

	private final Element element;

	private final Object value;

	/**
	 * Create a value based on a DOM element.
	 * 
	 * @param element the element
	 */
	public ElementValue(Element element) {
		super();
		this.element = element;
		// create the default value
		this.value = HaleIO.getComplexValue(element);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T as(Class<T> expectedType) {
		if (element == null)
			return null;

		if (expectedType.isAssignableFrom(Element.class)) {
			// return element as-is
			return (T) element;
		}

		if (expectedType.isAssignableFrom(value.getClass())) {
			// return element as-is
			return (T) value;
		}

		return HaleIO.getComplexValue(element, expectedType);
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
		return element;
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
