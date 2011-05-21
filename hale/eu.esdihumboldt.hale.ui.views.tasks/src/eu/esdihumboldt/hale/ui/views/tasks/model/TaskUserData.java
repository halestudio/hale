/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.ui.views.tasks.model;

import eu.esdihumboldt.hale.ui.views.tasks.internal.Messages;

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
				return Messages.TaskUserData_0; 
			case ACTIVE:
				return Messages.TaskUserData_1; 
			case IGNORED:
				return Messages.TaskUserData_2; 
			case COMPLETED:
				return Messages.TaskUserData_3; 
			default:
				return super.toString();
			}
		}
		
	}
	
	/**
	 * @return the status this Task is currently in.
	 */
	public TaskStatus getTaskStatus();
	
	/**
	 * Set the task status
	 * 
	 * @param status the task status
	 */
	public void setTaskStatus(TaskStatus status);
	
	/**
	 * @return the user comment associated with the task
	 */
	public String getUserComment();
	
	/**
	 * Set the task's user comment
	 * 
	 * @param comment the user comment
	 */
	public void setUserComment(String comment);

}
