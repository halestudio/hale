package eu.esdihumboldt.hale.io.haleconnect.ui.preferences;

import static eu.esdihumboldt.hale.io.haleconnect.HaleConnectServices.HALE_CONNECT_BASEPATH_CLIENT_DEFAULT;
import static eu.esdihumboldt.hale.io.haleconnect.HaleConnectServices.HALE_CONNECT_BASEPATH_DATA_DEFAULT;
import static eu.esdihumboldt.hale.io.haleconnect.HaleConnectServices.HALE_CONNECT_BASEPATH_PROJECTS_DEFAULT;
import static eu.esdihumboldt.hale.io.haleconnect.HaleConnectServices.HALE_CONNECT_BASEPATH_USERS_DEFAULT;

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

		store.setDefault(PreferenceConstants.HALE_CONNECT_BASEPATH_USE_DEFAULTS, true);
		store.setDefault(PreferenceConstants.HALE_CONNECT_BASEPATH_USERS,
				HALE_CONNECT_BASEPATH_USERS_DEFAULT);
		store.setDefault(PreferenceConstants.HALE_CONNECT_BASEPATH_DATA,
				HALE_CONNECT_BASEPATH_DATA_DEFAULT);
		store.setDefault(PreferenceConstants.HALE_CONNECT_BASEPATH_PROJECTS,
				HALE_CONNECT_BASEPATH_PROJECTS_DEFAULT);
		store.setDefault(PreferenceConstants.HALE_CONNECT_BASEPATH_CLIENT,
				HALE_CONNECT_BASEPATH_CLIENT_DEFAULT);
	}

}
