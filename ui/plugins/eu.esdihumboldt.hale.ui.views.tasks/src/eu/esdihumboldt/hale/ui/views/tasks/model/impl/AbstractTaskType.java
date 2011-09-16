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
