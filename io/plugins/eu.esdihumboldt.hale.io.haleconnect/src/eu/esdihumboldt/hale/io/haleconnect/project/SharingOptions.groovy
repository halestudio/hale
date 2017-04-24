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

package eu.esdihumboldt.hale.io.haleconnect.project

import com.google.gson.annotations.SerializedName

import groovy.transform.Immutable

/**
 * Options for sharing transformation projects on hale connect.
 * 
 * @author Florian Esser
 */
@Immutable
class SharingOptions {
	/**
	 * Controls whether the shared project is publicly visible
	 */
	@SerializedName("public")
	boolean publicAccess;
}
