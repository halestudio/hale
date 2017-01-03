/*
 * Copyright (c) 2016 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.ui.io.instance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.instance.io.InstanceValidator;

/**
 * Selection dialog for instance validators
 * 
 * @author Florian Esser
 */
public class ValidatorSelectionDialog extends Dialog {

	private final List<Object> input = new ArrayList<>();
	private IContentType contentType;
	private ComboViewer validators;
	private ISelection selection;

	/**
	 * @param parentShell parent shell
	 */
	protected ValidatorSelectionDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * @param parentShell parent shell
	 */
	public ValidatorSelectionDialog(IShellProvider parentShell) {
		super(parentShell);
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);

		GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL
				| GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
		data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
		composite.setLayoutData(data);

		Label label = new Label(composite, SWT.NONE);
		label.setText("Please select a validator if you want to validate the exported file");
		label.setLayoutData(GridDataFactory.swtDefaults().span(3, 1)
				.align(SWT.BEGINNING, SWT.BEGINNING).create());

		validators = new ComboViewer(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
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

		validators.setInput(input);

		validators.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				selection = event.getSelection();
			}
		});

		return composite;
	}

	private void updateInput() {
		// populate input
		if (contentType != null) {
			Collection<IOProviderDescriptor> factories = HaleIO
					.getProviderFactories(InstanceValidator.class);
			factories = HaleIO.filterFactories(factories, contentType);
			input.clear();
			input.addAll(factories);

			if (validators != null) {
				validators.setInput(input);
			}
		}

	}

	/**
	 * @return the contentType
	 */
	public IContentType getContentType() {
		return contentType;
	}

	/**
	 * @param contentType the contentType to set
	 */
	public void setContentType(IContentType contentType) {
		this.contentType = contentType;
		updateInput();
	}

	/**
	 * @return the current selection
	 */
	public IOProviderDescriptor getSelection() {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) selection;
			Object element = sel.getFirstElement();
			if (element instanceof IOProviderDescriptor) {
				return (IOProviderDescriptor) element;
			}
		}

		return null;
	}

}
