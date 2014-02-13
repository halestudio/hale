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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.openid4java.association.AssociationException;
import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.Message;
import org.openid4java.message.MessageException;
import org.openid4java.message.MessageExtension;
import org.openid4java.message.ParameterList;
import org.openid4java.message.ax.FetchRequest;
import org.openid4java.message.ax.FetchResponse;
import org.springframework.security.openid.AxFetchListFactory;
import org.springframework.security.openid.NullAxFetchListFactory;
import org.springframework.security.openid.OpenID4JavaConsumer;
import org.springframework.security.openid.OpenIDAttribute;
import org.springframework.security.openid.OpenIDAuthenticationStatus;
import org.springframework.security.openid.OpenIDAuthenticationToken;
import org.springframework.security.openid.OpenIDConsumer;
import org.springframework.security.openid.OpenIDConsumerException;
import org.springframework.util.StringUtils;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;

/**
 * OpenID consumer with support for being proxied, based on the
 * {@link OpenID4JavaConsumer}.
 * 
 * @author Simon Templer
 */
public class ProxyOpenIDConsumer implements OpenIDConsumer {

	private static final ALogger log = ALoggerFactory.getLogger(ProxyOpenIDConsumer.class);

	private static final String DISCOVERY_INFO_KEY = DiscoveryInformation.class.getName();

	private final AxFetchListFactory attributesToFetchFactory;

	private final ConsumerManager consumerManager;

	/**
	 * Default constructor.
	 */
	public ProxyOpenIDConsumer() {
		this.consumerManager = new ConsumerManager();
		this.attributesToFetchFactory = new NullAxFetchListFactory();
	}

	@Override
	public String beginConsumption(HttpServletRequest req, String identityUrl, String returnToUrl,
			String realm) throws OpenIDConsumerException {
		List<?> discoveries;
		try {
			discoveries = this.consumerManager.discover(identityUrl);
		} catch (DiscoveryException e) {
			throw new OpenIDConsumerException("Error during discovery", e);
		}

		DiscoveryInformation information = this.consumerManager.associate(discoveries);
		req.getSession().setAttribute(DISCOVERY_INFO_KEY, information);

		AuthRequest authReq;
		try {
			authReq = this.consumerManager.authenticate(information, returnToUrl, realm);

			log.debug("Looking up attribute fetch list for identifier: " + identityUrl);

			List<OpenIDAttribute> attributesToFetch = this.attributesToFetchFactory
					.createAttributeList(identityUrl);

			if (!(attributesToFetch.isEmpty())) {
				req.getSession().setAttribute("SPRING_SECURITY_OPEN_ID_ATTRIBUTES_FETCH_LIST",
						attributesToFetch);
				FetchRequest fetchRequest = FetchRequest.createFetchRequest();
				for (OpenIDAttribute attr : attributesToFetch) {
					if (log.isDebugEnabled()) {
						log.debug("Adding attribute " + attr.getType() + " to fetch request");
					}
					fetchRequest.addAttribute(attr.getName(), attr.getType(), attr.isRequired(),
							attr.getCount());
				}
				authReq.addExtension(fetchRequest);
			}
		} catch (MessageException e) {
			throw new OpenIDConsumerException("Error processing ConsumerManager authentication", e);
		} catch (ConsumerException e) {
			throw new OpenIDConsumerException("Error processing ConsumerManager authentication", e);
		}

		return authReq.getDestinationUrl(true);
	}

