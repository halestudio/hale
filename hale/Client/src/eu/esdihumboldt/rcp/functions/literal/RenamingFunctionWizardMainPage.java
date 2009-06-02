package eu.esdihumboldt.rcp.functions.literal;

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

import eu.esdihumboldt.hale.models.impl.SchemaServiceEnum;
import eu.esdihumboldt.hale.rcp.wizards.io.UrlFieldEditor;



public class RenamingFunctionWizardMainPage 
	extends WizardPage {
		
		private static Logger _log = Logger.getLogger(RenamingFunctionWizardMainPage.class);
		protected Composite ffe_container;
		protected Composite ufe_container;
		protected FileFieldEditor fileFieldEditor;
		protected UrlFieldEditor wfsFieldEditor;
		protected Button useWfsRadio;
		protected Button useFileRadio;
		private Button sourceDestination;
		private Button targetDestination;

        protected RenamingFunctionWizardMainPage(String pageName, String title)
		 {
		super(pageName, title, (ImageDescriptor) null);
		setTitle(pageName); //NON-NLS-1
		setDescription("Read a source or target schema from a local file or a " +
				"Web Feature Service"); //NON-NLS-1
		
	}

		
        /**
    	 * The parent methods where all controls are created for this {@link WizardPage}.
    	 * @param parent
    	 */
    	public void createControl(Composite parent) {
    		
            super.initializeDialogUnits(parent);
            this.setPageComplete(this.isPageComplete());
            
            Composite composite = new Composite(parent, SWT.NULL);
            composite.setLayout(new GridLayout());
            composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
                    | GridData.HORIZONTAL_ALIGN_FILL));
            composite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            composite.setFont(parent.getFont());

            this.createSourceGroup(composite);
            this.createDestinationGroup(composite);
            this.createOptionsGroup(composite);
            
            setErrorMessage(null);	// should not initially have error message
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
    		selectionArea.setText("Read Schema from ...");
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
    		this.useFileRadio = new Button(fileSelectionArea, SWT.RADIO);
    		useFileRadio.setSelection(true);
    		this.ffe_container = new Composite(fileSelectionArea, SWT.NULL);
    		ffe_container.setLayoutData(
    				new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
    		fileFieldEditor = new FileFieldEditor("fileSelect", 
    				"... file:", ffe_container); //NON-NLS-1 //NON-NLS-2
    		fileFieldEditor.getTextControl(ffe_container).addModifyListener(new ModifyListener(){
    			public void modifyText(ModifyEvent e) {
    				getWizard().getContainer().updateButtons();
    			}
    		});
    		String[] extensions = new String[] { "*.xml", "*.gml", "*.xsd" }; //NON-NLS-1
    		fileFieldEditor.setFileExtensions(extensions);
    		
    		// read from WFS (DescribeFeatureType)
    		this.useWfsRadio = new Button(fileSelectionArea, SWT.RADIO);
    		this.ufe_container = new Composite(fileSelectionArea, SWT.NULL);
    		ufe_container.setLayoutData(
    				new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
    		wfsFieldEditor = new UrlFieldEditor("urlSelect", 
    				"... WFS DescribeFeatureType:", ufe_container);
    		wfsFieldEditor.setEnabled(false, ufe_container);
    		wfsFieldEditor.getTextControl(ufe_container).addModifyListener(new ModifyListener() {
    			public void modifyText(ModifyEvent e) {
    				getWizard().getContainer().updateButtons();
    			}
    		});
    		
    		// add listeners to radio buttons
    		useFileRadio.addSelectionListener(new SelectionAdapter() {
    			@Override
    			public void widgetSelected(SelectionEvent e) {
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
    	 * creates the UI controls for the selection of the place where to import 
    	 * the schema to (target schema or source schema)
    	 * @param parent the parent {@link Composite}
    	 */
    	private void createDestinationGroup(Composite parent) {
    		// define source group composite
    		Group destinationArea = new Group(parent, SWT.NONE);
    		destinationArea.setText("Import Destination");
    		destinationArea.setLayout(new GridLayout());
    		GridData destinationAreaGD = new GridData(GridData.VERTICAL_ALIGN_FILL
                    | GridData.HORIZONTAL_ALIGN_FILL);
    		destinationAreaGD.grabExcessHorizontalSpace = true;
    		destinationArea.setLayoutData(destinationAreaGD);
    		destinationArea.setSize(destinationArea.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    		destinationArea.setFont(parent.getFont());
    		
    		sourceDestination = new Button(destinationArea, SWT.RADIO);
    		sourceDestination.setSelection(true);
    		sourceDestination.setText("Import as Source Schema");
    		
    		targetDestination = new Button(destinationArea, SWT.RADIO);
    		targetDestination.setText("Import as Target Schema");
    	}

    	/**
    	 * Creates the UI controls for the options that can be applied when 
    	 * importing a schema.
    	 * 
    	 * @param parent the parent {@link Composite}
    	 */
    	private void createOptionsGroup(Composite parent) {
    		Group optionsGroup = new Group(parent, SWT.NONE);
    		optionsGroup.setText("Import Options");
    		optionsGroup.setLayout(new GridLayout());
    		GridData optionsGroupGD = new GridData(GridData.VERTICAL_ALIGN_FILL
                    | GridData.HORIZONTAL_ALIGN_FILL);
    		optionsGroupGD.grabExcessHorizontalSpace = true;
    		optionsGroup.setLayoutData(optionsGroupGD);
    		optionsGroup.setSize(optionsGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    		optionsGroup.setFont(parent.getFont());
    		
    		// import supertypes/schema elements?
    		Button sourceDestination = new Button(optionsGroup, SWT.CHECK);
    		sourceDestination.setSelection(true);
    		sourceDestination.setText("Also import supertypes from imported " +
    				"schemas");
    		
    	}
    	
    	/**
    	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
    	 */
    	@Override
    	public boolean isPageComplete() {
    		if (this.fileFieldEditor != null && this.wfsFieldEditor != null) {
    			_log.debug("fileFieldEditor: " + this.fileFieldEditor.getStringValue());
    			try {
    				if (this.useWfsRadio.getSelection()) {
    					// test whether content of the WFS Field Editor validates to URL.
    					String test = this.wfsFieldEditor.getStringValue();
    					if (test != null && !test.equals("")) {
    						new URL(test);
    						_log.debug("wfsFieldEditor URL was OK.");
    					} 
    					else {
    						return false;
    					}
    				}
    				else {
    					// test whether content of the File Field Editor validates to URI.
    					String test = this.fileFieldEditor.getStringValue();
    					if (test != null && !test.equals("")) {
    						new URI(test.replaceAll("\\\\", "/"));
    						_log.debug("fileFieldEditor URI was OK.");
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
    		if (this.useWfsRadio.getSelection()) {
    			return this.wfsFieldEditor.getTextControl(
    					this.ufe_container).getText();
    		}
    		else {
    			return this.fileFieldEditor.getTextControl(
    					this.ffe_container).getText(); 
    		}
    	}
    	
    	public SchemaServiceEnum getSchemaType() {
    		if (sourceDestination.getSelection()) {
    			return SchemaServiceEnum.SOURCE_SCHEMA;
    		}
    		else return SchemaServiceEnum.TARGET_SCHEMA;
    	}
    	
}
