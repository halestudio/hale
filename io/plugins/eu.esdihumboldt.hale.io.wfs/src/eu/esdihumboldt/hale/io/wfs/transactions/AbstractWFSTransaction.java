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

package eu.esdihumboldt.hale.io.wfs.transactions;

import java.util.Map;
import java.util.Map.Entry;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.io.gml.writer.XmlWrapper;
import eu.esdihumboldt.hale.io.wfs.WFSVersion;

/**
 * Base class for WFS transactions.
 * 
 * @author Simon Templer
 */
public abstract class AbstractWFSTransaction implements XmlWrapper {

	private static final String WFS_DESIRED_PREFIX = "wfs";

	/**
	 * The WFS version that is targeted.
	 */
	protected final WFSVersion wfsVersion;

	/**
	 * @param wfsVersion the WFS version
	 */
	public AbstractWFSTransaction(WFSVersion wfsVersion) {
		super();
		this.wfsVersion = wfsVersion;
	}

	@Override
	public void configure(XMLStreamWriter writer, IOReporter reporter) {
		// try to bind namespace to wfs prefix
		String wfsPrefixNs = writer.getNamespaceContext().getNamespaceURI(WFS_DESIRED_PREFIX);
		if (wfsPrefixNs == null || wfsPrefixNs.equals(XMLConstants.NULL_NS_URI)) {
			try {
				writer.setPrefix(WFS_DESIRED_PREFIX, wfsVersion.wfsNamespace);
			} catch (XMLStreamException e) {
				reporter.warn(new IOMessageImpl("Failed to set the prefix for the WFS namespace", e));
			}
		}
	}

	@Override
	public void startWrap(XMLStreamWriter writer, IOReporter reporter) throws XMLStreamException {
		// transaction wrapper
		writer.writeStartElement(wfsVersion.wfsNamespace, "Transaction");
		writer.writeAttribute("version", wfsVersion.versionString);
		writer.writeAttribute("service", "WFS");

		// transaction name
		writer.writeStartElement(wfsVersion.wfsNamespace, getActionName());
		Map<String, String> attrs = getActionAttributes();
		if (attrs != null) {
			for (Entry<String, String> entry : attrs.entrySet()) {
				writer.writeAttribute(entry.getKey(), entry.getValue());
			}
		}

		// override me (and call super at the beginning) to extend
	}

	/**
	 * Get the local name of the element representing the action, e.g.
	 * <code>Insert</code>.
	 * 
	 * @return the action element name
	 */
	protected abstract String getActionName();

	/**
	 * Get the map of action attributes/parameters and their values.
	 * 
	 * @return attribute names mapped to values
	 */
	protected abstract Map<String, String> getActionAttributes();

	@Override
	public void endWrap(XMLStreamWriter writer, IOReporter reporter) throws XMLStreamException {
		// override me (and call super at the end) to extend

		// transaction name
		writer.writeEndElement();

		// transaction wrapper
		writer.writeEndElement();
	}

}
