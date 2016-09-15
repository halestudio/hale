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
import eu.esdihumboldt.hale.io.xsd.anytype.CustomTypeContentAssociation
import eu.esdihumboldt.hale.io.xsd.anytype.CustomTypeContentMode
import eu.esdihumboldt.util.groovy.xml.NSDOMBuilder
import eu.esdihumboldt.util.groovy.xml.NSDOMCategory
import groovy.transform.CompileStatic

/**
 * Complex value definition for {@link CustomTypeContentAssociation}.
 * 
 * @author Simon Templer
 */
@CompileStatic
public class CustomTypeContentAssociationType implements
ComplexValueType<CustomTypeContentAssociation, Void> {

	@Override
	CustomTypeContentAssociation fromDOM(Element fragment, Void context) {
		List<QName> property = null
		Element propertyElement = NSDOMCategory.firstChild(fragment, XMLSchemaIO.NS_HALE_XSD, 'property')
		if (propertyElement != null) {
			Value propertyValue = DOMValueUtil.fromTag(propertyElement)
			ValueList vl = propertyValue.as(ValueList)
			if (vl) {
				for (Value value : vl) {
					QName name = value.as(QName)
					if (name) {
						if (property == null) {
							property = new ArrayList<>()
						}
						property.add(name)
					}
				}
			}
		}

		CustomTypeContent config = null
		Element configElement = NSDOMCategory.firstChild(fragment, XMLSchemaIO.NS_HALE_XSD, 'config')
		if (configElement != null) {
			Value configValue = DOMValueUtil.fromTag(configElement)
			config = configValue.as(CustomTypeContent)
		}
		if (config == null) {
			// default
			config = new CustomTypeContent(CustomTypeContentMode.simple, new ArrayList<>())
		}

		new CustomTypeContentAssociation(property, config)
	}

	@Override
	Element toDOM(CustomTypeContentAssociation value) {
		ValueList propertyList = new ValueList()
		for (javax.xml.namespace.QName name : value.property) {
			propertyList.add(Value.complex(name));
		}

		def builder = NSDOMBuilder.newBuilder(xsd: XMLSchemaIO.NS_HALE_XSD)

		Element fragment = builder('xsd:association') {
			DOMValueUtil.valueTag(builder, 'xsd:property', Value.complex(propertyList))
			DOMValueUtil.valueTag(builder, 'xsd:config', Value.complex(value.config))
		}

		fragment
	}

	@Override
	Class<? extends Void> getContextType() {
		Void
	}
}
