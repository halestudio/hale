/*
 * Copyright (c) 2016 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.html.svg.mapping.json;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.stax.StAXSource;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.odysseus.staxon.json.JsonXMLConfig;
import de.odysseus.staxon.json.JsonXMLConfigBuilder;
import de.odysseus.staxon.json.JsonXMLInputFactory;
import de.odysseus.staxon.json.JsonXMLOutputFactory;
import de.odysseus.staxon.json.stream.impl.JsonStreamFactoryImpl;
import de.odysseus.staxon.xml.util.PrettyXMLStreamWriter;

@SuppressWarnings("javadoc")
public class JsonXML {

	public static void toJson(Reader xmlReader, Writer jsonWriter)
			throws XMLStreamException, FactoryConfigurationError, TransformerConfigurationException,
			TransformerException, TransformerFactoryConfigurationError {
		/*
		 * If we want to insert JSON array boundaries for multiple elements, we
		 * need to set the <code>autoArray</code> property. If our XML source
		 * was decorated with <code>&lt;?xml-multiple?&gt;</code> processing
		 * instructions, we'd set the <code>multiplePI</code> property instead.
		 * With the <code>autoPrimitive</code> property set, element text gets
		 * automatically converted to JSON primitives (number, boolean, null).
		 */
		JsonXMLConfig config = new JsonXMLConfigBuilder().namespaceDeclarations(true)
				.autoArray(true).autoPrimitive(true).prettyPrint(false).build();
		/*
		 * Create source (XML).
		 */
		XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(xmlReader);
		Source source = new StAXSource(reader);

		/*
		 * Create result (JSON).
		 */
		// create stream factory manually due to class loading issues
		XMLStreamWriter writer = new JsonXMLOutputFactory(config, new JsonStreamFactoryImpl())
				.createXMLStreamWriter(jsonWriter);
		Result result = new StAXResult(writer);

		/*
		 * Copy source to result via "identity transform".
		 */
		TransformerFactory.newInstance().newTransformer().transform(source, result);
	}

	public static void toXML(Reader jsonReader, Writer xmlWriter)
			throws XMLStreamException, FactoryConfigurationError, TransformerConfigurationException,
			TransformerException, TransformerFactoryConfigurationError {
		/*
		 * If the <code>multiplePI</code> property is set to <code>true</code>,
		 * the StAXON reader will generate <code>&lt;xml-multiple&gt;</code>
		 * processing instructions which would be copied to the XML output.
		 * These can be used by StAXON when converting back to JSON to trigger
		 * array starts. Set to <code>false</code> if you don't need to go back
		 * to JSON.
		 */
		JsonXMLConfig config = new JsonXMLConfigBuilder().multiplePI(false).build();
		/*
		 * Create source (JSON).
		 */
		XMLStreamReader reader = new JsonXMLInputFactory(config, new JsonStreamFactoryImpl())
				.createXMLStreamReader(jsonReader);
		Source source = new StAXSource(reader);

		/*
		 * Create result (XML).
		 */
		XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(xmlWriter);
		Result result = new StAXResult(new PrettyXMLStreamWriter(writer)); // format
																			// output

		/*
		 * Copy source to result via "identity transform".
		 */
		TransformerFactory.newInstance().newTransformer().transform(source, result);
	}

	public static Document toDOM(Reader jsonReader)
			throws XMLStreamException, FactoryConfigurationError, TransformerConfigurationException,
			TransformerException, TransformerFactoryConfigurationError,
			ParserConfigurationException, IOException, SAXException {
		/*
		 * Sadly not working like this - we get a NullPointerException because
		 * during the transformation setXmlVersion is called with null
		 */
//        JsonXMLConfig config = new JsonXMLConfigBuilder().multiplePI(false).build();
//        // Create source (JSON).
//        XMLStreamReader reader = new JsonXMLInputFactory(config, new JsonStreamFactoryImpl()).createXMLStreamReader(jsonReader);
//        Source source = new StAXSource(reader);
//        // Create result (DOM).
//        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//        Document doc = builder.newDocument();
//        Result result = new DOMResult(doc);
//        // Copy source to result via "identity transform".
//        TransformerFactory.newInstance().newTransformer().transform(source, result);
//        return doc;

		// XXX instead use the less tidy way via a string...
		StringWriter xmlWriter = new StringWriter();
		try {
			toXML(jsonReader, xmlWriter);
		} finally {
			xmlWriter.close();
		}
		// parse the document from the string
		String xmlString = xmlWriter.toString();
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		try (ByteArrayInputStream in = new ByteArrayInputStream(
				xmlString.getBytes(StandardCharsets.UTF_8))) {
			return builder.parse(in);
		}
	}

}