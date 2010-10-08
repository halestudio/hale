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

package eu.esdihumboldt.hale.rcp.wizards.io;

import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.goml.align.Alignment;

/**
 * This {@link WizardPage} contains the controls for saving an {@link Alignment} 
 * Project.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class SaveAlignmentProjectWizardMainPage extends WizardPage {
	
	private String result = null;
	
	private FileFieldEditor ffe;
	
	private Text projectName;
	
	private String initialName = Messages.SaveAlignmentProjectWizardMainPage_ProjectNameText;
	
//	private Text projectAuthor;

	/**
	 * @param string
	 * @param string2
	 */
	public SaveAlignmentProjectWizardMainPage(String pageName, String pageTitle) {
		super(pageName, pageTitle, (ImageDescriptor) null);
		setTitle(pageName); //NON-NLS-1
		setDescription(Messages.SaveAlignmentProjectWizardMainPage_Description); //NON-NLS-1
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		super.initializeDialogUnits(parent);
        this.setPageComplete(this.isPageComplete());
        
        // layout dialog
        GridLayout dialogLayout = new GridLayout();
        dialogLayout.numColumns = 1;
        dialogLayout.makeColumnsEqualWidth = false;
        dialogLayout.marginWidth = 0;
		dialogLayout.marginHeight = 0;
        parent.setLayout(dialogLayout);
        
		// define source group composite
		Group selectionArea = new Group(parent, SWT.NONE);
		selectionArea.setText(Messages.SaveAlignmentProjectWizardMainPage_LocationText1 +
				Messages.SaveAlignmentProjectWizardMainPage_LocationText2);
		selectionArea.setLayout(new GridLayout());
		GridData selectionAreaGD = new GridData(GridData.VERTICAL_ALIGN_FILL
                | GridData.HORIZONTAL_ALIGN_FILL);
		selectionAreaGD.grabExcessHorizontalSpace = true;
		selectionArea.setLayoutData(selectionAreaGD);
		selectionArea.setSize(selectionArea.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		selectionArea.setFont(parent.getFont());
		
		// write to HALE Project + OML file
		final Composite fileSelectionArea = new Composite(selectionArea, SWT.NONE);
		GridData fileSelectionData = new GridData(
				GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
		fileSelectionData.grabExcessHorizontalSpace = true;
		fileSelectionArea.setLayoutData(fileSelectionData);

		fileSelectionArea.setLayout(new GridLayout());
		Composite ffe_container = new Composite(fileSelectionArea, SWT.NULL);
		ffe_container.setLayoutData(
				new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		this.ffe = new FileFieldEditor(Messages.SaveAlignmentProjectWizardMainPage_FileSelectTitle, 
				Messages.SaveAlignmentProjectWizardMainPage_File, ffe_container); //NON-NLS-1 //NON-NLS-2
		this.ffe.getTextControl(ffe_container).addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				getWizard().getContainer().updateButtons();
			}
		});
		String[] extensions = new String[] { "*.xml" }; //NON-NLS-1 //$NON-NLS-1$
		this.ffe.setFileExtensions(extensions);
		
		Group optionsGroup = new Group(parent, SWT.NONE);
		optionsGroup.setText(Messages.SaveAlignmentProjectWizardMainPage_ProjectSavingText);
		optionsGroup.setLayout(new GridLayout(2, false));
		GridData optionsGroupGD = new GridData(GridData.VERTICAL_ALIGN_FILL
                | GridData.HORIZONTAL_ALIGN_FILL);
		optionsGroupGD.grabExcessHorizontalSpace = true;
		optionsGroup.setLayoutData(optionsGroupGD);
		optionsGroup.setSize(optionsGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		optionsGroup.setFont(parent.getFont());
		
		// enter a name for the project TODO: add other metadata
		Label pnLabel = new Label(optionsGroup, SWT.NONE);
		pnLabel.setText("Project name:");
		pnLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		
		this.projectName = new Text(optionsGroup, SWT.BORDER | SWT.SINGLE);
		this.projectName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		this.projectName.setText(initialName);
		this.projectName.setEditable(true);
//		this.projectAuthor = new Text(optionsGroup, SWT.BORDER | SWT.SINGLE);
//		this.projectAuthor.setLayoutData(new GridData(
//				GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL));
//		this.projectAuthor.setText(Messages.SaveAlignmentProjectWizardMainPage_ProjectAuthorText);
//		this.projectAuthor.setEditable(true);
		
		setErrorMessage(null);	// should not initially have error message
		super.setControl(selectionArea);
	}
	
	/**
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		if (this.ffe == null) {
			return false;
		}
		this.result = this.ffe.getStringValue();
		if (result != null && !result.isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 * @return the URI representing the selected output path.
	 */
	public String getResult() {
		return this.result;
	}
	
	/**
	 * @return the Name given for the project, or null if none was entered.
	 */
	public String getProjectName() {
		if (!this.projectName.getText().equals("Project Name")) { //$NON-NLS-1$
			return this.projectName.getText();
		}
		else {
			return null;
		}
	}
	
	/**
	 * Set the initial project name
	 * 
	 * @param initilName the initial project name
	 */
	public void setProjectName(String initilName) {
		this.initialName = initilName;
	}
	
	/**
	 * @return the Name given for the Author of the project, or <code>null</code> 
	 * if none was entered.
	 */
//	public String getProjectAuthor() {
//		if (!this.projectAuthor.getText().equals("Project Author Name")) { //$NON-NLS-1$
//			return this.projectAuthor.getText();
//		}
//		else {
//			return null;
//		}
//	}

}
