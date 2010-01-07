package eu.esdihumboldt.hale.rcp.views.tasks;

import org.eclipse.jface.viewers.ICellModifier;

import eu.esdihumboldt.hale.task.Task;

/**
 * 
 * This class implements an ICellModifier.
 * An ICellModifier is called when the user modifies a cell in the 
 * tableViewer of the Tasklist.
 *
 * @author cjauss
 */
public class TasklistCellModifier implements ICellModifier{
	
	/**
	 * Tasks don't need to be modified.
	 */
	public boolean canModify(Object _element, String _property) {
		return false;
	}
	
	
	/**
	 * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object, java.lang.String)
	 */
	public Object getValue(Object _element, String _property) {
		
		Task task = (Task)_element;
		
		Object result = null;
		
		if(_property.equals("Severity")){
			result = task.getSeverityLevel().name();
		}
		else if(_property.equals("Tasktype")){
			result = task.getTaskType();
		}
		else if(_property.equals("Title")){
			result = task.getTaskTitle();
		}
		else if(_property.equals("Value")){
			result = task.getValue();
		}
		else if(_property.equals("Source Implementation Name")){
			result = task.getSource().getImplementationName();
		}
		else if(_property.equals("Task Creation Reason")){
			result = task.getSource().getTaskCreationReason();
		}
		return result;
	}
	
	
	/**
	 * THIS VOID IS BLANK!!!
	 * Tasks don't need to be modified. 
	 */
	public void modify(Object _element, String _property, Object _value) {
	}
}
