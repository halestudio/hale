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

package eu.esdihumboldt.hale.rcp.wizards.functions.geometric;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.viewers.TreeViewer;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.omwg.FeatureClass;
import eu.esdihumboldt.hale.rcp.views.model.ModelNavigationView;
import eu.esdihumboldt.hale.rcp.wizards.io.UrlFieldEditor;

/**
 * TODO Explain the purpose of this type here.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class NetworkExpansionFunctionWizardPage 
	extends WizardPage {
	
	private Text inputAttributeText = null;
	private Text outputAttributeText = null;
	private Text expansionExpressionText = null;

	protected NetworkExpansionFunctionWizardPage(String pageName) {
		super(pageName);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		 super.initializeDialogUnits(parent);
        this.setPageComplete(this.isPageComplete());
        
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
                | GridData.HORIZONTAL_ALIGN_FILL));
        composite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        composite.setFont(parent.getFont());

        this.createConfigurationGroup(composite);
        
        setErrorMessage(null);	// should not initially have error message
		super.setControl(composite);

	}

	private void createConfigurationGroup(Composite parent) {
		// define source group composite
		Group configurationGroup = new Group(parent, SWT.NONE);
		configurationGroup.setText("Define Network to Polygon Expansion");
		configurationGroup.setLayout(new GridLayout());
		GridData configurationAreaGD = new GridData(GridData.VERTICAL_ALIGN_FILL
                | GridData.HORIZONTAL_ALIGN_FILL);
		configurationAreaGD.grabExcessHorizontalSpace = true;
		configurationGroup.setLayoutData(configurationAreaGD);
		configurationGroup.setSize(configurationGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		configurationGroup.setFont(parent.getFont());
		
		final Composite configurationComposite = new Composite(configurationGroup, SWT.NONE);
		GridData configurationLayoutData = new GridData(
				GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
		configurationLayoutData.grabExcessHorizontalSpace = true;
		configurationComposite.setLayoutData(configurationLayoutData);

		GridLayout fileSelectionLayout = new GridLayout();
		fileSelectionLayout.numColumns = 2;
		fileSelectionLayout.makeColumnsEqualWidth = false;
		fileSelectionLayout.marginWidth = 0;
		fileSelectionLayout.marginHeight = 0;
		configurationComposite.setLayout(fileSelectionLayout);
		
		final Label inputAttributeLabel = new Label(configurationComposite, SWT.NONE);
		inputAttributeLabel.setText("Source attribute:");
		this.inputAttributeText = new Text(configurationComposite, SWT.BORDER);
		this.inputAttributeText.setLayoutData(configurationLayoutData);
		this.inputAttributeText.setText(this.getSelectedFeatureType("source"));
		
		final Label outputAttributeLabel = new Label(configurationComposite, SWT.NONE);
		outputAttributeLabel.setText("Target attribute:");
		this.outputAttributeText = new Text(configurationComposite, SWT.BORDER);
		this.outputAttributeText.setLayoutData(configurationLayoutData);
		this.outputAttributeText.setText(this.getSelectedFeatureType("target"));
		
		final Label expansionExpressionLabel = new Label(configurationComposite, SWT.NONE);
		expansionExpressionLabel.setText("Expansion expression:");
		this.expansionExpressionText = new Text(configurationComposite, SWT.BORDER);
		this.expansionExpressionText.setLayoutData(configurationLayoutData);
		this.expansionExpressionText.setText("50");
		
	}
	
	public Cell getResultCell() {
		return new Cell();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		if (this.inputAttributeText != null 
				&& this.outputAttributeText != null 
				&& this.expansionExpressionText != null) {
			if (this.inputAttributeText.getText() != null 
					&& this.outputAttributeText.getText() != null 
					&& this.expansionExpressionText.getText() != null) {
				return true;
			}
		}
		return false;
	}

	private String getSelectedFeatureType(String selectedFeatureType) {
		String typeName = "";
		ModelNavigationView modelNavigation = getModelNavigationView();
		if (modelNavigation != null) {
			if (selectedFeatureType.equals("source")) {
				TreeItem[] sourceTreeSelection = 
					modelNavigation.getSourceSchemaViewer().getTree().getSelection();
				if (sourceTreeSelection.length == 1) {
					typeName = sourceTreeSelection[0].getText();
				}
			} else if (selectedFeatureType.equals("target")) {
				TreeItem[] targetTreeSelection = 
					modelNavigation.getTargetSchemaViewer().getTree().getSelection();
				if (targetTreeSelection.length == 1)
					typeName = targetTreeSelection[0].getText();
			}
		}
		return typeName;
	}
	
	protected ModelNavigationView getModelNavigationView() {
		ModelNavigationView attributeView = null;
		// get All Views
		IViewReference[] views = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getViewReferences();
		// get AttributeView
		// get AttributeView
		for (int count = 0; count < views.length; count++) {
			if (views[count].getId().equals(
					"eu.esdihumboldt.hale.rcp.views.model.ModelNavigationView")) {
				attributeView = (ModelNavigationView) views[count]
						.getView(false);
			}

		}
		return attributeView;
	}

}
