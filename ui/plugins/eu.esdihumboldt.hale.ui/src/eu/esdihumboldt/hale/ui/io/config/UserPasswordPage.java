/*
 * Copyright (c) 2016 wetransform GmbH
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

package eu.esdihumboldt.hale.ui.io.config;

import java.util.Map;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.config.UserPasswordCredentials;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.ui.io.IOWizard;
import eu.esdihumboldt.hale.ui.io.IOWizardPage;

/**
 * Configuration page user name and password based credentials.
 * 
 * @author Simon Templer
 */
public class UserPasswordPage extends AbstractConfigurationPage<IOProvider, IOWizard<IOProvider>>
		implements UserPasswordCredentials {

	private Text user;
	private Text password;

	/**
	 * Default constructor
	 */
	public UserPasswordPage() {
		super("userPassword", "Authentication", null);

		setDescription(
				"If required, please enter user name and password to use for authentication");
	}

	@Override
	public void enable() {
		// do nothing
	}

	@Override
	public void disable() {
		// do nothing
	}

	/**
	 * @see IOWizardPage#updateConfiguration(IOProvider)
	 */
	@Override
	public boolean updateConfiguration(IOProvider provider) {
		provider.setParameter(PARAM_USER,
				Value.of((user.getText().isEmpty()) ? (null) : (user.getText())));
		provider.setParameter(PARAM_PASSWORD, Value.of(password.getText()));

		return true;
	}

	@Override
	protected void createContent(Composite page) {
		page.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());

		// user
		Label labelUser = new Label(page, SWT.NONE);
		labelUser.setText("User:");
		labelUser.setLayoutData(GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).create());

		user = new Text(page, SWT.BORDER | SWT.SINGLE);
		user.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER)
				.grab(true, false).create());

		// user
		Label labelPassword = new Label(page, SWT.NONE);
		labelPassword.setText("Password:");
		labelPassword
				.setLayoutData(GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).create());

		password = new Text(page, SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
		password.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER)
				.grab(true, false).create());

		// filler
		new Label(page, SWT.NONE);

		// label with warning message
		Composite warnComp = new Composite(page, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(warnComp);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(warnComp);

		Label warnImage = new Label(warnComp, SWT.NONE);
		warnImage.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_OBJS_WARN_TSK));
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.BEGINNING).applyTo(warnImage);

		Label warn = new Label(warnComp, SWT.WRAP);
		warn.setText(
				"User and password may be saved in the project configuration as plain text. Be aware of this when distributing the project.");
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false)
				.hint(300, SWT.DEFAULT).applyTo(warn);

		setPageComplete(false);
	}

	@Override
	protected void onShowPage(boolean firstShow) {
		if (firstShow) {
			// setPageComplete(true);
			setPageComplete(true);
		}

		user.setFocus();
	}

	@Override
	public void loadPreSelection(IOConfiguration conf) {
		Map<String, Value> configs = conf.getProviderConfiguration();
		user.setText(configs.get(PARAM_USER).getStringRepresentation());
		password.setText(configs.get(PARAM_PASSWORD).getStringRepresentation());
	}
}
