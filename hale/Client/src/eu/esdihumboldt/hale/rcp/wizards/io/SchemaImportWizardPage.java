/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package eu.esdihumboldt.hale.rcp.wizards.io;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
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
 * 
 * This is the main page of the {@link SchemaImportWizard}.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class SchemaImportWizardPage 
	extends WizardPage {
	
	protected FileFieldEditor fileFieldEditor;
	
	protected UrlFieldEditor wfsFieldEditor;

	public SchemaImportWizardPage(String pageName, String pageTitle) {
		super(pageName, pageTitle, (ImageDescriptor) null); // FIXME ImageDescriptor
		setTitle(pageName); //NON-NLS-1
		setDescription("Read a source or target schema from a local file or a " +
				"Web Feature Service"); //NON-NLS-1
	}

	/**
	 * 
	 * @param parent
	 */
	@Override
	public void createControl(Composite parent) {
		
        super.initializeDialogUnits(parent);

        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
                | GridData.HORIZONTAL_ALIGN_FILL));
        composite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        composite.setFont(parent.getFont());

        this.createSourceGroup(composite);
        this.createDestinationGroup(composite);
        this.createOptionsGroup(composite);

        //this.restoreWidgetValues();
        //this.updateWidgetEnablements();
		//setPageComplete(determinePageCompletion());
        
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
		Button useFileRadio = new Button(fileSelectionArea, SWT.RADIO);
		useFileRadio.setSelection(true);
		final Composite ffe_container = new Composite(fileSelectionArea, SWT.NULL);
		ffe_container.setLayoutData(
				new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		fileFieldEditor = new FileFieldEditor("fileSelect", 
				"... file:", ffe_container); //NON-NLS-1 //NON-NLS-2
		fileFieldEditor.getTextControl(ffe_container).addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				IPath path = new Path(SchemaImportWizardPage.this.fileFieldEditor.getStringValue());
				//fileFieldEditor.set(path.lastSegment());
			}
		});
		String[] extensions = new String[] { "*.xml", "*.gml", "*.xsd" }; //NON-NLS-1
		fileFieldEditor.setFileExtensions(extensions);
		
		// read from WFS (DescribeFeatureType)
		Button useWfsRadio = new Button(fileSelectionArea, SWT.RADIO);
		final Composite ufe_container = new Composite(fileSelectionArea, SWT.NULL);
		ufe_container.setLayoutData(
				new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		wfsFieldEditor = new UrlFieldEditor("urlSelect", 
				"... WFS DescribeFeatureType:", ufe_container);
		wfsFieldEditor.setEnabled(false, ufe_container);
		
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
		// TODO Auto-generated method stub
		
	}

	/**
	 * Creates the UI controls for the options that can be applied when 
	 * importing a schema.
	 * 
	 * @param parent the parent {@link Composite}
	 */
	private void createOptionsGroup(Composite parent) {
		// TODO Auto-generated method stub
		
	}
	
}
