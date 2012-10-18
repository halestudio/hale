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

package eu.esdihumboldt.hale.server.security.util.impl;

import java.util.Collection;

import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import eu.esdihumboldt.hale.server.security.util.MultiWarSessionAuthenticationStrategy;
import eu.esdihumboldt.hale.server.security.util.SessionLogoutService;
import eu.esdihumboldt.hale.server.security.util.SessionTracker;
import eu.esdihumboldt.hale.server.security.util.SpringSessionTrackerListener;

/**
 * Service that tracks sessions and allows logout from multiple sessions
 * associated to the same user.<br>
 * To be populated through {@link #addLoginSession(HttpSession, Authentication)}
 * , e.g. by the {@link MultiWarSessionAuthenticationStrategy}, while
 * {@link SpringSessionTrackerListener} removes expired sessions.
 * 
 * @author Simon Templer
 */
public class SessionLogoutServiceImpl implements SessionTracker, SessionLogoutService {

	/**
	 * The attribute name under which the principal is stored, under which it
	 * was added to the service.
	 */
	public static final String SESSION_ATTRIBUTE_PRINCIPAL = "hale.server.sessionlogout.principal";

	/**
	 * Principals mapped to authenticated sessions.
	 */
	private final Multimap<Object, HttpSession> sessions = HashMultimap.create();

	/**
	 * @see SessionLogoutService#addLoginSession(HttpSession, Authentication)
	 */
	@Override
	public void addLoginSession(HttpSession session, Authentication authentication) {
		session.setAttribute(SESSION_ATTRIBUTE_PRINCIPAL, authentication.getPrincipal());
		sessions.put(authentication.getPrincipal(), session);
	}

	/**
	 * @see SessionLogoutService#logout(Authentication)
	 */
	@Override
	public void logout(Authentication authentication) {
		// XXX problem with this approach - a user can only be logged in once
		Collection<HttpSession> userSessions;
		synchronized (sessions) {
			// get all user sessions
			userSessions = sessions.removeAll(authentication.getPrincipal());
		}

		for (HttpSession session : userSessions) {
			// logout by invalidating the session
			session.invalidate();
		}
	}

	/**
	 * @see SessionTracker#addSession(HttpSession)
	 */
	@Override
	public void addSession(HttpSession session) {
		// ignore
	}

	/**
	 * @see SessionTracker#removeSession(HttpSession)
	 */
	@Override
	public void removeSession(HttpSession session) {
		Object principal = session.getAttribute(SESSION_ATTRIBUTE_PRINCIPAL);
		synchronized (sessions) {
			sessions.remove(principal, session);
		}
	}
}
