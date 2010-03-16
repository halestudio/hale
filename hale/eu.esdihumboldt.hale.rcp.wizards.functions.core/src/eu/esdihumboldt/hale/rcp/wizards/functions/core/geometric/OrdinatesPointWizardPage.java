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

package eu.esdihumboldt.hale.rcp.wizards.functions.core.geometric;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.rcp.utils.definition.DefinitionLabelFactory;
import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;
import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleComposedCellWizardPage;
import eu.esdihumboldt.hale.rcp.wizards.functions.core.math.MathExpressionFieldEditor;


/**
 * TODO The page of the ordinate to geometrical point wizard
 * 
 * @author Stefan Gessner
 * @version $Id$
 */
public class OrdinatesPointWizardPage extends
		AbstractSingleComposedCellWizardPage {

	private MathExpressionFieldEditor expressionEditorX = null;
	private MathExpressionFieldEditor expressionEditorY = null;
	
	private String initialExpression = null;
	
	/**
	 * Constructor
	 * 
	 * @param pageName the page name
	 */
	public OrdinatesPointWizardPage(String pageName) {
		super(pageName);
		setTitle(pageName);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		// TODO Add user interface
		
		 super.initializeDialogUnits(parent);
	        this.setPageComplete(this.isPageComplete());
	        
	        Composite composite = new Composite(parent, SWT.NULL);
	        composite.setLayout(new GridLayout());
	        composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
	        composite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	        composite.setFont(parent.getFont());

	        this.createConfigurationGroup(composite);
	        
	        setErrorMessage(null);	// should not initially have error message
			super.setControl(composite);

	}
	
	private void createConfigurationGroup(Composite parent) {
		// define source group composite
		Group configurationGroup = new Group(parent, SWT.NONE);
		configurationGroup.setText("Define ordinates to geometrical point");
		configurationGroup.setLayout(new GridLayout());
		GridData configurationAreaGD = new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL);
		configurationAreaGD.grabExcessHorizontalSpace = true;
		configurationGroup.setLayoutData(configurationAreaGD);
		configurationGroup.setSize(configurationGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		configurationGroup.setFont(parent.getFont());
		
		final Composite configurationComposite = new Composite(configurationGroup, SWT.NONE);
		configurationComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		GridLayout fileSelectionLayout = new GridLayout();
		fileSelectionLayout.numColumns = 3;
		fileSelectionLayout.makeColumnsEqualWidth = false;
		fileSelectionLayout.marginWidth = 0;
		fileSelectionLayout.marginHeight = 0;
		configurationComposite.setLayout(fileSelectionLayout);
		
		DefinitionLabelFactory dlf = (DefinitionLabelFactory) PlatformUI.getWorkbench().getService(DefinitionLabelFactory.class);
		TreeSet sourceTreeSet = (TreeSet) getParent().getSourceItems();
		TreeSet targetTreeSet = (TreeSet) getParent().getTargetItems();
		
		// expression
		Set<String> variablesX = new TreeSet<String>();
//		SchemaItem p = getParent().getSourceItem();
//		for (SchemaItem var : p.getParent().getChildren()) {
//			if (Number.class.isAssignableFrom(
//					var.getPropertyType().getBinding()) 
//					|| String.class.isAssignableFrom(
//							var.getPropertyType().getBinding())) {
//				variables.add(var.getName().getLocalPart());
//			}
//		}
		
		final Label inputAttributeLabel = new Label(configurationComposite, SWT.NONE);
		inputAttributeLabel.setText("X:");
		Control inputAttributeText = dlf.createLabel(configurationComposite, ((SchemaItem)sourceTreeSet.first()).getDefinition(), false);
		inputAttributeText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		// Expression X editor initiation
		this.expressionEditorX = new MathExpressionFieldEditor(
				"expression", "", configurationComposite, variablesX);
		if (this.initialExpression != null && !this.initialExpression.equals("")) {
			this.expressionEditorX.setStringValue(this.initialExpression);
		}
	
		this.expressionEditorX.setEmptyStringAllowed(false);
		this.expressionEditorX.setPage(this);
		this.expressionEditorX.setPropertyChangeListener(new IPropertyChangeListener() {
			
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(FieldEditor.IS_VALID)) {
					updateX();
				}
			}
		});	
		
		// expression
		Set<String> variablesY = new TreeSet<String>();
//		SchemaItem p = getParent().getSourceItem();
//		for (SchemaItem var : p.getParent().getChildren()) {
//			if (Number.class.isAssignableFrom(
//					var.getPropertyType().getBinding()) 
//					|| String.class.isAssignableFrom(
//							var.getPropertyType().getBinding())) {
//				variables.add(var.getName().getLocalPart());
//			}
//		}
		
		final Label blaAttributeLabel = new Label(configurationComposite, SWT.NONE);
		blaAttributeLabel.setText("Y:");
		Control blaAttributeText = dlf.createLabel(configurationComposite, ((SchemaItem)sourceTreeSet.last()).getDefinition(), false);
		blaAttributeText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		// Expression Y editor initiation
		this.expressionEditorY = new MathExpressionFieldEditor(
				"expression", "" , configurationComposite, variablesY);
		if (this.initialExpression != null && !this.initialExpression.equals("")) {
			this.expressionEditorY.setStringValue(this.initialExpression);
		}
		
		this.expressionEditorY.setEmptyStringAllowed(false);
		this.expressionEditorY.setPage(this);
		this.expressionEditorY.setPropertyChangeListener(new IPropertyChangeListener() {
			
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(FieldEditor.IS_VALID)) {
					updateY();
				}
			}
		});
		
		final Label AttributeLabel = new Label(configurationComposite, SWT.NONE);
		AttributeLabel.setText("Geometrical Point:");
		Control AttributeText = dlf.createLabel(configurationComposite, getParent().getFirstTargetItem().getDefinition(), false);
		AttributeText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

	}
	
	
	private void updateX() {
		setPageComplete(expressionEditorX.isValid());
	}
	
	private void updateY() {
		setPageComplete(expressionEditorY.isValid());
	}
	
	/**
	 * @see WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		return this.expressionEditorX != null 
						&& this.expressionEditorX.getStringValue() != null
						&& this.expressionEditorY != null
						&& this.expressionEditorY.getStringValue() != null;
	}

	/**
	 * @return the expansion expression
	 */
	public String getExpansionX() {
		return this.expressionEditorX.getStringValue();
	}
	public String getExpansionY() {
		return this.expressionEditorY.getStringValue();
	}
	
	public void setInitialExpression(String value) {
		this.initialExpression = value;
	}


}
