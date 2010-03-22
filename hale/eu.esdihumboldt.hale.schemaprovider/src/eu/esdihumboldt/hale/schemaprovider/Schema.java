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
package eu.esdihumboldt.hale.schemaprovider;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;

/**
 * Represents a schema
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class Schema {

	/**
	 * Empty schema instance
	 */
	public static final Schema EMPTY_SCHEMA =
		new Schema(new HashMap<String, SchemaElement>(), "", null);
	
	/**
	 * The feature types
	 */
	private final Map<String, SchemaElement> elements;
	
	/**
	 * The namespace
	 */
	private final String namespace;
	
	/**
	 * The schema location
	 */
	private final URL location;

	/**
	 * Constructor
	 * 
	 * @param elements the type definitions
	 * @param namespace the namespace
	 * @param location the location
	 */
	public Schema(Map<String, SchemaElement> elements, String namespace,
			URL location) {
		super();
		this.elements = elements;
		this.namespace = namespace;
		this.location = location;
	}

	/**
	 * @return the featureTypes
	 */
	public Map<String, SchemaElement> getElements() {
		return elements;
	}

	/**
	 * @return the namespace
	 */
	public String getNamespace() {
		return namespace;
	}

	/**
	 * @return the location
	 */
	public URL getLocation() {
		return location;
	}

}
