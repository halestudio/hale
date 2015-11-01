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

import java.util.HashSet;
import java.util.Set;

/**
 * Class representing a feature type resource.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class FeatureType extends AbstractResource {

	/**
	 * "Feature type ID" attribute.
	 */
	public static final String ID = "featureTypeId";
	/**
	 * "Feature type name" attribute.
	 */
	public static final String NAME = "featureTypeName";
	/**
	 * "Namespace ID" attribute.
	 */
	public static final String NAMESPACE_ID = "namespaceId";
	/**
	 * "Datastore ID" attribute.
	 */
	public static final String DATASTORE_ID = "dataStoreId";

	private static final String TEMPLATE_LOCATION = "/eu/esdihumboldt/hale/io/geoserver/template/data/featuretype-template.vm";

	private static final Set<String> allowedAttributes = new HashSet<String>();

	static {
		allowedAttributes.add(ID);
		allowedAttributes.add(NAME);
		allowedAttributes.add(NAMESPACE_ID);
		allowedAttributes.add(DATASTORE_ID);
	}

	/**
	 * Constructor.
	 * 
	 * @param name the feature type name
	 */
	public FeatureType(String name) {
		setAttribute(NAME, name);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.geoserver.Resource#name()
	 */
	@Override
	public String name() {
		return (String) getAttribute(NAME);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.geoserver.AbstractResource#allowedAttributes()
	 */
	@Override
	protected Set<String> allowedAttributes() {
		return allowedAttributes;
	}

	/**
	 * @see eu.esdihumboldt.hale.io.geoserver.AbstractResource#templateLocation()
	 */
	@Override
	protected String templateLocation() {
		return TEMPLATE_LOCATION;
	}
}
