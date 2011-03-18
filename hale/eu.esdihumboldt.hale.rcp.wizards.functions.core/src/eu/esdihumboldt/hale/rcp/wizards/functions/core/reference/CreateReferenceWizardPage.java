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
package eu.esdihumboldt.hale.rcp.wizards.functions.core.reference;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;
import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleCellWizardPage;
import eu.esdihumboldt.hale.rcp.wizards.functions.core.Messages;

/**
 * TODO: clarify whether both normal xlinks and gml:ReferenceTypes are needed 
 * (or whether ReferenceType is just a semantic element saying "by reference only")
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class CreateReferenceWizardPage 
	extends AbstractSingleCellWizardPage {
	
	private Text prefix = null;
	
	private Text postfix = null;

	private SchemaItem sourceItem = null;

	public CreateReferenceWizardPage(String pageName) {
		super(pageName);
		setTitle(pageName);
		setDescription(Messages.CreateReferenceWizardPage_0);
	}

	@Override
	public void createControl(Composite parent) {
		super.initializeDialogUnits(parent);
		
		this.sourceItem = getParent().getSourceItem();
		
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

		// Prefix to reference ID
		Label ccLabel = new Label(page, SWT.NONE);
		ccLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		ccLabel.setText(Messages.CreateReferenceWizardPage_1);
		
		this.prefix = new Text(page, SWT.BORDER);
		this.prefix.setText(""); //$NON-NLS-1$
		this.prefix.setEnabled(true);
		this.prefix.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		// Type name
		Label typenameLabel = new Label(page, SWT.NONE);
		typenameLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		typenameLabel.setText(Messages.CreateReferenceWizardPage_3);
		
		Text typeName = new Text(page, SWT.BORDER);
		typeName.setText(
				getParent().getSourceItem().getName().getLocalPart());
		typeName.setEnabled(false);
		typeName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// Postfix to reference ID
		Label providerLabel = new Label(page, SWT.NONE);
		providerLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		providerLabel.setText(Messages.CreateReferenceWizardPage_4);
		
		this.postfix = new Text(page, SWT.BORDER);
		this.postfix.setText(""); //$NON-NLS-1$
		this.postfix.setEnabled(true);
		this.postfix.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

	}
	
	public String getPrefix() {
		return this.prefix.getText();
	}
	
	public String getPostfix() {
		return this.postfix.getText();
	}
	
	public String getEntityName() {
		return this.sourceItem.getName().getLocalPart();
	}

}
