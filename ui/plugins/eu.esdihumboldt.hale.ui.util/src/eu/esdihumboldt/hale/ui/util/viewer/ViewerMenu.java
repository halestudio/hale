/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.util.viewer;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartSite;

/**
 * A viewer context menu.
 * @author Simon Templer
 */
public class ViewerMenu implements IMenuListener {
	
	/**
	 * Create a viewer context menu.
	 * @param site the (view) site containing the viewer
	 * @param viewer the viewer
	 */
	public ViewerMenu(IWorkbenchPartSite site, Viewer viewer) {
		MenuManager menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(this);
		Menu targetMenu = menuManager.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(targetMenu);
		
		// register context menus
		site.registerContextMenu(menuManager, viewer);
	}

	/**
	 * Override to change menu contents. By default only a marker for menu
	 * additions is part of the context menu.
	 * @see IMenuListener#menuAboutToShow(IMenuManager)
	 */
	@Override
	public void menuAboutToShow(IMenuManager manager) {
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

}
