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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
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
import org.apache.log4j.Logger;

public class Request {
	private CacheConfig cacheConfig;
	private HttpClient client;
	public final String cacheName = "HALE_WebRequest";
	
	private static Logger _log = Logger.getLogger(Request.class);
	
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
		
		// create a Cache instance	// TODO provide storage path via settings page? currently system default is used
		Cache cache = new Cache(this.cacheName, 300, MemoryStoreEvictionPolicy.LRU, true, "", true, 0, 0, true, 0, null);
		
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
	
	/**
	 * @see Request#get(URI)
	 */
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
		
		if (uri.getHost() == null) {
			throw new Exception("Empty host!");
		}
		
		// get the current cache for webrequests
		Cache cache = CacheManager.getInstance().getCache(this.cacheName);
		String name = this.removeSpecialChars(uri.toString());
		
		InputStream stream = null;
		String content = "";
		
		// if the entry does not exist fetch it from the web
		if (cache.get(name) == null){
			HttpResponse response = client.execute(new HttpGet(uri.toString()), new BasicHttpContext());
			HttpEntity entity = response.getEntity();
			
			EntityUtils.consume(entity);
			
			// convert the stream to a string
			InputStream in = entity.getContent();
			
			// sometimes the given InputStream from entity.getContent() is not a working one
			if (in.available() == 0) {
				in = uri.toURL().openStream();
			}
			
			content = this.streamToString(in);
			
			// and add it to the cache
			cache.put(new Element(name, content));
		} else {
			content = (String) cache.get(name).getObjectValue();
			
			// create the result stream
			stream = new ByteArrayInputStream(content.getBytes());
		}
		
		
		_log.info("Cachesize (Memory/Disk): "+CacheManager.getInstance().getCache(this.cacheName).getMemoryStoreSize()+
				" / "+CacheManager.getInstance().getCache(this.cacheName).getDiskStoreSize());
		
		_log.info(CacheManager.getInstance().getCache(this.cacheName).getStatistics().toString());
		
		return stream;
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
		
		return txt;
	}
	
	/**
	 * Converts a {@link InputStream} to a {@link String}.
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public String streamToString(InputStream in) throws IOException {
		if (in != null) {
			Writer writer = new StringWriter();
			
			char[] buffer = new char[1024];
			try {
				InputStreamReader isr = new InputStreamReader(in);
//				System.err.println("InputStreamReader.ready() "+isr.ready());
				Reader reader = new BufferedReader(isr);
//				System.err.println("Reader.ready() "+reader.ready());
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				in.close();
			}
			
			return writer.toString();
			} else {        
			return "";
		}
	}
	
	/**
	 * @see CacheManager#flush(String)
	 */
	public void flush() {
		CacheManager.flush(this.cacheName);
	}
}
