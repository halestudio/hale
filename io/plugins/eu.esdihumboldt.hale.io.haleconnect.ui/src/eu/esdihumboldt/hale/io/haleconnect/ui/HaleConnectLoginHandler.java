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

import java.lang.reflect.InvocationTargetException;
import java.net.SocketTimeoutException;
import java.text.MessageFormat;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectException;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectService;
import eu.esdihumboldt.hale.io.haleconnect.ui.internal.HaleConnectImages;
import eu.esdihumboldt.hale.io.haleconnect.ui.internal.HaleConnectUIPlugin;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.util.io.ThreadProgressMonitor;

/**
 * Handler for the hale connect login command
 * 
 * @author Florian Esser
 */
public class HaleConnectLoginHandler extends AbstractHandler {

	private static final ALogger log = ALoggerFactory.getLogger(HaleConnectLoginHandler.class);

	/**
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell parent = HandlerUtil.getActiveShell(event);
		HaleConnectLoginDialog loginDialog = createLoginDialog(parent);
		if (loginDialog.open() == Dialog.OK) {
			performLogin(loginDialog);
		}

		return null;
	}

	/**
	 * Login to hale connect using the credentials entered in the given
	 * {@link HaleConnectLoginDialog}.
	 * 
	 * @param loginDialog Login dialog with credentials
	 * @return true if successfully logged in
	 */
	public static boolean performLogin(HaleConnectLoginDialog loginDialog) {
		HaleConnectService hcs = HaleUI.getServiceProvider().getService(HaleConnectService.class);
		String username = loginDialog.getUsername();
		String password = loginDialog.getPassword();
		boolean saveCredentials = loginDialog.isSaveCredentials();

		try {
			final AtomicBoolean loginSuccessful = new AtomicBoolean(false);
			final AtomicReference<HaleConnectException> loginException = new AtomicReference<>(
					null);
			ThreadProgressMonitor.runWithProgressDialog(new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					monitor.setTaskName("Logging in to hale connect...");
					try {
						loginSuccessful.set(hcs.login(username, password));
					} catch (HaleConnectException e) {
						loginException.set(e);
					}
				}
			}, false);

			if (loginException.get() != null) {
				HaleConnectException e = loginException.get();
				if (e.getCause() instanceof SocketTimeoutException) {
					log.userError(
							"The connection to hale connect timed out. Please check your internet connection and proxy settings.",
							e);
				}
				else {
					log.userError("An error occurred while trying to login to hale connect", e);
				}
			}
			else if (loginSuccessful.get()) {
				loginDialog.close();
				if (saveCredentials) {
					try {
						HaleConnectUIPlugin.storeUsername(username);
						HaleConnectUIPlugin.storePassword(password);
					} catch (StorageException e) {
						log.error("hale connect credentials could not be saved.", e);
					}
				}

				log.userInfo("Login to hale connect successful.");
				return true;
			}
			else {
				log.userWarn("Login to hale connect failed, please check the credentials.");
			}
		} catch (Exception e) {
			log.userError("An error occurred while trying to login to hale connect", e);
		}

		return false;
	}

	/**
	 * Create a hale connect login dialog using the given {@link Shell}.
	 * 
	 * @param parent parent shell
	 * @return the login dialog
	 */
	public static HaleConnectLoginDialog createLoginDialog(Shell parent) {
		HaleConnectService hcs = HaleUI.getServiceProvider().getService(HaleConnectService.class);

		String username = "";
		String password = "";
		try {
			username = HaleConnectUIPlugin.getStoredUsername();
			password = HaleConnectUIPlugin.getStoredPassword();
		} catch (StorageException e) {
			log.error("Failed to retrieve hale connect credentials from preferences store", e);
		}

		if (hcs.isLoggedIn()) {
			if (!MessageDialog.openQuestion(parent, "Login to hale connect",
					MessageFormat.format(
							"You are currently logged in as \"{0}\". Do you wish to log in again?",
							hcs.getSession().getUsername()))) {
				return null;
			}
			else if (hcs.getSession().getUsername().equals(username)) {
				// Don't fill in from preferences if stored user name was used
				// to login before
				username = "";
				password = "";
			}
		}

		HaleConnectLoginDialog loginDialog = new HaleConnectLoginDialog(parent);
		loginDialog.setTitleAreaColor(new RGB(168, 208, 244));
		loginDialog.setTitleImage(
				HaleConnectImages.getImageRegistry().get(HaleConnectImages.IMG_HCLOGO_DIALOG));
		loginDialog.setUsername(username);
		loginDialog.setPassword(password);
		return loginDialog;
	}

}
