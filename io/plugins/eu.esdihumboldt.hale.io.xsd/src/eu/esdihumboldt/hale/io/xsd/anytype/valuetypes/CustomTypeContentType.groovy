/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.xsd.anytype.valuetypes;

import javax.xml.namespace.QName

import org.w3c.dom.Element

import eu.esdihumboldt.hale.common.core.io.ComplexValueType
import eu.esdihumboldt.hale.common.core.io.DOMValueUtil
import eu.esdihumboldt.hale.common.core.io.Value
import eu.esdihumboldt.hale.common.core.io.ValueList
import eu.esdihumboldt.hale.io.xsd.XMLSchemaIO
import eu.esdihumboldt.hale.io.xsd.anytype.CustomTypeContent
import eu.esdihumboldt.hale.io.xsd.anytype.CustomTypeContentMode
import eu.esdihumboldt.util.groovy.xml.NSDOMBuilder
import eu.esdihumboldt.util.groovy.xml.NSDOMCategory
import groovy.transform.CompileStatic

/**
 * Complex value definition for {@link CustomTypeContent}.
 * 
 * @author Simon Templer
 */
@CompileStatic
public class CustomTypeContentType implements
ComplexValueType<CustomTypeContent, Void> {

	/**
	 * Name of the mode attribute.
	 */
	public static final String ATTRIBUTE_MODE = 'mode'

	@Override
	CustomTypeContent fromDOM(Element fragment, Void context) {
		String modeString = fragment.getAttribute(ATTRIBUTE_MODE);
		CustomTypeContentMode mode
		try {
			mode = Enum.valueOf(CustomTypeContentMode, modeString)
		} catch (e) {
			// default fall-back
			mode = CustomTypeContentMode.simple
		}

		List<QName> elements = new ArrayList<>()
		Element elementsElement = NSDOMCategory.firstChild(fragment, XMLSchemaIO.NS_HALE_XSD, 'elements')
		if (elementsElement != null) {
			Value propertyValue = DOMValueUtil.fromTag(elementsElement)
			ValueList vl = propertyValue.as(ValueList)
			if (vl) {
				for (Value value : vl) {
					QName name = value.as(QName)
					if (name) {
						elements.add(name)
					}
				}
			}
		}

		new CustomTypeContent(mode, elements)
	}

	@Override
	Element toDOM(CustomTypeContent value) {
		// elements to value list
		ValueList elementList = new ValueList()
		for (javax.xml.namespace.QName name : value.elements) {
			elementList.add(Value.complex(name));
		}

		def builder = NSDOMBuilder.newBuilder(xsd: XMLSchemaIO.NS_HALE_XSD)

		Element fragment = builder('xsd:typeContent') {
			// write elements
			DOMValueUtil.valueTag(builder, 'xsd:elements', Value.complex(elementList))
		}
		fragment.setAttribute(ATTRIBUTE_MODE, value.mode.name());

		fragment
	}

	@Override
	Class<? extends Void> getContextType() {
		Void
	}
}
