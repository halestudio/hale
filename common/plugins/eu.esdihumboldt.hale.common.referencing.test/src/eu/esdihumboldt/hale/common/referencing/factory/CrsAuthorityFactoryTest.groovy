/*
 * Copyright (c) 2018 wetransform GmbH
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

package eu.esdihumboldt.hale.common.referencing.factory

import static org.junit.Assert.*

import org.geotools.referencing.CRS
import org.junit.Test

import groovy.transform.CompileStatic

/**
 * Tests for {@link CRSAuthorityFactory} implementations
 * 
 * @author Florian Esser
 */
@CompileStatic
class CrsAuthorityFactoryTest {
	/**
	 * Test CRS factories for AdV codes
	 * 
	 * @throws Exception
	 */
	@Test
	void testAdvCrsFactories() throws Exception {
		assertNotNull(CRS.decode('ADV:ETRS89_UTM32'))
		assertNotNull(CRS.decode('ADV:ETRS89_UTM33'))
		assertNotNull(CRS.decode('ADV:ETRS89_Lat-Lon'))
		assertNotNull(CRS.decode('ADV:DE_DHDN_3GK2'))
		assertNotNull(CRS.decode('ADV:DE_DHDN_3GK3'))
		assertNotNull(CRS.decode('ADV:DE_DHHN92_NH'))

		assertNotNull(CRS.decode('urn:adv:crs:etrs89_utm32'))
		assertNotNull(CRS.decode('urn:adv:crs:etrs89_utm33'))
		assertNotNull(CRS.decode('urn:adv:crs:etrs89_lat-lon'))
		assertNotNull(CRS.decode('urn:adv:crs:de_dhdn_3gk2'))
		assertNotNull(CRS.decode('urn:adv:crs:de_dhdn_3gk3'))
		assertNotNull(CRS.decode('urn:adv:crs:de_dhhn92_nh'))
	}
}
