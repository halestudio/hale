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

package eu.esdihumboldt.util.resource.internal;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension.Identifiable;
import eu.esdihumboldt.util.resource.ResourceResolver;

/**
 * A resolver and its configuration.
 * 
 * @author Simon Templer
 */
public class ResolverConfiguration implements Identifiable {

	private final String id;

	private final String resourceTypeId;

	private final Set<String> hosts = new HashSet<String>();

	private final ResourceResolver resourceResolver;

	/**
	 * Create a resolver configuration.
	 * 
	 * @param id the resolver ID
	 * @param conf the configuration element
	 * @throws IllegalStateException if no resolver is defined
	 */
	public ResolverConfiguration(String id, IConfigurationElement conf)
			throws IllegalStateException {
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
