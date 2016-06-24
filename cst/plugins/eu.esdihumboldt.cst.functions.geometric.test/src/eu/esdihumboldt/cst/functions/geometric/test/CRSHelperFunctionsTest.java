/*
 * Copyright (c) 2016 Data Harmonisation Panel
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

package eu.esdihumboldt.cst.functions.geometric.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import eu.esdihumboldt.cst.functions.geometric.CRSHelperFunctions;
import eu.esdihumboldt.hale.common.instance.geometry.impl.CodeDefinition;
import eu.esdihumboldt.hale.common.instance.geometry.impl.WKTDefinition;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;

/**
 * Tests for geometry helper functions
 * 
 * @author Simon Templer
 */
@SuppressWarnings("javadoc")
public class CRSHelperFunctionsTest {

	@Test
	public void testFromCode() {
		String code = "EPSG:4326";
		Map<String, Object> args = new HashMap<>();
		args.put("code", code);
		CRSDefinition crs = CRSHelperFunctions._from(args);
		assertNotNull(crs);
		assertNotNull(crs.getCRS());
		assertTrue(crs instanceof CodeDefinition);
		assertEquals(code, ((CodeDefinition) crs).getCode());
	}

	@Test
	public void testFromWKT() {
		String wkt = "GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137,298.257223563,AUTHORITY[\"EPSG\",\"7030\"]],AUTHORITY[\"EPSG\",\"6326\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.01745329251994328,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4326\"]]";
		CRSDefinition crs = CRSHelperFunctions._fromWKT(wkt);
		assertNotNull(crs);
		assertNotNull(crs.getCRS());
		assertTrue(crs instanceof WKTDefinition);
		assertEquals(wkt, ((WKTDefinition) crs).getWkt());
	}

}
