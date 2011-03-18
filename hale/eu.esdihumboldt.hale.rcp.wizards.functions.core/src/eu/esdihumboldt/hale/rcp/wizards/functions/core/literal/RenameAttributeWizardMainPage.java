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

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.rcp.utils.definition.DefinitionLabelFactory;
import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleCellWizardPage;
import eu.esdihumboldt.hale.rcp.wizards.functions.core.Messages;

/**
 * Main page of the {@link RenameAttributeWizard}
 * 
 * @author Thorsten Reitz, Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 * @since 2.0.0.M2
 */
public class RenameAttributeWizardMainPage extends AbstractSingleCellWizardPage {
	
	/**
	 * @see AbstractSingleCellWizardPage#AbstractSingleCellWizardPage(String)
	 */
	public RenameAttributeWizardMainPage(String pageName) {
		super(pageName);
	}

	/**
	 * Constructor
	 * 
	 * @param pageName the page name and title 
	 * @param pageDescription the page description
	 */
	public RenameAttributeWizardMainPage(String pageName, String pageDescription) {
		this(pageName);
		setTitle(pageName);
		setDescription(pageDescription);
	}

	/**
	 * @see IDialogPage#createControl(Composite)
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
		sourceLabel.setText(Messages.RenameAttributeWizardMainPage_0);
		
		DefinitionLabelFactory labelFactory = (DefinitionLabelFactory) PlatformUI.getWorkbench().getService(DefinitionLabelFactory.class);
		
		Control sourceName = labelFactory.createLabel(page, getParent().getSourceItem().getDefinition(), false);
		//Text sourceName = new Text(page, SWT.BORDER);
		//sourceName.setText(getParent().getSourceItem().getName().getLocalPart());
		//sourceName.setEnabled(false);
		sourceName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// target area
		Label targetLabel = new Label(page, SWT.NONE);
		targetLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		targetLabel.setText(Messages.RenameAttributeWizardMainPage_1);
		
		Control targetName = labelFactory.createLabel(page, getParent().getTargetItem().getDefinition(), false);
		//Text targetName = new Text(page, SWT.BORDER);
		//targetName.setText(getParent().getTargetItem().getName().getLocalPart());
		//targetName.setEnabled(false);
		targetName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	}

}
