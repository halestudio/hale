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

package eu.esdihumboldt.hale.io.gml.writer.internal;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * This class is a pretty print wrapper for XMLStreamWriter.
 * 
 * @author <a href="mailto:tonnhofer@lat-lon.de">Oliver Tonnhofer</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class IndentingXMLStreamWriter implements XMLStreamWriter {

	private final String indent;

	private final XMLStreamWriter s;

	private int level = 0;

	private boolean lastWasStart = false;

	/**
	 * Create a new {@link IndentingXMLStreamWriter} instance with default
	 * indentation and no whitespace stripping.
	 * 
	 * @param xmlStreamWriter the internal writer
	 */
	public IndentingXMLStreamWriter(XMLStreamWriter xmlStreamWriter) {
		this.s = xmlStreamWriter;
		this.indent = "  "; //$NON-NLS-1$
	}

	/**
	 * Create a new {@link IndentingXMLStreamWriter} instance with the specified
	 * indentation and whitespace stripping policy.
	 * 
	 * @param xmlWriter the internal writer
	 * @param indent the indent string for each indent level, must not be
	 *            <code>null</code>
	 */
	public IndentingXMLStreamWriter(XMLStreamWriter xmlWriter, String indent) {
		this.s = xmlWriter;
		this.indent = indent;
	}

	@Override
	public void close() throws XMLStreamException {
		s.close();
	}

	@Override
	public void flush() throws XMLStreamException {
		s.flush();
	}

	@Override
	public NamespaceContext getNamespaceContext() {
		return s.getNamespaceContext();
	}

	@Override
	public String getPrefix(String uri) throws XMLStreamException {
		return s.getPrefix(uri);
	}

	@Override
	public Object getProperty(String name) throws IllegalArgumentException {
		return s.getProperty(name);
	}

	@Override
	public void setDefaultNamespace(String uri) throws XMLStreamException {
		s.setDefaultNamespace(uri);
	}

	@Override
	public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
		s.setNamespaceContext(context);
	}

	@Override
	public void setPrefix(String prefix, String uri) throws XMLStreamException {
		s.setPrefix(prefix, uri);
	}

	@Override
	public void writeAttribute(String localName, String value) throws XMLStreamException {
		s.writeAttribute(localName, value);
	}

	@Override
	public void writeAttribute(String namespaceURI, String localName, String value)
			throws XMLStreamException {
		s.writeAttribute(namespaceURI, localName, value);
	}

	@Override
	public void writeAttribute(String prefix, String namespaceURI, String localName, String value)
			throws XMLStreamException {
		s.writeAttribute(prefix, namespaceURI, localName, value);
	}

	@Override
	public void writeCData(String data) throws XMLStreamException {
		s.writeCData(data);
	}

	@Override
	public void writeCharacters(String text) throws XMLStreamException {
		s.writeCharacters(text);
	}

	@Override
	public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
		s.writeCharacters(text, start, len);
	}

	@Override
	public void writeComment(String data) throws XMLStreamException {
		indent();
		s.writeComment(data);
		unindent();
	}

	@Override
	public void writeDTD(String dtd) throws XMLStreamException {
		s.writeDTD(dtd);
	}

	@Override
	public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {
		s.writeDefaultNamespace(namespaceURI);
	}

	@Override
	public void writeEmptyElement(String localName) throws XMLStreamException {
		indent();
		s.writeEmptyElement(localName);
		unindent();
	}

	@Override
	public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
		indent();
		s.writeEmptyElement(namespaceURI, localName);
		unindent();
	}

	@Override
	public void writeEmptyElement(String prefix, String localName, String namespaceURI)
			throws XMLStreamException {
		indent();
		s.writeEmptyElement(prefix, localName, namespaceURI);
		unindent();
	}

	@Override
	public void writeEntityRef(String name) throws XMLStreamException {
		s.writeEntityRef(name);
	}

	@Override
	public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {
		s.writeNamespace(prefix, namespaceURI);
	}

	@Override
	public void writeProcessingInstruction(String target) throws XMLStreamException {
		s.writeProcessingInstruction(target);
	}

	@Override
	public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
		s.writeProcessingInstruction(target, data);
	}

	@Override
	public void writeStartDocument() throws XMLStreamException {
		s.writeStartDocument();
		s.writeCharacters("\n"); //$NON-NLS-1$
	}

	@Override
	public void writeStartDocument(String version) throws XMLStreamException {
		s.writeStartDocument(version);
		s.writeCharacters("\n"); //$NON-NLS-1$
	}

	@Override
	public void writeStartDocument(String encoding, String version) throws XMLStreamException {
		s.writeStartDocument(encoding, version);
		s.writeCharacters("\n"); //$NON-NLS-1$
	}

	@Override
	public void writeStartElement(String localName) throws XMLStreamException {
		indent();
		s.writeStartElement(localName);
	}

	@Override
	public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
		indent();
		s.writeStartElement(namespaceURI, localName);
	}

	@Override
	public void writeStartElement(String prefix, String localName, String namespaceURI)
			throws XMLStreamException {
		indent();
		s.writeStartElement(prefix, localName, namespaceURI);
	}

	@Override
	public void writeEndDocument() throws XMLStreamException {
		s.writeEndDocument();
		s.writeCharacters("\n"); //$NON-NLS-1$
	}

	@Override
	public void writeEndElement() throws XMLStreamException {
		unindent();
		s.writeEndElement();
	}

	private final void unindent() throws XMLStreamException {
		level -= 1;
		if (!lastWasStart) {
			writeIndent(level);
		}
		if (level == 0) {
			s.writeCharacters("\n"); //$NON-NLS-1$
		}
		lastWasStart = false;
	}

	private final void indent() throws XMLStreamException {
		lastWasStart = true;
		writeIndent(level);
		level += 1;
	}

	private final void writeIndent(int level) throws XMLStreamException {
		if (level > 0) {
			StringBuilder b = new StringBuilder(level + 1);
			b.append('\n');
			for (int i = 0; i < level; i++) {
				b.append(indent);
			}
			s.writeCharacters(b.toString());
		}
	}
}
