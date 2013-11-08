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

package eu.esdihumboldt.hale.io.gml;

/**
 * Common constants on Inspire.
 * 
 * @author Kai Schwierczek
 * @author Simon Templer
 */
public interface InspireConstants {

	/**
	 * The default INSPIRE base type namespace. Default because the namespace
	 * differs with different versions of the schema.
	 */
	public static final String DEFAULT_INSPIRE_NAMESPACE_BASETYPES = "urn:x-inspire:specification:gmlas:BaseTypes:3.2";

	/**
	 * The common prefix shared by INSPIRE base type namespaces of different
	 * versions (up to version 3.2).
	 */
	public static final String PREFIX_1_INSPIRE_NAMESPACE_BASETYPES = "urn:x-inspire:specification:gmlas:BaseTypes";

	/**
	 * The common prefix shared by INSPIRE base type namespaces of different
	 * versions (starting from version 3.3).
	 */
	public static final String PREFIX_2_INSPIRE_NAMESPACE_BASETYPES = "http://inspire.ec.europa.eu/schemas/base";

	/**
	 * The local name of the spatial data set element.
	 */
	public static final String ELEMENT_SPATIAL_DATASET = "SpatialDataSet";
}
