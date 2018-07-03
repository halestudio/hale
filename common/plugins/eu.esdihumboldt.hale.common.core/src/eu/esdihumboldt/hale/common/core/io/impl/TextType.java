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

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eu.esdihumboldt.hale.common.core.io.ComplexValueType;
import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.Text;

/**
 * Text XML serialization.
 * 
 * @author Simon Templer
 */
public class TextType implements ComplexValueType<Text, Void> {

	@Override
	public Text fromDOM(Element fragment, Void context) {
		String text = fragment.getTextContent();
		if (text != null && text.length() >= 3) {
			// unwrap
			if (text.startsWith("\n")) {
				text = text.substring(1);
			}
			if (text.endsWith("\n")) {
				text = text.substring(0, text.length() - 1);
			}
		}
		return new Text(text);
	}

	@Override
	public Element toDOM(Text value) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		try {
			Document doc = dbf.newDocumentBuilder().newDocument();

			Element result = doc.createElementNS(HaleIO.NS_HALE_CORE, "text");
			result.setPrefix("core");
			result.setAttribute("xml:space", "preserve");

			String text = value.getText();
			if (!text.isEmpty() && text.contains("\r") && !text.contains("\n")) {
				// replace deprecated Mac OS \r line endings with \n
				// TODO also replace \r\n endings with \n ?
				text = text.replaceAll("\r", "\n");
			}

			StringBuilder content = new StringBuilder();
			// wrap content in line breaks for better looks in XML
			// if it itself contains line breaks
			boolean wrap = !text.isEmpty() && text.contains("\n");
			if (wrap) {
				content.append('\n');
			}
			content.append(text);
			if (wrap) {
				content.append('\n');
			}
			result.setTextContent(content.toString());

			return result;
		} catch (ParserConfigurationException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public Class<Void> getContextType() {
		return Void.class;
	}

}
