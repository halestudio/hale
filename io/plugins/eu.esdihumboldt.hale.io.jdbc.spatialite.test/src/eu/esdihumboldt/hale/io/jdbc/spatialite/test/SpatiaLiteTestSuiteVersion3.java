/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.jdbc.spatialite.test;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

/**
 * Points to test data stored in a database built with SpatiaLite version 3.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class SpatiaLiteTestSuiteVersion3 extends SpatiaLiteTestSuite {

	private static final String WKT_POINT = "POINT(712903.814217 5150722.45803)";

	/**
	 * Default constructor.
	 */
	public SpatiaLiteTestSuiteVersion3() {
		Geometry geometry = null;
		try {
			geometry = new WKTReader().read(WKT_POINT);
		} catch (ParseException e) {
			// should never happen
		}

		SOURCE_DB_NAME = "trento_sample.sqlite";
		SOURCE_DB_LOCATION = "/data/" + SOURCE_DB_NAME;
		SOUURCE_TYPE_LOCAL_NAME = "MunicipalHalls";

		PROPERTY_ID_NAME = "PK_UID";
		PROPERTY_ID_VALUE = 1;

		SOUURCE_TYPE_PROPERTY_NAMES = new String[] { "AREA", "COMU", "Geometry", "PERIMETER",
				PROPERTY_ID_NAME };
		SOUURCE_TYPE_PROPERTY_VALUES = new Object[] { 67013655.177800, 39, geometry, 49767.3225177,
				1 };
		SOURCE_INSTANCES_COUNT = 223;

		TARGET_DB_NAME = "trento_sample_target.sqlite";
	}

}
