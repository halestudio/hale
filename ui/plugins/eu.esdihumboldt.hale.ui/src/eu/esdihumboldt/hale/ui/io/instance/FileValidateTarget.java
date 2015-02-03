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
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.instance.io.InstanceValidator;
import eu.esdihumboldt.hale.common.instance.io.InstanceWriter;
import eu.esdihumboldt.hale.ui.io.IOWizardListener;
import eu.esdihumboldt.hale.ui.io.target.FileTarget;

/**
 * File target that adds instance validation. Only usable for instance export.
 * 
 * @author Simon Templer
 */
public class FileValidateTarget extends FileTarget<InstanceWriter> implements
		IOWizardListener<InstanceWriter, InstanceExportWizard> {

	private ComboViewer validators;

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
		validatorGroup.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).margins(10, 8)
				.create());

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
			input.add((contentType == null) ? ("Unrecognized content type") : (MessageFormat
					.format("No validator available for {0}", contentType.getName())));
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
		if (selection.isEmpty()) {
			getWizard().setValidatorFactory(null);
		}
		else if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) selection;
			Object element = sel.getFirstElement();
			if (element instanceof IOProviderDescriptor) {
				getWizard().setValidatorFactory((IOProviderDescriptor) element);
			}
			else {
				// element that disables validating
				getWizard().setValidatorFactory(null);
			}
		}
	}

}
