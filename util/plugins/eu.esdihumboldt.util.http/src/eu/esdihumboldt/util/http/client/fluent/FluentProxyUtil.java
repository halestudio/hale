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

package eu.esdihumboldt.util.http.client.fluent;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;

import eu.esdihumboldt.util.http.ProxyUtil;
import eu.esdihumboldt.util.http.client.ClientProxyUtil;

/**
 * Proxy utility methods related to the Apache HTTP fluent client API.
 * 
 * @author Simon Templer
 */
public class FluentProxyUtil {

	/**
	 * setup the given request object to go via proxy
	 * 
	 * @param request A fluent request
	 * @param proxy applying proxy to the fluent request
	 * @return Executor, for a fluent request
	 */
	public static Executor setProxy(Request request, Proxy proxy) {
		ProxyUtil.init();
		Executor executor = Executor.newInstance();

		if (proxy != null && proxy.type() == Type.HTTP) {
			InetSocketAddress proxyAddress = (InetSocketAddress) proxy.address();

			// set the proxy
			HttpHost proxyHost = new HttpHost(proxyAddress.getHostName(), proxyAddress.getPort());
			request.viaProxy(proxyHost);

			String userName = System.getProperty("http.proxyUser");
			String password = System.getProperty("http.proxyPassword");

			boolean useProxyAuth = userName != null && !userName.isEmpty();

			if (useProxyAuth) {
				Credentials cred = ClientProxyUtil.createCredentials(userName, password);

				executor.auth(new AuthScope(proxyHost.getHostName(), proxyHost.getPort()), cred);
			}
		}
		return executor;
	}

}
