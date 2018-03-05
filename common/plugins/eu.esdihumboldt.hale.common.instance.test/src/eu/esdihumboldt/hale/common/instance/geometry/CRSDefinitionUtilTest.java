/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.common.instance.geometry;

import static org.junit.Assert.assertEquals;

import java.text.MessageFormat;

import org.geotools.gml2.SrsSyntax;
import org.geotools.referencing.CRS;
import org.geotools.referencing.CRS.AxisOrder;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Test for {@link CRSDefinitionUtil}
 * 
 * @author Florian Esser
 */
@SuppressWarnings("javadoc")
public class CRSDefinitionUtilTest {

	/**
	 * Test the axis order of the WGS 84 CRS definition returned by Geotools
	 */
	@Test
	public void testEpsgCrsAxisOrder() throws Exception {
		String wgs84Code = "4326";

		for (SrsSyntax prefix : SrsSyntax.values()) {
			System.out
					.println(MessageFormat.format(">>> Testing SRS prefix \"{0}\"", prefix.name()));

			System.out.println(MessageFormat.format("Testing lat/lon \"{0}\"",
					prefix.getPrefix() + wgs84Code));
			CoordinateReferenceSystem wgs84latlon = CRS.decode(prefix.getPrefix() + wgs84Code);
			assertEquals(AxisOrder.NORTH_EAST, CRS.getAxisOrder(wgs84latlon));

			System.out.println(MessageFormat.format("Testing lon/lat \"{0}\"",
					prefix.getPrefix() + wgs84Code));
			CoordinateReferenceSystem wgs84lonlat = CRS.decode(prefix.getPrefix() + wgs84Code,
					true);

			// Longitude first currently works only with EPSG_CODE prefix
			if (SrsSyntax.EPSG_CODE.equals(prefix)) {
				assertEquals(AxisOrder.EAST_NORTH, CRS.getAxisOrder(wgs84lonlat));
			}
			else {
				// If this fails in the future, check if Geotools supports
				// longitude first for other prefixes now
				assertEquals(AxisOrder.NORTH_EAST, CRS.getAxisOrder(wgs84lonlat));
			}
		}
	}
}
