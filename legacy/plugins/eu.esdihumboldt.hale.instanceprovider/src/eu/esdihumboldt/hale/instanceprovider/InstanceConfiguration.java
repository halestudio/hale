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

package eu.esdihumboldt.hale.instanceprovider;

import java.net.URI;
import java.util.Collection;

import org.geotools.util.Version;

import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;

/**
 * Configuration for instance loading 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class InstanceConfiguration {
	
	private final URI schemaLocation;
	
	private final String namespace;
	
	private final Collection<SchemaElement> schema;
	
	private final Version instanceVersion;

	/**
	 * Constructor
	 * 
	 * @param schemaLocation the location of the corresponding schema
	 * @param namespace the namespace
	 * @param schema the schema elements
	 * @param instanceVersion the instance version
	 */
	public InstanceConfiguration(URI schemaLocation, String namespace,
			Collection<SchemaElement> schema, Version instanceVersion) {
		super();
		this.schemaLocation = schemaLocation;
		this.namespace = namespace;
		this.schema = schema;
		this.instanceVersion = instanceVersion;
	}

	/**
	 * @return the schemaLocation
	 */
	public URI getSchemaLocation() {
		return schemaLocation;
	}

	/**
	 * @return the namespace
	 */
	public String getNamespace() {
		return namespace;
	}

	/**
	 * @return the schema
	 */
	public Collection<SchemaElement> getSchema() {
		return schema;
	}

	/**
	 * @return the instanceVersion
	 */
	public Version getInstanceVersion() {
		return instanceVersion;
	}

}
