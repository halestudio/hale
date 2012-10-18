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

package eu.esdihumboldt.hale.server.webapp;

import org.apache.wicket.protocol.http.WebApplication;

import eu.esdihumboldt.hale.server.webapp.util.DynamicSpringComponentInjector;

/**
 * A basic class for web applications
 * 
 * @author Michel Kraemer
 */
public abstract class BaseWebApplication extends WebApplication {

	/**
	 * The default title of a web application
	 */
	public static final String DEFAULT_TITLE = "HALE Web";

	/**
	 * @return the main title of this application
	 */
	public abstract String getMainTitle();

	@Override
	public void init() {
		super.init();

		// enforce mounts so security interceptors based on URLs can't be fooled
		getSecuritySettings().setEnforceMounts(true);

		addComponentInstantiationListener(new DynamicSpringComponentInjector());
	}

}
