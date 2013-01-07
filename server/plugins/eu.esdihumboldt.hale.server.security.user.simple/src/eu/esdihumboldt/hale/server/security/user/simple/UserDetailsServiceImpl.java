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

package eu.esdihumboldt.hale.server.security.user.simple;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;

import org.osgi.framework.Bundle;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.server.security.UserConstants;
import eu.esdihumboldt.hale.server.security.user.simple.internal.Activator;

/**
 * Retrieves {@link UserDetails} based on property files. It searches for
 * properties files in <code>META-INF/credentials</code> folders in fragments.
 * 
 * @author Simon Templer
 */
public class UserDetailsServiceImpl implements UserDetailsService, UserConstants {

	private static final ALogger log = ALoggerFactory.getLogger(UserDetailsServiceImpl.class);

	private Map<String, String> users;

	/**
	 * @see UserDetailsService#loadUserByUsername(String)
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if (users == null) {
			update();
		}

		if (users.containsKey(username)) {
			String value = users.get(username);

			String[] parts = value.split(" ");

			if (parts == null || parts.length == 0 || parts[0] == null || parts[0].isEmpty()) {
				throw new UsernameNotFoundException("No password set for user");
			}

			// password
			String password = parts[0];

			// role
			String roleName = (parts.length > 1) ? (parts[1]) : (ROLE_USER);
			GrantedAuthority role = new SimpleGrantedAuthority(roleName);

			Collection<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
			authorities.add(role);

			return new User(username, password, true, true, true, true, authorities);
		}
		else {
			throw new UsernameNotFoundException("User " + username + " not found.");
		}
	}

	/**
	 * Update the user map
	 */
	@SuppressWarnings("unchecked")
	private synchronized void update() {
		users = new HashMap<String, String>();

		Bundle bundle = Activator.getInstance().getContext().getBundle();

		Enumeration<URL> files = bundle.findEntries("META-INF/credentials", "*.properties", true);

		if (files != null) {
			while (files.hasMoreElements()) {
				URL url = files.nextElement();

				Properties properties = new Properties();
				try {
					properties.load(url.openStream());

					Enumeration<String> names = (Enumeration<String>) properties.propertyNames();
					while (names.hasMoreElements()) {
						String name = names.nextElement();
						String value = properties.getProperty(name);

						users.put(name, value);
					}
				} catch (IOException e) {
					log.error("Error loading users from file", e);
				}
			}
		}
	}

}
