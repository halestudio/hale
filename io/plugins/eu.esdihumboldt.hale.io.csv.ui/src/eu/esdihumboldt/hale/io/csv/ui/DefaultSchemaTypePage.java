/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.csv.ui;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.schema.io.SchemaReader;
import eu.esdihumboldt.hale.io.csv.PropertyTypeExtension;
import eu.esdihumboldt.hale.io.csv.PropertyTypeFactory;
import eu.esdihumboldt.hale.io.csv.reader.CommonSchemaConstants;
import eu.esdihumboldt.hale.io.csv.reader.internal.AbstractTableSchemaReader;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.io.IOWizardPage;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;
import eu.esdihumboldt.hale.ui.io.schema.SchemaReaderConfigurationPage;
import eu.esdihumboldt.hale.ui.util.components.DynamicScrolledComposite;

/**
 * Default schema type configuration page <br>
 * Override onPage() method to set firstLine (header) and nextLine (content) and
 * call super method
 * 
 * @author Kevin Mais
 * @author Patrick Lieb
 */
public class DefaultSchemaTypePage extends SchemaReaderConfigurationPage {

	private String defaultString = "";
	/**
	 * Field editor for editing the schema name as string
	 */
	private StringFieldEditor sfe;
	private Group group;
	private String[] lastSecondRow = null;
	private final List<TypeNameField> fields = new ArrayList<TypeNameField>();
	private final List<ComboViewer> comboFields = new ArrayList<ComboViewer>();
	private ScrolledComposite sc;
	private final List<Boolean> validSel = new ArrayList<Boolean>();
	private Boolean valid = true;
	private Boolean isValid = true;
	private String[] header;
	private String[] secondRow;

	/**
	 * default constructor
	 * 
	 * @param pageName the page name
	 */
	public DefaultSchemaTypePage(String pageName) {
		super(pageName);

		setTitle("Typename Settings");
		setDescription("Enter a valid Name for your Type");

	}

	/**
	 * @see IOWizardPage#updateConfiguration
	 */
	@Override
	public boolean updateConfiguration(SchemaReader provider) {

		provider.setParameter(CommonSchemaConstants.PARAM_TYPENAME, Value.of(sfe.getStringValue()));

		StringBuffer propNamesBuffer = new StringBuffer();
		StringBuffer comboViewerBuffer = new StringBuffer();
		StringBuffer oldNamesBuffer = new StringBuffer();

		for (TypeNameField prop : fields) {
			propNamesBuffer.append(prop.getStringValue());
			propNamesBuffer.append(",");
		}
		propNamesBuffer.deleteCharAt(propNamesBuffer.lastIndexOf(","));
		String propNames = propNamesBuffer.toString();
		for (String string : lastSecondRow) {
			oldNamesBuffer.append(string);
			oldNamesBuffer.append(",");
		}
		oldNamesBuffer.deleteCharAt(oldNamesBuffer.lastIndexOf(","));
		String oldNames = oldNamesBuffer.toString();
		if (oldNames.equals(propNames)) {
			provider.setParameter(CommonSchemaConstants.PARAM_SKIP_N_LINES, Value.of(1));
		}
		else {
			provider.setParameter(CommonSchemaConstants.PARAM_SKIP_N_LINES, Value.of(0));
		}
		provider.setParameter(AbstractTableSchemaReader.PARAM_PROPERTY, Value.of(propNames));

		for (ComboViewer combo : comboFields) {
			comboViewerBuffer
					.append(((PropertyTypeFactory) ((IStructuredSelection) combo.getSelection())
							.getFirstElement()).getIdentifier());
			comboViewerBuffer.append(",");
		}
		comboViewerBuffer.deleteCharAt(comboViewerBuffer.lastIndexOf(","));
		String combViewNames = comboViewerBuffer.toString();
		provider.setParameter(AbstractTableSchemaReader.PARAM_PROPERTYTYPE,
				Value.of(combViewNames));

		return true;

	}

