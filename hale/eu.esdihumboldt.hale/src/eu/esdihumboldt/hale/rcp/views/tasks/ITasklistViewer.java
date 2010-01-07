package eu.esdihumboldt.hale.rcp.views.tasks;

import eu.esdihumboldt.hale.task.Task;

/**
 * Interface to update TaskListView when tasks where added or removed from the
 * TaskListView. This Interface needs to be implemented in the ContentProvider
 * of the TaskListview.
 * 
 * @author cjauss
 *
 */
public interface ITasklistViewer {
	/**
	 * Update the view to reflect the fact that a task was added 
	 * to the task list
	 * 
	 * @param task
	 */
	public void addTask(Task task);
	
	/**
	 * Update the view to reflect the fact that a task was removed 
	 * from the task list
	 * 
	 * @param task
	 */
	public void removeTask(Task task);
}
