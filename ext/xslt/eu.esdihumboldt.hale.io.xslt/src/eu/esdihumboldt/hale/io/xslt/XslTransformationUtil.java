/*
 * Copyright (c) 2012 Fraunhofer IGD
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
 *     Fraunhofer IGD
 */

package eu.esdihumboldt.hale.io.xslt;

import java.io.OutputStream;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.event.EventCartridge;

import eu.esdihumboldt.hale.io.gml.writer.internal.IndentingXMLStreamWriter;
import eu.esdihumboldt.hale.io.xslt.internal.FailOnInvalidReference;

/**
 * Utility methods that may be useful in {@link XslTransformation}s.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public abstract class XslTransformationUtil {

	/**
	 * Setup a XML writer configured with the namespace prefixes.
	 * 
	 * @param outStream the output stream to write the XML content to
	 * @param namespaces the namespace context, e.g. as retrieved from a
	 *            {@link XsltGenerationContext}
	 * @return the XML stream writer
	 * @throws XMLStreamException if an error occurs setting up the writer
	 */
	public static XMLStreamWriter setupXMLWriter(OutputStream outStream, NamespaceContext namespaces)
			throws XMLStreamException {
		// create and set-up a writer
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		// will set namespaces if these not set explicitly
		outputFactory.setProperty("javax.xml.stream.isRepairingNamespaces", //$NON-NLS-1$
				Boolean.valueOf(true));
		// create XML stream writer with UTF-8 encoding
		XMLStreamWriter tmpWriter = outputFactory.createXMLStreamWriter(outStream, "UTF-8"); //$NON-NLS-1$

		tmpWriter.setNamespaceContext(namespaces);

		return new IndentingXMLStreamWriter(tmpWriter);
	}

	/**
	 * Create a new {@link VelocityContext} that lets template merging fail if
	 * an invalid reference is encountered.
	 * 
	 * @return the velocity context
	 */
	public static VelocityContext createStrictVelocityContext() {
		VelocityContext context = new VelocityContext();
		EventCartridge eventCartridge = new EventCartridge();
		eventCartridge.addEventHandler(new FailOnInvalidReference());
		context.attachEventCartridge(eventCartridge);
		return context;
	}

}
