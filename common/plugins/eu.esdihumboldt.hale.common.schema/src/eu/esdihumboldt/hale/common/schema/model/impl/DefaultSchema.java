/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
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
