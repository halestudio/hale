/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.core.io.impl;

import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;

/**
 * Progress indicator with support for a subtask name.
 * 
 * @author Simon Templer
 */
public class SubtaskProgressIndicator extends ProgressIndicatorDecorator {

	private String taskName;

	/**
	 * Create a progress indicator with support for a subtask name.
	 * 
	 * @param decoratee the progress indicator to decorate
	 */
	public SubtaskProgressIndicator(ProgressIndicator decoratee) {
		super(decoratee);
	}

	/**
	 * @see ProgressIndicatorDecorator#begin(String, int)
	 */
	@Override
	public void begin(String taskName, int totalWork) {
		this.taskName = taskName;
		super.begin(taskName, totalWork);
	}

	/**
	 * @see ProgressIndicatorDecorator#setCurrentTask(String)
	 */
	@Override
	public void setCurrentTask(String taskName) {
		this.taskName = taskName;
		super.setCurrentTask(taskName);
	}

	/**
	 * Start a sub task
	 * 
	 * @param subtaskName the sub task name
	 */
	public void subTask(String subtaskName) {
		if (subtaskName == null) {
			if (taskName != null) {
				// reset to main task only
				setCurrentTask(taskName);
			}
			return;
		}

		if (taskName == null) {
			// set subtask as current task
			super.setCurrentTask(subtaskName);
		}
		else {
			super.setCurrentTask(getCombinedTaskName(taskName, subtaskName));
		}
	}

	/**
	 * Get the combined task name for a sub task
	 * 
	 * @param taskName the main task name
	 * @param subtaskName the sub task name
	 * @return the combined task name
	 */
	protected String getCombinedTaskName(String taskName, String subtaskName) {
		return taskName + ": " + subtaskName;
	}

}
