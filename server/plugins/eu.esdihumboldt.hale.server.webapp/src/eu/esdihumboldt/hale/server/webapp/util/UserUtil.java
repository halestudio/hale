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

package eu.esdihumboldt.hale.server.webapp.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.tinkerpop.blueprints.Graph;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.server.db.orient.DatabaseHelper;
import eu.esdihumboldt.hale.server.model.User;
import eu.esdihumboldt.hale.server.security.UserConstants;
import eu.esdihumboldt.util.blueprints.entities.NonUniqueResultException;

/**
 * Utilities related to the current user.
 * 
 * @author Simon Templer
 */
public class UserUtil {

	private static final ALogger log = ALoggerFactory.getLogger(UserUtil.class);

	/**
	 * Get the user's login name (or id)
	 * 
	 * @return the user login or <code>null</code>
	 */
	public static String getLogin() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.isAuthenticated()) {
			for (GrantedAuthority authority : auth.getAuthorities()) {
				if (authority.getAuthority().equals(UserConstants.ROLE_USER)
						|| authority.getAuthority().equals(UserConstants.ROLE_ADMIN)) {
					// only return the login for an actual user
					return auth.getName();
				}
			}
		}

		return null;
	}

	/**
	 * Get the current user's display name.
	 * 
	 * @param graph a graph to retrieve the user from, or <code>null</code>
	 * @return the current user's display name
	 */
	public static String getUserName(Graph graph) {
		String login = getLogin();
		if (login == null)
			return null;

		boolean cleanup = false;
		if (graph == null) {
			graph = DatabaseHelper.getGraph();
			cleanup = true;
		}
		try {
			User user = User.getByLogin(graph, login);

			return getDisplayName(user);
		} catch (NonUniqueResultException e) {
			log.error("Duplicate login in user database: " + login);
		} finally {
			if (cleanup) {
				graph.shutdown();
			}
		}

		return getDisplayName(null);
	}

	/**
	 * Get the display name for a given user.
	 * 
	 * @param user the user, may be <code>null</code>
	 * @return the user's display name
	 */
	public static String getDisplayName(User user) {
		if (user != null) {
			String name = user.getName();
			String surname = user.getSurname();
			if (name != null && !name.isEmpty()) {
				if (surname != null && !surname.isEmpty()) {
					return name + " " + surname;
				}
				return name;
			}
			if (surname != null && !surname.isEmpty()) {
				return surname;
			}
		}

		return "Anonymous";
	}
}
