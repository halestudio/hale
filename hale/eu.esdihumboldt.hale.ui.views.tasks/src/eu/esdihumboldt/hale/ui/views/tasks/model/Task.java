/**
 * 
 */
package eu.esdihumboldt.hale.ui.views.tasks.model;

import java.util.List;

import eu.esdihumboldt.hale.schemaprovider.model.Definition;
import eu.esdihumboldt.hale.ui.views.tasks.service.TaskService;

/**
 * A Task is any type of action to be done within the HALE application to 
 * describe that action's context and goal.
 * 
 * @author Thorsten Reitz, Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface Task extends Comparable<Task> {
	
	/**
	 * Get the name of the task's type
	 * 
	 * @return the task type name
	 */
	public String getTypeName();
	
	/**
	 * Get the main context element
	 * 
	 * @return the main context element
	 */
	public Definition getMainContext();
	
	/**
	 * @return the {@link Definition}s that form the context 
	 * of this {@link Task}, i.e. those which are directly modified by it. An 
	 * example would be the Mapping to be clarified.
	 */
	public List<? extends Definition> getContext();

	/**
	 * Clean up the task. This method is called when a task is removed from the
	 *  task service
	 */
	public void dispose();
	
	/**
	 * Set the task service the task has been added to
	 * 
	 * @param taskService the task service
	 */
	public void setTaskService(TaskService taskService);
	
}
