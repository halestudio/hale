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

package eu.esdihumboldt.hale.server.users;

import java.util.Collection;
import java.util.HashSet;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.server.db.orient.DatabaseHelper;
import eu.esdihumboldt.hale.server.model.User;
import eu.esdihumboldt.hale.server.security.UserConstants;
import eu.esdihumboldt.hale.server.webapp.BaseWebApplication;
import eu.esdihumboldt.util.blueprints.entities.NonUniqueResultException;

/**
 * Retrieves {@link UserDetails} from the server database.
 * 
 * @author Simon Templer
 */
public class UserDetailsServiceImpl implements UserDetailsService, UserConstants {

	private static final ALogger log = ALoggerFactory.getLogger(UserDetailsServiceImpl.class);

	/**
	 * @see UserDetailsService#loadUserByUsername(String)
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		OrientGraph graph = DatabaseHelper.getGraph();
		try {
			User user;
			try {
				user = User.getByLogin(graph, username);
			} catch (NonUniqueResultException e) {
				// multiple users w/ same login?!
				log.error("Found multiple user with login " + username, e);
				throw new IllegalStateException("Multiple users found for login");
			}

			if (user == null) {
				if (usingOpenID()) {
					// if using OpenID every user is automatically registered

					// create a default user
					user = User.create(graph);
					user.setLogin(username);
					user.setName("Unknown");
					user.setSurname("(OpenID)");
					user.setPassword("");

					log.info("Create default user for Open ID " + username);
				}
				else {
					throw new UsernameNotFoundException("User " + username + " not found.");
				}
			}

			Collection<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();

			// every user has the user role
			authorities.add(new SimpleGrantedAuthority(ROLE_USER));

			return new org.springframework.security.core.userdetails.User(user.getLogin(),
					user.getPassword(), true, true, true, true, authorities);
		} finally {
			graph.shutdown();
		}
	}

	private boolean usingOpenID() {
		return System.getProperty(BaseWebApplication.SYSTEM_PROPERTY_LOGIN_PAGE, "false")
				.toLowerCase().equals("openid");
	}
}
