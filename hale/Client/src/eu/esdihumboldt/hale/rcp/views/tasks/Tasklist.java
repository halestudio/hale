package eu.esdihumboldt.hale.rcp.views.tasks;

import java.util.HashSet;
import java.util.Iterator;


import eu.esdihumboldt.hale.task.Task;

/**
 * Implementation of HALE's Tasklist. Adding and removing of tasks 
 * to the list is supported. 
 * @author cjauss
 *
 */
public class Tasklist {
	
	private HashSet<Task> tasklist = new HashSet<Task>();

	@SuppressWarnings({"unchecked"})
	private HashSet changeListeners = new HashSet();
	
	public HashSet<Task> getTasklist(){
		return tasklist;
	}
	
	
	/**
	 * Add a task to the Tasklist.
	 */
	@SuppressWarnings("unchecked")
	public void addTask(Task _task){
		tasklist.add(_task);
		Iterator iterator = changeListeners.iterator();
		while(iterator.hasNext()){
			((ITasklistViewer) iterator.next()).addTask(_task);
		}
	}
	
	
	/**
	 * Removes a task from the Tasklist.
	 */
	@SuppressWarnings("unchecked")
	public void removeTask(Task _task){
		tasklist.remove(_task);
		Iterator iterator = changeListeners.iterator();
		while (iterator.hasNext())
			((ITasklistViewer) iterator.next()).removeTask(_task);
	}
	
	
	public void removeChangeListener(ITasklistViewer viewer) {
		changeListeners.remove(viewer);
	}
	
	
	@SuppressWarnings("unchecked")
	public void addChangeListener(ITasklistViewer viewer) {
		changeListeners.add(viewer);
	}
}