/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
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
