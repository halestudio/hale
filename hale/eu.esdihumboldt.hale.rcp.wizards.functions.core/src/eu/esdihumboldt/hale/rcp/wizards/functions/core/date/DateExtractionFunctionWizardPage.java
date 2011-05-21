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

package eu.esdihumboldt.hale.rcp.wizards.functions.core.date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.hale.rcp.wizards.functions.core.Messages;
import eu.esdihumboldt.hale.ui.model.functions.AbstractSingleCellWizardPage;

/**
 * FIXME Add Type description.
 * 
 * @author Ulrich Schaeffler
 * @partner 14 / TUM
 */
public class DateExtractionFunctionWizardPage extends AbstractSingleCellWizardPage {

	Text sourceFormat = null;
	
	Text targetFormat = null;
	
	/**
	 * @see AbstractSingleCellWizardPage#AbstractSingleCellWizardPage(String)
	 */
	public DateExtractionFunctionWizardPage(String pageName) {
		super(pageName);
		setTitle(pageName);
		setDescription(Messages.DateExtractionFunctionWizardPage_0);
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

		// source format
		Label ccLabel = new Label(page, SWT.NONE);
		ccLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		ccLabel.setText(Messages.DateExtractionFunctionWizardPage_1);
		
		this.sourceFormat = new Text(page, SWT.BORDER);
		this.sourceFormat.setText("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$
		this.sourceFormat.setEnabled(true);
		this.sourceFormat.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// target format
		Label providerLabel = new Label(page, SWT.NONE);
		providerLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		providerLabel.setText(Messages.DateExtractionFunctionWizardPage_3);
		
		this.targetFormat = new Text(page, SWT.BORDER);
		this.targetFormat.setText("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$
		this.targetFormat.setEnabled(true);
		this.targetFormat.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

	}
	
	/**
	 * @return the entered source format
	 */
	public String getSourceFormat() {
		if (this.sourceFormat != null) {
			return this.sourceFormat.getText();
		}
		return null;
	}
	
	/**
	 * @return the entered target format
	 */
	public String getTargetFormat() {
		if (this.targetFormat != null) {
			return this.targetFormat.getText();
		}
		return null;
	}

}
