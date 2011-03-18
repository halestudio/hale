/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.models.provider;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.geotools.feature.FeatureCollection;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.instanceprovider.InstanceConfiguration;
import eu.esdihumboldt.hale.instanceprovider.InstanceProvider;
import eu.esdihumboldt.hale.instanceprovider.gml.GmlInstanceProvider;
import eu.esdihumboldt.hale.instanceprovider.shape.ShapeInstanceProvider;

/**
 * FIXME Add Type description.
 * 
 * @author Thorsten Reitz, Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class InstanceProviderFactory {
	
//	private static ALogger _log = ALoggerFactory.getLogger(InstanceProviderFactory.class);
	
	/**
	 * The factory instance
	 */
	public static final InstanceProviderFactory INSTANCE = new InstanceProviderFactory();
	
	/**
	 * The available providers
	 */
	private final Set<InstanceProvider> providers = new HashSet<InstanceProvider>();
	
	/**
	 * Default constructor
	 */
	private InstanceProviderFactory() {
		super();
		
		providers.add(new ShapeInstanceProvider());
		providers.add(new GmlInstanceProvider());
	}
	
	/**
	 * Get the {@link InstanceProvider} for the given formats
	 * @param schemaFormat the schema format
	 * @param instanceFormat the instance format
	 * @return the instance provider or <code>null</code> if none matches the
	 *   formats
	 */
	public InstanceProvider getInstanceProvider(String schemaFormat, String instanceFormat) {
		for (InstanceProvider provider : providers) {
			if (provider.supportsSchemaFormat(schemaFormat) && provider.supportsInstanceFormat(instanceFormat)) {
				return provider;
			}
		}
		
		return null;
	}
	
	/**
	 * Get the {@link InstanceProvider}s for the given schema format
	 * @param schemaFormat the schema format
	 * @return the instance provider or <code>null</code> if none matches the
	 *   formats
	 */
	public Collection<InstanceProvider> getInstanceProvider(String schemaFormat) {
		Collection<InstanceProvider> result = new ArrayList<InstanceProvider>();
		for (InstanceProvider provider : providers) {
			if (provider.supportsSchemaFormat(schemaFormat)) {
				result.add(provider);
			}
		}
		
		return result;
	}
	
	/**
	 * Load a feature collection from a given location
	 * @param location the location
	 * @param schemaFormat the schema format
	 * @param instanceFormat the instance format
	 * @param conf the instance configuration
	 * @return the feature collection with the loaded features
	 * @throws IOException if loading the features fails
	 */
	public FeatureCollection<? extends FeatureType, ? extends Feature> getFeatureCollection(
			URI location, String schemaFormat, String instanceFormat, InstanceConfiguration conf) throws IOException {
		InstanceProvider ip = getInstanceProvider(schemaFormat, instanceFormat);
		
		if (ip == null) {
			throw new RuntimeException("No instance provider found for schema format "  //$NON-NLS-1$
					+ schemaFormat + " and instance format " + instanceFormat); //$NON-NLS-1$
		}
		
		return ip.loadInstances(location, conf, null);
	}

}
