/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
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
