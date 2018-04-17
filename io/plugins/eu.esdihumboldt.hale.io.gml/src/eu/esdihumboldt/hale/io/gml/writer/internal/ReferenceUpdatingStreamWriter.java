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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.springframework.util.StringUtils;

/**
 * Prefix-aware stream writer that updates local links in a specified attribute.
 * If the attribute is not explicitly named, all attributes with the local part
 * <code>"href"</code> will be updated.
 * 
 * @author Florian Esser
 */
public class ReferenceUpdatingStreamWriter extends PrefixAwareStreamWriterDecorator {

	private final ReferenceUpdater updater;
	private final QName attribute;

	/**
	 * Create the writer.
	 * 
	 * @param decoratee Writer to decorate
	 * @param updater Reference updater to use
	 */
	public ReferenceUpdatingStreamWriter(XMLStreamWriter decoratee, ReferenceUpdater updater) {
		super(decoratee);
		this.updater = updater;
		this.attribute = new QName("href");
	}

	/**
	 * Create the writer that updates the specified attribute. If the attribute
	 * to update contains only a local part (i.e. no prefix and namespace are
	 * specified), all attributes with that local part will be updated.
	 * 
	 * @param decoratee Writer to decorate
	 * @param updater Reference updater to use
	 * @param attributeToUpdate The attribute to update.
	 */
	public ReferenceUpdatingStreamWriter(XMLStreamWriter decoratee, ReferenceUpdater updater,
			QName attributeToUpdate) {
		super(decoratee);
		this.updater = updater;

		if (attributeToUpdate.getLocalPart() == null
				|| attributeToUpdate.getLocalPart().trim().isEmpty()) {
			throw new IllegalArgumentException(
					"Local part of attribute to update must not be empty");
		}
		this.attribute = attributeToUpdate;
	}

	/**
	 * @see eu.esdihumboldt.hale.io.gml.writer.internal.XMLStreamWriterDecorator#writeAttribute(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void writeAttribute(String prefix, String namespaceURI, String localName, String value)
			throws XMLStreamException {
		if (!isMatchingAttribute(prefix, namespaceURI, localName)) {
			super.writeAttribute(prefix, namespaceURI, localName, value);
			return;
		}

		value = updater.updateReference(value);
		super.writeAttribute(prefix, namespaceURI, localName, value);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.gml.writer.internal.XMLStreamWriterDecorator#writeAttribute(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public void writeAttribute(String namespaceURI, String localName, String value)
			throws XMLStreamException {
		if (!isMatchingAttribute(null, namespaceURI, localName)) {
			super.writeAttribute(namespaceURI, localName, value);
			return;
		}

		value = updater.updateReference(value);
		super.writeAttribute(namespaceURI, localName, value);
	}

	private boolean isMatchingAttribute(String prefix, String namespaceUri, String localName) {
		boolean result = true;
		if (StringUtils.hasText(attribute.getPrefix())) {
			result &= attribute.getPrefix().equals(prefix);
		}
		if (StringUtils.hasText(attribute.getNamespaceURI())) {
			result &= attribute.getNamespaceURI().equals(namespaceUri);
		}

		result &= attribute.getLocalPart().equals(localName);

		return result;
	}
}
