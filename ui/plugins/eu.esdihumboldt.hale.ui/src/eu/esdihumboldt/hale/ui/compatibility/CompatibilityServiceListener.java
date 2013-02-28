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

package eu.esdihumboldt.hale.ui.compatibility;

/**
 * Listener for compatibility services
 * 
 * @author Sebastian Reinhardt
 */
public interface CompatibilityServiceListener {

	/**
	 * processes the changes if the compatibility mode changed
	 * 
	 * @param correctness states if the compatibility is still fully given
	 * @param notification notification to be presented to the user
	 */
	public void compatibilityChanged(boolean correctness, String notification);

}
