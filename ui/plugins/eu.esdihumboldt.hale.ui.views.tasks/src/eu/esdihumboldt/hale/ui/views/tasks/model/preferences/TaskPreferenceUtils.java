/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.views.tasks.model.preferences;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;

import eu.esdihumboldt.hale.common.tasks.extension.TaskProviderExtension;
import eu.esdihumboldt.hale.common.tasks.extension.TaskProviderFactory;
import eu.esdihumboldt.hale.ui.views.tasks.internal.TasksViewPlugin;

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
		Map<String, TaskProviderFactory> result = new HashMap<String, TaskProviderFactory>();
		List<TaskProviderFactory> factories = TaskProviderExtension.getTaskProviderFactories();
		for (TaskProviderFactory factory : factories) {
			result.put(TASK_PROVIDER_PREFIX + factory.getIdentifier(), factory);
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
