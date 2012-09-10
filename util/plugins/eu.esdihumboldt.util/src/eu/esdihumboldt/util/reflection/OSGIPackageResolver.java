/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.util.reflection;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import eu.esdihumboldt.util.internal.Activator;

/**
 * <p>
 * Title: OSGIPackageResolver
 * </p>
 * <p>
 * Description: This package resolver is able to resolve packages from OSGI
 * bundles.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004-2009
 * </p>
 * <p>
 * Company: Fraunhofer IGD
 * </p>
 * 
 * @author Michel Kraemer
 * @version $Id: OSGIPackageResolver.java 5988 2009-11-19 14:30:20Z mkraemer $
 */
public class OSGIPackageResolver implements PackageResolver {

	/**
	 * The default package resolver
	 */
	private PackageResolver _def = new DefaultPackageResolver();

	/**
	 * @see PackageResolver#resolve(java.lang.String)
	 */
	@Override
	public URL resolve(String pkg) throws IOException {
		URL u = _def.resolve(pkg);
		if (u != null && u.toString().startsWith("bundleresource")) { //$NON-NLS-1$
			u = FileLocator.resolve(u);
		}
		else if (u == null) {
			// if the default package resolver could not resolve the package,
			// search all other bundles
			BundleContext ctx = Activator.getContext();
			Bundle[] bundles = ctx.getBundles();
			String packagePathStr = pkg.replaceAll("\\.", "/"); //$NON-NLS-1$ //$NON-NLS-2$
			Path packagePath = new Path(packagePathStr);
			u = searchBundles(packagePath, bundles);

			if (u == null) {
				packagePath = new Path("bin/" + packagePathStr); //$NON-NLS-1$
				u = searchBundles(packagePath, bundles);
			}
		}
		return u;
	}

	/**
	 * Searches a list of bundles for a given path
	 * 
	 * @param packagePath the path to search for
	 * @param bundles the bundles to search
	 * @return the URL to the path or null if the path was not found
	 * @throws IOException if an URL could not be resolved
	 */
	private URL searchBundles(Path packagePath, Bundle[] bundles) throws IOException {
		// prefer non-test bundles
		for (Bundle bnd : bundles) {
			if (bnd.getSymbolicName().endsWith(".test")) { //$NON-NLS-1$
				continue;
			}
			URL bu = FileLocator.find(bnd, packagePath, null);
			if (bu != null) {
				return FileLocator.resolve(bu);
			}
		}

		// now search test bundles also
		for (Bundle bnd : bundles) {
			if (!bnd.getSymbolicName().endsWith(".test")) { //$NON-NLS-1$
				continue;
			}
			URL bu = FileLocator.find(bnd, packagePath, null);
			if (bu != null) {
				return FileLocator.resolve(bu);
			}
		}

		return null;
	}
}
