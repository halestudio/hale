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

package eu.esdihumboldt.util.resource.internal;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.eclipse.extension.simple.IdentifiableExtension.Identifiable;
import eu.esdihumboldt.util.resource.ResourceResolver;

/**
 * A resolver and its configuration.
 * @author Simon Templer
 */
public class ResolverConfiguration implements Identifiable {

	private final String id;
	
	private final String resourceTypeId;
	
	private final Set<String> hosts = new HashSet<String>();
	
	private final ResourceResolver resourceResolver;
	
	/**
	 * Create a resolver configuration.
	 * @param id the resolver ID
	 * @param conf the configuration element
	 * @throws IllegalStateException if no resolver is defined
	 */
	public ResolverConfiguration(String id, IConfigurationElement conf) throws IllegalStateException {
		this.id = id;
		this.resourceTypeId = conf.getAttribute("resourceType");
		
		// collect host names
		for (IConfigurationElement host : conf.getChildren("host")) {
			String hostName = host.getAttribute("name");
			if (hostName != null) {
				hosts.add(hostName);
			}
		}
		
		ResourceResolver resolver = null;
		// try bundle resolver
		IConfigurationElement[] bundleResolvers = conf.getChildren("bundleResolver");
		if (bundleResolvers.length > 0) {
			resolver = new BundleResolver(bundleResolvers[0]);
		}
			
		if (resolver != null) {
			resourceResolver = resolver;
		}
		else {
			throw new IllegalStateException("No resolver defined");
		}
	}

	/**
	 * @see Identifiable#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * @return the resource type ID
	 */
	public String getResourceTypeId() {
		return resourceTypeId;
	}

	/**
	 * @return the host names
	 */
	public Set<String> getHosts() {
		return Collections.unmodifiableSet(hosts);
	}

	/**
	 * @return the resource resolver
	 */
	public ResourceResolver getResourceResolver() {
		return resourceResolver;
	}

}
