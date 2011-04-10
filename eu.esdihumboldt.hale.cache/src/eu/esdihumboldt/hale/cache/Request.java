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

package eu.esdihumboldt.hale.cache;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.cache.CacheResponseStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClient;

public class Request {
	private CacheConfig cacheConfig;
	private HttpClient client;
	
	private static Request instance = new Request();
	
	private Request() {
		this.cacheConfig = new CacheConfig();
		this.cacheConfig.setMaxCacheEntries(1000);
		this.cacheConfig.setMaxObjectSizeBytes(8192); // 8MB
		this.cacheConfig.setHeuristicCachingEnabled(true);
		this.cacheConfig.setSharedCache(true);

		this.client = new CachingHttpClient(new DefaultHttpClient(), this.cacheConfig);
	}
	
	public static Request getInstance() {
		return instance;
	}
	
	public InputStream get(URI name) throws ClientProtocolException, IOException {
		BasicHttpContext localContext = new BasicHttpContext();
		HttpGet httpget = new HttpGet(name.toString());
		HttpResponse response = client.execute(httpget, localContext);
		HttpEntity entity = response.getEntity();
		
		EntityUtils.consume(entity);
		CacheResponseStatus responseStatus = 
			(CacheResponseStatus) localContext.getAttribute(CachingHttpClient.CACHE_RESPONSE_STATUS);
		switch (responseStatus) {
		case CACHE_HIT:
		    System.out.println("A response was generated from the cache with no requests " +
		            "sent upstream");
		    break;
		case CACHE_MODULE_RESPONSE:
		    System.out.println("The response was generated directly by the caching module");
		    break;
		case CACHE_MISS:
		    System.out.println("The response came from an upstream server");
		    break;
		case VALIDATED:
		    System.out.println("The response was generated from the cache after validating " +
		            "the entry with the origin server");
		    break;
		}
		
		return entity.getContent();
	}
}
