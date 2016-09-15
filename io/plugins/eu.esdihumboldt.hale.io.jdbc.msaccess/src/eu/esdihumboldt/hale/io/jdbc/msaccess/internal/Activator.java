package eu.esdihumboldt.hale.io.jdbc.msaccess.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;

		Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;

	}

}
