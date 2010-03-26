/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.rcp.utils.codelist.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import eu.esdihumboldt.hale.rcp.utils.codelist.CodeList;

/**
 * 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class XmlCodeList implements CodeList {
	
	private static final Logger log = Logger.getLogger(XmlCodeList.class);
	
	private static final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
	
	private static final XPathFactory xpathFactory = XPathFactory.newInstance();
	
	private final String identifier;
	
	private final String namespace;
	
	private String description;
	
	private final URI location;
	
	private final Map<String, CodeEntry> entries = new HashMap<String, CodeEntry>();

	/**
	 * Create a code list from an XML document
	 * 
	 * @param in the input stream of the XML document
	 * @param location the code list location
	 * @throws Exception 
	 */
	public XmlCodeList(InputStream in, URI location) throws Exception {
		this.location = location;
		
		try {
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			
			// don't resolve DTDs - else loading the document might fail without internet connection
			builder.setEntityResolver(new EntityResolver() {
	            public InputSource resolveEntity(String publicId, String systemId)
	                throws SAXException, IOException
	            {
	                return new InputSource(new StringReader(""));
	            }
	        });

			Document doc = builder.parse(in);
			
			XPath xpath = xpathFactory.newXPath();
			// determine identifier
			Node identifier = ((NodeList) xpath.evaluate("Dictionary/identifier", doc, XPathConstants.NODESET)).item(0);
			this.namespace = identifier.getAttributes().getNamedItem("codeSpace").getTextContent();
			this.identifier = identifier.getTextContent();
			
			// determine description
			try {
				Node description = ((NodeList) xpath.evaluate("Dictionary/description", doc, XPathConstants.NODESET)).item(0);
				this.description = description.getTextContent();
			}
			catch (Exception e) {
				// is optional
				this.description = null;
			}
			
			// determine entries
			NodeList entries = (NodeList) xpath.evaluate("Dictionary/dictionaryEntry/Definition", doc, XPathConstants.NODESET);
			addEntries(entries);
		} catch (Exception e) {
			log.error("Error determinating type name(s)", e);
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
				
				if (child.getNodeName().equals("description")) {
					description = child.getTextContent();
				}
				else if (child.getNodeName().equals("identifier")) {
					identifier = child.getTextContent();
					try {
						namespace = child.getAttributes().getNamedItem("codeSpace").getTextContent();
					} catch (Exception e) {
						// optional
					}
				}
				else if (child.getNodeName().equals("name")) {
					name = child.getTextContent();
				}
			}
			
			if (namespace == null) {
				namespace = this.namespace;
			}
			
			if (name != null && identifier != null) {
				CodeEntry entry = new CodeEntry(name, description, identifier, namespace);
				this.entries.put(name, entry);
			}
		}
	}

	/**
	 * @see CodeList#getEntries()
	 */
	@Override
	public Collection<CodeEntry> getEntries() {
		return new ArrayList<CodeEntry>(entries.values());
	}

	/**
	 * @see CodeList#getDescritpion()
	 */
	@Override
	public String getDescritpion() {
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
	 * @see CodeList#getEntry(String)
	 */
	@Override
	public CodeEntry getEntry(String name) {
		return entries.get(name);
	}

	/**
	 * @see CodeList#getLocation()
	 */
	@Override
	public URI getLocation() {
		return location;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((identifier == null) ? 0 : identifier.hashCode());
		result = prime * result
				+ ((location == null) ? 0 : location.hashCode());
		result = prime * result
				+ ((namespace == null) ? 0 : namespace.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
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
		} else if (!identifier.equals(other.identifier))
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (namespace == null) {
			if (other.namespace != null)
				return false;
		} else if (!namespace.equals(other.namespace))
			return false;
		return true;
	}

}
