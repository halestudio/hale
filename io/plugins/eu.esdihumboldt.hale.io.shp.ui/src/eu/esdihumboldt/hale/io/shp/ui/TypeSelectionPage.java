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

import javax.xml.namespace.QName;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.Value;
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
					validateSelection();
				}
			});

			matchShortPropertyNames = new Button(page, SWT.CHECK);
			matchShortPropertyNames.setLayoutData(
					GridDataFactory.fillDefaults().grab(false, false).span(2, 1).create());
			matchShortPropertyNames.setText(
					"Match shortened property names in Shapefile to target type properties");

			page.layout();
			page.pack();
		}

		LocatableInputSupplier<? extends InputStream> currentSource = getWizard().getProvider()
				.getSource();
		if (!currentSource.equals(lastSource)) {
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
						selector.setSelection(new StructuredSelection(pt.getFirst()));
					}
				}
			}

			validateSelection();
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
			setMessage(null);
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
		if (selector.getSelectedObject() != null) {
			QName name = selector.getSelectedObject().getName();
			provider.setParameter(PARAM_TYPENAME, Value.of(name.toString()));
			provider.setParameter(PARAM_MATCH_SHORT_PROPERTY_NAMES,
					Value.of(matchShortPropertyNames.getSelection()));
		}
		else {
			return false;
		}

		return true;
	}

}
