/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.haleconnect.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.haleconnect.api.user.v1.model.UserInfo;

import eu.esdihumboldt.hale.io.haleconnect.HaleConnectSession;

/**
 * hale connect session details
 * 
 * @author Florian Esser
 */
public class HaleConnectSessionImpl implements HaleConnectSession {

	/**
	 * User name associated with the session
	 */
	private final String username;

	/**
	 * Token associated with the session
	 */
	private final String token;

	/**
	 * Current user's profile
	 */
	private final UserInfo profile;

	/**
	 * Creates a session with an associated username and token
	 * 
	 * @param username user name
	 * @param token JSON Web Token issued by hale connect
	 * @param profile Profile of the logged-in user
	 */
	public HaleConnectSessionImpl(String username, String token, UserInfo profile) {
		if (username == null || username.trim().isEmpty()) {
			throw new IllegalArgumentException("username must not be empty");
		}
		if (token == null || token.trim().isEmpty()) {
			throw new IllegalArgumentException("token must not be empty");
		}
		if (profile == null) {
			throw new IllegalArgumentException("profile must not be null");
		}

		this.username = username;
		this.token = token;
		this.profile = profile;
	}

	/**
	 * @return the username
	 */
	@Override
	public String getUsername() {
		return username;
	}

	/**
	 * @return the token
	 */
	@Override
	public String getToken() {
		return token;
	}

	/**
	 * @see eu.esdihumboldt.hale.io.haleconnect.HaleConnectSession#getUserId()
	 */
	@Override
	public String getUserId() {
		return profile.getId();
	}

	/**
	 * @see eu.esdihumboldt.hale.io.haleconnect.HaleConnectSession#getOrganisationIds()
	 */
	@Override
	public List<String> getOrganisationIds() {
		List<String> result = new ArrayList<>();
		if (profile.getOrgRoles() instanceof Map<?, ?>) {
			@SuppressWarnings("unchecked")
			Map<Object, Object> roleMap = (Map<Object, Object>) profile.getOrgRoles();
			roleMap.keySet().forEach(k -> result.add(k.toString()));
		}

		return result;
	}
}
