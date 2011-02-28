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

import java.net.Authenticator;

import org.apache.log4j.Logger;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.preference.IPreferenceStore;

import eu.esdihumboldt.hale.rcp.HALEActivator;

/**
 * Manages and applies the proxy settings
 * @author Simon Templer
 */
public class ProxySettings {
	
	private static Logger log = Logger.getLogger(ProxySettings.class);
	
	/**
	 * Install the proxy settings for them to be initialized
	 * when a proxy is used from {@link ProxyUtil}
	 */
	public static void install() {
		// try to setup now, if it fails, set it up to do it later
		try {
			applyCurrentSettings();
		} catch (Throwable e) {
			log.info("Proxy settings not applied, scheduling it for doing it later on demand", e);
			ProxyUtil.addInitializer(new Runnable() {
				
				@Override
				public void run() {
					applyCurrentSettings();
				}
			});
		}
	}
	
	/**
	 * Apply the current proxy settings to the system
	 */
	public static void applyCurrentSettings() {
		//update proxy system properties
		IPreferenceStore prefs = HALEActivator.getDefault().getPreferenceStore();
		String host = prefs.getString(
				PreferenceConstants.CONNECTION_PROXY_HOST);
		int port = prefs.getInt(PreferenceConstants.CONNECTION_PROXY_PORT);
		String nonProxyHosts = prefs.getString(
				PreferenceConstants.CONNECTION_NON_PROXY_HOSTS);
		
		if (nonProxyHosts != null) {
			// support additional delimiters for nonProxyHosts: comma and semicolon
			// the java mechanism needs the pipe as delimiter 
			// see also ProxyPreferencePage.performOk
			nonProxyHosts = nonProxyHosts.replaceAll(",", "|");
			nonProxyHosts = nonProxyHosts.replaceAll(";", "|");
		}
		
		if (host == null || host.isEmpty()) {
			System.clearProperty("http.proxyHost"); //$NON-NLS-1$
			System.clearProperty("http.proxyPort"); //$NON-NLS-1$
			System.clearProperty("http.nonProxyHosts"); //$NON-NLS-1$
			
			System.clearProperty("http.proxyUser");
			System.clearProperty("http.proxyPassword");
		} else {
			System.setProperty("http.proxyHost", host); //$NON-NLS-1$
			System.setProperty("http.proxyPort", String.valueOf(port)); //$NON-NLS-1$
			if (nonProxyHosts == null || nonProxyHosts.isEmpty()) {
				System.clearProperty("http.nonProxyHosts"); //$NON-NLS-1$
			} else {
				System.setProperty("http.nonProxyHosts", nonProxyHosts); //$NON-NLS-1$
			}
			
			// only check user/password if host is set
			String proxyUser = prefs.getString(PreferenceConstants.CONNECTION_PROXY_USER);
			if (proxyUser != null && !proxyUser.isEmpty()) {
				System.setProperty("http.proxyUser", proxyUser);
				
				try {
					String password = SecurePreferencesFactory.getDefault()
						.node(PreferenceConstants.SECURE_NODE_NAME)
						.get(PreferenceConstants.CONNECTION_PROXY_PASSWORD, null);

					if (password != null) {
						System.setProperty("http.proxyPassword", password);
						
						Authenticator.setDefault(new HttpAuth(proxyUser, password));
					}
					else {
						System.clearProperty("http.proxyPassword");
					}
				} catch (StorageException e) {
					log.error("Error accessing secure preferences for proxy password");
				}
			}
			else {
				System.clearProperty("http.proxyUser");
				System.clearProperty("http.proxyPassword");
			}
		}
	}

}
