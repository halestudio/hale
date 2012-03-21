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
 * Action to open dialogs via an actionId from within a cheatsheet or from the help. <br />
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
		} else {
			MessageDialog.openWarning(
					Display.getCurrent().getActiveShell(),
					"Action disabled", action.getFactory()
							.getDisabledReason());
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
