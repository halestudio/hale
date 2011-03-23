/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.rcp.wizards.functions.core.inspire;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.rcp.utils.definition.AttributeEditor;
import eu.esdihumboldt.hale.rcp.utils.definition.AttributeEditorFactory;
import eu.esdihumboldt.hale.rcp.utils.definition.DefinitionLabelFactory;
import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleCellWizardPage;
import eu.esdihumboldt.hale.rcp.wizards.functions.core.Messages;
import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.Definition;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * Main page of the {@link IdentifierFunctionWizard}
 * 
 * @author Thorsten Reitz, Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class IdentifierFunctionWizardPage extends
    AbstractSingleCellWizardPage {
	
	Text countryCode = null;
	
	Text providerName = null;
	
	Text productName = null;
	
	Text version = null;

	private AttributeEditor<?> nilEditor;

	/**
	 * @see AbstractSingleCellWizardPage#AbstractSingleCellWizardPage(String)
	 */
	public IdentifierFunctionWizardPage(String pageName) {
		super(pageName);
		setTitle(pageName);
		setDescription(Messages.IdentifierFunctionWizardPage_0);
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		AttributeEditorFactory aef = (AttributeEditorFactory) PlatformUI.getWorkbench().getService(AttributeEditorFactory.class);
		DefinitionLabelFactory dlf = (DefinitionLabelFactory) PlatformUI.getWorkbench().getService(DefinitionLabelFactory.class);
		
		super.initializeDialogUnits(parent);
		// create a composite to hold the widgets
		Composite page = new Composite(parent, SWT.NULL);
		setControl(parent);
		// create layout for this wizard page
		GridLayout gl = new GridLayout();
		gl.numColumns = 1;
		gl.marginLeft = 0;
		gl.marginTop = 20;
		gl.marginRight = 70;
		page.setLayout(gl);
		
		// identifier type
		TypeDefinition identifierType = null;
		Definition targetDef = getParent().getTargetItem().getDefinition();
		if (targetDef instanceof AttributeDefinition) {
			TypeDefinition propertyType = ((AttributeDefinition) targetDef).getAttributeType();
			for (AttributeDefinition attrib : propertyType.getAttributes()) {
				if (attrib.getTypeName().getLocalPart().equals("IdentifierType")) { //$NON-NLS-1$
					identifierType = attrib.getAttributeType();
				}
			}
		}
		
		// Namespace group
		Group nsGroup = new Group(page, SWT.NONE);
		nsGroup.setText(Messages.IdentifierFunctionWizardPage_2);
		nsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		nsGroup.setLayout(new GridLayout(2, false));
		
		// localId
		if (identifierType != null) {
			AttributeDefinition def = identifierType.getAttribute("namespace"); //$NON-NLS-1$
			if (def != null) {
				Control nsLabel = dlf.createLabel(nsGroup, def, false);
				nsLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
				
				Label nsDesc = new Label(nsGroup, SWT.NONE);
				nsDesc.setText(Messages.IdentifierFunctionWizardPage_4);
			}
		}
		
		// Country code
		Label ccLabel = new Label(nsGroup, SWT.NONE);
		ccLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		ccLabel.setText(Messages.IdentifierFunctionWizardPage_5);
		
		this.countryCode = new Text(nsGroup, SWT.BORDER);
		this.countryCode.setText("de"); //$NON-NLS-1$
		this.countryCode.setEnabled(true);
		this.countryCode.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// Provider name
		Label providerLabel = new Label(nsGroup, SWT.NONE);
		providerLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		providerLabel.setText(Messages.IdentifierFunctionWizardPage_7);
		
		this.providerName = new Text(nsGroup, SWT.BORDER);
		this.providerName.setText("fraunhofer"); //$NON-NLS-1$
		this.providerName.setEnabled(true);
		this.providerName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		// Product name
		Label productLabel = new Label(nsGroup, SWT.NONE);
		productLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		productLabel.setText(Messages.IdentifierFunctionWizardPage_9);
		
		this.productName = new Text(nsGroup, SWT.BORDER);
		this.productName.setText("humboldt-sample-transformed-data"); //$NON-NLS-1$
		this.productName.setEnabled(true);
		this.productName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		// Local ID group
		Group idGroup = new Group(page, SWT.NONE);
		idGroup.setText(Messages.IdentifierFunctionWizardPage_11);
		idGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		idGroup.setLayout(new GridLayout(2, false));
		
		// localId
		Control idLabel = null;
		if (identifierType != null) {
			AttributeDefinition def = identifierType.getAttribute("localId"); //$NON-NLS-1$
			if (def != null) {
				idLabel = dlf.createLabel(idGroup, def, false);
			}
		}
		if (idLabel == null) {
			idLabel = new Label(idGroup, SWT.NONE);
			((Label) idLabel).setText("localId"); //$NON-NLS-1$
		}
		idLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		
		Control localId = dlf.createLabel(idGroup, getParent().getSourceItem().getDefinition(), true);
		localId.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		
		// Version group
		Group versGroup = new Group(page, SWT.NONE);
		versGroup.setText(Messages.IdentifierFunctionWizardPage_14);
		versGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		versGroup.setLayout(new GridLayout(2, false));
		
		// Version
		Control versionLabel = null;
		if (identifierType != null) {
			AttributeDefinition def = identifierType.getAttribute("versionId"); //$NON-NLS-1$
			if (def != null) {
				versionLabel = dlf.createLabel(versGroup, def, false);
			}
		}
		if (versionLabel == null) {
			versionLabel = new Label(versGroup, SWT.NONE);
			((Label) versionLabel).setText(Messages.IdentifierFunctionWizardPage_16);
		}
		versionLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		
		this.version = new Text(versGroup, SWT.BORDER);
		this.version.setText(""); //$NON-NLS-1$
		this.version.setEnabled(true);
		this.version.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		// version nil reason
		if (identifierType != null) {
			AttributeDefinition def = identifierType.getAttribute("versionId"); //$NON-NLS-1$
			if (def != null) {
				def = def.getAttributeType().getAttribute("nilReason"); //$NON-NLS-1$
				if (def != null) {
					// label
					Control nilLabel = dlf.createLabel(versGroup, def, false);
					nilLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
					
					// editor
					nilEditor = aef.createEditor(versGroup, def);
					nilEditor.getControl().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
					nilEditor.setAsText("unknown"); // default to unknown //$NON-NLS-1$
				}
			}
		}
	}

	/**
	 * @return the entered Country Code
	 */
	public String getCountryCode() {
		if (this.countryCode != null) {
			return this.countryCode.getText();
		}
		return null;
	}

	/**
	 * @return the entered Provider Name
	 */
	public String getProviderName() {
		if (this.providerName != null) {
			return this.providerName.getText();
		}
		return null;
	}

	/**
	 * @return the entered Product Name
	 */
	public String getProductName() {
		if (this.productName != null) {
			return this.productName.getText();
		}
		return null;
	}
	
	/**
	 * @return the entered version
	 */
	public String getVersion() {
		if (this.version != null) {
			return this.version.getText();
		}
		return null;
	}

	/**
	 * @return the version nil reason
	 */
	public String getVersionNilReason() {
		if (nilEditor != null) {
			return nilEditor.getAsText();
		}
		
		return null;
	}

}
