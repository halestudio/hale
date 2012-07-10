/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.io.jdbc.ui;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.schema.io.SchemaReader;
import eu.esdihumboldt.hale.io.jdbc.JDBCSchemaReader;
import eu.esdihumboldt.hale.ui.io.IOWizardPage;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;
import eu.esdihumboldt.hale.ui.io.schema.SchemaReaderConfigurationPage;

/**
 * Configuration page for specifying database connection user name and password.
 * @author Simon Templer
 */
public class UserPasswordPage extends SchemaReaderConfigurationPage {
	
	//TODO common for schema/instance

	private Text user;
	private Text password;

	/**
	 * Default constructor
	 */
	public UserPasswordPage() {
		super("userPassword", "Authentication", null);
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
	public boolean updateConfiguration(SchemaReader provider) {
		provider.setParameter(JDBCSchemaReader.PARAM_USER, (user.getText().isEmpty())?(null):(user.getText()));
		provider.setParameter(JDBCSchemaReader.PARAM_PASSWORD, password.getText());
		
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
		
		password = new Text(page, SWT.BORDER | SWT.SINGLE);
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
