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

package eu.esdihumboldt.hale.rcp.wizards.functions.core.augmentation.nilreason;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.rcp.utils.definition.AttributeEditor;
import eu.esdihumboldt.hale.rcp.utils.definition.AttributeEditorFactory;
import eu.esdihumboldt.hale.rcp.utils.definition.DefinitionLabelFactory;
import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;
import eu.esdihumboldt.hale.rcp.wizards.augmentations.AugmentationWizardPage;
import eu.esdihumboldt.hale.rcp.wizards.functions.core.Messages;
import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;

/**
 * Main {@link NilReasonWizard} page
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class NilReasonWizardPage extends AugmentationWizardPage {
	
	private final String initialNilReason;
	private AttributeEditor<?> attributeValue;

	/**
	 * Constructor
	 * 
	 * @param pageName the page name 
	 * @param title the page title
	 * @param titleImage the title image
	 * @param initialNilReason the initial nil reason
	 *  
	 * @see AugmentationWizardPage#AugmentationWizardPage(String, String, ImageDescriptor)
	 */
	public NilReasonWizardPage(String pageName, String title,
			ImageDescriptor titleImage, String initialNilReason) {
		super(pageName, title, titleImage);
		
		setDescription(Messages.NilReasonWizardPage_0);
		
		this.initialNilReason = initialNilReason;
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		
		page.setLayout(new GridLayout(2, false));
		
		DefinitionLabelFactory dlf = (DefinitionLabelFactory) PlatformUI
			.getWorkbench().getService(DefinitionLabelFactory.class);
		AttributeEditorFactory aef = (AttributeEditorFactory) PlatformUI
			.getWorkbench().getService(AttributeEditorFactory.class);
		
		SchemaItem item = getParent().getItem();
		AttributeDefinition property = (AttributeDefinition) item.getDefinition();
		AttributeDefinition nilReason = property.getAttributeType().getAttribute("nilReason"); //$NON-NLS-1$
		
		final Label inputAttributeLabel = new Label(page,
				SWT.NONE);
		inputAttributeLabel.setText(Messages.NilReasonWizardPage_2);
		inputAttributeLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false,
				false));
		Control attributeName = dlf.createLabel(page,
				item.getDefinition(), false);
		attributeName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		
		Control nilReasonLabel = dlf.createLabel(page,
				nilReason, false);
		nilReasonLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false,
				false));
		
		attributeValue = aef.createEditor(page, nilReason);
		attributeValue.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.CENTER, true, false));
		attributeValue.setAsText(initialNilReason);
		
		setControl(page);
	}

	/**
	 * @return the type
	 */
	public String getNilReason() {
		return attributeValue.getAsText();
	}

}
