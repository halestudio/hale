/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
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
