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

package eu.esdihumboldt.hale.server.console.war;

import org.apache.wicket.Page;

import eu.esdihumboldt.hale.server.console.war.pages.WelcomePage;
import eu.esdihumboldt.hale.server.webapp.BaseWebApplication;

/**
 * The main web application for the administration console view.
 * 
 * @author Michel Kraemer
 */
public class ConsoleApplication extends BaseWebApplication {

	/**
	 * @see BaseWebApplication#getMainTitle()
	 */
	@Override
	public String getMainTitle() {
		return "HALE Web Administration Console";
	}

	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends Page> getHomePage() {
		return WelcomePage.class;
	}
}
