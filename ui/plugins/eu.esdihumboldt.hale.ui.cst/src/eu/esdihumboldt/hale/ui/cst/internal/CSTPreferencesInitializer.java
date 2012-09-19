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

package eu.esdihumboldt.hale.ui.cst.internal;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Preference initializer for keys defined in {@link CSTPreferencesConstants}.
 * 
 * @author Simon Templer
 */
public class CSTPreferencesInitializer extends AbstractPreferenceInitializer implements
		CSTPreferencesConstants {

	/**
	 * @see AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = CSTUIPlugin.getDefault().getPreferenceStore();

		store.setDefault(PREF_ACTIVE_TREE_HOOKS, ""); // by default none
														// selected
	}

}
