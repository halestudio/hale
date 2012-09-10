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
