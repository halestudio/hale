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

package eu.esdihumboldt.hale.rcp.commands;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import eu.esdihumboldt.hale.rcp.wizards.io.SchemaImportWizard;

/**
 * This Handler is used to load a source or target schema.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class LoadSchemaHandler 
	extends AbstractHandler 
	implements IHandler {
	
	private static Logger _log = Logger.getLogger(LoadSchemaHandler.class);

	/**
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		_log.debug("Arrived in the LoadSchemaHandler...");
		IImportWizard iw = new SchemaImportWizard();
		// Instantiates the wizard container with the wizard and opens it
		Shell shell = HandlerUtil.getActiveShell(event);
		WizardDialog dialog = new WizardDialog(shell, iw);
		//dialog.create();
		dialog.open();
		return null;
	}

}
