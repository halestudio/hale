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

package eu.esdihumboldt.hale.io.haleconnect.ui.projects;

import java.net.URI;
import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.PlatformUI;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.io.haleconnect.project.HaleConnectProjectWriter;
import eu.esdihumboldt.hale.ui.io.ExportWizard;
import eu.esdihumboldt.hale.ui.io.IOWizard;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;

/**
 * Wizard for sharing projects on hale connect
 * 
 * @author Florian Esser
 */
public class ShareProjectWizard extends ExportWizard<HaleConnectProjectWriter> {

	private static final ALogger log = ALoggerFactory.getLogger(ShareProjectWizard.class);

//	private final HaleConnectService haleConnect;

	/**
	 * Create the wizard
	 */
	public ShareProjectWizard() {
		super(HaleConnectProjectWriter.class);
//		this.haleConnect = HaleUI.getServiceProvider().getService(HaleConnectService.class);
	}

	@Override
	public void addPages() {
		super.addPages();
		addPage(new ShareProjectDetailsPage());

		for (AbstractConfigurationPage<? extends HaleConnectProjectWriter, ? extends IOWizard<HaleConnectProjectWriter>> confPage : getConfigurationPages()) {
			confPage.setPageComplete(false);
		}
	}

	@Override
	public boolean performFinish() {
		boolean result = super.performFinish();

		if (result) {
			try {
				URI clientAccessUrl = this.getProvider().getClientAccessUrl();
				MessageDialog successDialog = new MessageDialog(getShell(),
						"Project upload successful", null,
						"Project was successfully uploaded to hale connect.",
						MessageDialog.INFORMATION, new String[] { IDialogConstants.OK_LABEL }, 0) {

					@Override
					protected Control createCustomArea(Composite parent) {
						Link link = new Link(parent, SWT.WRAP);
						link.setText(MessageFormat.format(
								"To access this project online, please visit the following URL (login may be required):\n<a href=\"{0}\">{0}</a>.",
								clientAccessUrl));
						link.addSelectionListener(new SelectionAdapter() {

							@Override
							public void widgetSelected(SelectionEvent e) {
								try {
									// Open default external browser
									PlatformUI.getWorkbench().getBrowserSupport()
											.getExternalBrowser().openURL(new URL(e.text));
								} catch (Exception ex) {
									log.error(MessageFormat.format("Error opening browser: {0}",
											ex.getMessage()), e);
								}
							}
						});
						return link;
					}

				};
				successDialog.open();

				log.info(MessageFormat.format(
						"Project was successfully uploaded to hale connect and is available online at \"{0}\"",
						clientAccessUrl));
			} catch (IllegalArgumentException e) {
				// bad base path?
				log.error(MessageFormat.format("Error creating client access URL: {0}",
						e.getMessage()), e);
				log.userInfo("Project was successfully uploaded to hale connect.");
			}
		}

		return result;
	}

}
