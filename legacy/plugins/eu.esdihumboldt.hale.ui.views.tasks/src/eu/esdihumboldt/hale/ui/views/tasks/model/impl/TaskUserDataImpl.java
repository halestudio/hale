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

package eu.esdihumboldt.hale.ui.views.tasks.model.impl;

import eu.esdihumboldt.hale.ui.views.tasks.model.TaskUserData;

/**
 * Task user data implementation
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class TaskUserDataImpl implements TaskUserData {
	
	private TaskStatus status = TaskStatus.ACTIVE;
	
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
