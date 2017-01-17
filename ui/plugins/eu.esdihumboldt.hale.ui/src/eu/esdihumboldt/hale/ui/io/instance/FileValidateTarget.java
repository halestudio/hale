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
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.core.io.supplier.Locatable;
import eu.esdihumboldt.hale.common.instance.io.InstanceValidator;
import eu.esdihumboldt.hale.common.instance.io.InstanceWriter;
import eu.esdihumboldt.hale.io.validation.ProjectValidator;
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

	private final List<ValidatorEntry> validators = new ArrayList<>();
	private TableViewer validatorsTableViewer;
	private Button configureValidatorButton;
	private Button addValidatorButton;
	private Button removeValidatorButton;
	private SelectionListener configureButtonListener;

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
				.setLayout(GridLayoutFactory.swtDefaults().numColumns(3).margins(10, 8).create());

		// Add project validator by default

		IOProviderDescriptor pvDescriptor = HaleIO.findIOProviderFactory(IOProvider.class,
				getWizard().getContentType(), ProjectValidator.PROVIDER_ID);

		if (pvDescriptor != null) {
			ProjectValidator projectValidator = new ProjectValidator();
			List<? extends Locatable> schemas = getWizard().getProvider().getValidationSchemas();
			projectValidator.setSchemas(schemas.toArray(new Locatable[schemas.size()]));

			ValidatorEntry entry = new ValidatorEntry(projectValidator, pvDescriptor);
			validators.add(entry);

			getWizard().addValidator(projectValidator);
		}

		// viewer with validator list

		Composite tableContainer = new Composite(validatorGroup, SWT.NONE);
		tableContainer.setLayoutData(GridDataFactory.swtDefaults().span(3, 1).minSize(100, 100)
				.align(SWT.FILL, SWT.FILL).grab(true, true).create());
		TableColumnLayout layout = new TableColumnLayout();
		tableContainer.setLayout(layout);

		validatorsTableViewer = new TableViewer(tableContainer,
				SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER);
		validatorsTableViewer.getTable().setLinesVisible(true);
		validatorsTableViewer.getTable().setHeaderVisible(true);
		validatorsTableViewer.setContentProvider(ArrayContentProvider.getInstance());
		validatorsTableViewer.setInput(validators);

		TableViewerColumn typeColumn = new TableViewerColumn(validatorsTableViewer, SWT.NONE);
		layout.setColumnData(typeColumn.getColumn(), new ColumnWeightData(4));
		typeColumn.getColumn().setText("Validator");
		typeColumn.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				ValidatorEntry validator = (ValidatorEntry) element;
				return validator.getDescriptor().getDisplayName();
			}

			/**
			 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getImage(java.lang.Object)
			 */
			@Override
			public Image getImage(Object element) {
				ValidatorEntry validator = (ValidatorEntry) element;
				if (validator.getStatusMessage() != null
						&& !validator.getStatusMessage().isEmpty()) {
					return PlatformUI.getWorkbench().getSharedImages()
							.getImage(ISharedImages.IMG_DEC_FIELD_ERROR);
				}

				return null;
			}

		});

		TableViewerColumn errorColumn = new TableViewerColumn(validatorsTableViewer, SWT.NONE);
		layout.setColumnData(errorColumn.getColumn(), new ColumnWeightData(3));
		errorColumn.getColumn().setText("Status");
		errorColumn.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				ValidatorEntry validator = (ValidatorEntry) element;
				String message = validator.getStatusMessage();
				if (message == null || message.trim().isEmpty()) {
					message = "OK";
				}

				return message;
			}
		});

		validatorsTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateWizard(getSelectedValidator(event.getSelection()));
			}
		});

		// Selection dialog for adding validators to the list

		final ValidatorSelectionDialog validatorSelectionDialog = new ValidatorSelectionDialog(
				PlatformUI.getWorkbench().getDisplay().getActiveShell());
		addValidatorButton = new Button(validatorGroup, SWT.PUSH);
		addValidatorButton.setText("Add validator...");
		addValidatorButton.setEnabled(false);
		addValidatorButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				validatorSelectionDialog.create();
				validatorSelectionDialog.setContentType(getWizard().getContentType());
				validatorSelectionDialog.open();

				IOProviderDescriptor selection = validatorSelectionDialog.getSelection();
				addValidator(selection);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		// Remove button

		removeValidatorButton = new Button(validatorGroup, SWT.PUSH);
		removeValidatorButton.setText("Remove validator...");
		removeValidatorButton.setEnabled(false);
		removeValidatorButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ValidatorEntry selection = getSelectedValidator();
				getWizard().removeValidator(selection.getValidator());
				validators.remove(selection);
				updateWizard(getSelectedValidator());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		// Configure button

		configureValidatorButton = new Button(validatorGroup, SWT.PUSH);
		configureValidatorButton.setText("Configure validator...");
		configureValidatorButton.setEnabled(false);

		getWizard().addIOWizardListener(this);
	}

	@Override
	public void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);
		updateState();
	}

	@Override
	public void contentTypeChanged(IContentType contentType) {
		updateState();
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

	private void addValidator(IOProviderDescriptor selection) {
		InstanceValidator validator;
		try {
			validator = (InstanceValidator) selection.createExtensionObject();

			// set schemas
			List<? extends Locatable> schemas = getWizard().getProvider().getValidationSchemas();
			validator.setSchemas(schemas.toArray(new Locatable[schemas.size()]));
			getWizard().addValidator(validator);

			ValidatorEntry entry = new ValidatorEntry(validator, selection);
			validators.add(entry);

			updateWizard(getSelectedValidator());
		} catch (Exception e) {
			this.getPage().setErrorMessage("Could not instantiate validator.");
			log.error(MessageFormat.format("Could not instantiate validator {0}",
					selection.getIdentifier(), e));
		}
	}

	/**
	 * Update the wizard
	 * 
	 * @param entry the current selection
	 */
	private void updateWizard(final ValidatorEntry entry) {
		configureValidatorButton.setEnabled(false);
		if (this.configureButtonListener != null) {
			configureValidatorButton.removeSelectionListener(configureButtonListener);
			configureButtonListener = null;
		}

		updateState();

		if (entry == null) {
			removeValidatorButton.setEnabled(false);
			return;
		}

		removeValidatorButton.setEnabled(true);

		final ConfigurationDialogFactory configDialogFactory = ConfigurationDialogExtension
				.getInstance().getConfigurationDialog(entry.getDescriptor());

		if (configDialogFactory != null) {
			configureValidatorButton.setEnabled(true);
			configureButtonListener = new SelectionListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void widgetSelected(SelectionEvent e) {
					try {
						@SuppressWarnings("rawtypes")
						AbstractConfigurationDialog configDialog = configDialogFactory
								.createExtensionObject();

						configDialog.setProvider(entry.getValidator());
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
			configureValidatorButton.addSelectionListener(configureButtonListener);
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.target.FileTarget#updateState()
	 */
	@Override
	protected void updateState() {
		if (addValidatorButton != null) {
			IContentType contentType = getWizard().getContentType();
			Collection<IOProviderDescriptor> factories = new ArrayList<>();
			if (contentType != null) {
				factories.addAll(HaleIO.getProviderFactories(InstanceValidator.class));
				factories = HaleIO.filterFactories(factories, contentType);
			}

			if (factories.isEmpty()) {
				addValidatorButton.setEnabled(false);
			}
			else {
				addValidatorButton.setEnabled(true);
			}
		}

		if (validatorsTableViewer != null) {
			validatorsTableViewer.setInput(validators);
		}

		checkValidatorConfigurations();
		super.updateState();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.target.FileTarget#isValid()
	 */
	@Override
	protected boolean isValid() {
		return super.isValid() && checkValidatorConfigurations();
	}

	private ValidatorEntry getSelectedValidator() {
		return getSelectedValidator(validatorsTableViewer.getSelection());
	}

	private ValidatorEntry getSelectedValidator(ISelection selection) {
		if (selection == null || selection.equals(StructuredSelection.EMPTY)) {
			return null;
		}

		if (selection instanceof StructuredSelection) {
			Object selectedObject = ((StructuredSelection) selection).getFirstElement();
			if (selectedObject instanceof ValidatorEntry) {
				return (ValidatorEntry) selectedObject;
			}
		}

		return null;
	}

	/**
	 * Checks if the given {@link InstanceValidator}s are properly configured.
	 * If not, an error message is displayed and the owning {@link WizardPage}
	 * is told that is not complete.
	 * 
	 * @return true if all validator are properly configured
	 */
	private boolean checkValidatorConfigurations() {
		if (validators == null) {
			return true;
		}

		boolean allValid = true;
		for (ValidatorEntry validator : validators) {
			try {
				validator.getValidator().validate();
				validator.setStatusMessage(null);
			} catch (IOProviderConfigurationException e) {
				validator.setStatusMessage(e.getMessage());
				allValid = false;
			}
		}

		if (!allValid) {
			getPage().setErrorMessage("Validators are not configured properly.");
		}
		else {
			getPage().setErrorMessage(null);
		}

		if (validatorsTableViewer != null) {
			validatorsTableViewer.refresh();
		}

		return allValid;
	}

	private class ValidatorEntry {

		private final InstanceValidator validator;
		private final IOProviderDescriptor descriptor;
		private String statusMessage;

		/**
		 * Creates a new entry for the validator table
		 * 
		 * @param validator the {@link InstanceValidator}
		 * @param descriptor the {@link IOProviderDescriptor} of this validator
		 */
		public ValidatorEntry(InstanceValidator validator, IOProviderDescriptor descriptor) {
			this.validator = validator;
			this.descriptor = descriptor;
		}

		/**
		 * @return the {@link InstanceValidator} of this entry
		 */
		public InstanceValidator getValidator() {
			return validator;
		}

		/**
		 * @return the {@link IOProviderDescriptor} of this entry
		 */
		public IOProviderDescriptor getDescriptor() {
			return descriptor;
		}

		/**
		 * @return the status message (can be null)
		 */
		public String getStatusMessage() {
			return statusMessage;
		}

		/**
		 * @param statusMessage the status message to set
		 */
		public void setStatusMessage(String statusMessage) {
			this.statusMessage = statusMessage;
		}
	}
}
