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

package eu.esdihumboldt.hale.server.security.extender;

import org.springframework.context.ApplicationContext;

/**
 * An application context that knows about its security configuration. This
 * class contains items related to the application context of the current web
 * application and no global configuration.
 * 
 * @author Michel Kraemer
 */
public interface SecuredApplicationContext extends ApplicationContext {

	/**
	 * @return true if security management is enabled, false if everyone is
	 *         allowed to do everything
	 */
	boolean isSecurityEnabled();
}
