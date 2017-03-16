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

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.intro.IIntroPart;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;
import org.eclipse.ui.intro.config.IIntroContentProvider;
import org.eclipse.ui.intro.config.IIntroContentProviderSite;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectException;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectService;
import eu.esdihumboldt.hale.io.haleconnect.ui.internal.HaleConnectUIPlugin;
import eu.esdihumboldt.hale.ui.HaleUI;

/**
 * Content provider for the hale connect login section of the Welcome page
 * 
 * @author Florian Esser
 */
public class HaleConnectLoginContentProvider implements IIntroContentProvider, IIntroAction {

	private static final ALogger log = ALoggerFactory
			.getLogger(HaleConnectLoginContentProvider.class);

	private boolean disposed = false;

	/**
	 * @see org.eclipse.ui.intro.config.IIntroContentProvider#init(org.eclipse.ui.intro.config.IIntroContentProviderSite)
	 */
	@Override
	public void init(IIntroContentProviderSite site) {
		// Nothing to do
	}

	/**
	 * @see org.eclipse.ui.intro.config.IIntroContentProvider#createContent(java.lang.String,
	 *      java.io.PrintWriter)
	 */
	@Override
	public void createContent(String id, PrintWriter out) {
		if (disposed) {
			return;
		}

		String username = "";
		String password = "";
		try {
			username = HaleConnectUIPlugin.getStoredUsername();
			password = HaleConnectUIPlugin.getStoredPassword();
		} catch (StorageException e) {
			log.error("Failed to retrieve hale connect credentials from preferences store",
					e.getMessage());
		}

		HaleConnectService hcs = HaleUI.getServiceProvider().getService(HaleConnectService.class);
		VelocityEngine velocityEngine = new VelocityEngine();
		VelocityContext velocityContext = new VelocityContext();

		velocityContext.put("id", id);
		velocityContext.put("username", username);
		velocityContext.put("password", password);
		velocityContext.put("loggedIn", hcs.isLoggedIn());
		velocityContext.put("pluginId", "eu.esdihumboldt.hale.io.haleconnect.ui");
		velocityContext.put("actionClass", this.getClass().getName());

		InputStreamReader templateReader;
		try {
			templateReader = new InputStreamReader(HaleConnectLoginContentProvider.class
					.getResourceAsStream("templates/welcome-login.vm"), "UTF-8");
			velocityEngine.init();
			velocityEngine.evaluate(velocityContext, out, "hale-connect-login", templateReader);
		} catch (Exception e) {
			log.error("Error rendering template for hale connect login section.", e);
			out.print("Error rendering template for hale connect login section.");
		}
	}

	/**
	 * @see org.eclipse.ui.intro.config.IIntroContentProvider#createContent(java.lang.String,
	 *      org.eclipse.swt.widgets.Composite,
	 *      org.eclipse.ui.forms.widgets.FormToolkit)
	 */
	@Override
	public void createContent(String id, Composite parent, FormToolkit toolkit) {
		// TODO SWT welcome page not supported yet
	}

	/**
	 * 
	 * @see org.eclipse.ui.intro.config.IIntroAction#run(org.eclipse.ui.intro.IIntroSite,
	 *      java.util.Properties)
	 */
	@Override
	public void run(IIntroSite site, Properties params) {
		String username = params.getProperty("username", "");
		String password = params.getProperty("password", "");
		String savecreds = params.getProperty("savecreds", "");
		if (username.trim().isEmpty() || password.trim().isEmpty()) {
			// Using MessageDialog to prevent this from cluttering up the logs
			MessageDialog.openWarning(site.getShell(), "Login to hale connect",
					"You must provide both user name and password to login to hale connect.");
			return;
		}

		HaleConnectService hcs = HaleUI.getServiceProvider().getService(HaleConnectService.class);
		try {
			if (hcs.login(username, password)) {
				if ("true".equalsIgnoreCase(savecreds)) {
					try {
						HaleConnectUIPlugin.storeUsername(username);
						HaleConnectUIPlugin.storePassword(password);
					} catch (StorageException e) {
						log.error("hale connect credentials could not be saved.", e.getMessage());
					}
				}

				log.userInfo("Login to hale connect successful.");

				// Close and reopen the intro to update the displayed message
				IIntroPart introPart = PlatformUI.getWorkbench().getIntroManager().getIntro();
				if (introPart != null) {
					PlatformUI.getWorkbench().getIntroManager().closeIntro(introPart);
					PlatformUI.getWorkbench().getIntroManager().showIntro(site.getWorkbenchWindow(),
							false);
				}
			}
			else {
				log.userWarn("Login to hale connect failed, please check the credentials.");
			}
		} catch (HaleConnectException e) {
			log.userError("An error occurred while trying to login to hale connect", e);
		}
	}

	/**
	 * @see org.eclipse.ui.intro.config.IIntroContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		disposed = true;
	}

}
