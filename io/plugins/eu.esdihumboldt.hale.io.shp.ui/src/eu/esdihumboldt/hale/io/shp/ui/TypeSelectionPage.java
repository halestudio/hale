/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.io.shp.ui;

import java.io.InputStream;
import java.util.Collection;

import javax.xml.namespace.QName;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.shp.ShapefileConstants;
import eu.esdihumboldt.hale.io.shp.reader.internal.ShapeSchemaReader;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.common.definition.selector.TypeDefinitionSelector;
import eu.esdihumboldt.hale.ui.io.IOWizardPage;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;
import eu.esdihumboldt.hale.ui.io.instance.InstanceReaderConfigurationPage;

/**
 * Configuration page for selecting the schema type for Shapefile instances.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public class TypeSelectionPage extends InstanceReaderConfigurationPage implements
		ShapefileConstants {

	private TypeDefinitionSelector selector;

	private LocatableInputSupplier<? extends InputStream> lastSource;

	private TypeDefinition lastType;

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

			page.layout();
			page.pack();
		}

		LocatableInputSupplier<? extends InputStream> currentSource = getWizard().getProvider()
				.getSource();
		if (!currentSource.equals(lastSource)) {
			// if the source has changed

			lastSource = currentSource;
			ShapeSchemaReader reader = new ShapeSchemaReader();
			reader.setSource(lastSource);
			try {
				reader.execute(null);
				Collection<? extends TypeDefinition> types = reader.getSchema()
						.getMappingRelevantTypes();
				if (!types.isEmpty()) {
					lastType = types.iterator().next();
				}
				else {
					lastType = null;
				}
			} catch (Exception e) {
				lastType = null;
			}

			if (selector.getSelectedObject() == null) {
				// try to find a candidate for default selection
				for (TypeDefinition type : getWizard().getProvider().getSourceSchema()
						.getMappingRelevantTypes()) {
					if (isValidType(type)) {
						selector.setSelection(new StructuredSelection(type));
						break;
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
			if (isValidType(selected)) {
				setPageComplete(true);
				setMessage("The selected type is compatible to the file structure.",
						DialogPage.INFORMATION);
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
	 * @param type the type to test
	 * @return if the type is compatible
	 */
	protected boolean isValidType(TypeDefinition type) {
		if (lastType == null) {
			return false; // should not happen
		}

		// Shapefile types are flat, so only regard properties
		Collection<? extends PropertyDefinition> children = DefinitionUtil
				.getAllProperties(lastType);

		// every property must exist in the target type, with the same name
		for (PropertyDefinition property : children) {
			ChildDefinition<?> child = type.getChild(property.getName());
			if (child == null || child.asProperty() == null) {
				return false;
			}
		}

		return true;
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
			provider.setParameter(PARAM_TYPENAME, name.toString());
		}
		else {
			return false;
		}

		return true;
	}

}
