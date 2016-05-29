/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */
package eu.esdihumboldt.hale.ui.functions.inspire;

import java.util.Set;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.google.common.base.Objects;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.cst.functions.inspire.IdentifierFunction;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameterDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyFunctionDefinition;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.common.AttributeEditor;
import eu.esdihumboldt.hale.ui.common.definition.AttributeEditorFactory;
import eu.esdihumboldt.hale.ui.common.definition.DefinitionLabelFactory;
import eu.esdihumboldt.hale.ui.function.generic.AbstractGenericFunctionWizard;
import eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage;
import eu.esdihumboldt.hale.ui.functions.inspire.internal.Messages;

/**
 * Wizard page for the inspire identifier function
 * 
 * @author Thorsten Reitz
 * @author Kevin Mais
 */
public class IdentifierParameterPage extends HaleWizardPage<AbstractGenericFunctionWizard<?, ?>>
		implements ParameterPage, IdentifierFunction {

	private Text countryCode = null;

	private Text providerName = null;

	private Text productName = null;

	private Text version = null;

	private AttributeEditor<?> nilEditor;

	private String initialCountry;
	private String initialProvider;
	private String initialProduct;
	private String initialVersion;
	private String initialVersionNil;

	/**
	 * The main composite of the page
	 */
	private Composite page;

	/**
	 * The parent composite of the dialog page
	 */
	private Composite parent;

	/**
	 * The target entity when {@link #createContent(Composite)} was last called.
	 */
	private EntityDefinition lastEntity;

	/**
	 * /* Default Constructor
	 */
	public IdentifierParameterPage() {
		super("identifier", "Inspire Identifier", null);

		setDescription(Messages.IdentifierFunctionWizardPage_0);

		setPageComplete(false);
	}

	@Override
	public void setParameter(Set<FunctionParameterDefinition> params,
			ListMultimap<String, ParameterValue> initialValues) {
		if (initialValues != null) {
			initialCountry = initialValues.get(COUNTRY_PARAMETER_NAME).get(0).as(String.class);
			initialProvider = initialValues.get(DATA_PROVIDER_PARAMETER_NAME).get(0)
					.as(String.class);
			initialProduct = initialValues.get(PRODUCT_PARAMETER_NAME).get(0).as(String.class);
			initialVersion = initialValues.get(VERSION).get(0).as(String.class);
			initialVersionNil = initialValues.get(VERSION_NIL_REASON).get(0).as(String.class);
		}
		else {
			initialCountry = "";
			initialProvider = "";
			initialProduct = "";
			initialVersion = "";
			initialVersionNil = "";
		}
	}

	@Override
	public ListMultimap<String, ParameterValue> getConfiguration() {
		ListMultimap<String, ParameterValue> configuration = ArrayListMultimap.create(5, 1);

		configuration.put(COUNTRY_PARAMETER_NAME, new ParameterValue(countryCode.getText()));
		configuration.put(DATA_PROVIDER_PARAMETER_NAME, new ParameterValue(providerName.getText()));
		configuration.put(PRODUCT_PARAMETER_NAME, new ParameterValue(productName.getText()));
		configuration.put(VERSION, new ParameterValue(version.getText()));
		configuration.put(VERSION_NIL_REASON, new ParameterValue(nilEditor.getAsText()));

		return configuration;
	}

	/**
	 * @see HaleWizardPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);

		if (parent != null && !Objects.equal(lastEntity, determineTargetEntity())) {
			// recreate the page if the target entity has changed
			createContent(parent);
		}
	}

	@Override
	protected void createContent(Composite parent) {
		boolean relayout = this.parent != null;
		this.parent = parent;

		lastEntity = determineTargetEntity();

		if (lastEntity == null) {
			setPageComplete(false);
			return; // can't create controls
		}

		if (page != null) {
			// page was created before
			initialCountry = countryCode.getText();
			initialProvider = providerName.getText();
			initialProduct = productName.getText();
			initialVersion = version.getText();
			initialVersionNil = nilEditor.getAsText();

			page.dispose();
		}

		PropertyFunctionDefinition function = FunctionUtil.getPropertyFunction(ID,
				HaleUI.getServiceProvider());

		// create a composite to hold the widgets
		page = new Composite(parent, SWT.NULL);
		setControl(page);
		// create layout for this wizard page
		GridLayout gl = GridLayoutFactory.fillDefaults().create();
		page.setLayout(gl);

		AttributeEditorFactory aef = PlatformUI.getWorkbench()
				.getService(AttributeEditorFactory.class);
		DefinitionLabelFactory dlf = PlatformUI.getWorkbench()
				.getService(DefinitionLabelFactory.class);

		// identifier type
		TypeDefinition identifierType = null;
		Definition<?> def = lastEntity.getDefinition();

		if (def instanceof PropertyDefinition) {
			TypeDefinition typeDef = ((PropertyDefinition) def).getPropertyType();
			for (ChildDefinition<?> child : typeDef.getChildren()) {
				if (child.asProperty() != null && child.asProperty().getPropertyType().getName()
						.getLocalPart().equals("IdentifierType")) {
					identifierType = child.asProperty().getPropertyType();
				}
			}
		}

		// Namespace group
		Group nsGroup = new Group(page, SWT.NONE);
		nsGroup.setText(Messages.IdentifierFunctionWizardPage_2);
		nsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		nsGroup.setLayout(GridLayoutFactory//
				.swtDefaults()//
				.numColumns(2)//
				.spacing(8, 4)//
				.create());//

		// localId
		if (identifierType != null) {
			PropertyDefinition propDef = null; // $NON-NLS-1$
			for (ChildDefinition<?> child : identifierType.getChildren()) {
				String localName = child.getName().getLocalPart();
				if (localName.equals("namespace")) {
					if (child.asProperty() != null) {
						propDef = child.asProperty();
					}
				}
			}
			if (propDef != null) {
				Control nsLabel = dlf.createLabel(nsGroup, propDef, false);
				nsLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));

				Label nsDesc = new Label(nsGroup, SWT.NONE);
				nsDesc.setText(Messages.IdentifierFunctionWizardPage_4);
			}
		}

		// Country code
		Label ccLabel = new Label(nsGroup, SWT.NONE);
		ccLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		ccLabel.setText(Messages.IdentifierFunctionWizardPage_5);

		FunctionParameterDefinition param = function.getParameter(COUNTRY_PARAMETER_NAME);
		configureParameterLabel(ccLabel, param);

		this.countryCode = new Text(nsGroup, SWT.BORDER | SWT.SINGLE);
		this.countryCode.setText(initialCountry); // $NON-NLS-1$
		this.countryCode.setEnabled(true);
		this.countryCode.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// Provider name
		Label providerLabel = new Label(nsGroup, SWT.NONE);
		providerLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		providerLabel.setText(Messages.IdentifierFunctionWizardPage_7);

		param = function.getParameter(DATA_PROVIDER_PARAMETER_NAME);
		configureParameterLabel(providerLabel, param);

		this.providerName = new Text(nsGroup, SWT.BORDER | SWT.SINGLE);
		this.providerName.setText(initialProvider); // $NON-NLS-1$
		this.providerName.setEnabled(true);
		this.providerName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// Product name
		Label productLabel = new Label(nsGroup, SWT.NONE);
		productLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		productLabel.setText(Messages.IdentifierFunctionWizardPage_9);

		param = function.getParameter(PRODUCT_PARAMETER_NAME);
		configureParameterLabel(productLabel, param);

		this.productName = new Text(nsGroup, SWT.BORDER | SWT.SINGLE);
		this.productName.setText(initialProduct); // $NON-NLS-1$
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
			PropertyDefinition propDef = null; // $NON-NLS-1$
			for (ChildDefinition<?> child : identifierType.getChildren()) {
				String LocalName = child.getName().getLocalPart();
				if (LocalName.equals("localId")) {
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
		Control localId = dlf.createLabel(idGroup, getWizard().getUnfinishedCell().getSource()
				.get(null).get(0).getDefinition().getDefinition(), true);
		localId.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// Version group
		Group versGroup = new Group(page, SWT.NONE);
		versGroup.setText(Messages.IdentifierFunctionWizardPage_14);
		versGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		versGroup.setLayout(new GridLayout(2, false));

		// Version
		Control versionLabel = null;
		if (identifierType != null) {
			PropertyDefinition propDef = null; // $NON-NLS-1$
			for (ChildDefinition<?> child : identifierType.getChildren()) {
				String localName = child.getName().getLocalPart();
				if (localName.equals("versionId")) {
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
			((Label) versionLabel).setText(Messages.IdentifierFunctionWizardPage_16);
		}
		versionLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));

		this.version = new Text(versGroup, SWT.BORDER | SWT.SINGLE);
		this.version.setText(initialVersion); // $NON-NLS-1$
		this.version.setEnabled(true);
		this.version.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// version nil reason
		if (identifierType != null) {
			PropertyDefinition propDef = null; // $NON-NLS-1$
			for (ChildDefinition<?> child : identifierType.getChildren()) {
				String localName = child.getName().getLocalPart();
				if (localName.equals("versionId")) {
					if (child.asProperty() != null) {
						propDef = child.asProperty();
					}
				}
			}
			if (propDef != null) {
				for (ChildDefinition<?> child : propDef.getPropertyType().getChildren()) {
					String localName = child.getName().getLocalPart();
					if (localName.equals("nilReason")) {
						if (child.asProperty() != null) {
							propDef = child.asProperty();
						}
					}
				}

			}
			if (propDef != null) {
				// label
				Control nilLabel = dlf.createLabel(versGroup, propDef, false);
				nilLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));

				// editor
				nilEditor = aef.createEditor(versGroup, propDef, null, false);
				nilEditor.getControl()
						.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
				nilEditor.setAsText(initialVersionNil); // default to unknown
														// //$NON-NLS-1$
			}

		}

		if (relayout) {
			parent.layout();
			getContainer().getShell().pack();
		}

		setPageComplete(true);
	}

	/**
	 * Configure a label representing a parameter.
	 * 
	 * @param paramLabel the parameter label
	 * @param param the associated function parameter
	 */
	private void configureParameterLabel(Label paramLabel, FunctionParameterDefinition param) {
		if (param != null) {
			String name = param.getDisplayName();
			if (name != null && !name.isEmpty()) {
				paramLabel.setText(name);
			}

			String descr = param.getDescription();
			if (descr != null && !descr.isEmpty()) {
				ControlDecoration dec = new ControlDecoration(paramLabel, SWT.RIGHT);
				dec.setDescriptionText(descr);
				FieldDecoration fd = FieldDecorationRegistry.getDefault()
						.getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION);
				dec.setImage(fd.getImage());
			}
		}
	}

	/**
	 * Determine the target entity.
	 * 
	 * @return the target entity or <code>null</code> if it is not set
	 */
	private EntityDefinition determineTargetEntity() {
		try {
			return getWizard().getUnfinishedCell().getTarget().get(null).get(0).getDefinition();
		} catch (Exception e) {
			return null;
		}
	}

}
