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

package eu.esdihumboldt.hale.io.wfs.capabilities

import javax.annotation.Nullable

import eu.esdihumboldt.hale.io.wfs.WFSVersion
import groovy.transform.CompileStatic
import groovy.transform.Immutable


/**
 * Encapsulates information we need from WFS capabilities
 * @author Simon Templer
 */
@CompileStatic
@Immutable
class WFSCapabilities {
	WFSVersion version
	Map<String, WFSOperation> operations

	@Nullable
	WFSOperation getTransactionOp() {
		operations['Transaction']
	}
}

@CompileStatic
@Immutable
class WFSOperation {
	String name
	String httpPostUrl
	String httpGetUrl
}
