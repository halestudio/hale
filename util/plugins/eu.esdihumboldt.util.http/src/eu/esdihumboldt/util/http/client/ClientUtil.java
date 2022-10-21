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

package eu.esdihumboldt.util.http.client;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import eu.esdihumboldt.util.http.client.metrics.PoolingHttpClientConnectionManagerMetrics;
import eu.esdihumboldt.util.metrics.CollectorRegistryService;

/**
 * HTTP client utilities
 * 
 * @author Simon Templer, Michel Krämer
 */
public class ClientUtil {

	/**
	 * Create a thread safe HTTP client
	 * 
	 * @param clientName the client name the metrics should be labeled with
	 * 
	 * @return the created HTTP client
	 */
	public static CloseableHttpClient createThreadSafeHttpClient(String clientName) {
		// create HTTP client
		return threadSafeHttpClientBuilder(clientName).build();
	}

	/**
	 * Create a thread safe HTTP client
	 * 
	 * @param clientName the client name the metrics should be labeled with
	 * 
	 * @return the created HTTP client
	 */
	public static HttpClientBuilder threadSafeHttpClientBuilder(String clientName) {
		return threadSafeHttpClientBuilder(clientName, null, null, false);
	}

	/**
	 * Create a thread safe HTTP client
	 * 
	 * @param clientName the client name the metrics should be labeled with
	 * @param maxConnections the maximum number of total connections or
	 *            <code>null</code> to use the default (20)
	 * @param maxConnPerRoute the maximum number of connections per route or
	 *            <code>null</code> to use the default (2)
	 * @param enablePerHostMetrics if metrics that include information on routes
	 *            per individual host should be collected (if metric collection
	 *            is enabled)
	 * 
	 * @return the created HTTP client
	 */
	public static HttpClientBuilder threadSafeHttpClientBuilder(String clientName,
			Integer maxConnections, Integer maxConnPerRoute, boolean enablePerHostMetrics) {
		// create HTTP client builder
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();

		if (clientName != null) {
			PoolingHttpClientConnectionManagerMetrics.install(cm, clientName, enablePerHostMetrics,
					CollectorRegistryService.DEFAULT);
		}

		if (maxConnections != null) {
			cm.setMaxTotal(maxConnections);
		}

		if (maxConnPerRoute != null) {
			cm.setDefaultMaxPerRoute(maxConnPerRoute);
		}

		return HttpClientBuilder.create().setConnectionManager(cm);
	}

}
