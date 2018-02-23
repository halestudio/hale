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

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

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
//		IPreferenceStore preferences = TasksViewPlugin.getDefault().getPreferenceStore();
//
//		Map<String, TaskProviderFactory> taskProviders = TaskPreferenceUtils.getTaskProviders();
//		for (Entry<String, TaskProviderFactory> entry : taskProviders.entrySet()) {
//			preferences.setDefault(entry.getKey(), entry.getValue().isDefaultEnabled());
//		}
	}

}
