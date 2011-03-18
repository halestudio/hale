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
import eu.esdihumboldt.hale.rcp.wizards.functions.core.Messages;
import eu.esdihumboldt.hale.rcp.wizards.functions.core.math.MathExpressionFieldEditor;


/**
 * @author Stefan Gessner
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
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
		configurationGroup.setText(Messages.OrdinatesPointWizardPage_0);
		configurationGroup.setLayout(new GridLayout());
		GridData configurationAreaGD = new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL);
		configurationAreaGD.grabExcessHorizontalSpace = true;
		configurationGroup.setLayoutData(configurationAreaGD);
		configurationGroup.setSize(configurationGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		configurationGroup.setFont(parent.getFont());

		GridLayout fileSelectionLayout = new GridLayout();
		fileSelectionLayout.numColumns = 3;
		fileSelectionLayout.makeColumnsEqualWidth = false;
		fileSelectionLayout.marginWidth = 5;
		fileSelectionLayout.marginHeight = 5;
		configurationGroup.setLayout(fileSelectionLayout);
		
		TreeSet<SchemaItem> sourceTreeSet = (TreeSet<SchemaItem>) getParent().getSourceItems();
		
		// expression
		Set<String> variablesX = new TreeSet<String>();
		variablesX.add(sourceTreeSet.first().getName().getLocalPart());
		
		// Expression X editor initiation
		this.expressionEditorX = new MathExpressionFieldEditor(
				"expression", "X: ", configurationGroup, variablesX); //$NON-NLS-1$ //$NON-NLS-2$
		if (this.initialExpression != null && !this.initialExpression.equals("")) { //$NON-NLS-1$
			this.expressionEditorX.setStringValue(this.initialExpression);
		}
		this.expressionEditorX.insert(((SchemaItem)sourceTreeSet.first()).getName().getLocalPart());
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
		variablesY.add(sourceTreeSet.last().getName().getLocalPart());
		
		// Expression Y editor initiation
		this.expressionEditorY = new MathExpressionFieldEditor(
				"expression","Y: ", configurationGroup, variablesY); //$NON-NLS-1$ //$NON-NLS-2$
		if (this.initialExpression != null && !this.initialExpression.equals("")) { //$NON-NLS-1$
			this.expressionEditorY.setStringValue(this.initialExpression);
		}
		this.expressionEditorY.insert(((SchemaItem)sourceTreeSet.last()).getName().getLocalPart());
		
		this.expressionEditorY.setEmptyStringAllowed(false);
		this.expressionEditorY.setPage(this);
		this.expressionEditorY.setPropertyChangeListener(new IPropertyChangeListener() {
			
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(FieldEditor.IS_VALID)) {
					updateY();
				}
			}
		});
		
		final Label geoAttributeLabel = new Label(configurationGroup, SWT.NONE);
		geoAttributeLabel.setText(Messages.OrdinatesPointWizardPage_7);
		DefinitionLabelFactory dlf = (DefinitionLabelFactory) PlatformUI.getWorkbench().getService(DefinitionLabelFactory.class);
		Control geoAttributeText = dlf.createLabel(configurationGroup, getParent().getFirstTargetItem().getDefinition(), false);
		geoAttributeText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

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
