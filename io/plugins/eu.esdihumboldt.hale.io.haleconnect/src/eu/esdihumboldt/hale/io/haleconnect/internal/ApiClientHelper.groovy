/*
 * Copyright (c) 2018 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.haleconnect.internal

import java.text.MessageFormat

import eu.esdihumboldt.hale.io.haleconnect.BasePathResolver
import eu.esdihumboldt.util.http.ProxyUtil

/**
 * Helper class for the ApiClient classes of the different hale connect APIs
 * 
 * @author Florian Esser
 */
class ApiClientHelper {

	/**
	 * Sets common properties for an ApiClient. Since there is no common 
	 * interface for the different generated ApiClient classes, the
	 * properties are set dynamically. For this to succeed, the ApiClient
	 * must respond to the following methods:
	 * <ul>
	 * <li><code>getHttpClient()</code></li>
	 * <li><code>setBasePath(String)</code></li>
	 * <li><code>setApiKey(String)</code></li>
	 * <li><code>setApiKeyPrefix(String)</code></li>
	 * </ul>
	 * 
	 * @param apiClient the ApiClient
	 * @param service one of the services defined in {@link HaleConnectServices}
	 * @param resolver the base path resolver
	 * @param apiKey the API key
	 */
	static void setApiClientProperties(Object apiClient, String service, BasePathResolver resolver, String apiKey) {
		def basePath = resolver.getBasePath(service)
		if (!basePath) {
			throw new IllegalStateException("Unable to resolve base path")
		}

		if (apiClient.metaClass.respondsTo(apiClient, "setBasePath")) {
			apiClient.basePath = basePath
		}

		if (apiClient.metaClass.respondsTo(apiClient, "getHttpClient")) {
			URI basePathUri
			try {
				basePathUri = URI.create(basePath)
			} catch (IllegalArgumentException e) {
				throw new IllegalStateException(MessageFormat.format("Base path \"{0}\" is not a valid URL", basePath))
			}

			def proxy = ProxyUtil.findProxy(basePathUri)
			if (!proxy) {
				proxy = Proxy.NO_PROXY
			}
			apiClient.httpClient.proxy = proxy
		}

		if (apiClient.metaClass.respondsTo(apiClient, "setApiKey")) {
			apiClient.apiKey = apiKey
			apiClient.apiKeyPrefix = "Bearer"
		}
	}
}
