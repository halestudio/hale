/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.cache;

import static org.junit.Assert.fail;

import java.net.URI;

import org.junit.AfterClass;
import org.junit.Test;

/**
 * Tests for cached requests
 * 
 * @author Andreas Burchert
 */
public class RequestTest {

	/**
	 * Test retrieving online resources using {@link Request} with cache
	 * enabled.
	 */
	@Test
	public void testRequestCache() {
		testRequests(true);
	}

	/**
	 * Test retrieving online resources using {@link Request} with cache
	 * disabled.
	 */
	@Test
	public void testRequestNoCache() {
		testRequests(false);
	}

	/**
	 * Do some test requests.
	 * 
	 * @param doCache if the cache should be enabled
	 */
	private void testRequests(boolean doCache) {
		Request.getInstance().setCacheEnabled(doCache);
		try {
			Request.getInstance().get(new URI("http://schemas.opengis.net/gml/3.1.1/base/gml.xsd"))
					.close();
			Request.getInstance().get(new URI("http://schemas.opengis.net/gml/3.1.1/base/gml.xsd"))
					.close();
			Request.getInstance().get(new URI("http://schemas.opengis.net/gml/3.1.1/base/gml.xsd"))
					.close();
			Request.getInstance().get(new URI("http://schemas.opengis.net/gml/3.1.1/base/gml.xsd"))
					.close();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Shutdown the cache manager.
	 */
	@AfterClass
	public static void shutdownCache() {
		HaleCacheManager.getInstance().shutdown();
	}
}
