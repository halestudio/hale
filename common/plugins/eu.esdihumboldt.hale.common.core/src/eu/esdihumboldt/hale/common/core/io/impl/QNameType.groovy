

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

package eu.esdihumboldt.hale.common.core.io.impl;

import javax.xml.namespace.QName
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

import org.w3c.dom.Document
import org.w3c.dom.Element

import eu.esdihumboldt.hale.common.core.io.ComplexValueType
import eu.esdihumboldt.hale.common.core.io.HaleIO
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode

/**
 * XML serialization for {@link QName}.
 * 
 * @author Simon Templer
 */
@CompileStatic
public class QNameType extends AbstractGroovyValueJson<QName, Void>
implements ComplexValueType<QName, Void> {

	/**
	 * The name of the attribute holding the namespace.
	 */
	public static String ATTRIBUTE_NS = "namespace";

	@Override
	public QName fromDOM(Element fragment, Void context) {
		String ns = null;
		if (fragment.hasAttribute(ATTRIBUTE_NS)) {
			ns = fragment.getAttribute(ATTRIBUTE_NS);
		}

		if (ns == null) {
			return new QName(fragment.getTextContent());
		}
		else {
			return new QName(ns, fragment.getTextContent());
		}
	}

	@Override
	public Element toDOM(QName name) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		try {
			Document doc = dbf.newDocumentBuilder().newDocument();

			Element result = doc.createElementNS(HaleIO.NS_HALE_CORE, "name");
			result.setPrefix("core");

			if (name.getNamespaceURI() != null && !name.getNamespaceURI().isEmpty()) {
				result.setAttribute(ATTRIBUTE_NS, name.getNamespaceURI());
			}

			result.setTextContent(name.getLocalPart());

			return result;
		} catch (ParserConfigurationException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public Class<Void> getContextType() {
		return Void.class;
	}

	@Override
	@CompileStatic(TypeCheckingMode.SKIP)
	public QName fromJson(Object json, Void context) {
		// can be a string (no namespace) or a map
		if (!(json instanceof Map)) {
			new QName(json.toString())
		}
		else {
			def ns = json[ATTRIBUTE_NS]
			if (ns) {
				new QName(ns, json.name)
			}
			else {
				new QName(json.name)
			}
		}
	}

	@Override
	public Object toJson(QName value) {
		if (value.getNamespaceURI()) {
			[name: value.localPart, (ATTRIBUTE_NS): value.namespaceURI]
		}
		else {
			value.localPart
		}
	}
}
