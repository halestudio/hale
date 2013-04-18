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
import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.event.EventCartridge;

import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.gml.writer.internal.IndentingXMLStreamWriter;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlElements;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;
import eu.esdihumboldt.hale.io.xslt.internal.FailOnInvalidReference;
import eu.esdihumboldt.hale.io.xslt.xpath.FilterToXPath;

/**
 * Utility methods that may be useful in {@link XslTransformation}s.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public abstract class XslTransformationUtil {

	/**
	 * Create a XPath statement to select instances specified by the given type
	 * entity definition.
	 * 
	 * @param ted the type entity definition
	 * @param context the context for the XPath expression, e.g. the empty
	 *            string for the document root or <code>/</code> for anywhere in
	 *            the document
	 * @param namespaces the namespace context
	 * @return the XPath expression or <code>null</code> if there are no
	 *         elements that match the type
	 */
	public static String selectInstances(TypeEntityDefinition ted, String context,
			NamespaceContext namespaces) {
		TypeDefinition type = ted.getDefinition();

		// get the XML elements associated to the type
		XmlElements elements = type.getConstraint(XmlElements.class);

		if (elements.getElements().isEmpty()) {
			/*
			 * XXX dirty hack
			 * 
			 * In CityGML 1.0 no element for AppearanceType is defined, only a
			 * property that is not detected in this way. The source route
			 * element is not known here, so we also cannot do a search based on
			 * the type. Thus for now we handle it as a special case.
			 */
			QName typeName = ted.getDefinition().getName();
			if ("http://www.opengis.net/citygml/appearance/1.0".equals(typeName.getNamespaceURI())
					&& "AppearanceType".equals(typeName.getLocalPart())) {
				// create a dummy XML element
				elements = new XmlElements();
				elements.addElement(new XmlElement(new QName(
						"http://www.opengis.net/citygml/appearance/1.0", "Appearance"), ted
						.getDefinition(), null));
			}
			else
				// XXX dirty hack end
				return null;
		}

		// XXX which elements should be used?
		// for now use all elements
		StringBuilder select = new StringBuilder();
		boolean first = true;
		for (XmlElement element : elements.getElements()) {
			if (first) {
				first = false;
			}
			else {
				select.append(" | ");
			}

			select.append(context);
			select.append('/');
			String ns = element.getName().getNamespaceURI();
			if (ns != null && !ns.isEmpty()) {
				String prefix = namespaces.getPrefix(ns);
				if (prefix != null && !prefix.isEmpty()) {
					select.append(prefix);
					select.append(':');
				}
			}
			select.append(element.getName().getLocalPart());
		}

		// filter
		if (ted.getFilter() != null) {
			String filterxpath = FilterToXPath.toXPath(ted.getDefinition(), namespaces,
					ted.getFilter());

			if (filterxpath != null && !filterxpath.isEmpty()) {
				select.insert(0, '(');
				select.append(")[");
				select.append(StringEscapeUtils.escapeXml(filterxpath));
				select.append(']');
			}
		}

		return select.toString();
	}

	/**
	 * Setup a XML writer configured with the namespace prefixes and UTF-8
	 * encoding.
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
