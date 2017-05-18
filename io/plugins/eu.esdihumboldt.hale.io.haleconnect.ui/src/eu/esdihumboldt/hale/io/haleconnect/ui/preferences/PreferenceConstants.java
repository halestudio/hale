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

package eu.esdihumboldt.hale.io.haleconnect.ui.preferences;

import org.eclipse.equinox.security.storage.ISecurePreferences;

/**
 * Constants for hale connect preferences
 * 
 * @author Florian Esser
 */
@SuppressWarnings("javadoc")
public interface PreferenceConstants {

	/**
	 * Name of the node where passwords are stored in the
	 * {@link ISecurePreferences}
	 */
	static final String SECURE_NODE_NAME = "eu.esdihumboldt.hale"; //$NON-NLS-1$

	static final String HALE_CONNECT_BASEPATH_USE_DEFAULTS = "eu.esdihumboldt.hale.io.haleconnect.ui.endpoints.usedefaults";

	static final String HALE_CONNECT_BASEPATH_USERS = "eu.esdihumboldt.hale.io.haleconnect.ui.login.endpoint";
	static final String HALE_CONNECT_BASEPATH_DATA = "eu.esdihumboldt.hale.io.haleconnect.ui.endpoints.data";
	static final String HALE_CONNECT_BASEPATH_PROJECTS = "eu.esdihumboldt.hale.io.haleconnect.ui.endpoints.projects";
	static final String HALE_CONNECT_BASEPATH_CLIENT = "eu.esdihumboldt.hale.io.haleconnect.ui.endpoints.client";

	static final String HALE_CONNECT_USERNAME = "eu.esdihumboldt.hale.io.haleconnect.ui.login.username";
	static final String HALE_CONNECT_PASSWORD = "eu.esdihumboldt.hale.io.haleconnect.ui.login.password";
}
