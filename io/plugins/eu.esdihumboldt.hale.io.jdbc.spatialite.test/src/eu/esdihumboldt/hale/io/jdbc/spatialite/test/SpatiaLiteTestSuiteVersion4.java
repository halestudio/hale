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
 * Points to test data stored in a database built with SpatiaLite version 4.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class SpatiaLiteTestSuiteVersion4 extends SpatiaLiteTestSuite {

	private static final String WKT_POLYGON = "POLYGON((146.232727 -42.157501, 146.238007 -42.16111, "
			+ "146.24411 -42.169724, 146.257202 -42.193329, 146.272217 -42.209442, "
			+ "146.274689 -42.214165, 146.27832 -42.21833, 146.282471 -42.228882, "
			+ "146.282745 -42.241943, 146.291351 -42.255836, 146.290253 -42.261948, "
			+ "146.288025 -42.267502, 146.282471 -42.269997, 146.274994 -42.271111, "
			+ "146.266663 -42.270279, 146.251373 -42.262505, 146.246918 -42.258057, "
			+ "146.241333 -42.256111, 146.23468 -42.257782, 146.221344 -42.269165, "
			+ "146.210785 -42.274445, 146.20163 -42.27417, 146.196075 -42.271385, "
			+ "146.186646 -42.258057, 146.188568 -42.252785, 146.193298 -42.249443, "
			+ "146.200806 -42.248055, 146.209137 -42.249168, 146.217468 -42.248611, "
			+ "146.222473 -42.245277, 146.22525 -42.240555, 146.224121 -42.22805, "
			+ "146.224396 -42.221382, 146.228302 -42.217216, 146.231354 -42.212502, "
			+ "146.231628 -42.205559, 146.219421 -42.186943, 146.21637 -42.17028, "
			+ "146.216644 -42.16333, 146.219696 -42.158607, 146.225525 -42.156105, "
			+ "146.232727 -42.157501))";

	/**
	 * 
	 */
	public SpatiaLiteTestSuiteVersion4() {
		Geometry geometry = null;
		try {
			geometry = new WKTReader().read(WKT_POLYGON);
		} catch (ParseException e) {
			// should never happen
		}

		SOURCE_DB_NAME = "tasmania_water_bodies.sqlite";
		SOURCE_DB_LOCATION = "/data/" + SOURCE_DB_NAME;
		SOUURCE_TYPE_LOCAL_NAME = "tasmania_water_bodies";

		PROPERTY_ID_NAME = "PK_UID";
		PROPERTY_ID_VALUE = 1;

		SOUURCE_TYPE_PROPERTY_NAMES = new String[] { "AREA", "CNTRY_NAME", "CONTINENT", "Geometry",
				"PERIMETER", PROPERTY_ID_NAME, "WATER_TYPE" };
		SOUURCE_TYPE_PROPERTY_VALUES = new Object[] { 1064866676, "Australia", "Australia",
				geometry, 1071221047, 1, "Lake" };
		SOURCE_INSTANCES_COUNT = 7;

		TARGET_DB_NAME = "tasmania_water_bodies_target.sqlite";
	}
}
