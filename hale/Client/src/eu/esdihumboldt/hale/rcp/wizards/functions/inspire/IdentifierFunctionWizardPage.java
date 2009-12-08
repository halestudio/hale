/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.rcp.wizards.functions.inspire;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleCellWizardPage;
import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleComposedCellWizardPage;

/**
 * FIXME Add Type description.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class IdentifierFunctionWizardPage extends
    AbstractSingleCellWizardPage {
	
	Text countryCode = null;
	
	Text providerName = null;
	
	Text productName = null;

	/**
	 * @param pageName
	 */
	public IdentifierFunctionWizardPage(String pageName) {
		super(pageName);
		setTitle(pageName);
		setDescription("Configure the content of your INSPIRE identifer urn.");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		super.initializeDialogUnits(parent);
		// create a composite to hold the widgets
		Composite page = new Composite(parent, SWT.NULL);
		setControl(parent);
		// create layout for this wizard page
		GridLayout gl = new GridLayout();
		gl.numColumns = 2;
		gl.marginLeft = 0;
		gl.marginTop = 20;
		gl.marginRight = 70;
		page.setLayout(gl);

		// Country code
		Label ccLabel = new Label(page, SWT.NONE);
		ccLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		ccLabel.setText("Country Code:");
		
		this.countryCode = new Text(page, SWT.BORDER);
		this.countryCode.setText("de");
		this.countryCode.setEnabled(true);
		this.countryCode.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// Provider name
		Label providerLabel = new Label(page, SWT.NONE);
		providerLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		providerLabel.setText("Provider Name");
		
		this.providerName = new Text(page, SWT.BORDER);
		this.providerName.setText("fraunhofer");
		this.providerName.setEnabled(true);
		this.providerName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		// Product name
		Label productLabel = new Label(page, SWT.NONE);
		productLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		productLabel.setText("Product Name");
		
		this.productName = new Text(page, SWT.BORDER);
		this.productName.setText("humboldt-sample-transformed-data");
		this.productName.setEnabled(true);
		this.productName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		// Type name
		Label typenameLabel = new Label(page, SWT.NONE);
		typenameLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		typenameLabel.setText("Type Name");
		
		Text typeName = new Text(page, SWT.BORDER);
		typeName.setText(
				getParent().getSourceItem().getParent().getName().getLocalPart());
		typeName.setEnabled(true);
		typeName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

	}

	/**
	 * @return the entered Country Code
	 */
	public String getCountryCode() {
		if (this.countryCode != null) {
			return this.countryCode.getText();
		}
		return null;
	}

	/**
	 * @return the entered Provider Name
	 */
	public String getProviderName() {
		if (this.providerName != null) {
			return this.providerName.getText();
		}
		return null;
	}

	/**
	 * @return the entered Product Name
	 */
	public String getProductName() {
		if (this.productName != null) {
			return this.productName.getText();
		}
		return null;
	}

}
