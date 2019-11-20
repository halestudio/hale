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

package eu.esdihumboldt.hale.io.haleconnect

import groovy.transform.*

/**
 * Information about a hale connect user
 * 
 * @author Florian Esser
 */
@Immutable
@EqualsAndHashCode(includes = ["userId"])
class HaleConnectUserInfo {
	String userId
	String screenName
	String fullName

	/**
	 * Create a dummy user info object
	 * 
	 * @param userId ID of the user
	 * @return dummy info object
	 */
	static HaleConnectUserInfo dummyForId(String userId) {
		new HaleConnectUserInfo(userId: userId, screenName: "___user${userId}___", fullName: "<User ${userId}>")
	}
}
