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
