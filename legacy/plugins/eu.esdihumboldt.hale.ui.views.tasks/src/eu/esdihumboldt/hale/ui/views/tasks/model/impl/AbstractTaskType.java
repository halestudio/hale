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

package eu.esdihumboldt.hale.ui.views.tasks.model.impl;

import eu.esdihumboldt.hale.ui.views.tasks.model.TaskFactory;
import eu.esdihumboldt.hale.ui.views.tasks.model.TaskType;

/**
 * Abstract task type
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public abstract class AbstractTaskType implements TaskType {
	
	/**
	 * The task provider
	 */
	private final TaskFactory taskFactory;

	/**
	 * Create a new task type
	 * 
	 * @param taskFactory the task factory
	 */
	public AbstractTaskType(TaskFactory taskFactory) {
		super();
		this.taskFactory = taskFactory;
	}

	/**
	 * @see TaskType#getName()
	 */
	@Override
	public String getName() {
		return taskFactory.getTaskTypeName();
	}

	/**
	 * @see TaskType#getTaskFactory()
	 */
	@Override
	public TaskFactory getTaskFactory() {
		return taskFactory;
	}

}
