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

package eu.esdihumboldt.hale.ui.util;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartSite;

/**
 * A view context menu.
 * 
 * @author Simon Templer
 */
public class ViewContextMenu implements IMenuListener {

	/**
	 * Create a viewer context menu.
	 * 
	 * @param site the (view) site containing the viewer
	 * @param selectionProvider the view selection provider
	 * @param control the control the context menu should be associated to
	 */
	public ViewContextMenu(IWorkbenchPartSite site, ISelectionProvider selectionProvider,
			Control control) {
		MenuManager menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(this);
		Menu targetMenu = menuManager.createContextMenu(control);
		control.setMenu(targetMenu);

		// register context menus
		site.registerContextMenu(menuManager, selectionProvider);
	}

	/**
	 * Override to change menu contents. By default only a marker for menu
	 * additions is part of the context menu.
	 * 
	 * @see IMenuListener#menuAboutToShow(IMenuManager)
	 */
	@Override
	public void menuAboutToShow(IMenuManager manager) {
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

}
