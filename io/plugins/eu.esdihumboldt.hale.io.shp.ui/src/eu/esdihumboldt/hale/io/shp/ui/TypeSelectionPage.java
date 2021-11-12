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

package eu.esdihumboldt.hale.io.shp.ui;

import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.supplier.FilesIOSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.shp.ShapefileConstants;
import eu.esdihumboldt.hale.io.shp.reader.internal.ShapeInstanceReader;
import eu.esdihumboldt.hale.io.shp.reader.internal.ShapeSchemaReader;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.common.definition.selector.TypeDefinitionSelector;
import eu.esdihumboldt.hale.ui.io.IOWizardPage;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;
import eu.esdihumboldt.hale.ui.io.instance.InstanceReaderConfigurationPage;
import eu.esdihumboldt.util.Pair;

/**
 * Configuration page for selecting the schema type for Shapefile instances.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public class TypeSelectionPage extends InstanceReaderConfigurationPage
		implements ShapefileConstants {

	private TypeDefinitionSelector selector;

	private LocatableInputSupplier<? extends InputStream> lastSource;

	private TypeDefinition lastType;

	private Button matchShortPropertyNames;

	private boolean defaultSelection = false;

	/**
	 * Button to enable auto detection of the schemas. Useful when importing
	 * multiple source data for multiple schema files.
	 */
	private Button autoDetect;

	private Composite page;

	/**
	 * default constructor
	 */
	public TypeSelectionPage() {
		super("selectType");

		setTitle("Schema type");
		setDescription("Select the schema type matching your data.");
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

		if (firstShow) {
			selector = new TypeDefinitionSelector(page, "Select the corresponding schema type",
					getWizard().getProvider().getSourceSchema(), null);
			selector.getControl().setLayoutData(
					GridDataFactory.fillDefaults().grab(true, false).span(1, 1).create());

			selector.addSelectionChangedListener(new ISelectionChangedListener() {

				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					TypeDefinition selectedObject = selector.getSelectedObject();
					// user selected None in the dialog box. Reset to default.
					if (selectedObject == null) {
						autoDetect.setSelection(true);
						autoDetect.setEnabled(true);
						defaultSelection = false;
						validateSelection();
						return;
					}
					if (!defaultSelection) {
						// if the selection is changed then disable the
						// autoDetect as the user wants to use just a single
						// type.
						autoDetect.setSelection(false);
						autoDetect.setEnabled(false);
					}
					else {
						// it is the first call to this listener which was
						// triggered by the code so do not disable the
						// autodetect.
						defaultSelection = false;
					}
					validateSelection();
				}
			});

			matchShortPropertyNames = new Button(page, SWT.CHECK);
			matchShortPropertyNames.setLayoutData(
					GridDataFactory.fillDefaults().grab(false, false).span(2, 1).create());
			matchShortPropertyNames.setText(
					"Match shortened property names in Shapefile to target type properties");

			autoDetect = new Button(page, SWT.CHECK);
			autoDetect.setLayoutData(
					GridDataFactory.fillDefaults().grab(false, false).span(2, 1).create());
			autoDetect.setText("Ignore Schema type selection and auto detect types");

			autoDetect.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					if (autoDetect.getSelection()) {
						selector.getControl().setEnabled(false);
						setMessage(
								"All the types will be mapped automatically to their respective file structures.",
								DialogPage.INFORMATION);
						setPageComplete(true);
					}
					else {
						selector.getControl().setEnabled(true);
						// reset the message in the window asking user to select
						// schema.
						setMessage(null);
					}
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// default selection is false.
					selector.getControl().setEnabled(true);

				}
			});
			page.layout();
			page.pack();
		}

		populateSelector();

		setDefaultOptions();
	}

	/**
	 * Populate selector by default based on the compatibility of the schema and
	 * the selected data file.
	 */
	private void populateSelector() {
		LocatableInputSupplier<? extends InputStream> currentSource = getWizard().getProvider()
				.getSource();

		// avoid NPE when relative path check box is selected when loading the
		// schema.
		if (currentSource != null && !currentSource.equals(lastSource)) {
			// if the source has changed

			lastSource = currentSource;
			lastType = ShapeSchemaReader.readShapeType(lastSource);

			if (selector.getSelectedObject() == null) {
				// try to find a candidate for default selection
				if (lastType != null) {
					Pair<TypeDefinition, Integer> pt = ShapeInstanceReader
							.getMostCompatibleShapeType(getWizard().getProvider().getSourceSchema(),
									lastType, lastType.getName().getLocalPart());
					if (pt != null) {
						defaultSelection = true;
						selector.setSelection(new StructuredSelection(pt.getFirst()));
					}
				}
			}

			validateSelection();
		}
	}

	/**
	 * Set default options to the checkbox based on if the user navigated from
	 * single file import or multiple file import.
	 */
	private void setDefaultOptions() {
		// check how many data files were imported by the user.
		List<URI> locations = null;
		if (lastSource instanceof FilesIOSupplier) {
			locations = ((FilesIOSupplier) lastSource).getLocations();
		}
		else {
			locations = Arrays.asList(lastSource.getLocation());
		}

		if (locations != null && locations.size() > 1) {
			// set auto detect to true as the user navigated from multiple
			// files selection
			autoDetect.setSelection(true);
			selector.setSelection(StructuredSelection.EMPTY);
		}
		else {
			// user navigated from single file selection
			autoDetect.setSelection(false);
			setMessage(null);
		}
	}

	/**
	 * Validate the current selection. {@link #onShowPage(boolean)} must have
	 * been called first to set {@link #lastType}.
	 */
	protected void validateSelection() {
		if (lastType == null) {
			setMessage("Failed to load Shapefile structure.", DialogPage.ERROR);
			setPageComplete(false);
			return;
		}

		TypeDefinition selected = selector.getSelectedObject();

		if (selected != null) {
			int comp = ShapeInstanceReader.checkCompatibility(selected, lastType);

			if (comp > 0) {
				setPageComplete(true);
				if (comp >= 100) {
					setMessage("The selected type is compatible to the file structure.",
							DialogPage.INFORMATION);
				}
				else {
					setMessage("The selected type is only ~" + comp
							+ "% compatible to the file structure.", WARNING);
				}
				return;
			}
			else {
				setMessage("The selected type is not compatible to the file structure.",
						DialogPage.ERROR);
			}
		}
		else {
			setPageComplete(true);
			setMessage(
					"All the types will be mapped automatically to their respective file structures.",
					DialogPage.INFORMATION);

			return;
		}

		setPageComplete(false);
	}

	/**
	 * Determines if the given type is compatible to the structure of the
	 * selected file.
	 * 
	 * @param schemaType the type to test
	 * @return if the type is compatible
	 */
	protected boolean isValidType(TypeDefinition schemaType) {
		if (lastType == null) {
			return false; // should not happen
		}

		return ShapeInstanceReader.checkCompatibility(schemaType, lastType) > 0;
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

		Label label = new Label(page, SWT.NONE);
		label.setText("Schema type:");

		setPageComplete(false);
	}

	/**
	 * @see IOWizardPage#updateConfiguration(IOProvider)
	 */
	@Override
	public boolean updateConfiguration(InstanceReader provider) {

		// make sure if the selection box is empty and autoDetect is checked
		// then the user should be able to finish the wizard.
		if ((autoDetect.getSelection() && selector.getSelectedObject() == null)
				|| selector.getSelectedObject() != null) {
			if (selector.getSelectedObject() != null) {
				QName name = selector.getSelectedObject().getName();
				provider.setParameter(PARAM_TYPENAME, Value.of(name.toString()));
			}
			provider.setParameter(PARAM_MATCH_SHORT_PROPERTY_NAMES,
					Value.of(matchShortPropertyNames.getSelection()));
			provider.setParameter(PARAM_AUTO_DETECT_SCHEMA_TYPES,
					Value.of(autoDetect.getSelection()));
		}
		else {
			return false;
		}

		return true;
	}

}
