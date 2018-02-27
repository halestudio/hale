/*
 * Copyright (c) 2018 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.gml.writer.internal;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Decorator for {@link XMLStreamWriter}.
 */
public class XMLStreamWriterDecorator implements XMLStreamWriter {

	/**
	 * The decoratee
	 */
	protected final XMLStreamWriter decoratee;

	/**
	 * Create a new {@link XMLStreamWriterDecorator}.
	 * 
	 * @param decoratee the decoratee
	 */
	protected XMLStreamWriterDecorator(XMLStreamWriter decoratee) {
		this.decoratee = decoratee;
	}

	@Override
	public void writeStartElement(String localName) throws XMLStreamException {
		decoratee.writeStartElement(localName);
	}

	@Override
	public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
		decoratee.writeStartElement(namespaceURI, localName);
	}

	@Override
	public void writeStartElement(String prefix, String localName, String namespaceURI)
			throws XMLStreamException {
		decoratee.writeStartElement(prefix, localName, namespaceURI);
	}

	@Override
	public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
		decoratee.writeEmptyElement(namespaceURI, localName);
	}

	@Override
	public void writeEmptyElement(String prefix, String localName, String namespaceURI)
			throws XMLStreamException {
		decoratee.writeEmptyElement(prefix, localName, namespaceURI);
	}

	@Override
	public void writeEmptyElement(String localName) throws XMLStreamException {
		decoratee.writeEmptyElement(localName);
	}

	@Override
	public void writeEndElement() throws XMLStreamException {
		decoratee.writeEndElement();
	}

	@Override
	public void writeEndDocument() throws XMLStreamException {
		decoratee.writeEndDocument();
	}

	@Override
	public void close() throws XMLStreamException {
		decoratee.close();
	}

	@Override
	public void flush() throws XMLStreamException {
		decoratee.flush();
	}

	@Override
	public void writeAttribute(String localName, String value) throws XMLStreamException {
		decoratee.writeAttribute(localName, value);
	}

	@Override
	public void writeAttribute(String prefix, String namespaceURI, String localName, String value)
			throws XMLStreamException {
		decoratee.writeAttribute(prefix, namespaceURI, localName, value);
	}

	@Override
	public void writeAttribute(String namespaceURI, String localName, String value)
			throws XMLStreamException {
		decoratee.writeAttribute(namespaceURI, localName, value);
	}

	@Override
	public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {
		decoratee.writeNamespace(prefix, namespaceURI);
	}

	@Override
	public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {
		decoratee.writeDefaultNamespace(namespaceURI);
	}

	@Override
	public void writeComment(String data) throws XMLStreamException {
		decoratee.writeComment(data);
	}

	@Override
	public void writeProcessingInstruction(String target) throws XMLStreamException {
		decoratee.writeProcessingInstruction(target);
	}

	@Override
	public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
		decoratee.writeProcessingInstruction(target, data);
	}

	@Override
	public void writeCData(String data) throws XMLStreamException {
		decoratee.writeCData(data);
	}

	@Override
	public void writeDTD(String dtd) throws XMLStreamException {
		decoratee.writeDTD(dtd);
	}

	@Override
	public void writeEntityRef(String name) throws XMLStreamException {
		decoratee.writeEntityRef(name);
	}

	@Override
	public void writeStartDocument() throws XMLStreamException {
		decoratee.writeStartDocument();
	}

	@Override
	public void writeStartDocument(String version) throws XMLStreamException {
		decoratee.writeStartDocument(version);
	}

	@Override
	public void writeStartDocument(String encoding, String version) throws XMLStreamException {
		decoratee.writeStartDocument(encoding, version);
	}

	@Override
	public void writeCharacters(String text) throws XMLStreamException {
		decoratee.writeCharacters(text);
	}

	@Override
	public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
		decoratee.writeCharacters(text, start, len);
	}

	@Override
	public String getPrefix(String uri) throws XMLStreamException {
		return decoratee.getPrefix(uri);
	}

	@Override
	public void setPrefix(String prefix, String uri) throws XMLStreamException {
		decoratee.setPrefix(prefix, uri);
	}

	@Override
	public void setDefaultNamespace(String uri) throws XMLStreamException {
		decoratee.setDefaultNamespace(uri);
	}

	@Override
	public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
		decoratee.setNamespaceContext(context);
	}

	@Override
	public NamespaceContext getNamespaceContext() {
		return decoratee.getNamespaceContext();
	}

	@Override
	public Object getProperty(String name) throws IllegalArgumentException {
		return decoratee.getProperty(name);
	}

}
