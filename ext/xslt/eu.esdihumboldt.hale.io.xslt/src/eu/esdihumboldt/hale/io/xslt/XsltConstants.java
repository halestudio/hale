/*
 * Copyright (c) 2013 Fraunhofer IGD
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
 *     Fraunhofer IGD
 */

package eu.esdihumboldt.hale.io.xslt;

/**
 * TODO Type description
 * 
 * @author Simon Templer
 */
public interface XsltConstants {

	/**
	 * Namespace URI for XSLT.
	 */
	public static final String NS_URI_XSL = "http://www.w3.org/1999/XSL/Transform";

	/**
	 * Namespace URI for custom XSL functions provided by the export.
	 */
	public static final String NS_CUSTOM_XSL = "http://www.esdi-humboldt.eu/hale/xsl";

	/**
	 * Namespace URI for definitions used by HALE in XSLT.
	 */
	public static final String NS_XSL_DEFINITIONS = "http://www.esdi-humboldt.eu/hale/xslt/definitions";

	/**
	 * Fixed prefix for the XSLT namespace.
	 */
	public static final String NS_PREFIX_XSL = "xsl";

	/**
	 * Fixed prefix for the XML Schema Instance namespace.
	 */
	public static final String NS_PREFIX_XSI = "xsi";

	/**
	 * Fixed prefix for the XML Schema namespace.
	 */
	public static final String NS_PREFIX_XS = "xs";

	/**
	 * Fixed prefix for the HALE XSL namespace.
	 */
	public static final String NS_PREFIX_CUSTOM_XSL = "hx";

	/**
	 * Fixed prefix for the HALE XSL definitions namespace.
	 */
	public static final String NS_PREFIX_XSL_DEFINITIONS = "def";

}
