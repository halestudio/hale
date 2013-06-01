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

package eu.esdihumboldt.hale.ui.launchaction;

import org.eclipse.equinox.app.IApplicationContext;

/**
 * Action that can be triggered when launching the HALE UI application with
 * specific arguments (e.g. -action &lt;ID&gt;). Also, the action may be
 * configured through additional arguments.
 * 
 * @author Simon Templer
 */
public interface LaunchAction {

	/**
	 * Initialize the launch action.
	 * 
	 * @param context the application context of the launched application.
	 */
	public void init(IApplicationContext context);

	/**
	 * Called when the workbench window was opened.
	 */
	public void onOpenWorkbenchWindow();

}
