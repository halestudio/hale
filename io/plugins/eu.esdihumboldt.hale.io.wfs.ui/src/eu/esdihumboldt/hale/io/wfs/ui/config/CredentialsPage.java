/*
 * Copyright (c) 2016 wetransform GmbH
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

package eu.esdihumboldt.hale.io.wfs.ui.config;

import eu.esdihumboldt.hale.ui.io.config.UserPasswordPage;

/**
 * WFS credentials page.
 * 
 * @author Simon Templer
 */
public class CredentialsPage extends UserPasswordPage {

	/**
	 * Default constructor.
	 */
	public CredentialsPage() {
		super();
		setDescription(
				"Please provide user name and password if authentication is needed to access the WFS");
	}

}
