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
 * User data of a task
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface TaskUserData {

	/**
	 * Task status
	 */
	public enum TaskStatus {
		/** a new task */
		NEW,
		/** an active task */
		ACTIVE,
		/** the task is completed even though it is still generated */
		COMPLETED,
		/** the task is ignored */
		IGNORED;

		/**
		 * @see Enum#toString()
		 */
		@Override
		public String toString() {
			switch (this) {
			case NEW:
				return "new";
			case ACTIVE:
				return "active";
			case IGNORED:
				return "ignored";
			case COMPLETED:
				return "completed";
			default:
				return super.toString();
			}
		}

	}

	/**
	 * @return the status this Task is currently in.
	 */
	TaskStatus getTaskStatus();

	/**
	 * Set the task status
	 * 
	 * @param status the task status
	 */
	void setTaskStatus(TaskStatus status);

	/**
	 * @return the user comment associated with the task
	 */
	String getUserComment();

	/**
	 * Set the task's user comment
	 * 
	 * @param comment the user comment
	 */
	void setUserComment(String comment);

}
