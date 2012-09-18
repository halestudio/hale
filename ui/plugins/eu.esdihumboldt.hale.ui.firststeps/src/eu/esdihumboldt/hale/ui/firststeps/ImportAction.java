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

package eu.esdihumboldt.hale.ui.firststeps;

import org.eclipse.help.ILiveHelpAction;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;

import eu.esdihumboldt.hale.ui.io.action.IOWizardAction;

/**
 * Action to open dialogs via an actionId from within a cheatsheet or from the
 * help. <br />
 * It needs one parameter, the actionId.
 * 
 * @author Kai Schwierczek
 */
public class ImportAction extends Action implements ICheatSheetAction, ILiveHelpAction {

	private String actionId;

	/**
	 * @see org.eclipse.help.ILiveHelpAction#setInitializationString(java.lang.String)
	 */
	@Override
	public void setInitializationString(String data) {
		actionId = data;
	}

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		if (Display.getCurrent() == null) {
			// execute in display thread
			PlatformUI.getWorkbench().getDisplay().asyncExec(this);
			return;
		}

		if (actionId == null)
			return;

		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().forceActive();

		IOWizardAction action = new IOWizardAction(actionId);
		if (action.isEnabled()) {
			action.addPropertyChangeListener(new IPropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent event) {
					if (IAction.RESULT.equals(event.getProperty()))
						notifyResult((Boolean) event.getNewValue());
				}
			});
			action.run();
		}
		else {
			MessageDialog.openWarning(Display.getCurrent().getActiveShell(), "Action disabled",
					action.getFactory().getDisabledReason());
			notifyResult(false);
		}
		action.dispose();
	}

	/**
	 * @see org.eclipse.ui.cheatsheets.ICheatSheetAction#run(java.lang.String[],
	 *      org.eclipse.ui.cheatsheets.ICheatSheetManager)
	 */
	@Override
	public void run(String[] params, ICheatSheetManager manager) {
		if (params.length > 0) {
			actionId = params[0];
			run();
		}
	}
}
