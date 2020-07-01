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
 * Info about an organisation in hale connect
 * 
 * @author Florian Esser
 */
@Immutable
@EqualsAndHashCode(includes = ["id"])
class HaleConnectOrganisationInfo {
	String id
	String name

	/**
	 * Create a dummy organisation info object
	 * 
	 * @param orgId ID of the organisation
	 * @return dummy info object
	 */
	static HaleConnectOrganisationInfo dummyForId(String orgId) {
		new HaleConnectOrganisationInfo(id: orgId, name: "<Organisation ${orgId}>");
	}
}
