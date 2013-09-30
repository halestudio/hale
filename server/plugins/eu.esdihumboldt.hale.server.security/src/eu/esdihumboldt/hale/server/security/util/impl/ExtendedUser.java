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

package eu.esdihumboldt.hale.server.security.util.impl;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import eu.esdihumboldt.hale.server.security.ExtendedUserDetails;

/**
 * Extended user default implementation.
 * 
 * @author Simon Templer
 */
public class ExtendedUser extends User implements ExtendedUserDetails {

	private static final long serialVersionUID = 4159630722205690562L;

	private boolean newUser;

	/**
	 * @see User#User(String, String, boolean, boolean, boolean, boolean,
	 *      Collection)
	 */
	public ExtendedUser(String username, String password, boolean enabled,
			boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired,
				accountNonLocked, authorities);
	}

	/**
	 * @see User#User(String, String, Collection)
	 */
	public ExtendedUser(String username, String password,
			Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
	}

	@Override
	public boolean isNewUser() {
		return newUser;
	}

	/**
	 * Set if the user is a newly created user.
	 * 
	 * @param newUser if the user is new
	 */
	public void setNewUser(boolean newUser) {
		this.newUser = newUser;
	}

}
