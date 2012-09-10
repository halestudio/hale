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

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.log4j.Logger;

import de.fhg.igd.osgi.util.OsgiUtils;
import de.fhg.igd.osgi.util.configuration.IConfigurationService;
import de.fhg.igd.osgi.util.configuration.JavaPreferencesConfigurationService;
import de.fhg.igd.osgi.util.configuration.NamespaceConfigurationServiceDecorator;

/**
 * This class manages requests and caching for remote files.
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class Request {

	private String cacheName = "", cachePath = ""; //$NON-NLS-1$ //$NON-NLS-2$
	private boolean enabled = true;

	private static final String DELIMITER = "/"; //$NON-NLS-1$
	private IConfigurationService org;

	private static Logger _log = Logger.getLogger(Request.class);

	HttpClient httpclient = ClientUtil.createThreadSafeHttpClient();

	/**
	 * The instance of {@link Request}
	 */
	private static Request instance;

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

		this.org = new NamespaceConfigurationServiceDecorator(org, Request.class.getPackage()
				.getName().replace(".", DELIMITER), //$NON-NLS-1$
				DELIMITER);

		// get saved seeting
		this.enabled = this.org.getBoolean("cache.enabled", false); //$NON-NLS-1$

		// initialize the cache
		this.init();
	}

	/**
	 * Initialize the cache.
	 */
	private void init() {
		if (this.enabled) {
			this.cacheName = this.org.get("cache.name"); //$NON-NLS-1$
			this.cachePath = this.org.get("cache.path"); //$NON-NLS-1$

			// use system property to set the proper diskStorePath
			String tmpDir = System.getProperty("java.io.tmpdir"); //$NON-NLS-1$
			System.setProperty("java.io.tmpdir", this.cachePath); //$NON-NLS-1$

			// this cache has already been initialized
			if (CacheManager.getInstance().getCache(this.cacheName) != null) {
				return;
			}

			// initialize CacheManager
			CacheManager.create();

			// create a Cache instance - providing cachePath has no effect
			Cache cache = new Cache(this.cacheName, 300, MemoryStoreEvictionPolicy.LRU, true,
					this.cachePath, true, 0, 0, true, 0, null);

			// set disk store path - this has no effect!
			cache.setDiskStorePath(this.cachePath);

			// add it to CacheManger
			CacheManager.getInstance().addCache(cache);

			// reset java.io.tmpdir
			System.setProperty("java.io.tmpdir", tmpDir); //$NON-NLS-1$
		}
	}

	/**
	 * Returns the instance of this class
	 * 
	 * @return instance
	 */
	public static Request getInstance() {
		if (instance == null) {
			instance = new Request();
		}
		return instance;
	}

	/**
	 * @param uri to load from
	 * 
	 * @return {@link InputStream}
	 * 
	 * @throws URISyntaxException if the uri is malformed
	 * @throws Exception may contain IOException
	 * 
	 * @see Request#get(URI)
	 */
	public InputStream get(String uri) throws URISyntaxException, Exception {
		return this.get(new URI(uri));
	}

	/**
	 * This function handles all Request and does the caching.
	 * 
	 * @param uri to file
	 * 
	 * @return an {@link InputStream} to uri
	 * 
	 * @throws Exception if something goes wrong
	 */
	public InputStream get(URI uri) throws Exception {
		// check for local files
		if (uri.toString().startsWith("file:") || uri.toString().startsWith("bundleresource:")) { //$NON-NLS-1$
			return this.getLocal(uri.toURL());
		}

		// no host given
		if (uri.getHost() == null) {
			throw new Exception("Empty host!"); //$NON-NLS-1$
		}

		// no caching activated
		if (!this.enabled) {
			return uri.toURL().openStream();
		}

		// get the current cache for webrequests
		Cache cache = CacheManager.getInstance().getCache(this.cacheName);
		String link = this.removeSpecialChars(uri.toString());

		InputStream stream = null;
		String content = ""; //$NON-NLS-1$

		// if the entry does not exist fetch it from the web
		if (cache.get(link) == null) {
			InputStream in;

			// fallback encoding
			String encoding = "UTF-8"; //$NON-NLS-1$

			// try to download stuff with HttpGet
//			HttpClient httpclient = ClientUtil.createThreadSafeHttpClient();
			HttpGet httpget = new HttpGet(uri);
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();

			// create InputStream
			in = entity.getContent();

			// use url.openStream() as fallback if there aren't bytes available
			if (in.available() <= 0) {
				in = uri.toURL().openStream();
			}
			// if HttpGet was successful we try to get the encoding
			else if (entity.getContentLength() > 0) {
				// first from the data itself
				if (entity.getContentEncoding() != null) {
					encoding = entity.getContentEncoding().getValue();
				}
				// or from the returned header codes
				else {
					Header[] header = response.getHeaders("Content-Type"); //$NON-NLS-1$
					for (Header h : header) {
						String head = h.getValue();
						if (head.contains("charset=")) //$NON-NLS-1$
							encoding = h
									.getValue()
									.substring(h.getValue().indexOf("charset=")).replace("charset=", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
				}
			}

			// create a String out of the Stream
			content = this.streamToString(in, encoding);

			// and add it to the cache
			cache.put(new Element(link, content));

			// fetch the file from cache to prevent already closed streams
			return this.get(uri);
		}
		else {
			content = (String) cache.get(link).getObjectValue();

			// create the result stream
			stream = new ByteArrayInputStream(content.getBytes("UTF-8")); //$NON-NLS-1$
		}

		_log.debug("Cachesize (Memory/Disk): " + CacheManager.getInstance().getCache(this.cacheName).getMemoryStoreSize() + //$NON-NLS-1$
				" / " + CacheManager.getInstance().getCache(this.cacheName).getDiskStoreSize()); //$NON-NLS-1$

//		_log.info(CacheManager.getInstance().getCache(this.cacheName).getStatistics().toString()); // this may decrease performance

		return stream;
	}

	/**
	 * This function is used if the url is a local file.
	 * 
	 * @param file path to file
	 * 
	 * @return {@link InputStream}
	 * 
	 * @throws IOException if the file could not be read
	 */
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
		txt = txt.replace("http://", ""); //$NON-NLS-1$ //$NON-NLS-2$
		txt = txt.replace("/", "_"); //$NON-NLS-1$ //$NON-NLS-2$
		txt = txt.replace(":", "_"); //$NON-NLS-1$ //$NON-NLS-2$

		return txt;
	}

	/**
	 * Converts a {@link InputStream} to a {@link String}.
	 * 
	 * @param in the {@link InputStream}
	 * @param encoding the encoding
	 * 
	 * @return the whole content of in
	 * 
	 * @throws IOException if the file could not be read
	 */
	public String streamToString(InputStream in, String encoding) throws IOException {
		if (in != null) {
			// create a StringWriter
			Writer writer = new StringWriter();

			// create a buffer
			char[] buffer = new char[1024];
			try {
				// try to get a InputStreamReader from InputStream with encoding
				InputStreamReader isr = new InputStreamReader(in, encoding);
				Reader reader = new BufferedReader(isr);
				int n;

				// read all data
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				// close the InputStream
				in.close();
			}

			// and return its data
			return writer.toString();
		}
		// if the InputStrean is null, return ""
		else {
			return ""; //$NON-NLS-1$
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

	/**
	 * Is true if caching is enabled.
	 * 
	 * @return boolean
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * 
	 * @param enabled enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		this.init();
	}
}
