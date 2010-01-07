package eu.esdihumboldt.hale.rcp.views.tasks;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.task.Task;




/**
 * Provides Images for the elements of the Tasklist.
 * @author cjauss
 *
 */
public class TasklistLabelProvider extends LabelProvider implements ITableLabelProvider{
	
	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Object _element, int _columnIndex) {
		Image result = null;
		
		if(_columnIndex == 0){
			Task task = (Task) _element;
			if(task.getSeverityLevel().name().equals("error")){
				result = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
			}
			else if(task.getSeverityLevel().name().equals("warning")){
				result = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_WARN_TSK);
			}
			else if(task.getSeverityLevel().name().equals("task")){
				result = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
			}
		}
		return result;
	}
	
	
	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object _element, int _columnIndex) {
		String result = "";
		Task task = (Task) _element;
		
		switch(_columnIndex){
			case 0: result = task.getSeverityLevel().name();break;
			case 1: result = task.getValue()+"";break;
			case 2: result = task.getTaskType();break;
			case 3: result = task.getTaskTitle();break;
			case 4: result = task.getSource().getImplementationName();break;
			case 5: result = task.getSource().getTaskCreationReason();break;
		}
		return result;
	}
}
