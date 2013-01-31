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

	/**
	 * Create a value based on a DOM element.
	 * 
	 * @param element the element
	 */
	public ElementValue(Element element) {
		super();
		this.element = element;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAs(Class<T> expectedType) {
		if (element == null)
			return null;

		if (expectedType.isAssignableFrom(Element.class)) {
			// return element as-is
			return (T) element;
		}

		return HaleIO.getComplexValue(element, expectedType);
	}

	@Override
	public Object getValue() {
		// XXX should this rather be the complex value?
		return element;
	}

	@Override
	public boolean isEmpty() {
		return element == null;
	}

	@Override
	public boolean isRepresentedAsDOM() {
		return true;
	}

	@Override
	public Element getDOMReprensentation() {
		return element;
	}

	@Override
	public String getStringRepresentation() {
		return getAs(String.class);
	}

	@Override
	public <T> T getAs(Class<T> expectedType, T defValue) {
		T value = getAs(expectedType);
		if (value == null)
			return defValue;
		return value;
	}

}
