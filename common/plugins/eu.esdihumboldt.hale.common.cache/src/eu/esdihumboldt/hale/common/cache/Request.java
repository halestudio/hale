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

import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;

import com.google.common.io.ByteStreams;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import de.fhg.igd.osgi.util.OsgiUtils;
import de.fhg.igd.osgi.util.configuration.IConfigurationService;
import de.fhg.igd.osgi.util.configuration.JavaPreferencesConfigurationService;
import de.fhg.igd.osgi.util.configuration.NamespaceConfigurationServiceDecorator;
import eu.esdihumboldt.util.http.ProxyUtil;

/**
 * This class manages requests and caching for remote files.
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class Request {

	private static final ALogger log = ALoggerFactory.getLogger(Request.class);

	private static final String CACHE_NAME = "haleResourceCache";

	private boolean cacheEnabled;

	private static final String DELIMITER = "/"; //$NON-NLS-1$

	private final IConfigurationService configService;

	private final Map<Proxy, DefaultHttpClient> clients = new HashMap<Proxy, DefaultHttpClient>();

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
			// 3. default to system properties
			org = new JavaPreferencesConfigurationService(false, null, true);
		}

		configService = new NamespaceConfigurationServiceDecorator(org, Request.class.getPackage()
				.getName().replace(".", DELIMITER), //$NON-NLS-1$
				DELIMITER);

		// get saved seeting
		cacheEnabled = configService.getBoolean("hale.cache.enabled", true); //$NON-NLS-1$

		// initialize the cache
		init();
	}

	/**
	 * Initialize the cache.
	 */
	private void init() {
		if (cacheEnabled) {
			File cacheDir = new File(System.getProperty("java.io.tmpdir"));
			Location location = Platform.getInstanceLocation();
			if (location != null) {
				try {
					File instanceLoc = new File(URI.create(location.getURL().toString()
							.replaceAll(" ", "%20")));
					cacheDir = instanceLoc;
				} catch (Exception e) {
					// ignore
				}
			}

			try {
				// create the configuration from the template
				SimpleTemplateEngine engine = new SimpleTemplateEngine(
						Request.class.getClassLoader());
				Template template = engine.createTemplate(Request.class.getResource("ehcache.xml"));

				Map<String, Object> binding = new HashMap<>();
				// replace the cache directory
				binding.put("cache_dir", cacheDir.getAbsolutePath());

				ByteArrayOutputStream data = new ByteArrayOutputStream();
				try (Writer writer = new OutputStreamWriter(data, "UTF-8")) {
					template.make(binding).writeTo(writer);
				}

				// initialize the cache manager
				if (HaleCacheManager.create(new ByteArrayInputStream(data.toByteArray())).getCache(
						CACHE_NAME) != null) {
					return;
				}

				// create a Cache instance - providing cachePath has no effect
				Cache cache = new Cache(CACHE_NAME, 300, MemoryStoreEvictionPolicy.LRU, true, null,
						true, 0, 0, true, 0, null);

				// add it to CacheManger
				HaleCacheManager.getInstance().addCache(cache);
			} catch (Exception e) {
				log.error("Cache initialization failed", e);
			}

		}
	}

	/**
	 * Returns the instance of this class
	 * 
	 * @return instance
	 */
	public synchronized static Request getInstance() {
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
	 * @throws URISyntaxException if the URI is malformed
	 * @throws Exception may contain IOException
	 * 
	 * @see Request#get(URI)
	 */
	public InputStream get(String uri) throws URISyntaxException, Exception {
		return get(new URI(uri));
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
		String scheme = uri.getScheme();

		if (!scheme.equals("http") && !scheme.equals("https")) { //$NON-NLS-1$
			// get non-HTTP(S) resources through URL.openStream
			return getLocal(uri.toURL());
		}

		// no caching activated
		if (!cacheEnabled) {
			return openStream(uri);
		}

		// get the current cache for web requests
		Cache cache = HaleCacheManager.getInstance().getCache(CACHE_NAME);
		String key = uri.toString(); // removeSpecialChars(uri.toString());

		Element element = cache.get(key);
		if (element == null) {
			// if the entry does not exist fetch it from the web
			InputStream in = openStream(uri);
			byte[] data;
			try {
				data = ByteStreams.toByteArray(in);
			} finally {
				in.close();
			}

			// and add it to the cache
			cache.put(new Element(key, data));

			return new ByteArrayInputStream(data);
		}
		else {
			byte[] data = (byte[]) element.getObjectValue();
			return new ByteArrayInputStream(data);
		}
	}

	/**
	 * Open a stream for the given URI.
	 * 
	 * @param uri the URI
	 * @return the opened input stream
	 * @throws IOException if opening the input stream fails
	 */
	private InputStream openStream(URI uri) throws IOException {
		Proxy proxy = ProxyUtil.findProxy(uri);
		DefaultHttpClient client = getClient(proxy);

		HttpGet httpget = new HttpGet(uri);
		HttpResponse response = client.execute(httpget);

		InputStream in;

		if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			// fall back to URL.openStream
			in = uri.toURL().openStream();
		}
		else {
			HttpEntity entity = response.getEntity();

			// create InputStream
			in = entity.getContent();
		}

		return in;
	}

	/**
	 * Get the HTTP client for a given proxy
	 * 
	 * @param proxy the proxy
	 * @return the client configured for the proxy
	 */
	private synchronized DefaultHttpClient getClient(Proxy proxy) {
		DefaultHttpClient client = clients.get(proxy);

		if (client == null) {
			client = ClientUtil.createThreadSafeHttpClient();
			ProxyUtil.setupClient(client, proxy);

			// set timeout
			// 2 seconds socket timeout
			client.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 3000);
			// 2 seconds connections timeout
			client.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);

			clients.put(proxy, client);
		}

		return client;
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
	 * @see HaleCacheManager#flush(String)
	 */
	public void flush() {
		if (cacheEnabled)
			HaleCacheManager.flush(CACHE_NAME);
	}

	/**
	 * @see HaleCacheManager#shutdown()
	 */
	public void shutdown() {
		HaleCacheManager.getInstance().shutdown();
	}

	/**
	 * @see HaleCacheManager#removalAll()
	 */
	public void clear() {
		HaleCacheManager.getInstance().getCache(CACHE_NAME).removeAll();
	}

	/**
	 * Is true if caching is enabled.
	 * 
	 * @return boolean
	 */
	public boolean isCacheEnabled() {
		return cacheEnabled;
	}

	/**
	 * 
	 * @param enabled enabled
	 */
	public void setCacheEnabled(boolean enabled) {
		this.cacheEnabled = enabled;
		init();
	}

}
