package eu.esdihumboldt.hale.io.jdbc.spatialite.internal;

import java.sql.DriverManager;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Bundle activator. It registers the SQLite driver with the
 * {@link DriverManager}.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/**
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;

		Class.forName("org.sqlite.JDBC");
	}

	/**
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
