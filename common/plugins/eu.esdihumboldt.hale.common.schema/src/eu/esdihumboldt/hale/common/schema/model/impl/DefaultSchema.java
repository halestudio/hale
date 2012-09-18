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

package eu.esdihumboldt.hale.common.schema.model.impl;

import java.net.URI;

import eu.esdihumboldt.hale.common.core.io.supplier.Locatable;
import eu.esdihumboldt.hale.common.schema.model.Schema;

/**
 * Default {@link Schema} implemenation
 * 
 * @author Simon Templer
 */
public class DefaultSchema extends DefaultTypeIndex implements Schema {

	private final String namespace;

	private final URI location;

	/**
	 * Create a schema
	 * 
	 * @param namespace the schema namespace
	 * @param location the schema location
	 */
	public DefaultSchema(String namespace, URI location) {
		super();
		this.namespace = namespace;
		this.location = location;
	}

	/**
	 * @see Locatable#getLocation()
	 */
	@Override
	public URI getLocation() {
		return location;
	}

	/**
	 * @see Schema#getNamespace()
	 */
	@Override
	public String getNamespace() {
		return namespace;
	}

}
