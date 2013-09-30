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

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.ConsumerManager;
import org.springframework.security.openid.AxFetchListFactory;
import org.springframework.security.openid.OpenID4JavaConsumer;
import org.springframework.security.openid.OpenIDConsumerException;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;

/**
 * OpenID consumer with support for being proxied.
 * 
 * @author Simon Templer
 */
public class ProxyOpenIDConsumer extends OpenID4JavaConsumer {

	private static final ALogger log = ALoggerFactory.getLogger(ProxyOpenIDConsumer.class);

	/**
	 * @see OpenID4JavaConsumer#OpenID4JavaConsumer()
	 */
	public ProxyOpenIDConsumer() throws ConsumerException {
		super();
	}

	/**
	 * @see OpenID4JavaConsumer#OpenID4JavaConsumer(AxFetchListFactory)
	 */
	public ProxyOpenIDConsumer(AxFetchListFactory attributesToFetchFactory)
			throws ConsumerException {
		super(attributesToFetchFactory);
	}

	/**
	 * @see OpenID4JavaConsumer#OpenID4JavaConsumer(ConsumerManager,
	 *      AxFetchListFactory)
	 */
	public ProxyOpenIDConsumer(ConsumerManager consumerManager,
			AxFetchListFactory attributesToFetchFactory) throws ConsumerException {
		super(consumerManager, attributesToFetchFactory);
	}

	@Override
	public String beginConsumption(HttpServletRequest req, String identityUrl, String returnToUrl,
			String realm) throws OpenIDConsumerException {
		// unproxy returnToUrl
		String forwardedFor = req.getHeader("X-Forwarded-For");
		if (forwardedFor != null) {
			// use default returnTo URL
			returnToUrl = req.getRequestURL().toString();

			// use default realm
			try {
				URL url = new URL(returnToUrl);
				int port = url.getPort();

				StringBuilder realmBuffer = new StringBuilder(returnToUrl.length())
						.append(url.getProtocol()).append("://").append(url.getHost());

				if (port > 0) {
					realmBuffer.append(":").append(port);
				}
				realmBuffer.append("/");
				realm = realmBuffer.toString();
			} catch (MalformedURLException e) {
				log.error("Could not determine realm as returnToUrl was not a valid URL: ["
						+ returnToUrl + "]", e);
			}
		}

		return super.beginConsumption(req, identityUrl, returnToUrl, realm);
	}

}
