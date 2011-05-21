/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.rcp.commandHandlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import eu.esdihumboldt.hale.models.ProjectService;
import eu.esdihumboldt.hale.rcp.HALEActivator;
import eu.esdihumboldt.hale.Messages;
import eu.esdihumboldt.hale.rcp.wizards.io.SaveAlignmentProjectWizard;
import eu.esdihumboldt.hale.ui.util.ExceptionHelper;

/**
 * This type handles the saving of Alignment Projects.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class SaveAlignmentProjectHandler extends AbstractHandler implements
		IHandler {

	/**
	 * @see IHandler#execute(ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ProjectService ps = (ProjectService) PlatformUI.getWorkbench().getService(ProjectService.class);
		try {
			// try saving project
			if (!ps.save()) {
				// have to use save as
				IExportWizard iw = new SaveAlignmentProjectWizard();
				// Instantiates the wizard container with the wizard and opens it
				Shell shell = HandlerUtil.getActiveShell(event);
				WizardDialog dialog = new WizardDialog(shell, iw);
				dialog.open();
			}
		} catch (Exception e) {
			String message = Messages.SaveAlignmentProjectWizard_SaveFaild;
			ExceptionHelper.handleException(
					message, HALEActivator.PLUGIN_ID, e);
		}
		
		return null;
	}
}
