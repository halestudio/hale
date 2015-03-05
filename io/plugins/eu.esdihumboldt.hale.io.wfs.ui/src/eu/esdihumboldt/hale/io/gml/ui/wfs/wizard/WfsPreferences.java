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

package eu.esdihumboldt.hale.io.gml.ui.wfs.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import eu.esdihumboldt.hale.io.wfs.ui.internal.WFSUIPlugin;

/**
 * WFS preferences
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class WfsPreferences extends AbstractPreferenceInitializer implements WfsPreferenceConstants {

	/**
	 * @see AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore preferences = WFSUIPlugin.getDefault().getPreferenceStore();

		preferences.setDefault(KEY_RECENT_WFS_COUNT, 0);
	}

	/**
	 * Save the recently used WFS
	 * 
	 * @param recent the search path
	 */
	public static void setRecent(List<String> recent) {
		IPreferenceStore preferences = WFSUIPlugin.getDefault().getPreferenceStore();

		int size = Math.min(MAX_RECENT_WFS, recent.size());

		// TODO remove old ones
		/*
		 * int oldCount = preferences.getInt(KEY_SEARCH_PATH_COUNT); int
		 * numRemove = oldCount - searchPath.size();
		 * 
		 * if (numRemove > 0) { for (int i = ...) }
		 */

		// set new path
		preferences.setValue(KEY_RECENT_WFS_COUNT, size);
		for (int i = 0; i < size; i++) {
			preferences.setValue(KEY_RECENT_WFS_PREFIX + i, recent.get(i));
		}
	}

	/**
	 * Get the recently used WFS
	 * 
	 * @return the search path
	 */
	public static List<String> getRecent() {
		IPreferenceStore preferences = WFSUIPlugin.getDefault().getPreferenceStore();

		List<String> result = new ArrayList<String>();
		int count = preferences.getInt(KEY_RECENT_WFS_COUNT);

		for (int i = 0; i < count; i++) {
			String path = preferences.getString(KEY_RECENT_WFS_PREFIX + i);
			if (path != null) {
				result.add(path);
			}
		}

		return result;
	}

}
