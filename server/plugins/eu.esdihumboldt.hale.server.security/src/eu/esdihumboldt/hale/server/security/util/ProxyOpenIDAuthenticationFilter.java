/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.server.security.util;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.openid.OpenIDAuthenticationFilter;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;

/**
 * Open ID Authentication filter that supports proxying with X-Forwarded-For
 * header.
 * 
 * @author Simon Templer
 */
public class ProxyOpenIDAuthenticationFilter extends OpenIDAuthenticationFilter {

	/**
	 * Redeclared constant from APIUtil (which is not used here not to introduce
	 * the dependency)
	 */
	public static final String SYSTEM_PROPERTY_SERVER_URL = "hale.api.base-url";

	private static final ALogger log = ALoggerFactory
			.getLogger(ProxyOpenIDAuthenticationFilter.class);

	@Override
	protected String buildReturnToUrl(HttpServletRequest request) {
		String forwardedFor = request.getHeader("X-Forwarded-For");
		String returnTo = super.buildReturnToUrl(request);

		if (forwardedFor != null) {
			log.warn("Proxy detected - X-Forwarded-For: " + forwardedFor);

			Iterable<String> parts = Splitter.on(',').trimResults().split(forwardedFor);
			String outwardProxy = null;
			try {
				outwardProxy = Iterables.get(parts, 1);

				// original returnTo
				URI org = URI.create(returnTo);
				// build proxy URL, assuming http as scheme
				URI uri = new URI("http", outwardProxy, org.getPath(), org.getQuery(),
						org.getFragment());
				returnTo = uri.toString();
			} catch (Exception e) {
				log.warn("Error building proxy return URL from X-Forwarded-For");

				// try HALE base URL system property as fall-back
				String baseUrl = System.getProperty(SYSTEM_PROPERTY_SERVER_URL);
				if (baseUrl != null) {
					try {
						// original returnTo
						URI org = URI.create(returnTo);
						// build proxy URL
						URI uri = new URI(baseUrl + org.getRawPath());
						returnTo = uri.toString();
					} catch (Exception e1) {
						log.warn("Error building proxy return URL from "
								+ SYSTEM_PROPERTY_SERVER_URL + " system property", e1);
					}
				}
			}
		}

		return returnTo;
	}
}
