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

package eu.esdihumboldt.hale.ui.application;

/**
 * Application execution context.
 * 
 * @author Simon Templer
 */
public class ApplicationContext {

	private String launchAction;

	/**
	 * @return the launch action ID or <code>null</code>
	 */
	public String getLaunchAction() {
		return launchAction;
	}

	/**
	 * Set the launch action identifier.
	 * 
	 * @param launchAction the launch action ID
	 */
	public void setLaunchAction(String launchAction) {
		this.launchAction = launchAction;
	}

}
