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

package eu.esdihumboldt.hale.app.bgis.ade.common;

/**
 * BGIS CityGML ADE applications constants.
 * 
 * @author Simon Templer
 */
public interface BGISAppConstants {

	/**
	 * ADE namespace.
	 */
//	public static final String ADE_NS = "http://www.bund.de/AGeoBw";
	public static final String ADE_NS = "http://www.datenbasisDokumente.geoinfo.svc/schemata/";

	/**
	 * Default value for enumerations.
	 */
	public static final String ENUMERATION_DEFAULT = "noInformation";

	/**
	 * Default value for numbers.
	 */
	public static final String NUMBER_DEFAULT = "-999999";

	/**
	 * Default value for strings and anything else (e.g. dates)
	 */
	public static final String DEFAULT = "No Information";

	/**
	 * Identifier of the alignment content type.
	 */
	public static final String ALIGNMENT_CONTENT_TYPE = "eu.esdihumboldt.hale.io.align.26";

}