	@Override
	public OpenIDAuthenticationToken endConsumption(HttpServletRequest request)
			throws OpenIDConsumerException {
		ParameterList openidResp = new ParameterList(request.getParameterMap());

		DiscoveryInformation discovered = (DiscoveryInformation) request.getSession().getAttribute(
				DISCOVERY_INFO_KEY);

		if (discovered == null) {
			throw new OpenIDConsumerException(
					"DiscoveryInformation is not available. Possible causes are lost session or replay attack");
		}

		@SuppressWarnings("unchecked")
		List<OpenIDAttribute> attributesToFetch = (List<OpenIDAttribute>) request.getSession()
				.getAttribute("SPRING_SECURITY_OPEN_ID_ATTRIBUTES_FETCH_LIST");

		request.getSession().removeAttribute(DISCOVERY_INFO_KEY);
		request.getSession().removeAttribute("SPRING_SECURITY_OPEN_ID_ATTRIBUTES_FETCH_LIST");

		StringBuffer receivingURL = request.getRequestURL();

		// XXX Proxy handling start
		String forwardedFor = request.getHeader("X-Forwarded-For");
		if (forwardedFor != null) {
			String proxyReceiving = ProxyOpenIDAuthenticationFilter.buildReturnToForProxy(
					receivingURL.toString(), forwardedFor);
			if (proxyReceiving != null) {
				receivingURL = new StringBuffer(proxyReceiving);
			}
			else {
				log.error("Could not determine proxy receiving URL");
			}
		}
		// XXX Proxy handling end

		String queryString = request.getQueryString();

		if (StringUtils.hasLength(queryString)) {
			receivingURL.append("?").append(request.getQueryString());
		}

		VerificationResult verification;
		try {
			verification = this.consumerManager.verify(receivingURL.toString(), openidResp,
					discovered);
		} catch (MessageException e) {
			throw new OpenIDConsumerException("Error verifying openid response", e);
		} catch (DiscoveryException e) {
			throw new OpenIDConsumerException("Error verifying openid response", e);
		} catch (AssociationException e) {
			throw new OpenIDConsumerException("Error verifying openid response", e);

		}

		Identifier verified = verification.getVerifiedId();

		if (verified == null) {
			Identifier id = discovered.getClaimedIdentifier();
			return new OpenIDAuthenticationToken(OpenIDAuthenticationStatus.FAILURE,
					(id == null) ? "Unknown" : id.getIdentifier(), "Verification status message: ["
							+ verification.getStatusMsg() + "]",
					Collections.<OpenIDAttribute> emptyList());

		}

		List<OpenIDAttribute> attributes = fetchAxAttributes(verification.getAuthResponse(),
				attributesToFetch);

		return new OpenIDAuthenticationToken(OpenIDAuthenticationStatus.SUCCESS,
				verified.getIdentifier(), "some message", attributes);
	}

	private List<OpenIDAttribute> fetchAxAttributes(Message authSuccess,
			List<OpenIDAttribute> attributesToFetch) throws OpenIDConsumerException {
		if ((attributesToFetch == null)
				|| (!(authSuccess.hasExtension("http://openid.net/srv/ax/1.0")))) {
			return Collections.emptyList();
		}

		log.debug("Extracting attributes retrieved by attribute exchange");

		List<OpenIDAttribute> attributes = Collections.emptyList();
		FetchResponse fetchResp;
		try {
			MessageExtension ext = authSuccess.getExtension("http://openid.net/srv/ax/1.0");
			if (ext instanceof FetchResponse) {
				fetchResp = (FetchResponse) ext;
				attributes = new ArrayList<OpenIDAttribute>(attributesToFetch.size());

				for (OpenIDAttribute attr : attributesToFetch) {
					@SuppressWarnings("unchecked")
					List<String> values = fetchResp.getAttributeValues(attr.getName());
					if (!(values.isEmpty())) {
						OpenIDAttribute fetched = new OpenIDAttribute(attr.getName(),
								attr.getType(), values);
						fetched.setRequired(attr.isRequired());
						attributes.add(fetched);
					}
				}
			}
		} catch (MessageException e) {
			throw new OpenIDConsumerException("Attribute retrieval failed", e);
		}

		if (log.isDebugEnabled()) {
			log.debug("Retrieved attributes" + attributes);
		}

		return attributes;
	}

}
