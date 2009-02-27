package eu.esdihumboldt.hale.rcp.views.tasks;

import java.util.HashSet;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import eu.esdihumboldt.hale.task.Task;


/**
 * Contentprovider for the Tasklists TableViewer.
 * @author cjauss
 *
 */
public class TasklistContentProvider implements IStructuredContentProvider, ITasklistViewer{

	private TableViewer tableViewer;
	
	public TasklistContentProvider(TableViewer _tViewer){
		super();
		this.tableViewer = _tViewer;
	}
	
	public void dispose() {
		// TODO Auto-generated method stub
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

	public Object[] getElements(Object inputElement){
		Tasklist tlist = (Tasklist) tableViewer.getInput();
		HashSet<Task> tasklist= tlist.getTasklist();
		return tasklist.toArray();
	}
	
	/**
	 * @see ITaskListViewer#addTask(Task)
	 */
	public void addTask(Task _task){
		this.tableViewer.add(_task);
	}
	
	/**
	 * @see ITaskListViewer#removeTask(Task)
	 */
	public void removeTask(Task _task){
		this.tableViewer.remove(_task);
	}
}
