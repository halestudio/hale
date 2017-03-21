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
	 * Creates a session with an associated username and token
	 * 
	 * @param username user name
	 * @param token JSON Web Token issued by hale connect
	 */
	public HaleConnectSessionImpl(String username, String token) {
		if (username == null || username.trim().isEmpty()) {
			throw new IllegalArgumentException("username must not be empty");
		}
		if (token == null || token.trim().isEmpty()) {
			throw new IllegalArgumentException("token must not be empty");
		}

		this.username = username;
		this.token = token;
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
}
