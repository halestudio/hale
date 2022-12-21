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

package eu.esdihumboldt.hale.io.haleconnect.ui.projects

import eu.esdihumboldt.hale.io.haleconnect.Owner
import groovy.transform.Immutable
/**
 * Entry in the owner filter dropdown of LoadHaleConnectProjectWizardPage
 *
 * After migration to java17, if this class is marked as @Immutable then 
 * at runtime an exception is thrown indicating that clone() is not applicable to Owner class.
 * Therefore, using @Canonical which provides similar functionality but immutability. 
 * 
 * @author Florian Esser
 */
@Immutable
class OwnerFilterEntry {
	/**
	 * Owner or owners to display if this entry is selected
	 */
	List<Owner> owner

	/**
	 * Label for the dropdown entry
	 */
	String label
}
