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

package eu.esdihumboldt.hale.ui.firststeps;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;

import eu.esdihumboldt.hale.ui.service.project.handler.NewProjectHandler;

/**
 * A cheatsheet action to create a new project.
 * 
 * @author Kai Schwierczek
 */
public class NewProjectAction extends Action implements ICheatSheetAction {
	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		try {
			new NewProjectHandler().execute(null);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see org.eclipse.ui.cheatsheets.ICheatSheetAction#run(java.lang.String[],
	 *      org.eclipse.ui.cheatsheets.ICheatSheetManager)
	 */
	@Override
	public void run(String[] params, ICheatSheetManager manager) {
		run();
	}
}
