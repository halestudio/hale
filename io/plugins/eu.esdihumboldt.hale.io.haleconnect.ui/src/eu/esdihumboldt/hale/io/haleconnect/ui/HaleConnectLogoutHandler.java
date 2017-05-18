/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.haleconnect.ui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectService;
import eu.esdihumboldt.hale.io.haleconnect.ui.internal.HaleConnectUIPlugin;
import eu.esdihumboldt.hale.ui.HaleUI;

/**
 * Handler for the hale connect clear credentials command
 * 
 * @author Florian Esser
 */
public class HaleConnectLogoutHandler extends AbstractHandler {

	private static final ALogger log = ALoggerFactory.getLogger(HaleConnectLogoutHandler.class);

	/**
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell parent = HandlerUtil.getActiveShell(event);
		if (!MessageDialog.openConfirm(parent, "Clear hale connect credentials",
				"This will invalidate your current hale connect session and remove all stored credentials.")) {
			return null;
		}

		HaleConnectService hcs = HaleUI.getServiceProvider().getService(HaleConnectService.class);
		hcs.clearSession();
		try {
			HaleConnectUIPlugin.storeUsername("");
			HaleConnectUIPlugin.storePassword("");

			log.userInfo("hale connect credentials cleared");
		} catch (StorageException e) {
			log.userError("Error clearing credentials", e);
		}

		return null;
	}

}
