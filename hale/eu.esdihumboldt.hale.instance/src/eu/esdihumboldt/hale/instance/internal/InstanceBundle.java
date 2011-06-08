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

package eu.esdihumboldt.hale.instance.internal;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Instance bundle activator
 * @author Simon Templer
 * @author Michel Krämer
 */
public class InstanceBundle implements BundleActivator {
	
	/**
	 * Name of the Eclipse Equinox bundle
	 */
	public static final String EQUINOX_BUNDLE = "org.eclipse.osgi";

	private static InstanceBundle _instance;
	
	private BundleContext context;
	
	/**
	 * @see BundleActivator#start(BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		this.context = context;
		_instance = this;
	}

	/**
	 * @see BundleActivator#stop(BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		// do nothing
	}
	
	/**
	 * @return the context
	 */
	public BundleContext getContext() {
		return context;
	}

//	/**
//	 * Loads a class locally from a bundle. Does not respect packages
//	 * imported by the bundle but finds classes which are really defined
//	 * in this bundle.
//	 * @param bnd the bundle to load the class from
//	 * @param className the name of the class to load
//	 * @return the loaded class
//	 * @throws ClassNotFoundException if the class could not be found or
//	 * if the bundle is not able to load classes locally
//	 */
//	private static Class<?> loadLocalClass(Bundle bnd, String className)
//		throws ClassNotFoundException {
//		if (bnd instanceof BundleHost) {
//			BundleHost bh = (BundleHost)bnd;
//			BundleLoaderProxy blp = bh.getLoaderProxy();
//			if (blp != null && blp.getBundleLoader() != null) {
//				SingleSourcePackage ssp =
//					new SingleSourcePackage(bnd.getSymbolicName(), blp);
//				return ssp.loadClass(className);
//			}
//		}
//		throw new ClassNotFoundException(className);
//	}
	
	/**
	 * Load a class from the given bundles
	 * 
	 * @param bundles the bundles
	 * @param preferredBundleName the symbolic name of the preferred bundle
	 * (can be null)
	 * @param className the class name 
	 * 
	 * @return the loaded class or <code>null</code>
	 */
	public static Class<?> loadClass(Bundle[] bundles, String preferredBundleName, String className) {
		if (preferredBundleName != null) {
			for (Bundle bundle : bundles) {
				if (bundle.getSymbolicName().equals(preferredBundleName)) {
					try {
						return bundle.loadClass(className);
					} catch (ClassNotFoundException e) {
						// try to load the class directly from the bundle
						// and not through imported packages. This helps
						// if the test bundle is no fragment but imports
						// packages from the bundle to test that have the
						// same name as the packages where the test classes
						// reside.
//						try {
//							return loadLocalClass(bundle, className);
//						} catch (ClassNotFoundException e1) {
//							// ignore
//						}
					}
				}
			}
		}
		
		Bundle eclipse = null;
		for (Bundle bundle : bundles) {
			if (!bundle.getSymbolicName().equals(EQUINOX_BUNDLE)) {
				try {
					return  bundle.loadClass(className);
				} catch (ClassNotFoundException e) {
					// ignore
				}
			} else {
				eclipse = bundle;
			}
		}
		
		if (eclipse != null) {
			try {
				return eclipse.loadClass(className);
			} catch (ClassNotFoundException e) {
				//ignore
			}
		}
		
		return null;
	}
	
	/**
	 * Load the class with the given name. The class will be loaded from the
	 *   OSGi context if it is available
	 * 
	 * @param name the class name
	 * @param preferredBundleName the symbolic name of the preferred bundle
	 * (can be null)
	 * 
	 * @return the loaded class or <code>null</code>
	 */
	public static Class<?> loadClass(String name, String preferredBundleName) {
		if (_instance != null && _instance.getContext() != null) {
			return loadClass(_instance.getContext().getBundles(), preferredBundleName, name);
		}
		else {
			try {
				return Class.forName(name);
			}
			catch (Throwable e) {
				return null;
			}
		}
	}

}
