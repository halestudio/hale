package eu.esdihumboldt.hale.io.haleconnect.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import eu.esdihumboldt.hale.io.haleconnect.ui.internal.HaleConnectUIPlugin;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/**
	 * Default base path of the hale connect user service
	 */
	public static String HALE_CONNECT_BASEPATH_USERS_DEFAULT = "https://users.haleconnect.com/v1";

	/**
	 * Default base path of the hale connect project store
	 */
	public static final String HALE_CONNECT_BASEPATH_PROJECTS_DEFAULT = "https://project-store.haleconnect.com";

	/**
	 * Default base path of the hale connect bucket service
	 */
	public static final String HALE_CONNECT_BASEPATH_DATA_DEFAULT = "https://data.haleconnect.com";

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
				HALE_CONNECT_BASEPATH_USERS_DEFAULT);
		store.setDefault(PreferenceConstants.HALE_CONNECT_BASEPATH_DATA,
				HALE_CONNECT_BASEPATH_DATA_DEFAULT);
		store.setDefault(PreferenceConstants.HALE_CONNECT_BASEPATH_PROJECTS,
				HALE_CONNECT_BASEPATH_PROJECTS_DEFAULT);
	}

}
