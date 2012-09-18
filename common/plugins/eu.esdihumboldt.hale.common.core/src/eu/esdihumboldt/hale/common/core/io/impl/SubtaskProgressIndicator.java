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
