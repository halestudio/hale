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

package eu.esdihumboldt.hale.io.codelist.xml.reader;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.codelist.CodeList;

/**
 * Reads an XML based code list
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class XmlCodeList implements CodeList {

	private static final ALogger log = ALoggerFactory.getLogger(XmlCodeList.class);

	private static final DocumentBuilderFactory builderFactory = DocumentBuilderFactory
			.newInstance();

	private static final XPathFactory xpathFactory = XPathFactory.newInstance();

	private static final String DEF_NS = "http://www.opengis.net/gml"; //$NON-NLS-1$

	private final String identifier;

	private String namespace;

	private String description;

	private final URI location;

	private final Map<String, CodeEntry> entriesByName = new LinkedHashMap<String, CodeEntry>();

	private final Map<String, CodeEntry> entriesByIdentifier = new LinkedHashMap<String, CodeEntry>();

	/**
	 * Create a code list from an XML document
	 * 
	 * @param in the input stream of the XML document
	 * @param location the code list location
	 * @throws Exception if creating the code list fails
	 */
	public XmlCodeList(InputStream in, URI location) throws Exception {
		this.location = location;

		try {
			DocumentBuilder builder = builderFactory.newDocumentBuilder();

			// don't resolve DTDs - else loading the document might fail without
			// internet connection
			builder.setEntityResolver(new EntityResolver() {

				@Override
				public InputSource resolveEntity(String publicId, String systemId)
						throws SAXException, IOException {
					return new InputSource(new StringReader("")); //$NON-NLS-1$
				}
			});

			Document doc = builder.parse(in);

			XPath xpath = xpathFactory.newXPath();
			String id = null;
			// determine identifier
			try {
				Node identifier = ((NodeList) xpath.evaluate("Dictionary/identifier", doc, //$NON-NLS-1$
						XPathConstants.NODESET)).item(0);
				this.namespace = identifier.getAttributes().getNamedItem("codeSpace") //$NON-NLS-1$
						.getTextContent();
				id = identifier.getTextContent();
			} catch (Throwable e) {
				// ignore
			}

			if (id == null) {
				try {
					Node identifier = ((NodeList) xpath.evaluate("Dictionary/name", doc, //$NON-NLS-1$
							XPathConstants.NODESET)).item(0);
					id = identifier.getTextContent();
				} catch (Throwable e) {
					// ignore
				}
			}

			if (id == null) {
				id = location.getPath();
				int idx = id.lastIndexOf("/"); //$NON-NLS-1$
				if (idx >= 0 && idx + 1 < id.length()) {
					id = id.substring(idx + 1);
				}
			}

			this.identifier = id;

			// determine description
			try {
				Node description = ((NodeList) xpath.evaluate("Dictionary/description", doc, //$NON-NLS-1$
						XPathConstants.NODESET)).item(0);
				this.description = description.getTextContent();
			} catch (Throwable e) {
				// is optional
				this.description = null;
			}

			// determine entries
			NodeList entries = (NodeList) xpath.evaluate("Dictionary/dictionaryEntry/Definition", //$NON-NLS-1$
					doc, XPathConstants.NODESET);
			addEntries(entries);

			if (this.namespace == null) {
				// use default namespace
				this.namespace = DEF_NS;
			}
		} catch (Exception e) {
			log.error("Error determinating type name(s)", e); //$NON-NLS-1$
			throw e;
		}
	}

	private void addEntries(NodeList entries) {
		for (int i = 0; i < entries.getLength(); i++) {
			Node node = entries.item(i);
			NodeList children = node.getChildNodes();

			String description = null;
			String identifier = null;
			String namespace = null;
			String name = null;

			for (int j = 0; j < children.getLength(); j++) {
				Node child = children.item(j);

				if (child.getNodeName().endsWith("description")) { //$NON-NLS-1$
					description = child.getTextContent();
				}
				else if (child.getNodeName().endsWith("identifier")) { //$NON-NLS-1$
					identifier = child.getTextContent();
					try {
						namespace = child.getAttributes().getNamedItem("codeSpace") //$NON-NLS-1$
								.getTextContent();
					} catch (Exception e) {
						// optional
						namespace = null;
					}
				}
				else if (child.getNodeName().endsWith("name")) { //$NON-NLS-1$
					name = child.getTextContent();

					if (identifier == null
							&& child.getAttributes().getNamedItem("codeSpace") != null) {
						// compensate a missing identifier if a name w/
						// codeSpace is there
						identifier = child.getTextContent();
						try {
							namespace = child.getAttributes().getNamedItem("codeSpace") //$NON-NLS-1$
									.getTextContent();
						} catch (Exception e) {
							// optional
						}
					}
				}
			}

			if (this.namespace == null) {
				if (namespace == null) {
					// use a default namespace
					this.namespace = DEF_NS;
				}
				else {
					this.namespace = namespace;
				}
			}

			if (namespace == null) {
				namespace = this.namespace;
			}

			if (identifier != null) {
				if (name == null) {
					// default name to identifier if no name is provided
					name = identifier;
				}

				CodeEntry entry = new CodeEntry(name, description, identifier, namespace);
				this.entriesByName.put(name, entry);
				this.entriesByIdentifier.put(identifier, entry);
			}
		}
	}

	/**
	 * @see CodeList#getEntries()
	 */
	@Override
	public Collection<CodeEntry> getEntries() {
		return new ArrayList<CodeEntry>(entriesByIdentifier.values());
	}

	/**
	 * @see CodeList#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * @see CodeList#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * @see CodeList#getNamespace()
	 */
	@Override
	public String getNamespace() {
		return namespace;
	}

	/**
	 * @see CodeList#getEntryByName(String)
	 */
	@Override
	public CodeEntry getEntryByName(String name) {
		return entriesByName.get(name);
	}

	/**
	 * @see CodeList#getEntryByIdentifier(String)
	 */
	@Override
	public CodeEntry getEntryByIdentifier(String identifier) {
		return entriesByIdentifier.get(identifier);
	}

	/**
	 * @see CodeList#getLocation()
	 */
	@Override
	public URI getLocation() {
		return location;
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
		return result;
	}

	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		XmlCodeList other = (XmlCodeList) obj;
		if (identifier == null) {
			if (other.identifier != null)
				return false;
		}
		else if (!identifier.equals(other.identifier))
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		}
		else if (!location.equals(other.location))
			return false;
		if (namespace == null) {
			if (other.namespace != null)
				return false;
		}
		else if (!namespace.equals(other.namespace))
			return false;
		return true;
	}

}
