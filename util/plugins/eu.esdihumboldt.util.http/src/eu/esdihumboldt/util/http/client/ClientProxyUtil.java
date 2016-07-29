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

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.util.http.ProxyUtil;

/**
 * Proxy utility methods related to the Apache HTTP client API.
 * 
 * @author Simon Templer
 */
public class ClientProxyUtil {

	private static ALogger _log = ALoggerFactory.getLogger(ClientProxyUtil.class);

	/**
	 * Set-up the given HTTP client to use the given proxy
	 * 
	 * @param builder the HTTP client builder
	 * @param proxy the proxy
	 * @return the client builder adapted with the proxy settings
	 */
	public static HttpClientBuilder applyProxy(HttpClientBuilder builder, Proxy proxy) {
		ProxyUtil.init();

		// check if proxy shall be used
		if (proxy != null && proxy.type() == Type.HTTP) {
			InetSocketAddress proxyAddress = (InetSocketAddress) proxy.address();

			// set the proxy
			HttpHost proxyHost = new HttpHost(proxyAddress.getHostName(), proxyAddress.getPort());
			builder = builder.setProxy(proxyHost);

			String user = System.getProperty("http.proxyUser"); //$NON-NLS-1$
			String password = System.getProperty("http.proxyPassword"); //$NON-NLS-1$
			boolean useProxyAuth = user != null && !user.isEmpty();

			if (useProxyAuth) {
				// set the proxy credentials
				CredentialsProvider credsProvider = new BasicCredentialsProvider();

				credsProvider.setCredentials(
						new AuthScope(proxyAddress.getHostName(), proxyAddress.getPort()),
						createCredentials(user, password));
				builder = builder.setDefaultCredentialsProvider(credsProvider)
						.setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());
			}

			_log.trace("Set proxy to " + proxyAddress.getHostName() + ":" + //$NON-NLS-1$ //$NON-NLS-2$
					proxyAddress.getPort() + ((useProxyAuth) ? (" as user " + user) : (""))); //$NON-NLS-1$ //$NON-NLS-2$
		}

		return builder;
	}

	/**
	 * Create a credentials object.
	 * 
	 * @param user the user name
	 * @param password the password
	 * @return the created credentials
	 */
	public static Credentials createCredentials(String user, String password) {
		Credentials credentials;
		int sepIndex = user.indexOf('\\');
		if (sepIndex > 0 && sepIndex + 1 < user.length()) {
			// assume this is DOMAIN \ user for NTLM authentication
			String userName = user.substring(sepIndex + 1);
			String domain = user.substring(0, sepIndex);
			String workstation = null;
			credentials = new NTCredentials(userName, password, workstation, domain);
		}
		else {
			credentials = new UsernamePasswordCredentials(user, password);
		}
		return credentials;
	}

}
