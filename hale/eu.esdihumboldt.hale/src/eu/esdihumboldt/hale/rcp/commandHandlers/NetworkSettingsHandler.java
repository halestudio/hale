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
import org.eclipse.ui.handlers.HandlerUtil;

import eu.esdihumboldt.hale.rcp.utils.NetworkSettingsDialog;

/**
 * This Handler is used to display a dialog page for configuring the network 
 * settings (proxy) to use.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class NetworkSettingsHandler 
	extends AbstractHandler 
	implements IHandler {

	/**
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// Instantiates the wizard container with the wizard and opens it
		NetworkSettingsDialog dialog = new NetworkSettingsDialog(
				HandlerUtil.getActiveShell(event));
		dialog.open();
		return null;
	}

}
