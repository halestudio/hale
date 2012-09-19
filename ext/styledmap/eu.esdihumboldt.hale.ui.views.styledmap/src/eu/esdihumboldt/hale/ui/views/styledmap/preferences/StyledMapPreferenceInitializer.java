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

package eu.esdihumboldt.hale.ui.views.styledmap.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import eu.esdihumboldt.hale.ui.views.styledmap.internal.StyledMapBundle;

/**
 * Preference initializer for the styled map view.
 * 
 * @author Simon Templer
 */
public class StyledMapPreferenceInitializer extends AbstractPreferenceInitializer implements
		StyledMapPreferenceConstants {

	private static final String DEFAULT_MAP_LAYOUT = "eu.esdihumboldt.hale.ui.views.styledmap.default";

	/**
	 * @see AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = StyledMapBundle.getDefault().getPreferenceStore();

		store.setDefault(CURRENT_MAP_LAYOUT, DEFAULT_MAP_LAYOUT);
	}

}
