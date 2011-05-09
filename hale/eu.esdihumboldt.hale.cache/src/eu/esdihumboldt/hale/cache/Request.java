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

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;

import de.fhg.igd.osgi.util.OsgiUtils;
import de.fhg.igd.osgi.util.configuration.IConfigurationService;
import de.fhg.igd.osgi.util.configuration.JavaPreferencesConfigurationService;
import de.fhg.igd.osgi.util.configuration.NamespaceConfigurationServiceDecorator;

public class Request {

	private String cacheName = "", cachePath = "";
	private boolean enabled = true;
	
	private static final String DELIMITER = "/";
	private IConfigurationService org;
	
	private static Logger _log = Logger.getLogger(Request.class);
	
	/**
	 * The instance of {@link Request}
	 */
	private static Request instance = new Request();
	
	/**
	 * Constructor.
	 */
	private Request() {
		IConfigurationService org = OsgiUtils.getService(IConfigurationService.class);
		if (org == null) {
			// if no configuration service is present, fall back to new instance
			
			// 1. use user prefs, may not have rights to access system prefs
		    // 2. no default properties
		    // 3. don't default to system properties
			org = new JavaPreferencesConfigurationService(false, null, false);
		}
		
		this.org = new NamespaceConfigurationServiceDecorator(
				org, 
				Request.class.getPackage().getName().replace(".", DELIMITER),  //$NON-NLS-1$
				DELIMITER);
		
		// get saved seeting
		this.enabled = this.org.getBoolean("cache.enabled", false);
		
		// initialize the cache
		this.init();
	}
	
	private void init() {
		if (this.enabled) {
			this.cacheName = this.org.get("cache.name");
			this.cachePath = this.org.get("cache.path");
			
			// use system property to set the proper diskStorePath
			String tmpDir = System.getProperty("java.io.tmpdir");
			System.setProperty("java.io.tmpdir", this.cachePath);
			
			// this cache has already been initialized
			if (CacheManager.getInstance().getCache(this.cacheName) != null) {
				return;
			}
			
			// initialize CacheManager
			CacheManager.create();
			
			// create a Cache instance - providing cachePath has no effect
			Cache cache = new Cache(this.cacheName, 300, MemoryStoreEvictionPolicy.LRU, 
					true, this.cachePath, true, 0, 0, true, 0, null);
			
			// set disk store path - this has no effect!
			cache.setDiskStorePath(this.cachePath);
			
			
			// add it to CacheManger
			CacheManager.getInstance().addCache(cache);
			
			// reset java.io.tmpdir
			System.setProperty("java.io.tmpdir", tmpDir);
		}
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
		
		// no caching activated
		if (!this.enabled) {
			return uri.toURL().openStream();
		}
		
		// get the current cache for webrequests
		Cache cache = CacheManager.getInstance().getCache(this.cacheName);
		String name = this.removeSpecialChars(uri.toString());
		
		InputStream stream = null;
		String content = "";
		
		// if the entry does not exist fetch it from the web
		if (cache.get(name) == null){
			InputStream in;
			
			in = uri.toURL().openStream();
			
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
				Reader reader = new BufferedReader(isr);
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
		if (this.enabled)
			CacheManager.flush(this.cacheName);
	}
	
	/**
	 * @see CacheManager#shutdown()
	 */
	public void shutdown() {
		CacheManager.getInstance().shutdown();
	}
	
	/**
	 * @see CacheManager#removalAll()
	 */
	public void clear() {
		CacheManager.getInstance().getCache(cacheName).removeAll();
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		this.init();
	}
}
