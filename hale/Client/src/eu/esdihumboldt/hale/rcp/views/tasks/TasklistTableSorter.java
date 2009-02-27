package eu.esdihumboldt.hale.rcp.views.tasks;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import eu.esdihumboldt.hale.task.Task;

/**
 * Sorter for the TasklistViews TableViewer. The Sorter can sort Severity column, 
 * Type column, Title column, Value column, Source Implementation Name column
 * and the Task Creation Reason column of the Tableviewer.
 * 
 * @author cjauss
 *
 */
public class TasklistTableSorter extends ViewerSorter{
	
	public static final int SEVERITY = 0;
	public static final int TYPE = 1;
	public static final int TITLE = 2;
	public static final int VALUE = 3;
	public static final int SOURCEIMPLEMENTAIONNAME = 4;
	public static final int TASKCREATIONREASON = 5;
	
	private int criteria;
	
	
	public TasklistTableSorter(int _criteria){
		super();
		this.criteria = _criteria;
	}
	
	
	@Override
	public int compare(Viewer _viewer, Object _o1, Object _o2){
		Task task1 = (Task) _o1;
		Task task2 = (Task) _o2;
		
		switch(criteria){
		case SEVERITY: return compareSeverity(task1,task2);
		case TYPE: return compareType(task1,task2);
		case TITLE: return compareTitle(task1,task2);
		case VALUE: return compareValue(task1,task2);
		case SOURCEIMPLEMENTAIONNAME: return compareSourceImpl(task1,task2);
		case TASKCREATIONREASON: return compareTaskCreate(task1,task2);
		default: return 0;
		}
	}
	
	
	/**
	 * Returns a number reflecting the collation order of the given tasks
	 * based on the description.
	 *
	 * @param _task1 the first task element to be ordered
	 * @param resource2 the second task element to be ordered
	 * @return a negative number if the first element is less  than the 
	 *  second element; the value <code>0</code> if the first element is
	 *  equal to the second element; and a positive number if the first
	 *  element is greater than the second element
	 */
	@SuppressWarnings("unchecked")
	private int compareSeverity(Task _task1, Task _task2) {
		return getComparator().compare(_task1.getSeverityLevel().name(),
				_task2.getSeverityLevel().name());
	}
	
	
	/**
	 * Returns a number reflecting the collation order of the given tasks
	 * based on the description.
	 *
	 * @param _task1 the first task element to be ordered
	 * @param resource2 the second task element to be ordered
	 * @return a negative number if the first element is less  than the 
	 *  second element; the value <code>0</code> if the first element is
	 *  equal to the second element; and a positive number if the first
	 *  element is greater than the second element
	 */
	@SuppressWarnings("unchecked")
	private int compareSourceImpl(Task _task1, Task _task2) {
		return getComparator().compare(_task1.getSource().getImplementationName(),
				_task2.getSource().getImplementationName());
	}
	
	
	/**
	 * Returns a number reflecting the collation order of the given tasks
	 * based on the description.
	 *
	 * @param _task1 the first task element to be ordered
	 * @param resource2 the second task element to be ordered
	 * @return a negative number if the first element is less  than the 
	 *  second element; the value <code>0</code> if the first element is
	 *  equal to the second element; and a positive number if the first
	 *  element is greater than the second element
	 */
	@SuppressWarnings("unchecked")
	private int compareTaskCreate(Task _task1, Task _task2) {
		return getComparator().compare(_task1.getSource().getTaskCreationReason(),
				_task2.getSource().getTaskCreationReason());
	}
	
	
	/**
	 * Returns a number reflecting the collation order of the given tasks
	 * based on the description.
	 *
	 * @param task1 the first task element to be ordered
	 * @param resource2 the second task element to be ordered
	 * @return a negative number if the first element is less  than the 
	 *  second element; the value <code>0</code> if the first element is
	 *  equal to the second element; and a positive number if the first
	 *  element is greater than the second element
	 */
	private int compareValue(Task _task1, Task _task2) {
		double result = _task1.getValue() - _task2.getValue();
		if(result>0){
			return 1;
		}
		else if(result<0){
			return -1;
		}
		else{
			return 0;
		}
	}


	/**
	 * Returns a number reflecting the collation order of the given tasks
	 * based on the description.
	 *
	 * @param task1 the first task element to be ordered
	 * @param resource2 the second task element to be ordered
	 * @return a negative number if the first element is less  than the 
	 *  second element; the value <code>0</code> if the first element is
	 *  equal to the second element; and a positive number if the first
	 *  element is greater than the second element
	 */
	@SuppressWarnings("unchecked")
	private int compareTitle(Task _task1, Task _task2) {
		return getComparator().compare(_task1.getTaskTitle(), _task2.getTaskTitle());
	}


	/**
	 * Returns a number reflecting the collation order of the given tasks
	 * based on the description.
	 *
	 * @param task1 the first task element to be ordered
	 * @param resource2 the second task element to be ordered
	 * @return a negative number if the first element is less  than the 
	 *  second element; the value <code>0</code> if the first element is
	 *  equal to the second element; and a positive number if the first
	 *  element is greater than the second element
	 */
	@SuppressWarnings("unchecked")
	private int compareType(Task _task1, Task _task2) {
		return getComparator().compare(_task1.getTaskType(), _task2.getTaskType());
	}
}
