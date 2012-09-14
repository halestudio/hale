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

import java.util.List;

import eu.esdihumboldt.hale.schemaprovider.model.Definition;
import eu.esdihumboldt.hale.ui.views.tasks.model.Task;
import eu.esdihumboldt.hale.ui.views.tasks.service.TaskService;

/**
 * Base task implementation
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class BaseTask implements Task, Comparable<Task> {
	
	private TaskService taskService = null;
	
	private final String typeName;
	
	private final List<? extends Definition> context;
	
	/**
	 * Create a new task
	 * 
	 * @param typeName the name of the task type
	 * @param context the task context
	 */
	public BaseTask(String typeName, 
			List<? extends Definition> context) {
		super();
		this.typeName = typeName;
		this.context = context;
	}
	
	/**
	 * Remove the task
	 */
	protected void invalidate() {
		if (taskService != null) {
			taskService.removeTask(this);
		}
	}

	/**
	 * @see Task#setTaskService(TaskService)
	 */
	@Override
	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}

	/**
	 * @see Task#getContext()
	 */
	@Override
	public List<? extends Definition> getContext() {
		return context;
	}

	/**
	 * @see Task#getMainContext()
	 */
	@Override
	public Definition getMainContext() {
		if (context.size() > 0) {
			return context.get(0);
		}
		else {
			throw new IllegalStateException("No valid task context defined"); //$NON-NLS-1$
		}
	}

	/**
	 * @see Task#getTypeName()
	 */
	@Override
	public String getTypeName() {
		return typeName;
	}

	/**
	 * @see Task#dispose()
	 */
	@Override
	public void dispose() {
		// do nothing
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((context == null) ? 0 : context.hashCode());
		result = prime * result
				+ ((typeName == null) ? 0 : typeName.hashCode());
		return result;
	}

	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public final boolean equals(Object obj) {
		// test if the other is a task with equal context and type name
		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Task))
			return false;
		Task other = (Task) obj;
		if (context == null) {
			if (other.getContext() != null)
				return false;
		} else if (!context.equals(other.getContext()))
			return false;
		if (typeName == null) {
			if (other.getTypeName() != null)
				return false;
		} else if (!typeName.equals(other.getTypeName()))
			return false;
		return true;
	}

	/**
	 * @see Comparable#compareTo(Object)
	 */
	@Override
	public int compareTo(Task other) {
		if (other == null) {
			return -1;
		}
		
		int result = getMainContext().getIdentifier().compareTo(other.getMainContext().getIdentifier());
		if (result == 0) {
			return getTypeName().compareTo(other.getTypeName());
		}
		else {
			return result;
		}
	}

}
