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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.hale.rcp.wizards.augmentations.AugmentationWizardPage;

/**
 * 
 *
 * @author Anna Pitaev
 * @partner 04 / Logica
 * @version $Id$ 
 */
public class ConstantValueWizardPage extends AugmentationWizardPage {

	/** text field for the name of the target attribute */
	private Text attributeNameText;
	
	/** text field for the default value to set up */
	private StyledText attributeValueText;

	public ConstantValueWizardPage(String pageName, String title,
			ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
		setTitle(title);
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
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
	        
	        setErrorMessage(null);	// should not initially have error message
			super.setControl(composite);
			

		}
	
	private void createConfigurationGroup(Composite parent) {
		// define source group composite
		Group configurationGroup = new Group(parent, SWT.NONE);
		configurationGroup.setText("Please enter a default value for the attribute");
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

		GridLayout attributeInputLayout = new GridLayout();
		attributeInputLayout.numColumns = 2;
		attributeInputLayout.makeColumnsEqualWidth = false;
		attributeInputLayout.marginWidth = 0;
		attributeInputLayout.marginHeight = 0;
		configurationComposite.setLayout(attributeInputLayout);
		
		final Label inputAttributeLabel = new Label(configurationComposite, SWT.NONE);
		inputAttributeLabel.setText("Attribute name:");
		this.attributeNameText = new Text(configurationComposite, SWT.BORDER);
		this.attributeNameText.setLayoutData(configurationLayoutData);
		this.attributeNameText.setText(getParent().getItem().getName().getLocalPart());
		this.attributeNameText.setEnabled(false);
		
		final Label outputAttributeLabel = new Label(configurationComposite, SWT.NONE);
		outputAttributeLabel.setText("Attribute default value:");
		this.attributeValueText = new StyledText(configurationComposite, SWT.BORDER);
		this.attributeValueText.setLayoutData(configurationLayoutData);
		//if cell already exists and valid, display the old default value
		if ((getParent().getResultCell()!= null && getParent().getResultCell().getEntity2()!= null && getParent().getResultCell().getEntity2().getTransformation()!=null && getParent().getResultCell().getEntity2().getTransformation().getParameters()!= null)){
			String oldValue = "";
			IParameter tmpParameter = null;
			List<IParameter> parameters = getParent().getResultCell().getEntity2().getTransformation().getParameters();
			Iterator iterator = parameters.iterator();
			while(iterator.hasNext()){
				tmpParameter = (IParameter)iterator.next();
				if (tmpParameter.getName().equals(getParamName())) oldValue = tmpParameter.getValue();
			}
			this.attributeValueText.setText(oldValue);
			this.attributeValueText.setCaretOffset(oldValue.length());
			
		}
		this.attributeValueText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e){
				updatePageComplete();
				
			}

			
		});
		
		this.attributeValueText.setEnabled(true);
		
		
		
		
	
		
		
		
	}
	
	/**
	 * @see WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		boolean isComplete = false;
		if (this.attributeValueText!=null && !this.attributeValueText.getText().equals("")){
			isComplete = true;
		}
		return isComplete;
	}

	/**
	 * @see WizardPage#isPageComplete()
	 */
	private void updatePageComplete(){
		setPageComplete(this.isPageComplete());
	}
	/**
	 * Returns the name of the target attrubute
	 * @return
	 */
	public String getParamName() {
		
		return this.attributeNameText.getText();
	}
	
	/**
	 * Returns the default value to be set
	 * @return
	 */

	public String getParamValue() {
		
		return this.attributeValueText.getText();
	}

}
