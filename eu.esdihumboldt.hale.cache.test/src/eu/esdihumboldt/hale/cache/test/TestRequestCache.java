package eu.esdihumboldt.hale.cache.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.junit.Test;

import eu.esdihumboldt.hale.cache.CacheManager;
import eu.esdihumboldt.hale.cache.Request;

public class TestRequestCache {

	@Test
	public void testRequest() {
		try {
			Request.getInstance().get(new URI("http://schemas.opengis.net/gml/3.1.1/base/gml.xsd"));
			Request.getInstance().get(new URI("http://scasoft.de"));
			Request.getInstance().get(new URI("http://google.de"));
			Request.getInstance().get(new URI("http://lensbuyersguide.com/gallery/289/1/20_iso100_105mm.jpg"));
			Request.getInstance().get(new URI("http://schemas.opengis.net/gml/3.1.1/base/gml.xsd"));
			Request.getInstance().get(new URI("http://schemas.opengis.net/gml/3.1.1/base/gml.xsd"));
			Request.getInstance().get(new URI("http://schemas.opengis.net/gml/3.1.1/base/gml.xsd"));
			Request.getInstance().get(new URI("http://www.fraunhofer.de/rss/presse.jsp?et_cid=2&et_lid=2"));
			
			Request.getInstance().get(new URI("http://lensbuyersguide.com/gallery/289/1/20_iso100_105mm.jpg"));
			Request.getInstance().get(new URI("http://schemas.opengis.net/gml/3.1.1/base/gml.xsd"));
			Request.getInstance().get(new URI("http://schemas.opengis.net/gml/3.1.1/base/gml.xsd"));
			Request.getInstance().get(new URI("http://www.fraunhofer.de/rss/presse.jsp?et_cid=2&et_lid=2"));
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
		
		CacheManager.getInstance().shutdown();
	}
}
