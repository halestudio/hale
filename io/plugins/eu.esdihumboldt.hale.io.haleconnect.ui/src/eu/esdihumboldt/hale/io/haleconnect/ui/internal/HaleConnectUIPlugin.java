/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.haleconnect.ui.internal;

import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectService;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectServices;
import eu.esdihumboldt.hale.io.haleconnect.ui.preferences.PreferenceConstants;
import eu.esdihumboldt.hale.ui.HaleUI;

/**
 * hale connect UI plugin
 * 
 * @author Florian Esser
 */
public class HaleConnectUIPlugin extends AbstractUIPlugin {

	/**
	 * The plug-in ID
	 */
	public static final String PLUGIN_ID = "eu.esdihumboldt.hale.io.haleconnect.ui"; //$NON-NLS-1$

	private static final ALogger log = ALoggerFactory.getLogger(HaleConnectUIPlugin.class);

	private static HaleConnectUIPlugin plugin;

	/**
	 * @see AbstractUIPlugin#start(BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		try {
			HaleConnectService hcs = HaleUI.getServiceProvider()
					.getService(HaleConnectService.class);
			hcs.getBasePathManager().setBasePath(HaleConnectServices.USER_SERVICE,
					getPreference(PreferenceConstants.HALE_CONNECT_BASEPATH_USERS));
			hcs.getBasePathManager().setBasePath(HaleConnectServices.BUCKET_SERVICE,
					getPreference(PreferenceConstants.HALE_CONNECT_BASEPATH_DATA));
			hcs.getBasePathManager().setBasePath(HaleConnectServices.PROJECT_STORE,
					getPreference(PreferenceConstants.HALE_CONNECT_BASEPATH_PROJECTS));
		} catch (Throwable t) {
			log.error("Error initializing HaleConnectService", t);
		}
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static HaleConnectUIPlugin getDefault() {
		return plugin;
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#initializeImageRegistry(org.eclipse.jface.resource.ImageRegistry)
	 */
	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);

		reg.put(HaleConnectImages.IMG_HCLOGO_DIALOG,
				imageDescriptorFromPlugin(PLUGIN_ID, "images/hale-connect-small.png"));
		reg.put(HaleConnectImages.IMG_HCLOGO_PREFERENCES,
				imageDescriptorFromPlugin(PLUGIN_ID, "images/hale-connect-mini.png"));
	}

	/**
	 * @param preference preference ID
	 * @return the stored preference value
	 */
	public static String getPreference(String preference) {
		String basePath = HaleConnectUIPlugin.getDefault().getPreferenceStore()
				.getString(preference);
		return basePath;
	}

	/**
	 * Store a preference
	 *
	 * @param preference preference ID
	 * @param value preference value
	 */
	public static void storePreference(String preference, String value) {
		HaleConnectUIPlugin.getDefault().getPreferenceStore().setValue(preference, value);
	}

	/**
	 * @return the hale connect password stored in preferences
	 * @throws StorageException if exception occurred during decryption
	 */
	public static String getStoredPassword() throws StorageException {
		String password;
		password = SecurePreferencesFactory.getDefault().node(PreferenceConstants.SECURE_NODE_NAME)
				.get(PreferenceConstants.HALE_CONNECT_PASSWORD, "");
		return password;
	}

	/**
	 * Store a new hale connect password in preferences
	 * 
	 * @param password new password
	 * @throws StorageException if exception occurred during encryption
	 */
	public static void storePassword(String password) throws StorageException {
		SecurePreferencesFactory.getDefault().node(PreferenceConstants.SECURE_NODE_NAME)
				.put(PreferenceConstants.HALE_CONNECT_PASSWORD, password, true);
	}

	/**
	 * @return the hale connect user name stored in preferences
	 */
	public static String getStoredUsername() {
		String username;
		username = HaleConnectUIPlugin.getDefault().getPreferenceStore()
				.getString(PreferenceConstants.HALE_CONNECT_USERNAME);
		return username;
	}

	/**
	 * Store a new hale connect user name in preferences
	 * 
	 * @param username new user name
	 */
	public static void storeUsername(String username) {
		HaleConnectUIPlugin.getDefault().getPreferenceStore()
				.setValue(PreferenceConstants.HALE_CONNECT_USERNAME, username);
	}
}
