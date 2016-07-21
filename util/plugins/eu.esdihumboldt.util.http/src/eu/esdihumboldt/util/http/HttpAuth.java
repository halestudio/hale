/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.util.http;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * This class is used for proxy authentication. (Sometimes just setting System
 * properties does not work)
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class HttpAuth extends Authenticator {

	private String user = ""; //$NON-NLS-1$
	private String password = ""; //$NON-NLS-1$

	/**
	 * Constructor.
	 * 
	 * @param user proxyUser
	 * @param password proxyPassword
	 */
	public HttpAuth(String user, String password) {
		this.user = user;
		this.password = password;
	}

	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(this.user, this.password.toCharArray());
	}
}
