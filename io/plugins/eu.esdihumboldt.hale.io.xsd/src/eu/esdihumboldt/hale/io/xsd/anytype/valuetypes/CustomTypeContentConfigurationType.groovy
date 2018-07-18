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

import org.w3c.dom.Element;

import eu.esdihumboldt.hale.common.core.io.ComplexValueType;
import eu.esdihumboldt.hale.common.core.io.DOMValueUtil;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.ValueList;
import eu.esdihumboldt.hale.io.xsd.XMLSchemaIO;
import eu.esdihumboldt.hale.io.xsd.anytype.CustomTypeContentAssociation;
import eu.esdihumboldt.hale.io.xsd.anytype.CustomTypeContentConfiguration;
import eu.esdihumboldt.util.groovy.xml.NSDOMBuilder;
import groovy.transform.CompileStatic

/**
 * Complex value definition for {@link CustomTypeContentConfiguration}.
 * 
 * @author Simon Templer
 */
@CompileStatic
public class CustomTypeContentConfigurationType implements ComplexValueType<CustomTypeContentConfiguration, Void> {

	@Override
	public CustomTypeContentConfiguration fromDOM(Element fragment, Void context) {
		Value val = DOMValueUtil.fromTag(fragment);
		ValueList list = val.as(ValueList.class);
		List<CustomTypeContentAssociation> resultList = new ArrayList<>();
		if (list != null) {
			for (Value value : list) {
				CustomTypeContentAssociation assoc = value.as(CustomTypeContentAssociation.class);
				if (assoc != null) {
					resultList.add(assoc);
				}
			}
		}
		return new CustomTypeContentConfiguration(associations: resultList);
	}

	@Override
	public Element toDOM(CustomTypeContentConfiguration value) {
		ValueList list = new ValueList();
		for (CustomTypeContentAssociation assoc : value.getAssociations()) {
			list.add(Value.complex(assoc));
		}

		Map<String, String> prefixes = new HashMap<>();
		prefixes.put("xsd", XMLSchemaIO.NS_HALE_XSD);
		NSDOMBuilder builder;
		try {
			builder = NSDOMBuilder.newBuilder(prefixes);
			Element element = DOMValueUtil.valueTag(builder, "xsd:typeContentConfig",
					Value.complex(list));
			return element;
		} catch (Exception e) {
			throw new IllegalStateException("Error creating validator DOM representation", e);
		}
	}

	@Override
	public Class<? extends Void> getContextType() {
		return Void.class;
	}
}
