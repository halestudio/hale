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

package eu.esdihumboldt.hale.io.gml.writer;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.io.gml.writer.internal.StreamGmlWriter;

/**
 * Wraps the output of a {@link StreamGmlWriter}.
 * 
 * @author Simon Templer
 */
public interface XmlWrapper {

	/**
	 * Configure the stream writer after the initial setup by
	 * {@link StreamGmlWriter}, but before the document is started to be
	 * written.
	 * 
	 * @param writer the stream writer
	 * @param reporter the reporter to report any messages to
	 */
	public void configure(XMLStreamWriter writer, IOReporter reporter);

	/**
	 * Start the elements that should wrap the XML/GML document.
	 * 
	 * @param writer the stream writer
	 * @param reporter the reporter to report any messages to
	 * @throws XMLStreamException if an error occurs writing the XML
	 */
	public void startWrap(XMLStreamWriter writer, IOReporter reporter) throws XMLStreamException;

	/**
	 * End the elements started in
	 * {@link #startWrap(XMLStreamWriter, IOReporter)} to yield a valid XML
	 * document.
	 * 
	 * @param writer the stream writer
	 * @param reporter the reporter to report any messages to
	 * @throws XMLStreamException if an error occurs writing the XML
	 */
	public void endWrap(XMLStreamWriter writer, IOReporter reporter) throws XMLStreamException;

}
