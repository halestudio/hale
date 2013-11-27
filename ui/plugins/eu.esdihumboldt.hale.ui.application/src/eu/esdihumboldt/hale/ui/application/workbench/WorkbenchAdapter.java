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

package eu.esdihumboldt.hale.ui.application.workbench;

import org.eclipse.ui.IWorkbench;

/**
 * Basic {@link WorkbenchHook} implementation that does nothing. Extend and
 * override methods.
 * 
 * @author Simon Templer
 */
public class WorkbenchAdapter implements WorkbenchHook {

	@Override
	public void preStartup(IWorkbench workbench) {
		// override me
	}

	@Override
	public void postStartup(IWorkbench workbench) {
		// override me
	}

	@Override
	public boolean preShutdown(IWorkbench workbench) {
		// allow shutdown by default
		return true;
	}

	@Override
	public void postShutdown(IWorkbench workbench) {
		// override me
	}

}
