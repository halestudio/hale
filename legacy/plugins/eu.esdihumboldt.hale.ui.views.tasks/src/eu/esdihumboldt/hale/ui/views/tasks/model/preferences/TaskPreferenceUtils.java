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

package eu.esdihumboldt.hale.ui.views.tasks.model.preferences;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;

import eu.esdihumboldt.hale.ui.views.tasks.internal.TasksViewPlugin;
import eu.esdihumboldt.hale.ui.views.tasks.model.extension.TaskProviderExtension;
import eu.esdihumboldt.hale.ui.views.tasks.model.extension.TaskProviderFactory;

/**
 * Task preference utilities
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class TaskPreferenceUtils implements TaskPreferenceConstants {
	
	/**
	 * Get the preference keys and factories for the task providers
	 * 
	 * @return the preference keys and factories for the task providers
	 */
	public static Map<String, TaskProviderFactory> getTaskProviders() {
		Map<String , TaskProviderFactory> result = new HashMap<String, TaskProviderFactory>();
		List<TaskProviderFactory> factories = TaskProviderExtension.getTaskProviderFactories();
		for (TaskProviderFactory factory : factories) {
			result.put(TASK_PROVIDER_PREFIX + factory.getId(), factory);
		}
		return result;
	}
	
	/**
	 * Get if the task provider with the given id is set to active
	 * 
	 * @param id the task provider id
	 * 
	 * @return if the task provider is set to active
	 */
	public static boolean getTaskProviderActive(String id) {
		IPreferenceStore preferences = TasksViewPlugin.getDefault().getPreferenceStore();
		
		return preferences.getBoolean(TASK_PROVIDER_PREFIX + id);
	}
	
	/**
	 * Change the setting for the task provider with the given id
	 * 
	 * @param id the task provider id
	 * @param active the active setting value 
	 */
	public static void setTaskProviderActive(String id, boolean active) {
		IPreferenceStore preferences = TasksViewPlugin.getDefault().getPreferenceStore();
		
		preferences.setValue(TASK_PROVIDER_PREFIX + id, active);
	}

}
