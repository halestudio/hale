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

package eu.esdihumboldt.util.resource;

import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.InputSupplier;

import eu.esdihumboldt.util.resource.internal.ResolverConfiguration;
import eu.esdihumboldt.util.resource.internal.ResolverExtension;

/**
 * Utility methods for the resource extensions.
 * @author Simon Templer
 */
public abstract class Resources implements ResourcesConstants {
	
	/**
	 * Resource type mapped to host name mapped to resource resolver
	 */
	private static Map<String, Multimap<String, ResourceResolver>> resolvers;
	
	/**
	 * Try resolving an URI for a given resource type.
	 * @param uri the URI to the resource
	 * @param resourceType the resource type
	 * @return an input supplier for the resource or <code>null</code> if it was
	 *   not found through resource resolvers
	 */
	public static final InputSupplier<? extends InputStream> tryResolve(URI uri, String resourceType) {
		init();
		
		Multimap<String, ResourceResolver> typeResolvers = resolvers.get(resourceType);
		if (typeResolvers != null) {
			for (ResourceResolver resolver : typeResolvers.get(uri.getHost())) {
				try {
					return resolver.resolve(uri);
				} catch (ResourceNotFoundException e) {
					// ignore
				}
			}
		}
		
		return null;
	}

	private static synchronized void init() {
		if (resolvers == null) {
			resolvers = new HashMap<String, Multimap<String,ResourceResolver>>();
			
			// register resolvers
			for (ResolverConfiguration resolverConf : ResolverExtension.getInstance().getElements()) {
				String resourceType = resolverConf.getResourceTypeId();
				
				Multimap<String, ResourceResolver> typeResolvers = resolvers.get(resourceType);
				if (typeResolvers == null) {
					typeResolvers = HashMultimap.create();
					resolvers.put(resourceType, typeResolvers);
				}
				
				for (String host : resolverConf.getHosts()) {
					typeResolvers.put(host, resolverConf.getResourceResolver());
				}
			}
		}
	}

}
