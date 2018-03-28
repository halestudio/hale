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

package eu.esdihumboldt.hale.io.appschema;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Class holding constants and utility methods.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public abstract class AppSchemaIO {

	/**
	 * Namespace for app-schema mapping elements.
	 */
	public static final String APP_SCHEMA_NAMESPACE = "http://www.geotools.org/app-schema";
	/**
	 * Default prefix for app-schema namespace.
	 */
	public static final String APP_SCHEMA_PREFIX = "as";
	/**
	 * ID of app-schema mapping file content type.
	 */
	public static final String CONTENT_TYPE_MAPPING = "eu.esdihumboldt.hale.io.appschema.mapping";
	/**
	 * ID of app-schema configuration archive content type.
	 */
	public static final String CONTENT_TYPE_ARCHIVE = "eu.esdihumboldt.hale.io.appschema.archive";
	/**
	 * ID of app-schema configuration REST content type
	 */
	public static final String CONTENT_TYPE_REST = "eu.esdihumboldt.hale.io.appschema.rest";
	/**
	 * Datastore configuration parameter name.
	 */
	public static final String PARAM_DATASTORE = "appschema.source.datastore";
	/**
	 * Feature chaining configuration parameter name.
	 */
	public static final String PARAM_CHAINING = "appschema.feature.chaining";
	/**
	 * Include schema configuration parameter name.
	 */
	public static final String PARAM_INCLUDE_SCHEMA = "appschema.include.schema";
	/**
	 * Workspace configuration parameter name.
	 */
	public static final String PARAM_WORKSPACE = "appschema.workspace.conf";
	/**
	 * REST user configuration parameter name.
	 */
	public static final String PARAM_USER = "appschema.rest.user";
	/**
	 * REST password configuration parameter name.
	 */
	public static final String PARAM_PASSWORD = "appschema.rest.password";

	/**
	 * Location of the default mapping file template.
	 */
	public static final String MAPPING_TEMPLATE = "/eu/esdihumboldt/hale/io/geoserver/template/data/mapping-template.xml";
	/**
	 * Namespace configuration file name.
	 */
	public static final String NAMESPACE_FILE = "namespace.xml";
	/**
	 * Workspace configuration file name.
	 */
	public static final String WORKSPACE_FILE = "workspace.xml";
	/**
	 * Datastore configuration file name.
	 */
	public static final String DATASTORE_FILE = "datastore.xml";
	/**
	 * Feature type configuration file name.
	 */
	public static final String FEATURETYPE_FILE = "featuretype.xml";
	/**
	 * Layer configuration file name.
	 */
	public static final String LAYER_FILE = "layer.xml";
	/**
	 * Included types mapping configuration file name.
	 */
	public static final String INCLUDED_TYPES_MAPPING_FILE = "includedTypes.xml";

	/**
	 * Retrieve the first element descendant of <code>parent</code>, with the
	 * provided tag name.
	 * 
	 * @param parent the parent element
	 * @param tagName the tag name
	 * @return the first matching <code>Element</code> node descendant of
	 *         <code>parent</code>
	 */
	public static Element getFirstElementByTagName(Element parent, String tagName) {
		return getFirstElementByTagName(parent, tagName, null);
	}

	/**
	 * Retrieve the first element descendant of <code>parent</code>, with the
	 * provided tag name and namespace.
	 * 
	 * @param parent the parent element
	 * @param tagName the tag name
	 * @param namespace the namespace
	 * @return the first matching <code>Element</code> node descendant of
	 *         <code>parent</code>
	 */
	public static Element getFirstElementByTagName(Element parent, String tagName, String namespace) {
		if (namespace == null)
			namespace = "";

		NodeList elements = (namespace.isEmpty()) ? parent.getElementsByTagName(tagName) : parent
				.getElementsByTagNameNS(namespace, tagName);

		if (elements != null && elements.getLength() > 0) {
			return (Element) elements.item(0);
		}
		else {
			return null;
		}
	}
}
