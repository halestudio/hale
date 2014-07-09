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

package eu.esdihumboldt.hale.ui.common.service.compatibility;

import de.fhg.igd.eclipse.util.extension.exclusive.ExclusiveExtension;
import eu.esdihumboldt.hale.common.align.compatibility.CompatibilityMode;

/**
 * The Compatibility Service interface
 * 
 * @author Sebastian Reinhardt
 */
public interface CompatibilityService extends
		ExclusiveExtension<CompatibilityMode, CompatibilityModeFactory> {

	/**
	 * adds a listener to the service
	 * 
	 * @param listener the listener to add
	 */
	public void addCompatibilityListener(CompatibilityServiceListener listener);

	/**
	 * removes a listener from the service
	 * 
	 * @param listener the listener to remove
	 */
	public void removeCompatibilityListener(CompatibilityServiceListener listener);

}
