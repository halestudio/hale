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
package eu.esdihumboldt.hale.ui.functions.inspire;

import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.cst.functions.inspire.IdentifierFunction;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameter;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.common.Editor;
import eu.esdihumboldt.hale.ui.common.definition.AttributeEditorFactory;
import eu.esdihumboldt.hale.ui.common.definition.DefinitionLabelFactory;
import eu.esdihumboldt.hale.ui.function.generic.AbstractGenericFunctionWizard;
import eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage;
import eu.esdihumboldt.hale.ui.functions.inspire.internal.Messages;

/**
 * Wizard page for the inspire identifier function
 * 
 * @author Kevin Mais
 */
public class IdentifierParameterPage extends
		HaleWizardPage<AbstractGenericFunctionWizard<?, ?>> implements
		ParameterPage, IdentifierFunction {

	Text countryCode = null;

	Text providerName = null;

	Text productName = null;

	Text version = null;

	private Editor<?> nilEditor;

	private String initialCountry;
	private String initialProvider;
	private String initialProduct;
	private String initialVersion;
	private String initialVersionNil;

	private ListMultimap<String, String> configuration = ArrayListMultimap.create(5, 5);

	/**
	 * /* Default Constructor
	 */
	public IdentifierParameterPage() {
		super("identifier", Messages.IdentifierFunctionWizardPage_0, null);
		setPageComplete(false);
	}

	@Override
	public void setParameter(Set<FunctionParameter> params,
			ListMultimap<String, String> initialValues) {

			initialCountry = initialValues.get(COUNTRY_PARAMETER_NAME).get(0);
			initialProvider = initialValues.get(DATA_PROVIDER_PARAMETER_NAME)
					.get(0);
			initialProduct = initialValues.get(PRODUCT_PARAMETER_NAME).get(0);
			initialVersion = initialValues.get(VERSION).get(0);
			initialVersionNil = initialValues.get(VERSION_NIL_REASON).get(0);

		}


	@Override
	public ListMultimap<String, String> getConfiguration() {
		
		configuration.put(COUNTRY_PARAMETER_NAME, countryCode.getText());
		configuration.put(DATA_PROVIDER_PARAMETER_NAME, providerName.getText());
		configuration.put(PRODUCT_PARAMETER_NAME, productName.getText());
		configuration.put(VERSION, version.getText());
		configuration.put(VERSION_NIL_REASON, nilEditor.getAsText());
		
		return configuration;
	}

	@Override
	protected void createContent(Composite parent) {
		AttributeEditorFactory aef = (AttributeEditorFactory) PlatformUI
				.getWorkbench().getService(AttributeEditorFactory.class);
		DefinitionLabelFactory dlf = (DefinitionLabelFactory) PlatformUI
				.getWorkbench().getService(DefinitionLabelFactory.class);

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
		Definition<?> def = getWizard().getUnfinishedCell().getTarget()
				.get(null).get(0).getDefinition().getDefinition();

		if (def instanceof PropertyDefinition) {
			TypeDefinition typeDef = ((PropertyDefinition) def)
					.getPropertyType();
			for (ChildDefinition<?> child : typeDef.getChildren()) {
				if (child.asProperty() != null
						&& child.asProperty().getPropertyType().getName()
								.getLocalPart().equals("IdentifierType")) {
					identifierType = child.asProperty().getPropertyType();
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
			PropertyDefinition propDef = null; //$NON-NLS-1$
			for (ChildDefinition<?> child : identifierType.getChildren()) {
				String namespace = child.getName().getNamespaceURI().toString();
				if (namespace.equals("namespace")) {
					if (child.asProperty() != null) {
						propDef = child.asProperty();
					}
				}
			}
			if (propDef != null) {
				Control nsLabel = dlf.createLabel(nsGroup, propDef, false);
				nsLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false,
						false));

				Label nsDesc = new Label(nsGroup, SWT.NONE);
				nsDesc.setText(Messages.IdentifierFunctionWizardPage_4);
			}
		}

		// Country code
		Label ccLabel = new Label(nsGroup, SWT.NONE);
		ccLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		ccLabel.setText(Messages.IdentifierFunctionWizardPage_5);

		this.countryCode = new Text(nsGroup, SWT.BORDER);
		this.countryCode.setText(initialCountry); //$NON-NLS-1$
		this.countryCode.setEnabled(true);
		this.countryCode.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));

		// Provider name
		Label providerLabel = new Label(nsGroup, SWT.NONE);
		providerLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false,
				false));
		providerLabel.setText(Messages.IdentifierFunctionWizardPage_7);

		this.providerName = new Text(nsGroup, SWT.BORDER);
		this.providerName.setText(initialProvider); //$NON-NLS-1$
		this.providerName.setEnabled(true);
		this.providerName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false));

		// Product name
		Label productLabel = new Label(nsGroup, SWT.NONE);
		productLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false,
				false));
		productLabel.setText(Messages.IdentifierFunctionWizardPage_9);

		this.productName = new Text(nsGroup, SWT.BORDER);
		this.productName.setText(initialProduct); //$NON-NLS-1$
		this.productName.setEnabled(true);
		this.productName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));

		// Local ID group
		Group idGroup = new Group(page, SWT.NONE);
		idGroup.setText(Messages.IdentifierFunctionWizardPage_11);
		idGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		idGroup.setLayout(new GridLayout(2, false));

		// localId
		Control idLabel = null;
		if (identifierType != null) {
			PropertyDefinition propDef = null; //$NON-NLS-1$
			for (ChildDefinition<?> child : identifierType.getChildren()) {
				String namespace = child.getName().getNamespaceURI().toString();
				if (namespace.equals("localId")) {
					if (child.asProperty() != null) {
						propDef = child.asProperty();
					}
				}
			}
			if (propDef != null) {
				idLabel = dlf.createLabel(idGroup, propDef, false);
			}
		}
		if (idLabel == null) {
			idLabel = new Label(idGroup, SWT.NONE);
			((Label) idLabel).setText("localId"); //$NON-NLS-1$
		}
		idLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		Control localId = dlf.createLabel(idGroup, getWizard()
				.getUnfinishedCell().getSource().get(null).get(0)
				.getDefinition().getDefinition(), true);
		localId.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// Version group
		Group versGroup = new Group(page, SWT.NONE);
		versGroup.setText(Messages.IdentifierFunctionWizardPage_14);
		versGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		versGroup.setLayout(new GridLayout(2, false));

		// Version
		Control versionLabel = null;
		if (identifierType != null) {
			PropertyDefinition propDef = null; //$NON-NLS-1$
			for (ChildDefinition<?> child : identifierType.getChildren()) {
				String namespace = child.getName().getNamespaceURI().toString();
				if (namespace.equals("versionId")) {
					if (child.asProperty() != null) {
						propDef = child.asProperty();
					}
				}
			}
			if (propDef != null) {
				versionLabel = dlf.createLabel(versGroup, propDef, false);
			}
		}
		if (versionLabel == null) {
			versionLabel = new Label(versGroup, SWT.NONE);
			((Label) versionLabel)
					.setText(Messages.IdentifierFunctionWizardPage_16);
		}
		versionLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false,
				false));

		this.version = new Text(versGroup, SWT.BORDER);
		this.version.setText(initialVersion); //$NON-NLS-1$
		this.version.setEnabled(true);
		this.version.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));

		// version nil reason
		if (identifierType != null) {
			PropertyDefinition propDef = null; //$NON-NLS-1$
			for (ChildDefinition<?> child : identifierType.getChildren()) {
				String namespace = child.getName().getLocalPart();
				if (namespace.equals("versionId")) {
					if (child.asProperty() != null) {
						propDef = child.asProperty();
					}
				}
			}
			if (propDef != null) {
				for (ChildDefinition<?> child : propDef.getPropertyType()
						.getChildren()) {
					String namespace = child.getName().getNamespaceURI()
							.toString();
					if (namespace.equals("nilReason")) {
						if (child.asProperty() != null) {
							propDef = child.asProperty();
						}
					}
				}

			}
			if (propDef != null) {
				// label
				Control nilLabel = dlf.createLabel(versGroup, def, false);
				nilLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false,
						false));

				// editor
				nilEditor = aef.createEditor(versGroup, propDef);
				nilEditor.getControl().setLayoutData(
						new GridData(SWT.FILL, SWT.CENTER, true, false));
				nilEditor.setAsText(initialVersionNil); // default to unknown //$NON-NLS-1$
			}

		}
		
		setPageComplete(true);

	}

}
