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

import java.io.IOException;
import java.net.URL;

/**
 * <p>
 * Title: PackageResolver
 * </p>
 * <p>
 * Description: This interface provides a method which returns an URL for a
 * package.
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
public interface PackageResolver {

	/**
	 * Returns an URL for a package
	 * 
	 * @param pkg the package (e.g. de.fhg.igd.CityServer3D)
	 * @return the URL to the package
	 * @throws IOException if the URL could not be retrieved
	 */
	public URL resolve(String pkg) throws IOException;
}
