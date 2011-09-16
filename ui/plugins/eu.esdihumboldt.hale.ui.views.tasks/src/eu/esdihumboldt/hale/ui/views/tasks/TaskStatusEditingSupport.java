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

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.ui.views.tasks.model.ResolvedTask;
import eu.esdihumboldt.hale.ui.views.tasks.model.TaskUserData;
import eu.esdihumboldt.hale.ui.views.tasks.model.TaskUserData.TaskStatus;
import eu.esdihumboldt.hale.ui.views.tasks.service.TaskService;

/**
 * Editing support for the task status
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class TaskStatusEditingSupport extends AbstractTaskEditingSupport {

	/**
	 * Constructor
	 * 
	 * @param viewer the column viewer 
	 * @param taskService the task service
	 */
	public TaskStatusEditingSupport(ColumnViewer viewer, TaskService taskService) {
		super(viewer, taskService);
	}

	/**
	 * @see EditingSupport#getCellEditor(Object)
	 */
	@Override
	protected CellEditor getCellEditor(Object element) {
		ComboBoxViewerCellEditor editor = new ComboBoxViewerCellEditor((Composite) getViewer().getControl(), SWT.READ_ONLY);
		
		editor.setContenProvider(ArrayContentProvider.getInstance());
		editor.setLabelProvider(new LabelProvider());
		editor.setInput(new Object[]{TaskStatus.NEW, TaskStatus.ACTIVE, TaskStatus.COMPLETED, TaskStatus.IGNORED});
		
		return editor;
	}

	/**
	 * @see EditingSupport#getValue(Object)
	 */
	@Override
	protected Object getValue(Object element) {
		ResolvedTask task = getTask(element);
		
		Object value = null;
		if (task != null) {
			value = task.getTaskStatus();
		}
		
		if (value == null) {
			// may not return null as value
			return TaskStatus.ACTIVE;
		}
		else {
			return value;
		}
	}

	/**
	 * @see EditingSupport#setValue(Object, Object)
	 */
	@Override
	protected void setValue(Object element, Object value) {
		ResolvedTask task = getTask(element);
		if (task != null) {
			TaskUserData userData = getUserData(task);
			userData.setTaskStatus((TaskStatus) value);
			setUserData(task, userData);
		}
	}

}
