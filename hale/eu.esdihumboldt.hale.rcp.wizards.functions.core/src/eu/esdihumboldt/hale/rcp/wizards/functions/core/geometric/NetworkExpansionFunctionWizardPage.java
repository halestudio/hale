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

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.rcp.utils.definition.DefinitionLabelFactory;
import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleCellWizardPage;

/**
 * TODO Explain the purpose of this type here.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class NetworkExpansionFunctionWizardPage 
	extends AbstractSingleCellWizardPage {
	
	private Text expansionExpressionText = null;
	
	/**
	 * Constructor
	 * 
	 * @param pageName the page name
	 */
	protected NetworkExpansionFunctionWizardPage(String pageName) {
		super(pageName);
		
		setTitle(pageName);
	}

	/**
	 * @see IDialogPage#createControl(Composite)
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
		configurationComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		GridLayout fileSelectionLayout = new GridLayout();
		fileSelectionLayout.numColumns = 2;
		fileSelectionLayout.makeColumnsEqualWidth = false;
		fileSelectionLayout.marginWidth = 0;
		fileSelectionLayout.marginHeight = 0;
		configurationComposite.setLayout(fileSelectionLayout);
		
		DefinitionLabelFactory dlf = (DefinitionLabelFactory) PlatformUI.getWorkbench().getService(DefinitionLabelFactory.class);
		
		final Label inputAttributeLabel = new Label(configurationComposite, SWT.NONE);
		inputAttributeLabel.setText("Source attribute:");
		Control inputAttributeText = dlf.createLabel(configurationComposite, getParent().getSourceItem().getDefinition(), false);
		inputAttributeText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		final Label outputAttributeLabel = new Label(configurationComposite, SWT.NONE);
		outputAttributeLabel.setText("Target attribute:");
		Control outputAttributeText = dlf.createLabel(configurationComposite, getParent().getTargetItem().getDefinition(), false);
		outputAttributeText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		final Label expansionExpressionLabel = new Label(configurationComposite, SWT.NONE);
		expansionExpressionLabel.setText("Expansion expression:");
		this.expansionExpressionText = new Text(configurationComposite, SWT.BORDER);
		this.expansionExpressionText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		String value = "50";
		/*FIXME outcomment this when build errors are resolved - ITransformation trans = cell.getEntity1().getTransformation();
		if (trans != null && trans.getParameters() != null) {
			if (IParameter param : trans.getParameters()) {
				if (param.getName().equals("Expansion")) {
					value = param.getValue();
				}
			}
		}*/
		
		this.expansionExpressionText.setText(value);
		
	}
	
	/**
	 * @see WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		return expansionExpressionText != null && expansionExpressionText.getText() != null;
	}

	/**
	 * @return the expansion expression
	 */
	public String getExpansion() {
		return expansionExpressionText.getText();
	}

}
