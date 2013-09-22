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
import org.springframework.security.core.context.SecurityContextHolder;

import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.server.db.orient.DatabaseHelper;
import eu.esdihumboldt.hale.server.model.User;
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
			return auth.getName();
		}

		return null;
	}

	/**
	 * Get the current user.
	 * 
	 * @return the current user
	 */
	public static User getUser() {
		String login = getLogin();
		if (login == null)
			return null;

		OrientGraphNoTx graph = DatabaseHelper.getNonTransactionalGraph();
		try {
			return User.getByLogin(graph, login);
		} catch (NonUniqueResultException e) {
			log.error("Duplicate login in user database: " + login);
		} finally {
			graph.shutdown();
		}

		return null;
	}
}
