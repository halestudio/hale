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

package eu.esdihumboldt.hale.task;

import java.util.List;

import eu.esdihumboldt.hale.models.TaskService;
import eu.esdihumboldt.hale.schemaprovider.model.Definition;
import eu.esdihumboldt.hale.task.TaskType.SeverityLevel;

/**
 * Task decorator that provides convenience methods for accessing the task
 *   type information
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class ResolvedTask implements Task {
	
	private final Task task;
	
	private final TaskType type;

	/**
	 * Create a resolved task
	 * 
	 * @param task the task
	 * @param type the task's type
	 */
	public ResolvedTask(Task task, TaskType type) {
		super();
		this.task = task;
		this.type = type;
	}
	
	/**
	 * Resolve a task
	 * 
	 * @param registry the task type registry
	 * @param task the task to be resolved
	 * 
	 * @return the resolved task or <code>null</code> if the task type could not
	 *   be resolved
	 */
	public static ResolvedTask resolveTask(TaskRegistry registry, Task task) {
		TaskType type = registry.getType(task.getTypeName());
		if (type != null) {
			return new ResolvedTask(task, type);
		}
		else {
			return null;
		}
	}

	/**
	 * @see Task#dispose()
	 */
	@Override
	public void dispose() {
		task.dispose();
	}

	/**
	 * @see Task#getContext()
	 */
	@Override
	public List<? extends Definition> getContext() {
		return task.getContext();
	}

	/**
	 * @see Task#getMainContext()
	 */
	@Override
	public Definition getMainContext() {
		return task.getMainContext();
	}

	/**
	 * @see Task#getTaskStatus()
	 */
	@Override
	public TaskStatus getTaskStatus() {
		return task.getTaskStatus();
	}

	/**
	 * @see Task#getTypeName()
	 */
	@Override
	public String getTypeName() {
		return task.getTypeName();
	}

	/**
	 * Get the value of the task
	 * 
	 * @return the task value
	 */
	public double getValue() {
		return type.getValue(task);
	}
	
	/**
	 * The task factory that provides tasks of this type
	 * 
	 * @return the task factory
	 */
	public TaskFactory getTaskFactory() {
		return type.getTaskFactory();
	}
	
	/**
	 * Get the severity level
	 * 
	 * @see TaskType#getSeverityLevel(Task)
	 * 
	 * @return the severity level of the task 
	 */
	public SeverityLevel getSeverityLevel() {
		return type.getSeverityLevel(task);
	}
	
	/**
	 * Get the creation reason
	 * 
	 * @see TaskType#getReason(Task)
	 * 
	 * @return the task's creation reason
	 */
	public String getReason() {
		return type.getReason(task);
	}
	
	/**
	 * Get the task title
	 * 
	 * @see TaskType#getTitle(Task)
	 * 
	 * @return the task title
	 */
	public String getTitle() {
		return type.getTitle(task);
	}

	/**
	 * Get the task type
	 * 
	 * @return the task type
	 */
	public TaskType getType() {
		return type;
	}

	/**
	 * @see Task#setTaskService(TaskService)
	 */
	@Override
	public void setTaskService(TaskService taskService) {
		task.setTaskService(taskService);
	}

	/**
	 * @see Comparable#compareTo(Object)
	 */
	@Override
	public int compareTo(Task other) {
		if (other instanceof ResolvedTask) {
			int result = getSeverityLevel().compareTo(((ResolvedTask) other).getSeverityLevel());
			
			if (result == 0) {
				if (getValue() > ((ResolvedTask) other).getValue()) {
					return -1;
				}
				else if (getValue() < ((ResolvedTask) other).getValue()) {
					return 1;
				}
			}
			else {
				return result;
			}
		}
		
		return task.compareTo(other);
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return task.hashCode();
	}

	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Task))
			return false;
		Task other = (Task) obj;
		if (!task.equals(other))
			return false;
		return true;
	}

}
