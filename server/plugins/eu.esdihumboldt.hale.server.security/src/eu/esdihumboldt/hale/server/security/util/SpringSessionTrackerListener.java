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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.server.security.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;

/**
 * HTTP session listener that retrieves {@link SessionTracker}s from the Spring
 * web application context and forwards the information about created or
 * destroyed sessions to them.
 * 
 * @author Simon Templer
 */
public class SpringSessionTrackerListener implements HttpSessionListener {

//	/**
//	 * Session attribute name for the remote address of the initial request
//	 * creating the session.
//	 */
//	public static final String SESSION_ATTRIBUTE_REMOTE_ADDR = "eu.esdihumboldt.hale.server.session.remoteAddr";

	/**
	 * Name of the bean that may contain a collection of {@link SessionTracker}
	 * s.
	 */
	public static final String BEAN_NAME_TRACKERS_LIST = "sessionTrackers";

	private static final ALogger log = ALoggerFactory.getLogger(SpringSessionTrackerListener.class);

	/**
	 * @see HttpSessionListener#sessionCreated(HttpSessionEvent)
	 */
	@Override
	public void sessionCreated(HttpSessionEvent se) {
//		String ipAddr;
//		try {
//			ipAddr = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
//					.getRequest().getRemoteAddr();
//		} catch (IllegalStateException e) {
//			// ignore
//			ipAddr = null;
//		}
//		se.getSession().setAttribute(SESSION_ATTRIBUTE_REMOTE_ADDR, ipAddr);

		Iterable<SessionTracker> trackers = getSessionTrackers(se.getSession());
		for (SessionTracker tracker : trackers) {
			try {
				tracker.addSession(se.getSession()); // , ipAddr);
			} catch (Exception e) {
				log.error("Error while adding session to session tracker", e);
			}
		}
	}

	/**
	 * Get the available session trackers.
	 * 
	 * @param session the HTTP session
	 * 
	 * @return the session trackers or an empty iterable
	 */
	protected Iterable<SessionTracker> getSessionTrackers(HttpSession session) {
		ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(session
				.getServletContext());

		if (context == null) {
			return new ArrayList<SessionTracker>(0);
		}

		List<SessionTracker> trackers = new ArrayList<SessionTracker>();

		// all top level beans of type ServiceTracker
		trackers.addAll(context.getBeansOfType(SessionTracker.class).values());

		try {
			Collection<?> values = context.getBean(BEAN_NAME_TRACKERS_LIST, Collection.class);
			for (Object value : values) {
				if (value instanceof SessionTracker) {
					trackers.add((SessionTracker) value);
				}
			}
		} catch (Exception e) {
			// ignore
		}

		return trackers;
	}

	/**
	 * @see HttpSessionListener#sessionDestroyed(HttpSessionEvent)
	 */
	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		Iterable<SessionTracker> trackers = getSessionTrackers(se.getSession());
		for (SessionTracker tracker : trackers) {
			try {
				tracker.removeSession(se.getSession());
//						(String) se.getSession().getAttribute(SESSION_ATTRIBUTE_REMOTE_ADDR));
			} catch (Exception e) {
				log.error("Error while removing session to session tracker", e);
			}
		}
	}

}
