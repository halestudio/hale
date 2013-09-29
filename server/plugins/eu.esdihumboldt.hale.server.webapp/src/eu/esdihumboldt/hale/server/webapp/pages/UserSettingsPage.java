/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.server.webapp.pages;

import eu.esdihumboldt.hale.server.webapp.components.user.UserDetailsForm;

/**
 * User settings page.
 * 
 * @author Simon Templer
 */
public class UserSettingsPage extends SecuredPage {

	private static final long serialVersionUID = -8821612341650112316L;

	@Override
	protected void addControls() {
		super.addControls();

		add(new UserDetailsForm("details-form", false));
	}

}
