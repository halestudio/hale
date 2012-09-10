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

package eu.esdihumboldt.hale.io.gml.ui.wfs.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import eu.esdihumboldt.hale.io.gml.ui.internal.GmlUIPlugin;

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
		IPreferenceStore preferences = GmlUIPlugin.getDefault().getPreferenceStore();

		preferences.setDefault(KEY_RECENT_WFS_COUNT, 0);
	}

	/**
	 * Save the recently used WFS
	 * 
	 * @param recent the search path
	 */
	public static void setRecent(List<String> recent) {
		IPreferenceStore preferences = GmlUIPlugin.getDefault().getPreferenceStore();

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
		IPreferenceStore preferences = GmlUIPlugin.getDefault().getPreferenceStore();

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
