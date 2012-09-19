/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
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
