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
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.openid.OpenIDAttribute;
import org.springframework.security.openid.OpenIDAuthenticationToken;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import de.fhg.igd.osgi.util.configuration.IConfigurationService;
import eu.esdihumboldt.hale.server.db.orient.DatabaseHelper;
import eu.esdihumboldt.hale.server.model.User;
import eu.esdihumboldt.hale.server.security.UserConstants;
import eu.esdihumboldt.hale.server.security.util.impl.ExtendedUser;
import eu.esdihumboldt.util.blueprints.entities.NonUniqueResultException;

/**
 * Retrieves {@link UserDetails} from the server database.
 * 
 * @author Simon Templer
 */
public class UserDetailsServiceImpl implements UserDetailsService, UserConstants,
		AuthenticationUserDetailsService<OpenIDAuthenticationToken> {

	private static final ALogger log = ALoggerFactory.getLogger(UserDetailsServiceImpl.class);

	private IConfigurationService configurationService;

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
				throw new UsernameNotFoundException("User " + username + " not found.");
			}

			return createUserDetails(user, false);
		} finally {
			graph.shutdown();
		}
	}

//	private boolean usingOpenID() {
//		return System.getProperty(BaseWebApplication.SYSTEM_PROPERTY_LOGIN_PAGE, "false")
//				.toLowerCase().equals("openid");
//	}

	/**
	 * Create user details for a given user.
	 * 
	 * @param user the user
	 * @param newUser if the user is a newly created user
	 * @return the user details
	 */
	private UserDetails createUserDetails(User user, boolean newUser) {
		Collection<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();

		// every user has the user role
		authorities.add(new SimpleGrantedAuthority(ROLE_USER));

		if (configurationService != null) {
			boolean admin = configurationService.getBoolean("user." + user.getLogin() + ".admin",
					false);
			if (admin) {
				authorities.add(new SimpleGrantedAuthority(ROLE_ADMIN));
			}
		}

		ExtendedUser userDetails = new ExtendedUser(user.getLogin(), user.getPassword(), true,
				true, true, true, authorities);
		userDetails.setNewUser(newUser);
		return userDetails;
	}

	@Override
	public UserDetails loadUserDetails(OpenIDAuthenticationToken token)
			throws UsernameNotFoundException {
		OrientGraph graph = DatabaseHelper.getGraph();
		try {
			User user;
			try {
				user = User.getByLogin(graph, token.getIdentityUrl());
			} catch (NonUniqueResultException e) {
				// multiple users w/ same login?!
				log.error("Found multiple user with same identity URL: " + token.getIdentityUrl(),
						e);
				throw new IllegalStateException("Multiple users found for login");
			}

			boolean newUser = false;
			if (user == null) {
				// if using OpenID every user is automatically registered

				// create a default user
				user = User.create(graph);
				user.setLogin(token.getIdentityUrl());
				user.setPassword("");
				newUser = true;

				// try to retrieve info from attribute exchange
				for (OpenIDAttribute attribute : token.getAttributes()) {
					if (attribute.getCount() >= 0) {
						switch (attribute.getName()) {
						case "email": // fall through
						case "emailAlt":
							user.setEmail(attribute.getValues().get(0));
							break;
						case "firstName":
							user.setName(attribute.getValues().get(0));
							break;
						case "lastName":
							user.setSurname(attribute.getValues().get(0));
							break;
						}
					}
				}

				log.info("Create default user for Open ID " + token.getIdentityUrl());
			}

			return createUserDetails(user, newUser);
		} finally {
			graph.shutdown();
		}
	}

	/**
	 * @return the configuration service
	 */
	public IConfigurationService getConfigurationService() {
		return configurationService;
	}

	/**
	 * @param configurationService the configuration service to set
	 */
	public void setConfigurationService(IConfigurationService configurationService) {
		this.configurationService = configurationService;
	}
}
