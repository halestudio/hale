/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.rcp.wizards.functions.core.literal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleCellWizardPage;

/**
 * TODO Add Type comment
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 * @since 2.0.0.M2
 */
public class RenameAttributeWizardMainPage extends AbstractSingleCellWizardPage {
	
	private String nestedAttributePath = "";
	
	private Text nestedAttributeName;

	/**
	 * @param pageName
	 */
	public RenameAttributeWizardMainPage(String pageName) {
		super(pageName);
	}

	/**
	 * @param pageName
	 * @param pageDescription
	 */
	public RenameAttributeWizardMainPage(String pageName, String pageDescription) {
		this(pageName);
		setTitle(pageName);
		setDescription(pageDescription);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		super.initializeDialogUnits(parent);
		setControl(parent);
		// create a composite to hold the widgets
		Composite page = new Composite(parent, SWT.NULL);
		// create layout for this wizard page
		GridLayout gl = new GridLayout();
		gl.numColumns = 2;
		gl.marginLeft = 0;
		gl.marginTop = 20;
		gl.marginRight = 70;
		page.setLayout(gl);

		// source area
		Label sourceLabel = new Label(page, SWT.NONE);
		sourceLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		sourceLabel.setText("Source attribute: ");
		
		Text sourceName = new Text(page, SWT.BORDER);
		sourceName.setText(getParent().getSourceItem().getName().getLocalPart());
		sourceName.setEnabled(false);
		sourceName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// target area
		Label targetLabel = new Label(page, SWT.NONE);
		targetLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		targetLabel.setText("Target attribute: ");
		
		Text targetName = new Text(page, SWT.BORDER);
		targetName.setText(getParent().getTargetItem().getName().getLocalPart());
		targetName.setEnabled(false);
		targetName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		// Nested Attribute path
		Label nestedAttributeLabel = new Label(page, SWT.NONE);
		nestedAttributeLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		nestedAttributeLabel.setText("Nested Attribute Path: ");
		
		this.nestedAttributeName = new Text(page, SWT.BORDER);
		this.nestedAttributeName.setText(this.nestedAttributePath);
		this.nestedAttributeName.setEnabled(true);
		this.nestedAttributeName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

	}

	/**
	 * Set the path of a nested attribute. Is an optional parameter with the 
	 * following format: propertyNameA::propertynameB::...
	 * @param condition
	 */
	public void setNestedAttributePath(String condition) {
		this.nestedAttributePath = condition;
		if (this.nestedAttributeName != null) {
			this.nestedAttributeName.setText(condition);
		}
	}

	/**
	 * @return the nestedAttributePath.
	 */
	public String getNestedAttributePath() {
		return this.nestedAttributePath;
	}

}
