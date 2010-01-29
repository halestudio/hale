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

import java.net.URI;
import java.net.URL;

import org.apache.log4j.Logger;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * This is the first page of the {@link InstanceDataImportWizard}. It allows to
 * select the principal source of the geodata to be used in HALE.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class InstanceDataImportWizardMainPage 
	extends WizardPage {
	
	private static Logger _log = Logger.getLogger(InstanceDataImportWizardMainPage.class);
	
	private boolean useFile = true;
	
	private String result;
	
	private FileFieldEditor fileFieldEditor;
	
	private UrlFieldEditor wfsFieldEditor;
	
	// constructors ............................................................

	protected InstanceDataImportWizardMainPage(String pageName, String pageTitle) {
		super(pageName, pageTitle, (ImageDescriptor) null); // FIXME ImageDescriptor
		super.setTitle(pageName); //NON-NLS-1
		super.setDescription("Load geodata from a file or service to test your " +
				"defined mappings and transformations.");
	}
	
	// methods .................................................................

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		super.initializeDialogUnits(parent);
        
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
                | GridData.HORIZONTAL_ALIGN_FILL));
        composite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        composite.setFont(parent.getFont());

        this.createSourceGroup(composite);
        super.setPageComplete(false);
        super.setErrorMessage(null);
		super.setControl(composite);
	}
	
	/**
	 * Creates the UI controls for the selection of the source of the schema
	 * to be imported.
	 * 
	 * @param parent the parent {@link Composite}
	 */
	private void createSourceGroup(Composite parent) {
		
		// define source group composite
		Group selectionArea = new Group(parent, SWT.NONE);
		selectionArea.setText("Read Geodata from ...");
		selectionArea.setLayout(new GridLayout());
		GridData selectionAreaGD = new GridData(GridData.VERTICAL_ALIGN_FILL
                | GridData.HORIZONTAL_ALIGN_FILL);
		selectionAreaGD.grabExcessHorizontalSpace = true;
		selectionArea.setLayoutData(selectionAreaGD);
		selectionArea.setSize(selectionArea.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		selectionArea.setFont(parent.getFont());
		
		// read from file (XSD, GML, XML)
		final Composite fileSelectionArea = new Composite(selectionArea, SWT.NONE);
		GridData fileSelectionData = new GridData(
				GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
		fileSelectionData.grabExcessHorizontalSpace = true;
		fileSelectionArea.setLayoutData(fileSelectionData);

		GridLayout fileSelectionLayout = new GridLayout();
		fileSelectionLayout.numColumns = 2;
		fileSelectionLayout.makeColumnsEqualWidth = false;
		fileSelectionLayout.marginWidth = 0;
		fileSelectionLayout.marginHeight = 0;
		fileSelectionArea.setLayout(fileSelectionLayout);
		Button useFileRadio = new Button(fileSelectionArea, SWT.RADIO);
		useFileRadio.setSelection(true);
		final Composite ffe_container = new Composite(fileSelectionArea, SWT.NULL);
		ffe_container.setLayoutData(
				new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		this.fileFieldEditor = new FileFieldEditor("fileSelect", 
				"... file:", ffe_container); //NON-NLS-1 //NON-NLS-2
		this.fileFieldEditor.getTextControl(ffe_container).addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				getWizard().getContainer().updateButtons();
			}
		});
		String[] extensions = new String[] { "*.gml", "*.xml" }; //NON-NLS-1
		this.fileFieldEditor.setFileExtensions(extensions);
		
		// read from WFS (GetFeature)
		Button useWfsRadio = new Button(fileSelectionArea, SWT.RADIO);
		final Composite ufe_container = new Composite(fileSelectionArea, SWT.NULL);
		ufe_container.setLayoutData(
				new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		this.wfsFieldEditor = new UrlFieldEditor("urlSelect","... WFS GetFeature:", ufe_container,true);
		this.wfsFieldEditor.setEnabled(false, ufe_container);
		this.wfsFieldEditor.getTextControl(ufe_container).addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				getWizard().getContainer().updateButtons();
			}
		});
		
		// add listeners to radio buttons
		useFileRadio.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				useFile = ((Button)e.widget).getSelection();
				if (((Button)e.widget).getSelection()) {
					fileFieldEditor.setEnabled(true, ffe_container);
					wfsFieldEditor.setEnabled(false, ufe_container);
				}
				else {
					fileFieldEditor.setEnabled(false, ffe_container);
					wfsFieldEditor.setEnabled(true, ufe_container);
				}
			}
		});
		
		useWfsRadio.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				useFile = ((Button)e.widget).getSelection();
				if (((Button)e.widget).getSelection()) {
					fileFieldEditor.setEnabled(false, ffe_container);
					wfsFieldEditor.setEnabled(true, ufe_container);
				}
				else {
					fileFieldEditor.setEnabled(true, ffe_container);
					wfsFieldEditor.setEnabled(false, ufe_container);
				}
			}
		});
		
		// finish some stuff.
		fileSelectionArea.moveAbove(null);
		
	}
	
	/**
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		if (this.fileFieldEditor != null && this.wfsFieldEditor != null) {
			try {
				if (!this.useFile) {
					// test whether content of the WFS Field Editor validates to URL.
					String test = this.wfsFieldEditor.getStringValue();
					if (test != null && !test.equals("")) {
						new URL(test);
						_log.debug("wfsFieldEditor URL was OK.");
						this.result = test;
					} 
					else {
						return false;
					}
				}
				else {
					// test whether content of the File Field Editor validates to URI.
					String test = this.fileFieldEditor.getStringValue();
					if (test != null && !test.equals("")) {
						test = test.replace(" ", "%20");
						new URI(test.replaceAll("\\\\", "/"));
						_log.debug("fileFieldEditor URI was OK.");
						this.result = test;
					}
					else {
						return false;
					}
				}	
			} catch (Exception ex) {
				ex.printStackTrace();
				return false;
			} 
			_log.debug("Page is complete.");
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * @return a String representing the URL or URI to load the schema from.
	 */
	public String getResult() {
		return this.result;
	}

	public InstanceInterfaceType getInterfaceType() {
		if (this.useFile) {
			return InstanceInterfaceType.FILE;
		}
		else {
			return InstanceInterfaceType.WFS;
		}
	}

}
