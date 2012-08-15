/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.cst.internal;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Preference initializer for keys defined in {@link CSTPreferencesConstants}. 
 * @author Simon Templer
 */
public class CSTPreferencesInitializer extends AbstractPreferenceInitializer
		implements CSTPreferencesConstants {

	/**
	 * @see AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = CSTUIPlugin.getDefault().getPreferenceStore();
		
		store.setDefault(PREF_ACTIVE_TREE_HOOKS, ""); // by default none selected
	}

}
