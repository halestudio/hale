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

package eu.esdihumboldt.hale.rcp.wizards.functions.core.augmentation.constantvalue;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.cst.corefunctions.ConstantValueFunction;
import eu.esdihumboldt.hale.rcp.utils.definition.AttributeEditor;
import eu.esdihumboldt.hale.rcp.utils.definition.AttributeEditorFactory;
import eu.esdihumboldt.hale.rcp.utils.definition.DefinitionLabelFactory;
import eu.esdihumboldt.hale.rcp.wizards.augmentations.AugmentationWizardPage;
import eu.esdihumboldt.hale.rcp.wizards.functions.core.Messages;
import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.specification.cst.align.ext.IParameter;

/**
 * Main page of the {@link ConstantValueWizard}
 * 
 * @author Anna Pitaev, Simon Templer
 * @partner 04 / Logica; 01 / Fraunhofer Institute for Computer Graphics
 *          Research
 * @version $Id$
 */
public class ConstantValueWizardPage extends AugmentationWizardPage {

	/** text field for the name of the target attribute */
	// private Text attributeNameText;

	/** text field for the default value to set up */
	private AttributeEditor<?> attributeValue;

	/**
	 * @see AugmentationWizardPage#AugmentationWizardPage(String, String, ImageDescriptor)
	 */
	public ConstantValueWizardPage(String pageName, String title,
			ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
		setTitle(title);
	}

	/**
	 * @see IDialogPage#createControl(Composite)
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

		this.createConfigurationGroup(composite);

		setErrorMessage(null); // should not initially have error message
		super.setControl(composite);
	}

	private void createConfigurationGroup(Composite parent) {
		DefinitionLabelFactory dlf = (DefinitionLabelFactory) PlatformUI
				.getWorkbench().getService(DefinitionLabelFactory.class);
		AttributeEditorFactory aef = (AttributeEditorFactory) PlatformUI
				.getWorkbench().getService(AttributeEditorFactory.class);

		// define source group composite
		Group configurationGroup = new Group(parent, SWT.NONE);
		configurationGroup
				.setText(Messages.ConstantValueWizardPage_0);
		configurationGroup.setLayout(new GridLayout());
		GridData configurationAreaGD = new GridData(
				GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL);
		configurationAreaGD.grabExcessHorizontalSpace = true;
		configurationGroup.setLayoutData(configurationAreaGD);
		configurationGroup.setSize(configurationGroup.computeSize(SWT.DEFAULT,
				SWT.DEFAULT));
		configurationGroup.setFont(parent.getFont());

		final Composite configurationComposite = new Composite(
				configurationGroup, SWT.NONE);
		GridData configurationLayoutData = new GridData(
				GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
		configurationLayoutData.grabExcessHorizontalSpace = true;
		configurationComposite.setLayoutData(configurationLayoutData);

		GridLayout attributeInputLayout = new GridLayout();
		attributeInputLayout.numColumns = 2;
		attributeInputLayout.makeColumnsEqualWidth = false;
		attributeInputLayout.marginWidth = 0;
		attributeInputLayout.marginHeight = 0;
		configurationComposite.setLayout(attributeInputLayout);

		final Label inputAttributeLabel = new Label(configurationComposite,
				SWT.NONE);
		inputAttributeLabel.setText(Messages.ConstantValueWizardPage_1);
		Control attributeName = dlf.createLabel(configurationComposite,
				getParent().getItem().getDefinition(), false);
		attributeName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));

		final Label outputAttributeLabel = new Label(configurationComposite,
				SWT.NONE);
		outputAttributeLabel.setText(Messages.ConstantValueWizardPage_2);
		attributeValue = aef.createEditor(configurationComposite,
				(AttributeDefinition) getParent().getItem().getDefinition());
		attributeValue.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.CENTER, true, false));
		// if cell already exists and valid, display the old default value
		if ((getParent().getResultCell() != null
				&& getParent().getResultCell().getEntity2() != null
				&& getParent().getResultCell().getEntity2().getTransformation() != null && getParent()
				.getResultCell().getEntity2().getTransformation()
				.getParameters() != null)) {
			String oldValue = ""; //$NON-NLS-1$
			List<IParameter> parameters = getParent().getResultCell()
					.getEntity2().getTransformation().getParameters();
			Iterator<IParameter> iterator = parameters.iterator();
			while (iterator.hasNext()) {
				IParameter tmpParameter = iterator.next();
				if (tmpParameter.getName().equals(
						ConstantValueFunction.DEFAULT_VALUE_PARAMETER_NAME)) {
					oldValue = tmpParameter.getValue();
				}
			}
			attributeValue.setAsText(oldValue);
			// XXX this.attributeValueText.setCaretOffset(oldValue.length());

		}
		/*
		 * XXX this.attributeValueText.addModifyListener(new ModifyListener() {
		 * public void modifyText(ModifyEvent e){ updatePageComplete();
		 * 
		 * } });
		 */
	}

	/**
	 * @see WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		/*
		 * XXX boolean isComplete = false; if (attributeValue !=null &&
		 * attributeValue.getAsText() != null &&
		 * !attributeValue.getAsText().equals("")){ isComplete = true; } return
		 * isComplete;
		 */
		return true;
	}

	/*
	 * XXX private void updatePageComplete(){
	 * setPageComplete(this.isPageComplete()); }
	 */

	/**
	 * Returns the default value to be set
	 * 
	 * @return the parameter value
	 */
	public String getParamValue() {
		return attributeValue.getAsText();
	}

}
