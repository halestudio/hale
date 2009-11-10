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
package eu.esdihumboldt.hale.models.schema;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.opengis.feature.type.FeatureType;

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
		new Schema(new ArrayList<FeatureType>(), "", null);
	
	/**
	 * The feature types
	 */
	private final Collection<FeatureType> featureTypes;
	
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
	 * @param featureTypes the feature type collection
	 * @param namespace the namespace
	 * @param location the location
	 */
	public Schema(Collection<FeatureType> featureTypes, String namespace,
			URL location) {
		super();
		this.featureTypes = featureTypes;
		this.namespace = namespace;
		this.location = location;
	}

	/**
	 * @return the featureTypes
	 */
	public Collection<FeatureType> getFeatureTypes() {
		return featureTypes;
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
