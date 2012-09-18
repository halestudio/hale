/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.xsd;

import org.apache.ws.commons.schema.XmlSchemaAnnotated;
import org.apache.ws.commons.schema.XmlSchemaDocumentation;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Utilities and constants for XML schemas
 * 
 * @author Simon Templer
 */
public abstract class XMLSchemaIO {

//	/**
//	 * The XML schema content type ID
//	 */
//	public static final ContentType XSD_CT = ContentType.getContentType("XSD");

	/**
	 * Get the documentation from an annotated XML object
	 * 
	 * @param element the annotated element
	 * @return the description or <code>null</code>
	 */
	public static String getDescription(XmlSchemaAnnotated element) {
		if (element.getAnnotation() != null) {
			XmlSchemaObjectCollection annotationItems = element.getAnnotation().getItems();
			StringBuffer desc = new StringBuffer();
			for (int i = 0; i < annotationItems.getCount(); i++) {
				XmlSchemaObject item = annotationItems.getItem(i);
				if (item instanceof XmlSchemaDocumentation) {
					XmlSchemaDocumentation doc = (XmlSchemaDocumentation) item;
					NodeList markup = doc.getMarkup();
					for (int j = 0; j < markup.getLength(); j++) {
						Node node = markup.item(j);
						desc.append(node.getTextContent());
						desc.append('\n');
					}
				}
			}

			String description = desc.toString();
			if (!description.isEmpty()) {
				return description;
			}
		}

		return null;
	}

}
