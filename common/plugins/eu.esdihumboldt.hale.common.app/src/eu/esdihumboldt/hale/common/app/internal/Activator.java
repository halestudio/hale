package eu.esdihumboldt.hale.common.app.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Bundle activator.
 */
public class Activator implements BundleActivator {

	private static BundleContext context;

	/**
	 * Get the bundle context.
	 * 
	 * @return the bundle context
	 */
	public static BundleContext getContext() {
		return context;
	}

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
