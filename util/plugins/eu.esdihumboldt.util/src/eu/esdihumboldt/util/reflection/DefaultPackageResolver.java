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

import java.net.URL;

/**
 * <p>
 * Title: DefaultPackageResolver
 * </p>
 * <p>
 * Description: The default implementation for the PackageResolver interface
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004-2008
 * </p>
 * <p>
 * Company: Fraunhofer IGD
 * </p>
 * 
 * @author Michel Kraemer
 * @version $Id$
 */
public class DefaultPackageResolver implements PackageResolver {

	/**
	 * @see PackageResolver#resolve(java.lang.String)
	 */
	@Override
	public URL resolve(String pkg) {
		String package_path = pkg.replaceAll("\\.", "/"); //$NON-NLS-1$ //$NON-NLS-2$
		URL u = DefaultPackageResolver.class.getClassLoader().getResource(package_path);
		return u;
	}
}
