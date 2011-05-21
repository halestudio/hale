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

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import eu.esdihumboldt.hale.ui.views.tasks.internal.TasksViewPlugin;
import eu.esdihumboldt.hale.ui.views.tasks.model.extension.TaskProviderFactory;

/**
 * Sets the task related default preferences
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class TaskPreferenceInitializer extends AbstractPreferenceInitializer
	implements TaskPreferenceConstants {

	/**
	 * @see AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore preferences = TasksViewPlugin.getDefault().getPreferenceStore();

		Map<String, TaskProviderFactory> taskProviders = TaskPreferenceUtils.getTaskProviders();
		for (Entry<String, TaskProviderFactory> entry : taskProviders.entrySet()) {
			preferences.setDefault(entry.getKey(), entry.getValue().isDefaultEnabled());
		}
	}

}
