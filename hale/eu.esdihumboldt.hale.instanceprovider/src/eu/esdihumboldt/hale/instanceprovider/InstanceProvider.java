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

import java.io.IOException;
import java.net.URI;
import java.util.Set;

import org.geotools.feature.FeatureCollection;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.core.io.ProgressIndicator;

/**
 * Provides support for parsing feature instances
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public interface InstanceProvider {
	
	/**
	 * Method to load feature instance data
	 * 
	 * @param location URI which represents a file
	 * @param configuration the configuration parameter, may be <code>null</code>
	 * @param progress the progress indicator, may be <code>null</code>
	 * @return the feature collection containing the loaded features
	 * @throws IOException if loading the schema fails
	 */
	public FeatureCollection<FeatureType, Feature> loadInstances(URI location,
			InstanceConfiguration configuration, ProgressIndicator progress) throws IOException;
	
	/**
	 * Determines if the schema provider supports the given schema format
	 * 
	 * @param schemaFormat the schema format
	 * 
	 * @return true if the schema format is supported
	 */
	public boolean supportsSchemaFormat(String schemaFormat);

	/**
	 * Get the supported schema formats
	 * 
	 * @return the supported schema formats
	 */
	public Set<? extends String> getSupportedSchemaFormats();
	
	/**
	 * Determines if the instance provider supports the given instance format
	 * 
	 * @param instanceFormat the instance format
	 * 
	 * @return true if the schema format is supported
	 */
	public boolean supportsInstanceFormat(String instanceFormat);

	/**
	 * Get the supported instance formats
	 * 
	 * @return the supported instance formats
	 */
	public Set<? extends String> getSupportedInstanceFormats();

}
