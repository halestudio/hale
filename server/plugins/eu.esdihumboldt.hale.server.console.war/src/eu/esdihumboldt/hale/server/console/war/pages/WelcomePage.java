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

package eu.esdihumboldt.hale.server.console.war.pages;

import eu.esdihumboldt.hale.server.webapp.pages.BasePage;
import eu.esdihumboldt.hale.server.webapp.util.PageDescription;

/**
 * The main page for the administration console view. It provides access to the
 * OSGi console via the web interface
 * 
 * @author Michel Kraemer
 */
@PageDescription(title = "Console")
public class WelcomePage extends BasePage {

	private static final long serialVersionUID = -1394082788930930935L;

	/**
	 * Default constructor
	 */
	public WelcomePage() {
		// nothing to do here
	}
}
