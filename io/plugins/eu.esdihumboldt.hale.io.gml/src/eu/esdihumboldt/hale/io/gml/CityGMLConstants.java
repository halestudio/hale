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
 * Common constants on CityGML.
 * 
 * @author Simon Templer
 */
public interface CityGMLConstants {

	/**
	 * The local name of the member element of the root element where city
	 * objects occur.
	 */
	public static final String CITY_OBJECT_MEMBER_ELEMENT = "cityObjectMember";

	/**
	 * The local name of the CityGML default root element.
	 */
	public static final String CITY_MODEL_ELEMENT = "CityModel";

	/**
	 * The core part of the CityGML namespace that is independent of the version
	 * number.
	 */
	public static final String CITYGML_NAMESPACE_CORE = "http://www.opengis.net/citygml";

}
