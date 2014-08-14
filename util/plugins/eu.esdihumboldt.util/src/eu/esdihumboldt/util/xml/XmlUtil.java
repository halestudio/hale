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

package eu.esdihumboldt.util.xml;

import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Node;

/**
 * Utilities dealing with XML and DOM.
 * 
 * @author Simon Templer
 */
public class XmlUtil {

	/**
	 * Serialize a node to a String.
	 * 
	 * @param node the DOM node
	 * @param indent <code>true</code> if the output should be pretty-printed
	 * @return the XML string
	 * @throws TransformerException if the serialization fails
	 */
	public static String serialize(Node node, boolean indent) throws TransformerException {
		return serialize(new DOMSource(node), indent);
	}

	/**
	 * Serialize a XML source to a String.
	 * 
	 * @param source the XML source
	 * @param indent <code>true</code> if the output should be pretty-printed
	 * @return the XML string
	 * @throws TransformerException if the serialization fails
	 */
	public static String serialize(Source source, boolean indent) throws TransformerException {
		StringWriter writer = new StringWriter();
		serialize(source, new StreamResult(writer), indent, true);
		return writer.toString();
	}

	/**
	 * Serialize a XML source to a {@link StreamResult}.
	 * 
	 * @param source the XML source
	 * @param target the stream result to write to
	 * @param indent <code>true</code> if the output should be pretty-printed
	 * @param omitXmlDeclaration if the XML declaration should be omitted
	 * @throws TransformerException if the serialization fails
	 */
	public static void serialize(Source source, StreamResult target, boolean indent,
			boolean omitXmlDeclaration) throws TransformerException {
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, omitXmlDeclaration ? "yes"
				: "no");
		transformer.setOutputProperty(OutputKeys.INDENT, indent ? "yes" : "no");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.MEDIA_TYPE, "text/xml");
		transformer.transform(source, target);
	}

	/**
	 * Transform a Source (XML) to pretty formatted Result.
	 * 
	 * @param source the source, e.g. {@link DOMSource}
	 * @param target the target, e.g. {@link StreamResult}
	 * @throws TransformerException if the XSLT transformation for pretty
	 *             printing cannot be loaded
	 */
	public static void prettyPrint(Source source, Result target) throws TransformerException {

		StreamSource transformation = new StreamSource(
				XmlUtil.class.getResourceAsStream("prettyprint.xsl"));

		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer(transformation);

		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

		transformer.transform(source, target);
	}

}
