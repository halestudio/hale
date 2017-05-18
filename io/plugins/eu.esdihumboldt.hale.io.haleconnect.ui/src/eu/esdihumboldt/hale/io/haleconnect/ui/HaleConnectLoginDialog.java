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

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;

/**
 * hale connect login dialog
 * 
 * @author Florian Esser
 */
public class HaleConnectLoginDialog extends TitleAreaDialog {

	private static final ALogger log = ALoggerFactory.getLogger(HaleConnectLoginDialog.class);

	private Text usernameInput;
	private Text passwordInput;
	private Button saveCredentialsInput;

	private String username;
	private String password;
	private boolean saveCredentials;

	/**
	 * Instantiate a new login dialog.
	 *
	 * @param parentShell the parent SWT shell
	 */
	public HaleConnectLoginDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite contents = new Composite(parent, SWT.NONE);
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		contents.setLayoutData(layoutData);
		contents.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).margins(10, 20).create());

		Label labelUser = new Label(contents, SWT.NONE);
		labelUser.setText("User name:");
		labelUser.setLayoutData(GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.CENTER).create());

		this.usernameInput = new Text(contents, SWT.SINGLE | SWT.BORDER);
		usernameInput.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		usernameInput.setText(username);

		Label labelPassword = new Label(contents, SWT.NONE);
		labelPassword.setText("Password:");
		labelPassword
				.setLayoutData(GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.CENTER).create());

		this.passwordInput = new Text(contents, SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
		passwordInput.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER)
				.grab(true, false).create());
		passwordInput.setText(password);

		this.saveCredentialsInput = new Button(contents, SWT.CHECK);
		saveCredentialsInput.setText("  Save credentials");

		Link link = new Link(contents, SWT.NONE);
		link.setText(
				"Not registered yet? <a href=\"https://www.haleconnect.com\">Create an account.</a>");
		link.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser()
							.openURL(new URL(e.text));
				} catch (PartInitException | MalformedURLException e1) {
					log.userError("Unable to open URL in external browser.", e1);
				}
			}
		});
		link.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));

		return contents;
	}

	/**
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite contents = (Composite) super.createContents(parent);

		this.setTitle("hale connect");
		this.setMessage("Login with your hale connect account.");

		return contents;
	}

	/**
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Login to hale connect");
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		this.username = usernameInput.getText();
		this.password = passwordInput.getText();
		this.saveCredentials = saveCredentialsInput.getSelection();

		super.okPressed();
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#cancelPressed()
	 */
	@Override
	protected void cancelPressed() {
		this.username = null;
		this.password = null;
		this.saveCredentials = false;

		super.cancelPressed();
	}

	/**
	 * @return the user name entered
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * @param username the user name
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password entered
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * @param password the password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return whether the "Save crendentials" box was checked
	 */
	public boolean isSaveCredentials() {
		return this.saveCredentials;
	}

	/**
	 * @param save whether to check the "Save crendentials" box
	 */
	public void setSaveCredentials(boolean save) {
		this.saveCredentials = save;
	}
}
