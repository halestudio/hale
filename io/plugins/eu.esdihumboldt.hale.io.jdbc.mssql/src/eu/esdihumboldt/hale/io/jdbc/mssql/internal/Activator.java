package eu.esdihumboldt.hale.io.jdbc.mssql.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The Activator class for MS Sql controls the Plugin life cycle
 * 
 * @author Arun
 */
public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		Activator.context = context;
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		Activator.context = null;
	}

}
