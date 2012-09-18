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
package eu.esdihumboldt.hale.ui.application;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import eu.esdihumboldt.hale.ui.application.internal.Messages;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	private IWorkbenchAction undoAction;
	private IWorkbenchAction redoAction;
	private IWorkbenchAction introAction;
	private IWorkbenchAction savePerspectiveAction;
	private IContributionItem viewsShortList;

	/**
	 * @see ActionBarAdvisor#ActionBarAdvisor(IActionBarConfigurer)
	 */
	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	/**
	 * @see ActionBarAdvisor#makeActions(IWorkbenchWindow)
	 */
	@Override
	protected void makeActions(IWorkbenchWindow window) {
		super.makeActions(window);

		register(undoAction = ActionFactory.UNDO.create(window));
		register(redoAction = ActionFactory.REDO.create(window));
		register(introAction = ActionFactory.INTRO.create(window));
		register(savePerspectiveAction = ActionFactory.SAVE_PERSPECTIVE.create(window));

		viewsShortList = ContributionItemFactory.VIEWS_SHORTLIST.create(window);
	}

	/**
	 * @see ActionBarAdvisor#fillMenuBar(IMenuManager)
	 */
	@Override
	protected void fillMenuBar(IMenuManager menuBar) {
		menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));

		// edit menu
		IMenuManager menu = new MenuManager("Edit", IWorkbenchActionConstants.M_EDIT);
		menu.add(new GroupMarker(IWorkbenchActionConstants.EDIT_START));

		menu.add(undoAction);
		menu.add(redoAction);
		menu.add(new GroupMarker(IWorkbenchActionConstants.UNDO_EXT));
		menu.add(new Separator());

		menuBar.add(menu);

		// window menu
		IMenuManager windowMenu = new MenuManager(Messages.ApplicationWorkbenchWindowAdvisor_1,
				IWorkbenchActionConstants.M_WINDOW); //$NON-NLS-1$
		IMenuManager viewMenu = new MenuManager(Messages.ApplicationWorkbenchWindowAdvisor_3,
				"view"); //$NON-NLS-1$
		windowMenu.add(viewMenu);
		viewMenu.add(viewsShortList);

		windowMenu.add(savePerspectiveAction);

		menuBar.add(windowMenu);

		// help menu
		IMenuManager helpMenu = new MenuManager(Messages.ApplicationWorkbenchWindowAdvisor_2,
				IWorkbenchActionConstants.M_HELP); //$NON-NLS-1$ //$NON-NLS-2$
		helpMenu.add(introAction);

		menuBar.add(helpMenu);
	}

}
