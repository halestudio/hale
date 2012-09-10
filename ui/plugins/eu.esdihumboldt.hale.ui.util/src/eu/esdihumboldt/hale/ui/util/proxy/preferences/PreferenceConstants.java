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

import org.eclipse.equinox.security.storage.ISecurePreferences;

/**
 * This interface defines preference constants
 * 
 * @author Simon Templer
 */
public interface PreferenceConstants {

	/**
	 * Name of the node where passwords are stored in the
	 * {@link ISecurePreferences}
	 */
	public static final String SECURE_NODE_NAME = "eu.esdihumboldt.hale"; //$NON-NLS-1$

	/**
	 * The proxy to use for the server connection
	 */
	public static final String CONNECTION_PROXY_HOST = "http.proxyHost"; //$NON-NLS-1$

	/**
	 * The proxy port to use for the server connection
	 */
	public static final String CONNECTION_PROXY_PORT = "http.proxyPort"; //$NON-NLS-1$

	/**
	 * The proxy user name to use for the server connection
	 */
	public static final String CONNECTION_PROXY_USER = "http.proxyUser"; //$NON-NLS-1$

	/**
	 * The proxy password to use for the server connection
	 */
	public static final String CONNECTION_PROXY_PASSWORD = "http.proxyPassword"; //$NON-NLS-1$

	/**
	 * A list of hosts to connect to without using the proxy (separated by a |
	 * character)
	 */
	public static final String CONNECTION_NON_PROXY_HOSTS = "http.nonProxyHosts"; //$NON-NLS-1$

}
