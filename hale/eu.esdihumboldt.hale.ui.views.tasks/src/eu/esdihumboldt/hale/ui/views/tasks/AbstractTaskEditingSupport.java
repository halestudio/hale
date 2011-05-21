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

package eu.esdihumboldt.hale.ui.views.tasks;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;

import eu.esdihumboldt.hale.ui.util.tree.AbstractMultiColumnTreeNode;
import eu.esdihumboldt.hale.ui.views.tasks.model.ResolvedTask;
import eu.esdihumboldt.hale.ui.views.tasks.model.TaskUserData;
import eu.esdihumboldt.hale.ui.views.tasks.model.impl.TaskUserDataImpl;
import eu.esdihumboldt.hale.ui.views.tasks.service.TaskService;

/**
 * Editing support for the tasks
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class AbstractTaskEditingSupport extends EditingSupport {
	
	private final TaskService taskService;

	/**
	 * Constructor
	 * 
	 * @param viewer the column viewer 
	 * @param taskService the task service
	 */
	public AbstractTaskEditingSupport(ColumnViewer viewer, TaskService taskService) {
		super(viewer);
		
		this.taskService = taskService;
	}

	/**
	 * @see EditingSupport#canEdit(Object)
	 */
	@Override
	protected boolean canEdit(Object element) {
		return getTask(element) != null;
	}

	/**
	 * Get the element's resolved task if possible
	 * 
	 * @param element the element
	 * 
	 * @return the resolved task or <code>null</code>
	 */
	protected ResolvedTask getTask(Object element) {
		if (element instanceof AbstractMultiColumnTreeNode) {
			Object value = ((AbstractMultiColumnTreeNode) element).getFirstValue();
			if (value instanceof ResolvedTask) {
				return (ResolvedTask) value;
			}
		}
		
		return null;
	}
	
	/**
	 * Set the given task's user data
	 * 
	 * @param task the task
	 * @param userData the user data
	 */
	protected void setUserData(ResolvedTask task, TaskUserData userData) {
		taskService.setUserData(task.getTask(), userData);
	}
	
	/**
	 * Get the user data for the given task
	 *  
	 * @param task the task
	 * 
	 * @return the user data
	 */
	protected TaskUserData getUserData(ResolvedTask task) {
		TaskUserData userData = task.getUserData();
		if (userData == null) {
			userData = new TaskUserDataImpl();
		}
		return userData;
	}

}
