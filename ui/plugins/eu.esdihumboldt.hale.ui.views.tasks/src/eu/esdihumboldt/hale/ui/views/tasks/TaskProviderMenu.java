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

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.ui.views.tasks.model.extension.TaskProviderExtension;
import eu.esdihumboldt.hale.ui.views.tasks.model.extension.TaskProviderFactory;
import eu.esdihumboldt.hale.ui.views.tasks.service.TaskService;

/**
 * Task provider activation menu
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class TaskProviderMenu extends ContributionItem {

	/**
	 * Action for activating/deactivating a task provider 
	 */
	private static class TaskProviderAction extends Action {
		
		private final String taskProviderId;
		
		private final TaskService taskService;

		/**
		 * Create a new task provider action
		 * 
		 * @param factory the task provider factory
		 * @param taskService the task service
		 */
		public TaskProviderAction(TaskProviderFactory factory,
				TaskService taskService) {
			super(factory.getName(), AS_CHECK_BOX);
			
			this.taskProviderId = factory.getId();
			this.taskService = taskService;
			
			setChecked(taskService.taskProviderIsActive(taskProviderId));
			setToolTipText(factory.getDescription());
		}

		/**
		 * @see Action#run()
		 */
		@Override
		public void run() {
			if (isChecked()) {
				taskService.activateTaskProvider(taskProviderId);
			}
			else {
				taskService.deactivateTaskProvider(taskProviderId);
			}
		}

	}

	/**
	 * @see ContributionItem#fill(Menu, int)
	 */
	@Override
	public void fill(Menu menu, int index) {
		TaskService taskService = (TaskService) PlatformUI.getWorkbench().getService(TaskService.class);
		
		List<TaskProviderFactory> factories = TaskProviderExtension.getTaskProviderFactories();
		for (TaskProviderFactory factory : factories) {
			IAction action = new TaskProviderAction(factory, taskService);
			IContributionItem item = new ActionContributionItem(action);
			item.fill(menu, index++);
		}
	}

	/**
	 * @see ContributionItem#isDynamic()
	 */
	@Override
	public boolean isDynamic() {
		return true;
	}

}