	/**
	 * @see HaleWizardPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {

		super.onShowPage(firstShow);

		LocatableInputSupplier<? extends InputStream> source = getWizard().getProvider()
				.getSource();

		int indexStart = 0;
		int indexEnd = source.getLocation().getPath().length() - 1;

		// set the content of this page, type name and property name with data
		// type
		update();

		// get schema name based on file name
		if (source.getLocation().getPath() != null) {
			indexStart = source.getLocation().getPath().lastIndexOf("/") + 1;
			if (source.getLocation().getPath().lastIndexOf(".") >= 0) {
				indexEnd = source.getLocation().getPath().lastIndexOf(".");
			}

			// set type name
			defaultString = source.getLocation().getPath().substring(indexStart, indexEnd);
			sfe.setStringValue(defaultString);
			// setPageComplete(sfe.isValid());
		}
		// Content has changed so clear everything (if this is not first visit)
//		if (!firstShow && lastSecondRow != null && !Arrays.equals(header, lastSecondRow)) {
//			clearPage();
//		}

	}

	/**
	 * Update all fields (should be called if page is initialized or the
	 * selection of input (file or table) has been changed
	 */
	protected void update() {
		int length = 0;

		// the header is not valid so clear all content
		if (header == null || header.length == 0) {
			// clear properties
			clearPage();
			return;
		}

		if (secondRow == null || secondRow.length == 0) {
			secondRow = header;
		}

		if (header.length != 0) {
			length = header.length;
		}

		// dispose all property names if the read configuration has been changed
		if (lastSecondRow != null && !Arrays.equals(header, lastSecondRow)) {
			for (TypeNameField properties : fields) {
				properties.dispose();
				properties.getTextControl(group).dispose();
				properties.getLabelControl(group).dispose();
			}
			for (ComboViewer combViewer : comboFields) {
				combViewer.getCombo().dispose();
			}
			fields.clear();
			comboFields.clear();
		}

		if (!Arrays.equals(header, lastSecondRow)) {
			for (int i = 0; i < length; i++) {
				final TypeNameField propField;
				final ComboViewer cv;

				validSel.add(true);

				propField = new TypeNameField("properties", Integer.toString(i + 1), group);
				fields.add(propField);
				propField.setEmptyStringAllowed(false);
				propField.setErrorMessage("Please enter a valid Property Name");
				propField.setPropertyChangeListener(new IPropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent event) {

						HashSet<String> hs = new HashSet<String>();

						if (event.getProperty().equals(StringFieldEditor.VALUE)) {
							for (TypeNameField field : fields) {
								if (!hs.add(field.getStringValue())) {
									valid = false;
									break;
								}
								else {
									valid = true;
								}

							}

						}

						if (event.getProperty().equals(StringFieldEditor.IS_VALID)) {
							isValid = (Boolean) event.getNewValue();
						}

						setPageComplete(isValid());

					}
				});
				propField.setStringValue(header[i].replace(" ", ""));

				cv = new ComboViewer(group);
				comboFields.add(cv);
				cv.addSelectionChangedListener(new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent event) {

						int i = comboFields.indexOf(event.getSource());
						PropertyTypeFactory actualSelection = ((PropertyTypeFactory) ((IStructuredSelection) cv
								.getSelection()).getFirstElement());

						try {
							actualSelection.createExtensionObject().convertFromField(secondRow[i]);
							validSel.set(i, true);

						} catch (Exception e) {
							validSel.set(i, false);
						}
						if (validSel.contains(false)) {
							int j = validSel.indexOf(false) + 1;
							setMessage("Your selection in field # " + j + " is not valid!",
									WARNING);
						}
						else {
							setMessage(null);
						}

					}
				});
				cv.setContentProvider(ArrayContentProvider.getInstance());
				cv.setLabelProvider(new LabelProvider() {

					/**
					 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
					 */
					@Override
					public String getText(Object element) {
						if (element instanceof PropertyTypeFactory) {
							return ((PropertyTypeFactory) element).getDisplayName();
						}
						return super.getText(element);
					}
				});
				Collection<PropertyTypeFactory> elements = PropertyTypeExtension.getInstance()
						.getFactories();
				cv.setInput(elements);

				PropertyTypeFactory defaultSelection = PropertyTypeExtension.getInstance()
						.getFactory("java.lang.String");
				if (defaultSelection != null) {
					cv.setSelection(new StructuredSelection(defaultSelection));
				}
				else if (!elements.isEmpty()) {
					cv.setSelection(new StructuredSelection(elements.iterator().next()));
				}

			}
		}
		group.setLayout(new GridLayout(3, false));

		lastSecondRow = header;

		group.layout();
		sc.layout();
