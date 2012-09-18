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

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.junit.Test;

/**
 * Tests for cached requests
 * 
 * @author Andreas Burchert
 */
public class RequestTest {

	/**
	 * Test retrieving online resources using {@link Request}
	 */
	@Test
	public void testRequest() {
		// FIXME activate cache?
		try {
			Request.getInstance().get(new URI("http://schemas.opengis.net/gml/3.1.1/base/gml.xsd"));
			Request.getInstance().get(new URI("http://schemas.opengis.net/gml/3.1.1/base/gml.xsd"));
			Request.getInstance().get(new URI("http://schemas.opengis.net/gml/3.1.1/base/gml.xsd"));
			Request.getInstance().get(new URI("http://schemas.opengis.net/gml/3.1.1/base/gml.xsd"));
			Request.getInstance().get(
					new URI("http://www.fraunhofer.de/rss/presse.jsp?et_cid=2&et_lid=2"));

			Request.getInstance().get(new URI("http://schemas.opengis.net/gml/3.1.1/base/gml.xsd"));
			Request.getInstance().get(new URI("http://schemas.opengis.net/gml/3.1.1/base/gml.xsd"));
			Request.getInstance().get(
					new URI("http://www.fraunhofer.de/rss/presse.jsp?et_cid=2&et_lid=2"));
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			assertTrue(false);
		} catch (IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			assertTrue(false);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		assertTrue(true);
		// FIXME any better way to check the test results?

		CacheManager.getInstance().shutdown();
	}
}
