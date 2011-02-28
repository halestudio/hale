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

package eu.esdihumboldt.hale.rcp.utils.proxy;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.preference.IPreferenceStore;

import eu.esdihumboldt.hale.rcp.HALEActivator;

/**
 * Initializes the default preferences based on system
 * properties with the same name.
 * @author Michel Kraemer, Simon Templer
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer
	implements PreferenceConstants {
	
	private static Logger log = Logger.getLogger(PreferenceInitializer.class);
	
	/**
	 * @see AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = HALEActivator.getDefault().getPreferenceStore();
		
		//configure server host
		String host = System.getProperty(
				PreferenceConstants.CONNECTION_SERVER_HOST);
		if (host == null || host.isEmpty()) {
			host = "localhost"; //$NON-NLS-1$
		}
		store.setDefault(PreferenceConstants.CONNECTION_SERVER_HOST, host);
		
		//configure server port
		String portStr = System.getProperty(
				PreferenceConstants.CONNECTION_SERVER_PORT);
		int port = 8080;
		if (portStr != null && !portStr.isEmpty()) {
			port = Integer.parseInt(portStr);
		}
		store.setDefault(PreferenceConstants.CONNECTION_SERVER_PORT, port);
		
		//configure proxy host
		String proxyHost = System.getProperty(
				PreferenceConstants.CONNECTION_PROXY_HOST);
		if (proxyHost == null) {
			proxyHost = ""; //$NON-NLS-1$
		}
		store.setDefault(PreferenceConstants.CONNECTION_PROXY_HOST, proxyHost);
		
		//configure proxy port
		String proxyPortStr = System.getProperty(
				PreferenceConstants.CONNECTION_PROXY_PORT);
		int proxyPort = 8080;
		if (proxyPortStr != null && !proxyPortStr.isEmpty()) {
			proxyPort = Integer.parseInt(proxyPortStr);
		}
		store.setDefault(PreferenceConstants.CONNECTION_PROXY_PORT, proxyPort);
		
		// configure proxy user
		String proxyUser = System.getProperty(CONNECTION_PROXY_USER);
		if (proxyUser == null) {
			proxyUser = "";
		}
		store.setDefault(CONNECTION_PROXY_USER, proxyUser);
		
		// configure proxy user
		ISecurePreferences secPref = SecurePreferencesFactory.getDefault();
		String proxyPassword = System.getProperty(CONNECTION_PROXY_PASSWORD);
		try {
			if (proxyPassword != null && secPref.node(SECURE_NODE_NAME).get(CONNECTION_PROXY_PASSWORD, null) == null) {
				secPref.node(SECURE_NODE_NAME).put(CONNECTION_PROXY_PASSWORD, proxyPassword, true);
			}
		} catch (StorageException e) {
			log.warn("Error accessing secure preferences");
		}
		
		//configure non proxy hosts
		String nonProxyHosts = System.getProperty(
				PreferenceConstants.CONNECTION_NON_PROXY_HOSTS);
		if (nonProxyHosts == null) {
			nonProxyHosts = "";
		}
		store.setDefault(PreferenceConstants.CONNECTION_NON_PROXY_HOSTS,
				nonProxyHosts);
		
		store.setDefault(PreferenceConstants.EXPORT_GRID_TEXT_COMMA, ",");
		store.setDefault(PreferenceConstants.EXPORT_GRID_TEXT_Z_PRECISION, 2);
	}
}