//		setPageComplete(sfe.isValid() && isValid && valid);
	}

	/**
	 * Clear the page. Can be called when the content has changed. Set everthing
	 * on an initial state
	 */
	private void clearPage() {
		// clear properties
		for (TypeNameField properties : fields) {
			properties.dispose();
			properties.getTextControl(group).dispose();
			properties.getLabelControl(group).dispose();
		}
		for (ComboViewer combViewer : comboFields) {
			combViewer.getCombo().dispose();
		}
		validSel.clear();
		fields.clear();
		comboFields.clear();
		sfe.setStringValue("");
		group.layout();
		sc.layout();
		lastSecondRow = null;
	}

	/**
	 * @see HaleWizardPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite parent) {

		Composite holder = new Composite(parent, SWT.NONE);
		holder.setLayout(GridLayoutFactory.fillDefaults().create());

		sc = new DynamicScrolledComposite(holder, SWT.V_SCROLL);
		sc.setExpandHorizontal(true);

		sc.setLayoutData(
				GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 200).create());

		Composite page = new Composite(sc, SWT.NONE);
		page.setLayout(new GridLayout(2, false));

		sfe = new TypeNameField("typename", "Typename", page);
		sfe.setEmptyStringAllowed(false);
		// sfe.setErrorMessage("Please enter a valid Type Name");
		sfe.setPropertyChangeListener(new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(StringFieldEditor.IS_VALID)) {
					setPageComplete(isValid());
				}
			}
		});

		sfe.setStringValue(defaultString);
		// sfe.setPage(this);

		group = new Group(page, SWT.NONE);
		group.setText("Properties");
		group.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(2, 1).create());
		group.setLayout(GridLayoutFactory.swtDefaults().numColumns(3).equalWidth(false)
				.margins(5, 5).create());

		sc.setContent(page);
		sc.layout();

//		setPageComplete(sfe.isValid());
	}

	/**
	 * @param header the firstLine to set
	 */
	public void setHeader(String[] header) {
		this.header = header;
	}

	/**
	 * @param secondRow the nextLine to set
	 */
	public void setSecondRow(String[] secondRow) {
		this.secondRow = secondRow;
	}

	/**
	 * @return true, if the state of properties and type names are valid
	 */
	public boolean isValid() {
		if (sfe.isValid() && isValid && valid && header != null && header.length != 0) {
			setErrorMessage(null);
			return true;
		}
		else {
			showMessage();
			return false;
		}
	}

	/**
	 * Set Message based on not valid condition
	 */
	private void showMessage() {
		if (header == null || header.length == 0)
			setErrorMessage("The file contains no data");
		else if (!sfe.isValid())
			setErrorMessage("Please enter a valid Type Name");
		else if (!isValid)
			setErrorMessage("Please enter a valid Property Name");
		else if (!valid)
			setErrorMessage("Doublicated property name is not allowed");
		// else if();
	}

	/**
	 * @param value the string to set
	 */
	public void setStringFieldEditorValue(String value) {
		this.sfe.setStringValue(value);
	}

	/**
	 * @see AbstractConfigurationPage#enable()
	 */
	@Override
	public void enable() {
		// not required
	}

	/**
	 * @see AbstractConfigurationPage#disable()
	 */
	@Override
	public void disable() {
		// not required
	}
}
