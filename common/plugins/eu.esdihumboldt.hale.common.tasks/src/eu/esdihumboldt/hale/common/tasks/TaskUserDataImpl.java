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

package eu.esdihumboldt.hale.common.tasks;

/**
 * Task user data implementation
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class TaskUserDataImpl implements TaskUserData {

	private TaskStatus status = TaskStatus.NEW;

	private String comment = ""; //$NON-NLS-1$

	/**
	 * @see TaskUserData#getTaskStatus()
	 */
	@Override
	public TaskStatus getTaskStatus() {
		return status;
	}

	/**
	 * @see TaskUserData#getUserComment()
	 */
	@Override
	public String getUserComment() {
		return comment;
	}

	/**
	 * @see TaskUserData#setTaskStatus(TaskStatus)
	 */
	@Override
	public void setTaskStatus(TaskStatus status) {
		this.status = status;
	}

	/**
	 * @see TaskUserData#setUserComment(String)
	 */
	@Override
	public void setUserComment(String comment) {
		this.comment = comment;
	}

}
