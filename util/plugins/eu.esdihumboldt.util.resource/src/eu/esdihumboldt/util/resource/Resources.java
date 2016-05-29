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

package eu.esdihumboldt.util.resource;

import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import eu.esdihumboldt.util.io.InputSupplier;
import eu.esdihumboldt.util.resource.internal.ResolverConfiguration;
import eu.esdihumboldt.util.resource.internal.ResolverExtension;

/**
 * Utility methods for the resource extensions.
 * 
 * @author Simon Templer
 */
public abstract class Resources implements ResourcesConstants {

	/**
	 * Resource type mapped to host name mapped to resource resolver
	 */
	private static Map<String, Multimap<String, ResourceResolver>> resolvers;

	/**
	 * Try resolving an URI for a given resource type.
	 * 
	 * @param uri the URI to the resource
	 * @param resourceType the resource type, <code>null</code> for any resource
	 *            type
	 * @return an input supplier for the resource or <code>null</code> if it was
	 *         not found through resource resolvers
	 */
	public static final InputSupplier<? extends InputStream> tryResolve(URI uri,
			String resourceType) {
		init();

		if (resourceType != null) {
			Multimap<String, ResourceResolver> typeResolvers = resolvers.get(resourceType);
			if (typeResolvers != null) {
				return tryResolve(typeResolvers, uri);
			}
		}
		else {
			for (Multimap<String, ResourceResolver> typeResolver : resolvers.values()) {
				InputSupplier<? extends InputStream> input = tryResolve(typeResolver, uri);
				if (input != null) {
					// return first match found
					return input;
				}
			}
		}

		return null;
	}

	private static InputSupplier<? extends InputStream> tryResolve(
			Multimap<String, ResourceResolver> typeResolvers, URI uri) {
		for (ResourceResolver resolver : typeResolvers.get(uri.getHost())) {
			try {
				return resolver.resolve(uri);
			} catch (ResourceNotFoundException e) {
				// ignore
			}
		}

		return null;
	}

	private static synchronized void init() {
		if (resolvers == null) {
			resolvers = new HashMap<String, Multimap<String, ResourceResolver>>();

			// register resolvers
			for (ResolverConfiguration resolverConf : ResolverExtension.getInstance()
					.getElements()) {
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
