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

import java.io.File;
import java.net.URL;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import eu.esdihumboldt.hale.gmlparser.GmlHelper.ConfigurationType;

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
	
	private URL result;
	
	private FileFieldEditor fileFieldEditor;
	
	private UrlFieldEditor wfsFieldEditor;
	
	private final String schemaNamespace;

	private ComboViewer configSelector;
	
	// constructors ............................................................

	/**
	 * Constructor
	 * 
	 * @param pageName the page name
	 * @param pageTitle the page title
	 * @param schemaNamespace the schema namespace
	 */
	protected InstanceDataImportWizardMainPage(String pageName, String pageTitle,
			String schemaNamespace) {
		super(pageName, pageTitle, (ImageDescriptor) null); // FIXME ImageDescriptor
		super.setTitle(pageName); //NON-NLS-1
		super.setDescription(Messages.InstanceDataImportWizardMainPage_LoadGeoDescription1 +
				Messages.InstanceDataImportWizardMainPage_LoadGeoDescription2);
		
		this.schemaNamespace = schemaNamespace;
	}
	
	// methods .................................................................

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		super.initializeDialogUnits(parent);
        
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
                | GridData.HORIZONTAL_ALIGN_FILL));
        composite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        composite.setFont(parent.getFont());

        this.createSourceGroup(composite, schemaNamespace);
        setPageComplete(false);
		setControl(composite);
		
		if (schemaNamespace == null) {
			setErrorMessage("You have to load a source schema before you can load instance data");
		}
		else {
			setErrorMessage(null);
		}
	}
	
	/**
	 * Creates the UI controls for the selection of the source of the schema
	 * to be imported.
	 * 
	 * @param parent the parent {@link Composite}
	 * @param schemaNamespace the schema namespace 
	 */
	private void createSourceGroup(Composite parent, String schemaNamespace) {
		
		// define source group composite
		Group selectionArea = new Group(parent, SWT.NONE);
		selectionArea.setText(Messages.InstanceDataImportWizardMainPage_ReadGeodata);
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
		this.fileFieldEditor = new FileFieldEditor("fileSelect",  //$NON-NLS-1$
				Messages.InstanceDataImportWizardMainPage_File, ffe_container); //NON-NLS-1 //NON-NLS-2
		this.fileFieldEditor.getTextControl(ffe_container).addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				getWizard().getContainer().updateButtons();
			}
		});
		String[] extensions = new String[] { "*.gml", "*.xml" }; //NON-NLS-1 //$NON-NLS-1$ //$NON-NLS-2$
		this.fileFieldEditor.setFileExtensions(extensions);
		
		// read from WFS (GetFeature)
		Button useWfsRadio = new Button(fileSelectionArea, SWT.RADIO);
		useWfsRadio.setEnabled(true);
		final Composite ufe_container = new Composite(fileSelectionArea, SWT.NULL);
		ufe_container.setLayoutData(
				new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		this.wfsFieldEditor = new UrlFieldEditor("urlSelect","... WFS GetFeature:", ufe_container, schemaNamespace, true); //$NON-NLS-1$ //$NON-NLS-2$
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
				useFile = !((Button)e.widget).getSelection();
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
		
		// import options
		Group optionsGroup = new Group(parent, SWT.NONE);
		optionsGroup.setText("Import options");
		optionsGroup.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		optionsGroup.setLayout(new GridLayout());
		
		configSelector = new ComboViewer(optionsGroup);
		configSelector.setContentProvider(ArrayContentProvider.getInstance());
		configSelector.setLabelProvider(new LabelProvider());
		configSelector.getControl().setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		
		ConfigurationType[] values = ConfigurationType.values();
		configSelector.setInput(values);
		
		configSelector.setSelection(new StructuredSelection(ConfigurationType.GML3));
		
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
					if (test != null && !test.equals("")) { //$NON-NLS-1$
						this.result = new URL(test);
						_log.debug("wfsFieldEditor URL was OK."); //$NON-NLS-1$
					} 
					else {
						return false;
					}
				}
				else {
					// test whether content of the File Field Editor validates to URI.
					String test = this.fileFieldEditor.getStringValue();
					if (test != null && !test.equals("")) { //$NON-NLS-1$
						File file = new File(test);
						if (file.exists()) {
							this.result = file.toURI().toURL();
						}
					}
					else {
						return false;
					}
				}	
			} catch (Exception ex) {
				ex.printStackTrace();
				return false;
			} 
			_log.debug("Page is complete."); //$NON-NLS-1$
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * @return a String representing the URL or URI to load the schema from.
	 */
	public URL getResult() {
		return this.result;
	}
	
	/**
	 * Get the selected configuration type
	 * 
	 * @return the configuration type
	 */
	public ConfigurationType getConfiguration() {
		ISelection sel = configSelector.getSelection();
		if (sel.isEmpty() || !(sel instanceof IStructuredSelection)) {
			return ConfigurationType.GML3;
		}
		else {
			return (ConfigurationType) ((IStructuredSelection) sel).getFirstElement();
		}
	}

//	public InstanceInterfaceType getInterfaceType() {
//		if (this.useFile) {
//			return InstanceInterfaceType.FILE;
//		}
//		else {
//			return InstanceInterfaceType.WFS;
//		}
//	}

}
