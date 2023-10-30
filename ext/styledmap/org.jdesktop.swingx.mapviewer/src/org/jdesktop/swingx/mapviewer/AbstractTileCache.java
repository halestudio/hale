package org.jdesktop.swingx.mapviewer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jdesktop.swingx.mapviewer.DefaultTileFactory.TileRunner;

import eu.esdihumboldt.util.http.ProxyUtil;
import eu.esdihumboldt.util.http.client.ClientProxyUtil;
import eu.esdihumboldt.util.http.client.ClientUtil;

/*+-------------+----------------------------------------------------------*
 *|  |  |_|_|_|_|   Fraunhofer-Institut fuer Graphische Datenverarbeitung  *
 *|__|__|_|_|_|_|     (Fraunhofer Institute for Computer Graphics)         *
 *|  |  |_|_|_|_|                                                          *
 *|__|__|_|_|_|_|                                                          *
 *|  __ |    ___|                                                          *
 *| /_  /_  / _ |     Fraunhoferstrasse 5                                  *
 *|/   / / /__/ |     D-64283 Darmstadt, Germany                           *
 *+-------------+----------------------------------------------------------*/

/**
 * AbstractTileCache
 * 
 * @author Simon Templer
 */
public abstract class AbstractTileCache implements TileCache {

	private static final Log log = LogFactory.getLog(AbstractTileCache.class);

	private final Map<Proxy, CloseableHttpClient> clients = new HashMap<Proxy, CloseableHttpClient>();

	/**
	 * Load the tile image
	 * 
	 * @param tile the tile info
	 * @return the tile image or <code>null</code> if loading failed
	 */
	protected abstract BufferedImage load(TileInfo tile);

	/**
	 * Opens an input stream for the given URI, make sure to close it after user
	 * 
	 * @param uri the URI
	 * @return the input stream
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	@SuppressWarnings("javadoc")
	protected InputStream openInputStream(URI uri) throws IllegalStateException, IOException {
		if (!uri.getScheme().startsWith("http")) {
			// open non-http uris (e.g. filesystem, jar entry)
			return uri.toURL().openStream();
		}
		else {
			// open a http connection using the commons http client
			Proxy proxy = ProxyUtil.findProxy(uri);
			CloseableHttpClient client = getHttpClient(proxy);

			HttpGet get = new HttpGet(uri);
			HttpResponse response = client.execute(get);

			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode != HttpStatus.SC_OK) {
				log.warn("Getting tile failed: " + response.getStatusLine());
			}

			return response.getEntity().getContent();
		}
	}

	/**
	 * Provides a HTTP client for {@link TileRunner}s to use
	 * 
	 * @param proxy the connection proxy
	 * @return the HTTP client
	 */
	protected synchronized CloseableHttpClient getHttpClient(Proxy proxy) {
		CloseableHttpClient client = clients.get(proxy);

		if (client == null) {
			HttpClientBuilder builder = ClientUtil.threadSafeHttpClientBuilder(null);
			builder = ClientProxyUtil.applyProxy(builder, proxy);

			client = builder.build();

			clients.put(proxy, client);
		}

		return client;
	}

	/**
	 * @see TileCache#clear()
	 */
	@Override
	public synchronized void clear() {
		// close and remove HTTP clients

		for (CloseableHttpClient client : clients.values()) {
			try {
				client.close();
			} catch (Exception e) {
				log.debug("Error closing HTTP client", e);
			}
		}

		clients.clear();
	}

}
