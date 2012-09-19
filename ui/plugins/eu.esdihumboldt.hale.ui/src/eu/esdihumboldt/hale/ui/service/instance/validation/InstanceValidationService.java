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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.service.instance.validation;

/**
 * Service that listens to the instance service and validates instances.
 * 
 * @author Kai Schwierczek
 */
public interface InstanceValidationService {

	/**
	 * Adds a listener.
	 * 
	 * @param listener the listener to add
	 */
	public void addListener(InstanceValidationListener listener);

	/**
	 * Removes a listener.
	 * 
	 * @param listener the listener to remove
	 */
	public void removeListener(InstanceValidationListener listener);

	/**
	 * Returns whether the automatic instance validation after each
	 * transformation is enabled.
	 * 
	 * @return whether the automatic instance validation after each
	 *         transformation is enabled
	 */
	public boolean isValidationEnabled();

	/**
	 * Set whether the automatic instance validation after each transformation
	 * is enabled.
	 * 
	 * @param enable whether the automatic instance validation after each
	 *            transformation is enabled
	 */
	public void setValidationEnabled(boolean enable);
}
