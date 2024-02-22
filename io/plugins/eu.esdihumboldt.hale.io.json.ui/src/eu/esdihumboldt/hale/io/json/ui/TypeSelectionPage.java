/*
 * Copyright (c) 2022 wetransform GmbH
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

package eu.esdihumboldt.hale.io.json.ui;

import javax.xml.namespace.QName;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.json.JsonInstanceReader;
import eu.esdihumboldt.hale.io.json.internal.JsonReadMode;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.common.definition.selector.TypeDefinitionSelector;
import eu.esdihumboldt.hale.ui.io.IOWizardPage;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;
import eu.esdihumboldt.hale.ui.io.instance.InstanceReaderConfigurationPage;
import eu.esdihumboldt.hale.ui.util.viewer.EnumContentProvider;

/**
 * Configuration page for selecting the feature type for GeoJSON/JSON instances.
 * 
 * @author Emanuela Epure
 */
public class TypeSelectionPage extends InstanceReaderConfigurationPage {

	private Composite page;
	private Button forceUsageOfDefaultSelectedType;
	private TypeDefinitionSelector selectorFeatureTypes;
	private ComboViewer readModeOrderCombo;
	private Label setTypeLabel;

	/**
	 * default constructor
	 */
	public TypeSelectionPage() {
		super("selectType");

		setTitle("Feature type");
		setDescription("Select the Feature types matching your data.");
	}

	/**
	 * @see AbstractConfigurationPage#enable()
	 */
	@Override
	public void enable() {
		// nothing
	}

	/**
	 * @see AbstractConfigurationPage#disable()
	 */
	@Override
	public void disable() {
		// do nothing
	}

	/**
	 * Set default options to the checkbox based on if the user navigated from
	 * single file import or multiple file import.
	 */
	private void setDefaultOptions() {
		// Set the selection to the first element
		final ISelection defaultValue = new StructuredSelection(JsonReadMode.auto);
		readModeOrderCombo.setSelection(defaultValue);

		forceUsageOfDefaultSelectedType.setSelection(false);
		selectorFeatureTypes.setSelection(StructuredSelection.EMPTY);
	}

	/**
	 * Validate the current selection.
	 */
	protected void validateSelection() {
		TypeDefinition selected = selectorFeatureTypes.getSelectedObject();
		JsonReadMode selMode = getSelectedReadMode();
		boolean forceSelection = forceUsageOfDefaultSelectedType.getSelection();

		if (selMode != null) {
			if (selected != null || !forceSelection) {
				setPageComplete(true);
				setMessage("File structure: " + selMode.label, DialogPage.INFORMATION);
			}
			else {
				// if forcing using the default type is enabled, a type needs to
				// be selected
				setPageComplete(false);
				setMessage("Select the feature type to use", DialogPage.WARNING);
			}
		}
		else {
			setPageComplete(false);
			setMessage("What kind of structure has your file?", DialogPage.INFORMATION);
		}

		return;
	}

	/**
	 * @see HaleWizardPage#createContent(Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		this.page = page;
		page.setLayout(new GridLayout(2, false));
		GridData layoutData = new GridData();
		layoutData.widthHint = 200;

		Group autodetectOrderGroup = new Group(page, SWT.NONE);
		autodetectOrderGroup.setText("Choose the most appropriate read mode:");
		GridLayoutFactory.swtDefaults().numColumns(1).applyTo(autodetectOrderGroup);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(autodetectOrderGroup);

		readModeOrderCombo = new ComboViewer(autodetectOrderGroup);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(readModeOrderCombo.getControl());
		readModeOrderCombo.setContentProvider(EnumContentProvider.getInstance());
		readModeOrderCombo.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof JsonReadMode) {
					return ((JsonReadMode) element).getLabel();
				}
				return super.getText(element);
			}

		});
		readModeOrderCombo.setInput(JsonReadMode.class);
		readModeOrderCombo.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				validateSelection();
			}
		});

		// add some space
		new Label(page, SWT.NONE);
		new Label(page, SWT.NONE);

		forceUsageOfDefaultSelectedType = new Button(page, SWT.CHECK);
		forceUsageOfDefaultSelectedType.setLayoutData(
				GridDataFactory.fillDefaults().grab(false, false).span(2, 1).create());
		forceUsageOfDefaultSelectedType
				.setText("Force usage of the default type to be used for all instances");

		forceUsageOfDefaultSelectedType.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!forceUsageOfDefaultSelectedType.getSelection()) {
					setMessage(
							"All the types will be mapped automatically to the selected feature type.",
							DialogPage.INFORMATION);
					selectorFeatureTypes.setSelection(StructuredSelection.EMPTY);
				}
				// reload the selector as sometimes in Mac it doesn't
				// reflect the change.
				selectorFeatureTypes.getControl().requestLayout();
				validateSelection();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// default selection is false.
				selectorFeatureTypes.getControl().setEnabled(true);

			}
		});

		setTypeLabel = new Label(page, SWT.NONE);
		setTypeLabel.setText("Feature type:");

		page.layout();
		page.pack();

		setPageComplete(false);
	}

	/**
	 * @see HaleWizardPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);

		if (firstShow) {
			selectorFeatureTypes = new TypeDefinitionSelector(page,
					"Select the corresponding feature type",
					getWizard().getProvider().getSourceSchema(), null);
			selectorFeatureTypes.getControl().setLayoutData(
					GridDataFactory.fillDefaults().grab(true, false).span(2, 1).create());

			selectorFeatureTypes.addSelectionChangedListener(new ISelectionChangedListener() {

				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					validateSelection();
					selectorFeatureTypes.getControl().requestLayout();
				}
			});

			setDefaultOptions();
		}
	}

	private JsonReadMode getSelectedReadMode() {
		ISelection selection = readModeOrderCombo.getSelection();
		if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
			return (JsonReadMode) ((IStructuredSelection) selection).getFirstElement();
		}
		return null;
	}

	/**
	 * @see IOWizardPage#updateConfiguration(IOProvider)
	 */
	@Override
	public boolean updateConfiguration(InstanceReader provider) {
		JsonInstanceReader jsonInstanceReader = (JsonInstanceReader) provider;

		JsonReadMode selMode = getSelectedReadMode();
		if (selMode == null) {
			selMode = JsonReadMode.auto;
		}
		jsonInstanceReader.setReadMode(selMode);

		boolean forceDefault = forceUsageOfDefaultSelectedType.getSelection();
		jsonInstanceReader.setForceDefaultType(forceDefault);

		if (selectorFeatureTypes.getSelectedObject() != null) {
			QName name = selectorFeatureTypes.getSelectedObject().getName();
			jsonInstanceReader.setDefaultType(name);
		}
		else {
			jsonInstanceReader.setDefaultType(null);
			if (forceDefault) {
				// a type needs to be selected if it is to be forced
				return false;
			}
		}

		return true;
	}

}
