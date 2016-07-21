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

package eu.esdihumboldt.util.http;

import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;

/**
 * Proxy utility methods
 * 
 * @author Simon Templer
 */
public class ProxyUtil {

	private static ALogger _log = ALoggerFactory.getLogger(ProxyUtil.class);

	private static boolean initialized = false;

	private static final Set<Runnable> initializers = new HashSet<Runnable>();

	/**
	 * Tries to find the system's proxy configuration for a given URI
	 * 
	 * @param uri the URI
	 * @return the proxy configuration (host and port)
	 */
	public static Proxy findProxy(URI uri) {
		init();

		// BUGFIX: Don't use this property since it makes the connection hang!
		// Rather set the proxy through the system properties
		// "http.proxyHost" and "http.proxyPort".
		// System.setProperty("java.net.useSystemProxies", "true");

		List<Proxy> proxies = ProxySelector.getDefault().select(uri);
		if (proxies != null && proxies.size() > 0) {
			for (Proxy proxy : proxies) {
				if (proxy.type() == Proxy.Type.HTTP) {
					return proxy;
				}
			}
		}

		return Proxy.NO_PROXY;

		/*
		 * The following code is obsolete System properties are handled
		 * correctly by proxy selector The only thing that the code supports
		 * additionally is setting using a proxy for the host that is the proxy
		 * host
		 */
		/*
		 * String strProxyHost = System.getProperty("http.proxyHost"); String
		 * strProxyPort = System.getProperty("http.proxyPort"); String
		 * strNonProxyHosts = System.getProperty("http.nonProxyHosts"); String[]
		 * nonProxyHosts; if (strNonProxyHosts != null) { nonProxyHosts =
		 * strNonProxyHosts.split("\\|"); } else { nonProxyHosts = new
		 * String[0]; }
		 * 
		 * if (strProxyHost != null && strProxyPort != null) { boolean noProxy =
		 * false; for (int i = 0; i < nonProxyHosts.length; ++i) { if
		 * (nonProxyHosts[i].equalsIgnoreCase(uri.getHost())) { noProxy = true;
		 * break; } }
		 * 
		 * if (!noProxy) { int proxyPort = Integer.parseInt(strProxyPort);
		 * return new InetSocketAddress(strProxyHost, proxyPort); } }
		 * 
		 * return null;
		 */
	}

	/**
	 * Add a proxy initializer. It will be called before the first proxy usage
	 * 
	 * @param initializer the initializer
	 */
	public static void addInitializer(Runnable initializer) {
		synchronized (initializers) {
			if (initialized) {
				try {
					initializer.run();
				} catch (Exception e) {
					_log.error("Error executing proxy initializer", e); //$NON-NLS-1$
				}
			}
			else {
				initializers.add(initializer);
			}
		}
	}

	/**
	 * Run proxy initializers.
	 */
	public static void init() {
		synchronized (initializers) {
			if (!initialized) {
				for (Runnable initializer : initializers) {
					try {
						initializer.run();
					} catch (Exception e) {
						_log.error("Error executing proxy initializer", e); //$NON-NLS-1$
					}
				}

				initializers.clear();

				initialized = true;
			}
		}
	}

}
