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
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.hale.Messages;

/**
 * FIXME Add Type description.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class OpenAlignmentProjectWizardMainPage 
	extends WizardPage {
	
	protected FileFieldEditor ffe = null;

	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	protected OpenAlignmentProjectWizardMainPage(String pageName, String pageTitle) {
		super(pageName, pageTitle, (ImageDescriptor) null); // FIXME ImageDescriptor
		super.setTitle(pageName); //NON-NLS-1
		super.setDescription(Messages.OpenAlignmentProjectWizardMainPage_SuperWindowDescription);
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		super.initializeDialogUnits(parent);
        this.setPageComplete(this.isPageComplete());
        
		// define open group composite
		Group selectionArea = new Group(parent, SWT.NONE);
		selectionArea.setText(Messages.OpenAlignmentProjectWizardMainPage_SelectProjectText);
		selectionArea.setLayout(new GridLayout());
		GridData selectionAreaGD = new GridData(GridData.VERTICAL_ALIGN_FILL
                | GridData.HORIZONTAL_ALIGN_FILL);
		selectionAreaGD.grabExcessHorizontalSpace = true;
		selectionArea.setLayoutData(selectionAreaGD);
		selectionArea.setSize(selectionArea.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		selectionArea.setFont(parent.getFont());
		
		final Composite fileSelectionArea = new Composite(selectionArea, SWT.NONE);
		GridData fileSelectionData = new GridData(
				GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
		fileSelectionData.grabExcessHorizontalSpace = true;
		fileSelectionArea.setLayoutData(fileSelectionData);

		GridLayout fileSelectionLayout = new GridLayout();
		fileSelectionLayout.numColumns = 1;
		fileSelectionLayout.makeColumnsEqualWidth = false;
		fileSelectionLayout.marginWidth = 0;
		fileSelectionLayout.marginHeight = 0;
		fileSelectionArea.setLayout(fileSelectionLayout);
		Composite ffe_container = new Composite(fileSelectionArea, SWT.NULL);
		ffe_container.setLayoutData(
				new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		this.ffe = new FileFieldEditor("fileSelect",  //$NON-NLS-1$
				Messages.OpenAlignmentProjectWizardMainPage_File, ffe_container); //NON-NLS-1 //NON-NLS-2
		this.ffe.getTextControl(ffe_container).addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				getWizard().getContainer().updateButtons();
			}
		});
		String[] extensions = new String[] { "*.xml" }; //NON-NLS-1 //$NON-NLS-1$
		this.ffe.setFileExtensions(extensions);
		
		setErrorMessage(null);	// should not initially have error message
		super.setControl(selectionArea);
	}

	/**
	 * @return the path of the selected project file as a String.
	 */
	public String getResult() {
		return this.ffe.getStringValue();
	}

}
