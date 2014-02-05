/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.schema.model.validate.factory.internal;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

import eu.esdihumboldt.hale.common.core.io.ComplexValueType;
import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.impl.ValueListType;
import eu.esdihumboldt.hale.common.schema.model.validate.factory.ValidatorValue;
import eu.esdihumboldt.util.groovy.xml.NSDOMBuilder;

/**
 * Complex value descriptor for {@link ValidatorValue}.
 * 
 * @author Simon Templer
 */
public class ValidatorValueType implements ComplexValueType<ValidatorValue, Void> {

	/**
	 * Name of the attribute holding the validator type identifier.
	 */
	public static final String ATTRIBUTE_TYPE = "type";

	@Override
	public ValidatorValue fromDOM(Element fragment, Void context) {
		Value val = ValueListType.fromTag(fragment);
		String id = fragment.getAttribute(ATTRIBUTE_TYPE);
		return new ValidatorValue(id, val);
	}

	@Override
	public Element toDOM(ValidatorValue value) {
		Map<String, String> prefixes = new HashMap<>();
		prefixes.put("core", HaleIO.NS_HALE_CORE);
		NSDOMBuilder builder;
		try {
			builder = NSDOMBuilder.newInstance(prefixes);
			Element element = ValueListType.valueTag(builder, "core:validator",
					value.getValidatorRepresentation());
			element.setAttribute(ATTRIBUTE_TYPE, value.getType());
			return element;
		} catch (Exception e) {
			throw new IllegalStateException("Error creating validator DOM representation", e);
		}
	}

	@Override
	public Class<Void> getContextType() {
		return Void.class;
	}

}
