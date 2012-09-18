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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import org.eclipse.core.runtime.IConfigurationElement;
import org.osgi.framework.Bundle;

import com.google.common.io.InputSupplier;

import eu.esdihumboldt.util.resource.ResourceNotFoundException;
import eu.esdihumboldt.util.resource.ResourceResolver;

/**
 * Resource resolver that attempts to find a resource at the URI path in the
 * bundle that registered the resolver as extension.
 * 
 * @author Simon Templer
 */
public class BundleResolver implements ResourceResolver {

	private final Bundle bundle;

	/**
	 * Create a bundle resolver.
	 * 
	 * @param conf the configuration element
	 */
	public BundleResolver(IConfigurationElement conf) {
		String bundleName = conf.getContributor().getName();
		Bundle contributor = null;

		for (Bundle bundle : ResourceBundle.getBundleContext().getBundles()) {
			if (bundle.getSymbolicName().equals(bundleName)) {
				contributor = bundle;
				break;
			}
		}

		if (contributor == null) {
			throw new IllegalStateException("Contributing bundle not found: " + bundleName);
		}

		this.bundle = contributor;
	}

	/**
	 * @see ResourceResolver#resolve(URI)
	 */
	@Override
	public InputSupplier<? extends InputStream> resolve(URI uri) throws ResourceNotFoundException {
		final URL entry = bundle.getEntry(uri.getPath());
		if (entry == null) {
			throw new ResourceNotFoundException("Resource with path " + uri.getPath()
					+ " not contained in bundle " + bundle.getSymbolicName());
		}
		return new InputSupplier<InputStream>() {

			@Override
			public InputStream getInput() throws IOException {
				return entry.openStream();
			}
		};
	}

}
