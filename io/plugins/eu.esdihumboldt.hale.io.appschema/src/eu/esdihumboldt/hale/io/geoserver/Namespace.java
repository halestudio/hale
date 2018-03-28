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

package eu.esdihumboldt.hale.io.geoserver;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import eu.esdihumboldt.hale.io.appschema.AppSchemaIO;

/**
 * Class representing a namespace resource.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class Namespace extends AbstractResource {

	/**
	 * "Namespace ID" attribute.
	 */
	public static final String ID = "namespaceId";
	/**
	 * "Namespace prefix" attribute.
	 */
	public static final String PREFIX = "prefix";
	/**
	 * "Namespace URI" attribute.
	 */
	public static final String URI = "uri";
	/**
	 * "Namespace is isolated" attribute.
	 */
	public static final String ISOLATED = "isIsolated";

	private static final String TEMPLATE_LOCATION = "/eu/esdihumboldt/hale/io/geoserver/template/data/namespace-template.vm";

	private static final Set<String> allowedAttributes = new HashSet<String>();

	static {
		allowedAttributes.add(ID);
		allowedAttributes.add(PREFIX);
		allowedAttributes.add(URI);
		allowedAttributes.add(ISOLATED);
	}

	private static final String ELEMENT_NAMESPACE = "namespace";
	private static final String ELEMENT_ID = "id";
	private static final String ELEMENT_PREFIX = "prefix";
	private static final String ELEMENT_URI = "uri";
	private static final String ELEMENT_ISOLATED = "isolated";

	/**
	 * Constructor.
	 * 
	 * <p>
	 * The provided <code>prefix</prefix> is used as the resource name.
	 * </p>
	 * 
	 * @param prefix the namespace prefix
	 */
	Namespace(String prefix) {
		setAttribute(PREFIX, prefix);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.geoserver.Resource#name()
	 */
	@Override
	public String name() {
		return (String) getAttribute(PREFIX);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.geoserver.AbstractResource#allowedAttributes()
	 */
	@Override
	protected Set<String> allowedAttributes() {
		return Collections.unmodifiableSet(allowedAttributes);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.geoserver.AbstractResource#templateLocation()
	 */
	@Override
	protected String templateLocation() {
		return TEMPLATE_LOCATION;
	}

	/**
	 * Parses the first namespace defined in the provided document.
	 * 
	 * @param doc document from where to extract the namespace
	 * @return the first namespace found in the document
	 */
	public static Namespace fromDocument(Document doc) {
		if (doc == null) {
			return null;
		}

		Namespace namespace = null;

		NodeList namespaceNodes = doc.getElementsByTagName(ELEMENT_NAMESPACE);
		if (namespaceNodes.getLength() > 0) {
			Element namespaceEl = (Element) namespaceNodes.item(0);
			Element prefixEl = AppSchemaIO.getFirstElementByTagName(namespaceEl, ELEMENT_PREFIX);
			Element uriEl = AppSchemaIO.getFirstElementByTagName(namespaceEl, ELEMENT_URI);
			if (prefixEl != null && uriEl != null) {
				String prefix = prefixEl.getTextContent();
				String uri = uriEl.getTextContent();
				if (prefix != null && uri != null && !prefix.trim().isEmpty()
						&& !uri.trim().isEmpty()) {
					// create new namespace only if prefix and uri attributes
					// are present
					namespace = ResourceBuilder.namespace(prefix).setAttribute(Namespace.URI, uri)
							.build();

					Element idEl = AppSchemaIO.getFirstElementByTagName(namespaceEl, ELEMENT_ID);
					if (idEl != null && idEl.getTextContent() != null) {
						String id = idEl.getTextContent();
						namespace.setAttribute(ID, id.trim());
					}
					Element isolatedEl = AppSchemaIO.getFirstElementByTagName(namespaceEl,
							ELEMENT_ISOLATED);
					if (isolatedEl != null && isolatedEl.getTextContent() != null) {
						String isolated = isolatedEl.getTextContent();
						namespace.setAttribute(ISOLATED, Boolean.valueOf(isolated.trim()));
					}
				}
			}
		}

		return namespace;
	}
}
