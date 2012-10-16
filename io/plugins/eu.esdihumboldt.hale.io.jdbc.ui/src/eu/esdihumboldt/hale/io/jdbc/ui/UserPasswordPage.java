/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.jdbc.ui;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.io.jdbc.JDBCConstants;
import eu.esdihumboldt.hale.ui.io.IOWizard;
import eu.esdihumboldt.hale.ui.io.IOWizardPage;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;

/**
 * Configuration page for specifying database connection user name and password.
 * @author Simon Templer
 */
public class UserPasswordPage extends AbstractConfigurationPage<IOProvider, IOWizard<IOProvider>>
	implements JDBCConstants {
	
	private Text user;
	private Text password;

	/**
	 * Default constructor
	 */
	public UserPasswordPage() {
		super("userPassword", "Authentication", null);
		
		setDescription("Please enter user name and password for the database connection");
	}

	/**
	 * @see AbstractConfigurationPage#enable()
	 */
	@Override
	public void enable() {
		// do nothing
	}

	/**
	 * @see AbstractConfigurationPage#disable()
	 */
	@Override
	public void disable() {
		// do nothing
	}

	/**
	 * @see IOWizardPage#updateConfiguration(IOProvider)
	 */
	@Override
	public boolean updateConfiguration(IOProvider provider) {
		provider.setParameter(PARAM_USER, (user.getText().isEmpty())?(null):(user.getText()));
		provider.setParameter(PARAM_PASSWORD, password.getText());
		
		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		page.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());
		
		// user
		Label labelUser = new Label(page, SWT.NONE);
		labelUser.setText("User:");
		labelUser.setLayoutData(GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).create());
		
		user = new Text(page, SWT.BORDER | SWT.SINGLE);
		user.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
		
		// user
		Label labelPassword = new Label(page, SWT.NONE);
		labelPassword.setText("Password:");
		labelPassword.setLayoutData(GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).create());
		
		password = new Text(page, SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
		password.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
		
		setPageComplete(false);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {
		if (firstShow) {
			setPageComplete(true);
		}
	}

}
