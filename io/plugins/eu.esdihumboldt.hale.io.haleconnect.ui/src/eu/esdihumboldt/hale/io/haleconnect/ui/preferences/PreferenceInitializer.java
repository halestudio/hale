package eu.esdihumboldt.hale.io.haleconnect.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import eu.esdihumboldt.hale.io.haleconnect.ui.internal.HaleConnectUIPlugin;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = HaleConnectUIPlugin.getDefault().getPreferenceStore();

		store.setDefault(PreferenceConstants.HALE_CONNECT_BASEPATH_USERS,
				"https://users.haleconnect.com/v1");
		store.setDefault(PreferenceConstants.HALE_CONNECT_BASEPATH_DATA,
				"https://data.haleconnect.com/");
		store.setDefault(PreferenceConstants.HALE_CONNECT_BASEPATH_PROJECTS,
				"https://project-store.haleconnect.com/");
	}

}
