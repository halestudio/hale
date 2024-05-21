/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.gml.geometry;

/**
 * Common constants on GML.
 * 
 * @author Simon Templer
 */
public interface GMLConstants {

	/**
	 * The core part of the GML namespace that is independent of the version
	 * number (and is the namespace of GML versions up to 3.1.1).
	 */
	public static final String GML_NAMESPACE_CORE = "http://www.opengis.net/gml";

	/**
	 * The GML namespace
	 */
	public static final String NS_GML = "http://www.opengis.net/gml";

	/**
	 * The GML 3.2 namespace
	 */
	public static final String NS_GML_32 = "http://www.opengis.net/gml/3.2";

	/**
	 * Namespace URL for the WFS standard, defined by the OGC
	 */
	public static final String NS_WFS = "http://www.opengis.net/wfs";

}
