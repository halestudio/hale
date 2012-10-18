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

package eu.esdihumboldt.hale.server.security.util;

import java.util.UUID;

/**
 * Token utility.
 * 
 * @author Simon Templer
 */
public class Token {

	private static final String RUNTIME_TOKEN = UUID.randomUUID().toString();

	/**
	 * Get a token that is always the same in this JVM instance.
	 * 
	 * @return the runtime token
	 */
	public String getRuntimeToken() {
		return RUNTIME_TOKEN;
	}

}
