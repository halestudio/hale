/*
 * Copyright (c) 2015 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.io.instance;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.core.io.supplier.Locatable;
import eu.esdihumboldt.hale.common.instance.io.InstanceValidator;
import eu.esdihumboldt.hale.common.instance.io.InstanceWriter;
import eu.esdihumboldt.hale.ui.io.IOWizardListener;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationDialog;
import eu.esdihumboldt.hale.ui.io.config.ConfigurationDialogExtension;
import eu.esdihumboldt.hale.ui.io.config.ConfigurationDialogFactory;
import eu.esdihumboldt.hale.ui.io.target.FileTarget;

/**
 * File target that adds instance validation. Only usable for instance export.
 * 
 * @author Simon Templer
 */
public class FileValidateTarget extends FileTarget<InstanceWriter>
		implements IOWizardListener<InstanceWriter, InstanceExportWizard> {

	private static final ALogger log = ALoggerFactory.getLogger(FileValidateTarget.class);

	private ComboViewer validators;
	private Button configureButton;
	private SelectionListener configureButtonListener;
	private ControlDecoration configureButtonDecoration;

	@Override
	public InstanceExportWizard getWizard() {
		return (InstanceExportWizard) super.getWizard();
	}

	@Override
	public void createControls(Composite parent) {
		super.createControls(parent);
		// page has a 3-column grid layout

		Group validatorGroup = new Group(parent, SWT.NONE);
		validatorGroup.setText("Validation");
		validatorGroup.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING)
				.grab(true, false).span(3, 1).indent(8, 10).create());
		validatorGroup
				.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).margins(10, 8).create());

		Label vabel = new Label(validatorGroup, SWT.NONE);
		vabel.setText("Please select a validator if you want to validate the exported file");
		vabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));

		validators = new ComboViewer(validatorGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		validators.getControl().setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		validators.setContentProvider(ArrayContentProvider.getInstance());
		validators.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof IOProviderDescriptor) {
					return ((IOProviderDescriptor) element).getDisplayName();
				}
				return super.getText(element);
			}

		});

		configureButton = new Button(validatorGroup, SWT.PUSH);
		configureButton.setText("Configure validator...");
		configureButton.setEnabled(false);

		configureButtonDecoration = new ControlDecoration(configureButton, SWT.RIGHT | SWT.TOP);
		configureButtonDecoration.setDescriptionText("Please configure the selected validator");
		FieldDecoration errorDecoration = FieldDecorationRegistry.getDefault()
				.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
		configureButtonDecoration.setImage(errorDecoration.getImage());
		configureButtonDecoration.hide();

		updateInput();

		// process selection changes
		validators.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				updateWizard(selection);
			}
		});

		getWizard().addIOWizardListener(this);
	}

	@Override
	public void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);

		updateInput();
	}

	@Override
	public void contentTypeChanged(IContentType contentType) {
		updateInput();
	}

	@Override
	public void providerDescriptorChanged(IOProviderDescriptor providerFactory) {
		// do nothing
	}

	@Override
	public void dispose() {
		getWizard().removeIOWizardListener(this);

		super.dispose();
	}

	/**
	 * Update the input of the validator combo viewer
	 */
	private void updateInput() {
		// remember selection
		ISelection lastSelection = validators.getSelection();

		// populate input
		List<Object> input = new ArrayList<Object>();

		IContentType contentType = getWizard().getContentType();
		if (contentType != null) {
			Collection<IOProviderDescriptor> factories = HaleIO
					.getProviderFactories(InstanceValidator.class);
			factories = HaleIO.filterFactories(factories, contentType);
			if (!factories.isEmpty()) {
				input.add("No validation");
			}
			input.addAll(factories);
		}

		if (input.isEmpty()) {
			input.add((contentType == null) ? ("Unrecognized content type")
					: (MessageFormat.format("No validator available for {0}",
							contentType.getName())));
			validators.getControl().setEnabled(false);
		}
		else {
			validators.getControl().setEnabled(true);
		}

		validators.setInput(input);

		ISelection newSelection = new StructuredSelection(input.get(0));

		// retain old selection if possible
		if (!lastSelection.isEmpty() && lastSelection instanceof IStructuredSelection) {
			Object selected = ((IStructuredSelection) lastSelection).getFirstElement();
			if (input.contains(selected)) {
				newSelection = lastSelection;
			}
		}

		validators.setSelection(newSelection, true);

		// process current selection
		ISelection selection = validators.getSelection();
		updateWizard(selection);
	}

	/**
	 * Update the wizard
	 * 
	 * @param selection the current selection
	 */
	private void updateWizard(ISelection selection) {
		configureButton.setEnabled(false);
		if (this.configureButtonListener != null) {
			configureButton.removeSelectionListener(configureButtonListener);
			configureButtonListener = null;
		}
		getPage().setErrorMessage(null);

		if (selection.isEmpty()) {
			getWizard().setValidator(null);
		}
		else if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) selection;
			Object element = sel.getFirstElement();
			if (element instanceof IOProviderDescriptor) {
				InstanceValidator validator = getWizard().getValidator();
				if (validator == null || !((IOProviderDescriptor) element).getProviderType()
						.isInstance(validator)) {
					try {
						validator = (InstanceValidator) ((IOProviderDescriptor) element)
								.createExtensionObject();

						// configure validator
						List<? extends Locatable> schemas = getWizard().getProvider()
								.getValidationSchemas();
						validator.setSchemas(schemas.toArray(new Locatable[schemas.size()]));
					} catch (Exception e) {
						this.getPage().setErrorMessage("Could not instantiate validator.");
						log.error(MessageFormat.format("Could not instantiate validator {0}",
								((IOProviderDescriptor) element).getIdentifier()), e);
					}

					getWizard().setValidator(validator);
				}

				final ConfigurationDialogFactory configDialogFactory = ConfigurationDialogExtension
						.getInstance().getConfigurationDialog((IOProviderDescriptor) element);

				if (configDialogFactory != null) {
					configureButton.setEnabled(true);
					configureButtonListener = new SelectionListener() {

						@SuppressWarnings("unchecked")
						@Override
						public void widgetSelected(SelectionEvent e) {
							try {
								@SuppressWarnings("rawtypes")
								AbstractConfigurationDialog configDialog = configDialogFactory
										.createExtensionObject();

								InstanceValidator validator = getWizard().getValidator();
								configDialog.setProvider(validator);
								configDialog.create();
								configDialog.open();
								updateState();
							} catch (Exception ex) {
								throw new RuntimeException(ex.getMessage(), ex);
							}
						}

						@Override
						public void widgetDefaultSelected(SelectionEvent e) {
							widgetSelected(e);
						}
					};
					configureButton.addSelectionListener(configureButtonListener);
				}
			}
			else {
				// element that disables validating
				getWizard().setValidator(null);
			}
		}

		updateState();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.target.FileTarget#updateState()
	 */
	@Override
	protected void updateState() {
		checkValidatorConfiguration(getWizard().getValidator());
		super.updateState();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.target.FileTarget#isValid()
	 */
	@Override
	protected boolean isValid() {
		return super.isValid() && checkValidatorConfiguration(getWizard().getValidator());
	}

	/**
	 * Checks if the given {@link InstanceValidator} is properly configured. If
	 * not, an error message is displayed and the owning {@link WizardPage} is
	 * told that is not complete.
	 * 
	 * @param validator {@link InstanceValidator} to check
	 */
	private boolean checkValidatorConfiguration(InstanceValidator validator) {
		getPage().setErrorMessage(null);
		if (configureButtonDecoration != null) {
			configureButtonDecoration.hide();
		}

		if (validator == null) {
			return true;
		}

		try {
			validator.validate();
		} catch (IOProviderConfigurationException e) {
			getPage().setErrorMessage(e.getMessage());
			if (configureButtonDecoration != null) {
				configureButtonDecoration.show();
			}
			return false;
		}

		return true;
	}

}
