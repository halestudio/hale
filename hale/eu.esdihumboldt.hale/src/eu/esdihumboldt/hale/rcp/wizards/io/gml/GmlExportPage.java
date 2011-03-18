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

package eu.esdihumboldt.hale.rcp.wizards.io.gml;

import java.io.File;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.Messages;
import eu.esdihumboldt.hale.rcp.wizards.io.SaveFileFieldEditor;

/**
 * Options for the GML export
 * 
 * @author Simon Templer
 * @version $Id$
 */
public class GmlExportPage 
	extends WizardPage {
	
//	private static Logger _log = Logger.getLogger(GmlExportPage.class);
	
	private SaveFileFieldEditor file;
	
	private Button validate;
	
	/**
	 * Constructor
	 * 
	 * @param pageName the page name
	 * @param pageTitle the page title
	 */
	protected GmlExportPage(String pageName, String pageTitle) {
		super(pageName, pageTitle, (ImageDescriptor) null);
		super.setTitle(pageName);
		super.setDescription(Messages.getString("GmlExportPage.0")); //$NON-NLS-1$
	}
	
	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		super.initializeDialogUnits(parent);
        
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout(3, false));
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        file = new SaveFileFieldEditor("file", Messages.getString("GmlExportPage.2"), composite); //$NON-NLS-1$ //$NON-NLS-2$
        file.getTextControl(composite).addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				getWizard().getContainer().updateButtons();
			}
		});
        file.setEmptyStringAllowed(false);
        file.setFileExtensions(new String[]{"*.gml", "*.xml"}); //$NON-NLS-1$ //$NON-NLS-2$
        
        Composite sep = new Composite(composite, SWT.NONE);
		sep.setLayoutData(GridDataFactory.swtDefaults().hint(0, 0).create());
        
        validate = new Button(composite, SWT.CHECK);
        validate.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1));
        validate.setText(Messages.getString("GmlExportPage.5")); //$NON-NLS-1$
        validate.setSelection(true);
        
        setPageComplete(false);
		setControl(composite);
	}

	/**
	 * @see WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		if (file != null && file.getStringValue() != null && 
				!file.getStringValue().isEmpty()) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Get the target file
	 * 
	 * @return the target file
	 */
	public File getTargetFile() {
		return new File(file.getStringValue());
	}
	
	/**
	 * Get if the GML file shall be validated
	 * 
	 * @return if the GML file shall be validated
	 */
	public boolean getValidate() {
		return validate.getSelection();
	}
	
}
