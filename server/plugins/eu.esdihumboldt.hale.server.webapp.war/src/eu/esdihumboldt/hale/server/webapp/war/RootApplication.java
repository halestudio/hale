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

package eu.esdihumboldt.hale.server.webapp.war;

import org.apache.wicket.Page;

import eu.esdihumboldt.hale.server.webapp.BaseWebApplication;
import eu.esdihumboldt.hale.server.webapp.war.pages.WelcomePage;

/**
 * The root web application for the user interface.
 * 
 * @author Michel Kraemer
 */
public class RootApplication extends BaseWebApplication {

	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends Page> getHomePage() {
		return WelcomePage.class;
	}

}
