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

package eu.esdihumboldt.util.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import eu.esdihumboldt.util.reflection.OSGIPackageResolver;
import eu.esdihumboldt.util.reflection.ReflectionHelper;

/**
 * Bundle activator
 * 
 * @author Simon Templer
 */
public class Activator implements BundleActivator {

	private static BundleContext context;

	/**
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		Activator.context = context;

		// register the OSGi package resolver on activation
		ReflectionHelper.setPackageResolver(new OSGIPackageResolver());
	}

	/**
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		// do nothing
	}

	/**
	 * Get the bundle context.
	 * 
	 * @return the bundle context
	 */
	public static BundleContext getContext() {
		return context;
	}

}
