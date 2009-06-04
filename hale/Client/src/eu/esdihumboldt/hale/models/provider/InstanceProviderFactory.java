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

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

/**
 * FIXME Add Type description.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class InstanceProviderFactory {
	
	private static Logger _log = Logger.getLogger(InstanceProviderFactory.class);
	
	private static InstanceProviderFactory instance = new InstanceProviderFactory();
	
	private static Map<String, InstanceProvider> providers;
	
	private InstanceProviderFactory() {
		InstanceProviderFactory.providers = new HashMap<String, InstanceProvider>();
		
		// initialize InstanceProviders. TODO: use external config file.
		List<String> ipClassNames = new ArrayList<String>();
		ipClassNames.add("eu.esdihumboldt.hale.models.provider.instance.GML3InstanceProvider");
		for (String ipClassName : ipClassNames) {
			try {
				Class<?> instance_provider_class = Class.forName(ipClassName);
				InstanceProvider instance_provider = (InstanceProvider) instance_provider_class
						.newInstance();
				InstanceProviderFactory.providers.put(instance_provider
						.getSupportedMimeType(), instance_provider);
			} catch (Exception ex) {
				_log.error("Unable to instantiate an "
								+ "InstanceProvider object belonging to the requested instance type."
								+ " Please specify a valid insance type."
								+ ex);
			}
		}
	}
	
	public static FeatureCollection<? extends FeatureType, ? extends Feature> getFeatureCollection(
			URL location) {
		// determine what kind geodata has to be read.
		
		// FIXME
		// delegate to the appropriate InstanceProvider
		return null;
	}

}
