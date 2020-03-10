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

import static com.squareup.okhttp.CipherSuite.*

import java.text.MessageFormat

import com.squareup.okhttp.ConnectionSpec
import com.squareup.okhttp.TlsVersion

import eu.esdihumboldt.hale.io.haleconnect.BasePathResolver
import eu.esdihumboldt.util.http.ProxyUtil

/**
 * Helper class for the ApiClient classes of the different hale connect APIs
 * 
 * @author Florian Esser
 */
class ApiClientHelper {

	/**
	 * {@link ConnectionSpec} that uses TLS v1.2 only and is limited to cipher
	 * suites with perfect forward security (PFS) as specified in "Technische
	 * Richtlinie TR-02102-2, Kryptographische Verfahren: Empfehlungen und
	 * Schlüssellängen, Teil 2 - Verwendung von Transport Layer Security"
	 * (version 2019-01) as published by the German Federal Office for
	 * Information Security.
	 */
	public static final ConnectionSpec API_CONNECTION_SPEC = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
	.tlsVersions(TlsVersion.TLS_1_2)
	.cipherSuites(
	TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256,
	TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384,
	TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
	TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384,
	/*TLS_ECDHE_ECDSA_WITH_AES_128_CCM,*/    // TODO not supported
	/*TLS_ECDHE_ECDSA_WITH_AES_256_CCM,*/    // by OkHttp 2.7.5
	TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256,
	TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384,
	TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
	TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384,
	TLS_DHE_DSS_WITH_AES_128_CBC_SHA256,
	TLS_DHE_DSS_WITH_AES_256_CBC_SHA256,
	TLS_DHE_DSS_WITH_AES_128_GCM_SHA256,
	TLS_DHE_DSS_WITH_AES_256_GCM_SHA384,
	TLS_DHE_RSA_WITH_AES_128_CBC_SHA256,
	TLS_DHE_RSA_WITH_AES_256_CBC_SHA256,
	TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
	TLS_DHE_RSA_WITH_AES_256_GCM_SHA384,
	/*TLS_DHE_RSA_WITH_AES_128_CCM,*/        // TODO not supported
	/*TLS_DHE_RSA_WITH_AES_256_CCM*/)        // by OkHttp 2.7.5
	.build();

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

	/**
	 * Build a {@link ConnectionSpec} for {@link OkHttpClient}s that uses TLS v1.2 only and
	 * is limited to cipher suites with perfect forward security (PFS) as specified in
	 * "Technische Richtlinie TR-02102-2 Kryptographische Verfahren: Empfehlungen und
	 * Schlüssellängen, Teil 2 - Verwendung von Transport Layer Security" (version 2019-01)
	 * as published by the German Federal Office for Information Security.<br>
	 * <br>
	 * The result can be applied to the http client of the hale connect API clients like so:<br>
	 * <pre>
	 *     apiClient.getHttpClient().setConnectionSpecs(ApiClientHelper.buildConnectionSpec())
	 * </pre>
	 *
	 * @return Singleton list containing the connection spec
	 */
	static List<ConnectionSpec> buildConnectionSpec() {
		[API_CONNECTION_SPEC]
	}
}
