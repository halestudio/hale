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

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.common.io.ByteStreams;

import de.fhg.igd.osgi.util.OsgiUtils;
import de.fhg.igd.osgi.util.configuration.IConfigurationService;
import de.fhg.igd.osgi.util.configuration.JavaPreferencesConfigurationService;
import de.fhg.igd.osgi.util.configuration.NamespaceConfigurationServiceDecorator;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.util.PlatformUtil;
import eu.esdihumboldt.util.http.ProxyUtil;
import eu.esdihumboldt.util.http.client.ClientProxyUtil;
import eu.esdihumboldt.util.http.client.ClientUtil;
import eu.esdihumboldt.util.io.InputStreamDecorator;
import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

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

	private final Map<Proxy, CloseableHttpClient> clients = new HashMap<Proxy, CloseableHttpClient>();

	/**
	 * The instance of {@link Request}
	 */
	private static Request instance;

	/**
	 * Constructor.
	 */
	private Request() {
		// FIXME right way to aquire configuration service?
		IConfigurationService org = OsgiUtils.getService(IConfigurationService.class);
		if (org == null) {
			// if no configuration service is present, fall back to new instance

			// 1. use user prefs, may not have rights to access system prefs
			// 2. no default properties
			// 3. default to system properties
			org = new JavaPreferencesConfigurationService(false, null, true);
		}

		configService = new NamespaceConfigurationServiceDecorator(org,
				Request.class.getPackage().getName().replace(".", DELIMITER), //$NON-NLS-1$
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
		File cacheDir = null;
		try {
			// this will throw up in non-OSGi environments
			cacheDir = PlatformUtil.getInstanceLocation();
		} catch (Throwable t) {
			cacheEnabled = false;
		}

		if (cacheEnabled) {
			try {
				if (cacheDir == null) {
					cacheDir = new File(System.getProperty("java.io.tmpdir"));
				}

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
				if (HaleCacheManager.create(new ByteArrayInputStream(data.toByteArray()))
						.getCache(CACHE_NAME) != null) {
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

		String key = uri.toString(); // removeSpecialChars(uri.toString());
		Cache cache = null;
		Element element = null;
		if (cacheEnabled) {
			// get the current cache for web requests
			cache = HaleCacheManager.getInstance().getCache(CACHE_NAME);

			element = cache.get(key);
		}

		if (element == null) {
			// if the entry does not exist fetch it from the web
			InputStream in = openStream(uri);
			byte[] data;
			try {
				data = ByteStreams.toByteArray(in);
			} finally {
				in.close();
			}

			if (cache != null) {
				// and add it to the cache
				cache.put(new Element(key, data));
			}

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
	 * @return the opened input stream, the caller is responsible to close it
	 * @throws IOException if opening the input stream fails
	 */
	private InputStream openStream(URI uri) throws IOException {
		Proxy proxy = ProxyUtil.findProxy(uri);
		CloseableHttpClient client = getClient(proxy);

		HttpGet httpget = new HttpGet(uri);
		final CloseableHttpResponse response = client.execute(httpget);

		InputStream in;

		if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			// close connection
			response.close();

			// fall back to URL.openStream
			in = uri.toURL().openStream();
		}
		else {
			HttpEntity entity = response.getEntity();

			// create InputStream
			in = new InputStreamDecorator(entity.getContent()) {

				@Override
				public void close() throws IOException {
					super.close();
					// ensure the response is closed
					response.close();
				}

				@Override
				protected void finalize() throws Throwable {
					// not sure if this actually has any effect
					close();

					super.finalize();
				}

			};
		}

		return in;
	}

	/**
	 * Get the HTTP client for a given proxy
	 * 
	 * @param proxy the proxy
	 * @return the client configured for the proxy
	 */
	private synchronized CloseableHttpClient getClient(Proxy proxy) {
		CloseableHttpClient client = clients.get(proxy);

		if (client == null) {
			String clientName = "hale-request-" + proxy.toString();
			HttpClientBuilder builder = ClientUtil.threadSafeHttpClientBuilder(clientName);
			builder = ClientProxyUtil.applyProxy(builder, proxy);

			// set timeouts

			// determine from Oracle VM specific system properties, see
			// http://docs.oracle.com/javase/7/docs/technotes/guides/net/properties.html
			int connectTimeout;
			String cts = System.getProperty("sun.net.client.defaultConnectTimeout");
			try {
				connectTimeout = Integer.parseInt(cts);
			} catch (Exception e) {
				// fall back to default
				connectTimeout = 10000;
			}
			int socketTimeout;
			String sts = System.getProperty("sun.net.client.defaultReadTimeout");
			try {
				socketTimeout = Integer.parseInt(sts);
			} catch (Exception e) {
				// fall back to default
				socketTimeout = 20000;
			}

			// socket timeout
			/*
			 * Unclear when this setting would apply (doc says for non-blocking
			 * I/O operations), it does not seem to be applied for requests as
			 * done in openStream (instead the value in
			 * RequestConfig.socketTimeout is used)
			 */
			SocketConfig socketconfig = SocketConfig.custom().setSoTimeout(socketTimeout).build();
			// connection and socket timeout
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout)
					.setConnectTimeout(connectTimeout).build();

			builder.setDefaultRequestConfig(requestConfig).setDefaultSocketConfig(socketconfig);

			// customizable behavior

			client = builder.build();

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
