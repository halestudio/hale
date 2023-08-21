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
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.json.JsonInstanceReader;
import eu.esdihumboldt.hale.io.json.ui.util.EnumJSONAutoOrderTypes;
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
@SuppressWarnings("restriction")
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
	 * @see HaleWizardPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);

		page.setLayout(new GridLayout(2, false));
		GridData layoutData = new GridData();
		layoutData.widthHint = 200;
		
		if (firstShow) {

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
					if (element instanceof EnumJSONAutoOrderTypes) {
						return ((EnumJSONAutoOrderTypes) element).getJsonModeOrder();
					}
					return super.getText(element);
				}

			});
			readModeOrderCombo.setInput(EnumJSONAutoOrderTypes.class);
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
			forceUsageOfDefaultSelectedType
					.setLayoutData(GridDataFactory.fillDefaults().grab(false, false).span(2, 1).create());
			forceUsageOfDefaultSelectedType.setText("Force usage of the default type to be used for all instances");

			forceUsageOfDefaultSelectedType.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					if (!forceUsageOfDefaultSelectedType.getSelection()) {
						setMessage("All the types will be mapped automatically to the selected feature type.",
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

			selectorFeatureTypes = new TypeDefinitionSelector(page, "Select the corresponding feature type",
					getWizard().getProvider().getSourceSchema(), null);
			selectorFeatureTypes.getControl()
					.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(2, 1).create());

			selectorFeatureTypes.addSelectionChangedListener(new ISelectionChangedListener() {

				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					TypeDefinition selectedObject = selectorFeatureTypes.getSelectedObject();
					if (selectedObject == null) {
						forceUsageOfDefaultSelectedType.setSelection(false);
					} else {
						forceUsageOfDefaultSelectedType.setSelection(true);

						setTypeLabel.getParent().layout();
					}
					validateSelection();
					selectorFeatureTypes.getControl().requestLayout();
				}
			});

			page.layout();
			page.pack();
		}

		setDefaultOptions();
		setPageComplete(false);
	}

	/**
	 * Set default options to the checkbox based on if the user navigated from
	 * single file import or multiple file import.
	 */
	private void setDefaultOptions() {
		forceUsageOfDefaultSelectedType.setSelection(false);
		selectorFeatureTypes.setSelection(StructuredSelection.EMPTY);
	}

	/**
	 * Validate the current selection. {@link #onShowPage(boolean)} must have been
	 * called first to set {@link #lastType}.
	 */
	protected void validateSelection() {
		TypeDefinition selected = selectorFeatureTypes.getSelectedObject();
		ISelection readModeOrderComboSelection = readModeOrderCombo.getSelection();
		boolean forceSelection = forceUsageOfDefaultSelectedType.getSelection();

		if (!readModeOrderComboSelection.isEmpty()) {
			if ((selected != null && forceSelection) || (selected == null && !forceSelection)) {
				setPageComplete(true);
				setMessage("All the types will be mapped automatically to the selected feature types",
						DialogPage.INFORMATION);
			} else {
				setPageComplete(false);
				setMessage("Select one feature type", DialogPage.INFORMATION);
			}
		} else {
			setPageComplete(false);
			setMessage("Select the most appropriate read mode", DialogPage.INFORMATION);
		}

		return;
	}

	/**
	 * @see HaleWizardPage#createContent(Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		this.page = page;

		setPageComplete(false);
	}

	/**
	 * @see IOWizardPage#updateConfiguration(IOProvider)
	 */
	@Override
	public boolean updateConfiguration(InstanceReader provider) {
		if (readModeOrderCombo.getSelection() != null) {
			provider.setParameter(JsonInstanceReader.PARAM_READ_MODE, Value.of(readModeOrderCombo.getSelection()));
		}
		provider.setParameter(JsonInstanceReader.PARAM_FORCE_DEFAULT_TYPE,
				Value.of(forceUsageOfDefaultSelectedType.getSelection()));

		// make sure if the selection box is empty and autoDetect is not checked
		// then the user should be able to finish the wizard.
		if ((!forceUsageOfDefaultSelectedType.getSelection() && selectorFeatureTypes.getSelectedObject() == null)
				|| (forceUsageOfDefaultSelectedType.getSelection()
						&& selectorFeatureTypes.getSelectedObject() != null)) {
			if (selectorFeatureTypes.getSelectedObject() != null) {
				QName name = selectorFeatureTypes.getSelectedObject().getName();
				provider.setParameter(JsonInstanceReader.PARAM_FORCE_DEFAULT_TYPE, Value.of(name.toString()));
			}
		} else {
			return false;
		}

		return true;
	}

}
