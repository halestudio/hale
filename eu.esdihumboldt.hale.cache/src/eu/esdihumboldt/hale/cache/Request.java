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
import java.net.URISyntaxException;
import java.net.URL;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClient;
import org.apache.http.impl.client.cache.ehcache.EhcacheHttpCacheStorage;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;

public class Request {
	private CacheConfig cacheConfig;
	private HttpClient client;
	public final String cacheName = "HALE_WebRequest";
	
	/**
	 * The instance of {@link Request}
	 */
	private static Request instance = new Request();
	
	/**
	 * Constructor.
	 */
	private Request() {
		// new cache configuration
		this.cacheConfig = new CacheConfig();
		this.cacheConfig.setHeuristicCachingEnabled(true);
		this.cacheConfig.setSharedCache(true);
		
		// initialize CacheManager
		CacheManager.create();
		
		// create a Cache instance
		Cache cache = new Cache(this.cacheName, 30, MemoryStoreEvictionPolicy.LRU, true, "", true, 0, 0, true, 0, null);
		
		// add it to CacheManger
		CacheManager.getInstance().addCache(cache);
		

		// create the http storage cache
		EhcacheHttpCacheStorage ehcache = new EhcacheHttpCacheStorage(cache, this.cacheConfig);
		
		// create the http client
		this.client = new CachingHttpClient(new DefaultHttpClient(), ehcache, this.cacheConfig);
	}
	
	/**
	 * Returns the instance of this class
	 * 
	 * @return instance
	 */
	public static Request getInstance() {
		return instance;
	}
	
	public InputStream get(String uri) throws URISyntaxException, Exception {
		return this.get(new URI(uri));
	}
	
	/**
	 * This function handles all Request and does the caching.
	 * 
	 * @param name
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public InputStream get(URI uri) throws Exception {
		// check for local files
		if (uri.toString().startsWith("file:")) {
			return this.getLocal(uri.toURL());
		}
		
		// get the current cache for webrequests
		Cache cache = CacheManager.getInstance().getCache(this.cacheName);
		String name = this.removeSpecialChars(uri.toString());
		
		HttpEntity entity;
		// if the entry does not exist fetch it from the web
		if (cache.get(name) == null){
			HttpResponse response = client.execute(new HttpGet(uri.toString()), new BasicHttpContext());
			entity = response.getEntity();
			
			EntityUtils.consume(entity);
			
			// and add it to the chache
			cache.put(new Element(name, entity));
		} else {
			entity = (HttpEntity) cache.get(name).getObjectValue();
		}
		
		System.out.println("Cachesize (Memory/Disk): "+CacheManager.getInstance().getCache(this.cacheName).getMemoryStoreSize()+
				" / "+CacheManager.getInstance().getCache(this.cacheName).getDiskStoreSize());
		
		System.out.println(CacheManager.getInstance().getCache(this.cacheName).getStatistics().toString());
		
		return entity.getContent();
	}
	
	private InputStream getLocal(URL file) throws IOException {
		return file.openStream();
	}
	
	/**
	 * Removes some substrings.
	 * 
	 * @param txt String with special chars
	 * 
	 * @return clean string
	 */
	private String removeSpecialChars(String txt) {
		txt = txt.replace("http://", "");
		txt = txt.replace("/", "_");
		txt = txt.replace(":", "_");
		System.out.println(txt);
		
		return txt;
	}
}
