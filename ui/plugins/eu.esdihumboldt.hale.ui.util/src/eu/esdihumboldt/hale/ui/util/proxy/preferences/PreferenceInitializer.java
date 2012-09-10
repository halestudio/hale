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

package eu.esdihumboldt.hale.ui.util.proxy.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.preference.IPreferenceStore;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.ui.util.internal.UIUtilitiesPlugin;

/**
 * Initializes the default preferences based on system properties with the same
 * name.
 * 
 * @author Michel Kraemer, Simon Templer
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer implements
		PreferenceConstants {

	private static ALogger log = ALoggerFactory.getLogger(PreferenceInitializer.class);

	/**
	 * @see AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = UIUtilitiesPlugin.getDefault().getPreferenceStore();

		// configure proxy host
		String proxyHost = System.getProperty(PreferenceConstants.CONNECTION_PROXY_HOST);
		if (proxyHost == null) {
			proxyHost = ""; //$NON-NLS-1$
		}
		store.setDefault(PreferenceConstants.CONNECTION_PROXY_HOST, proxyHost);

		// configure proxy port
		String proxyPortStr = System.getProperty(PreferenceConstants.CONNECTION_PROXY_PORT);
		int proxyPort = 8080;
		if (proxyPortStr != null && !proxyPortStr.isEmpty()) {
			proxyPort = Integer.parseInt(proxyPortStr);
		}
		store.setDefault(PreferenceConstants.CONNECTION_PROXY_PORT, proxyPort);

		// configure proxy user
		String proxyUser = System.getProperty(CONNECTION_PROXY_USER);
		if (proxyUser == null) {
			proxyUser = ""; //$NON-NLS-1$
		}
		store.setDefault(CONNECTION_PROXY_USER, proxyUser);

		// configure proxy user
		ISecurePreferences secPref = SecurePreferencesFactory.getDefault();
		String proxyPassword = System.getProperty(CONNECTION_PROXY_PASSWORD);
		try {
			if (proxyPassword != null
					&& secPref.node(SECURE_NODE_NAME).get(CONNECTION_PROXY_PASSWORD, null) == null) {
				secPref.node(SECURE_NODE_NAME).put(CONNECTION_PROXY_PASSWORD, proxyPassword, true);
			}
		} catch (StorageException e) {
			log.warn("Error accessing secure preferences"); //$NON-NLS-1$
		}

		// configure non proxy hosts
		String nonProxyHosts = System.getProperty(PreferenceConstants.CONNECTION_NON_PROXY_HOSTS);
		if (nonProxyHosts == null) {
			nonProxyHosts = ""; //$NON-NLS-1$
		}
		store.setDefault(PreferenceConstants.CONNECTION_NON_PROXY_HOSTS, nonProxyHosts);
	}
}
