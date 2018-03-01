/*
 * Copyright (c) 2018 wetransform GmbH
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

package eu.esdihumboldt.hale.common.tasks;

/**
 * Interface that allows a {@link Task} to be aware of its user data
 * 
 * @author Florian Esser
 */
public interface TaskUserDataAware {

	/**
	 * Set the task's user data
	 * 
	 * @param data can be <code>null</code>
	 * @return true if the user data was updated
	 */
	boolean setUserData(TaskUserData data);

	/**
	 * Populate user data object with task-intrinsic information.
	 * 
	 * @param data user data object to populate
	 */
	void populateUserData(TaskUserData data);
}
