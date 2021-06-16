package eu.esdihumboldt.hale.io.jdbc.mysql.internal;

import java.sql.DriverManager;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.mysql.cj.jdbc.Driver;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		
		//Class.forName("com.mysql.cj.jdbc.Driver");
		DriverManager.registerDriver(new Driver());
		
	}

	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
