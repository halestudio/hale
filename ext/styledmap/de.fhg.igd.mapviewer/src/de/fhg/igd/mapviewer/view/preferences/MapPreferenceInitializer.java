/*
 * Copyright (c) 2016 Fraunhofer IGD
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
 *     Fraunhofer IGD <http://www.igd.fraunhofer.de/>
 */
package de.fhg.igd.mapviewer.view.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import de.fhg.igd.eclipse.ui.util.extension.selective.PreferencesSelectiveExtension;
import de.fhg.igd.mapviewer.view.MapviewerPlugin;

/**
 * Map preferences initializer.
 * 
 * @author Simon Templer
 */
public class MapPreferenceInitializer extends AbstractPreferenceInitializer {

	/**
	 * @see AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = MapviewerPlugin.getDefault().getPreferenceStore();

		store.setDefault(MapPreferenceConstants.ACTIVE_MAP_PAINTERS,
				PreferencesSelectiveExtension.PREFERENCE_ALL);

		store.setDefault(MapPreferenceConstants.ACTIVE_TILE_OVERLAYS,
				PreferencesSelectiveExtension.PREFERENCE_ALL);
	}

}
