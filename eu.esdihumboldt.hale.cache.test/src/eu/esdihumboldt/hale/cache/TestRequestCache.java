package eu.esdihumboldt.hale.cache;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.junit.Test;

//import eu.esdihumboldt.hale.cache.Request;

public class TestRequestCache {

	@Test
	public void testRequest() {
		try {
			Request.getInstance().get(new URI("http://scasoft.de"));
			Request.getInstance().get(new URI("http://google.de"));
			Request.getInstance().get(new URI("http://lensbuyersguide.com/gallery/289/1/20_iso100_105mm.jpg"));
			Request.getInstance().get(new URI("http://schemas.opengis.net/gml/3.1.1/base/gml.xsd"));
			Request.getInstance().get(new URI("http://scasoft.de"));
			Request.getInstance().get(new URI("http://google.de"));
			Request.getInstance().get(new URI("http://lensbuyersguide.com/gallery/289/1/20_iso100_105mm.jpg"));
			Request.getInstance().get(new URI("http://schemas.opengis.net/gml/3.1.1/base/gml.xsd"));
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(true);
	}
}
