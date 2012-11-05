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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.server.status.war.pages;

import eu.esdihumboldt.hale.server.webapp.pages.BasePage;
import eu.esdihumboldt.hale.server.webapp.util.PageDescription;

/**
 * Displays status components in a tabbed panel.
 * 
 * @author Simon Templer
 */
@PageDescription(title = "Server status")
public class StatusPage extends BasePage {

	private static final long serialVersionUID = -7981372509600164074L;

	@Override
	protected void addControls(boolean loggedIn) {
		super.addControls(loggedIn);

		// TODO
	}

}
