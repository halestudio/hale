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

	private static final String TEMPLATE_LOCATION = "/eu/esdihumboldt/hale/io/geoserver/template/data/namespace-template.vm";

	private static final Set<String> allowedAttributes = new HashSet<String>();

	static {
		allowedAttributes.add(ID);
		allowedAttributes.add(PREFIX);
		allowedAttributes.add(URI);
	}

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

}
