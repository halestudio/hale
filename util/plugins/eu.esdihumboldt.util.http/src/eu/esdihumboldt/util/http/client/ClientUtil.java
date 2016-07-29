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

/**
 * HTTP client utilities
 * 
 * @author Simon Templer, Michel Krämer
 */
public class ClientUtil {

	/**
	 * Create a thread safe HTTP client
	 * 
	 * @return the created HTTP client
	 */
	public static CloseableHttpClient createThreadSafeHttpClient() {
		// create HTTP client
		return threadSafeHttpClientBuilder().build();
	}

	/**
	 * Create a thread safe HTTP client
	 * 
	 * @return the created HTTP client
	 */
	public static HttpClientBuilder threadSafeHttpClientBuilder() {
		// create HTTP client builder
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		return HttpClientBuilder.create().setConnectionManager(cm);
	}

}
