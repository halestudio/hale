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

package eu.esdihumboldt.hale.task.preferences;

import java.util.Map;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import eu.esdihumboldt.hale.rcp.HALEActivator;
import eu.esdihumboldt.hale.task.extension.TaskProviderFactory;

/**
 * Sets the task related default preferences
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class TaskPreferenceInitializer extends AbstractPreferenceInitializer
	implements TaskPreferenceConstants {

	/**
	 * @see AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore preferences = HALEActivator.getDefault().getPreferenceStore();

		Map<String, TaskProviderFactory> taskProviders = TaskPreferenceUtils.getTaskProviders();
		for (String key : taskProviders.keySet()) {
			preferences.setDefault(key, true);
		}
	}

}
