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
package eu.esdihumboldt.hale.ui.io.legacy;

import java.util.Map;

import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import eu.esdihumboldt.hale.ui.internal.Messages;
import eu.esdihumboldt.hale.ui.io.legacy.mappingexport.MappingExportExtension;

/**
 * This is the main page of the {@link MappingExportWizard}.
 * 
 * TODO: Add an overview over the mapping that has been created. Use component 
 * for source view of mapping.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class MappingExportWizardMainPage 
	extends WizardPage {
	
	private String result = null;
	
	private FileFieldEditor ffe;
	
	private String selectedFormat = ""; //$NON-NLS-1$

	/**
	 * @param pageName
	 * @param pageTitle
	 */
	public MappingExportWizardMainPage(String pageName, String pageTitle) {
		super(pageName, pageTitle, (ImageDescriptor) null);
		setTitle(pageName); //NON-NLS-1
		setDescription(Messages.MappingExportWizardMainPage_MappingExportDescription); //NON-NLS-1
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		super.initializeDialogUnits(parent);
        this.setPageComplete(this.isPageComplete());
        
		// define source group composite
		Group selectionArea = new Group(parent, SWT.NONE);
		selectionArea.setText(Messages.MappingExportWizardMainPage_SelectionAreaText);
		selectionArea.setLayout(new GridLayout());
		GridData selectionAreaGD = new GridData(GridData.VERTICAL_ALIGN_FILL
                | GridData.HORIZONTAL_ALIGN_FILL);
		selectionAreaGD.grabExcessHorizontalSpace = true;
		selectionArea.setLayoutData(selectionAreaGD);
		selectionArea.setSize(selectionArea.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		selectionArea.setFont(parent.getFont());
		
		// select format to export to
		final Composite formatSelectionArea = new Composite(selectionArea, SWT.NONE);
		formatSelectionArea.setLayout(new GridLayout(2, false));
		GridData formatSelectionData = new GridData(
				GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
		formatSelectionData.grabExcessHorizontalSpace = true;
		formatSelectionArea.setLayoutData(formatSelectionData);
		final Map<String, String> formats = MappingExportExtension.getRegisteredExportProviderInfo();
		final String[] items = new String[formats.keySet().size()];
		int i = 0;
		for (String name : formats.keySet()) {
			items[i++] = name + " (" + formats.get(name) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		final Label protocolVersionLabel = new Label(formatSelectionArea, SWT.NONE);
		protocolVersionLabel.setText(Messages.MappingExportWizardMainPage_0); //$NON-NLS-1$
		protocolVersionLabel.setToolTipText(Messages.MappingExportWizardMainPage_1); //$NON-NLS-1$
		final Combo formatCombo = new Combo (formatSelectionArea, SWT.READ_ONLY);
		formatCombo.setItems(items);
		formatCombo.setLayoutData(new GridData(
				GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL));
		formatCombo.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedFormat = items[formatCombo.getSelectionIndex()];
				
				String ext = formats.get(getSelectedFormatName());
				ffe.setFileExtensions(new String[]{ext});
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore
			}
		});
		
		// select path to export to
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
		this.ffe = new SaveFileFieldEditor("fileSelect",  //$NON-NLS-1$
				Messages.MappingExportWizardMainPage_File, ffe_container);
		this.ffe.getTextControl(ffe_container).addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				getWizard().getContainer().updateButtons();
			}
		});
		
		setErrorMessage(null);	// should not initially have error message
		super.setControl(selectionArea);
	}
	
	/**
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		if (this.selectedFormat.equals("")) { //$NON-NLS-1$
			return false;
		}
		if (this.ffe == null) {
			return false;
		}
		if (this.ffe.getStringValue() != null && !this.ffe.getStringValue().isEmpty()) {
			this.result = this.ffe.getStringValue();
			if (result != null) {
				return true;
			}
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
	 * @return the name of the selected Format.
	 */
	public String getSelectedFormatName() {
		return this.selectedFormat.substring(0, this.selectedFormat.indexOf(" (")); //$NON-NLS-1$
	}

}
