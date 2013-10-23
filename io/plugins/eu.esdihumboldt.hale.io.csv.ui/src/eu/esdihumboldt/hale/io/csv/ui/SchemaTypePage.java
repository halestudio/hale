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

package eu.esdihumboldt.hale.io.csv.ui;

import java.io.IOException;
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

import au.com.bytecode.opencsv.CSVReader;
import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.schema.io.SchemaReader;
import eu.esdihumboldt.hale.io.csv.PropertyTypeExtension;
import eu.esdihumboldt.hale.io.csv.PropertyTypeFactory;
import eu.esdihumboldt.hale.io.csv.reader.internal.CSVInstanceReader;
import eu.esdihumboldt.hale.io.csv.reader.internal.CSVSchemaReader;
import eu.esdihumboldt.hale.io.csv.reader.internal.CSVUtil;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.io.IOWizardPage;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;
import eu.esdihumboldt.hale.ui.io.schema.SchemaReaderConfigurationPage;
import eu.esdihumboldt.hale.ui.util.components.DynamicScrolledComposite;

/**
 * Creates the Page used for the Schema Type
 * 
 * @author Kevin Mais
 */
public class SchemaTypePage extends SchemaReaderConfigurationPage {

	private String defaultString = "";
	private StringFieldEditor sfe;
	private Group group;
	private String[] last_firstLine = null;
	private final List<TypeNameField> fields = new ArrayList<TypeNameField>();
	private final List<ComboViewer> comboFields = new ArrayList<ComboViewer>();
	private final List<Boolean> validSel = new ArrayList<Boolean>();
	private Boolean valid = true;
	private Boolean isValid = true;
	private ScrolledComposite sc;
	private static final ALogger log = ALoggerFactory.getLogger(SchemaTypePage.class);

	/**
	 * default constructor
	 */
	public SchemaTypePage() {
		super("Schema Type");
		// is never used

		setTitle("Typename Settings");
		setDescription("Enter a valid Name for your Type");

	}

	/**
	 * @see AbstractConfigurationPage#enable()
	 */
	@Override
	public void enable() {
		// Auto-generated method stub

	}

	/**
	 * @see AbstractConfigurationPage#disable()
	 */
	@Override
	public void disable() {
		// Auto-generated method stub

	}

	/**
	 * @see IOWizardPage#updateConfiguration
	 */
	@Override
	public boolean updateConfiguration(SchemaReader provider) {

		provider.setParameter(CSVSchemaReader.PARAM_TYPENAME, Value.of(sfe.getStringValue()));

		StringBuffer propNamesBuffer = new StringBuffer();
		StringBuffer comboViewerBuffer = new StringBuffer();
		StringBuffer oldNamesBuffer = new StringBuffer();

		for (TypeNameField prop : fields) {
			propNamesBuffer.append(prop.getStringValue());
			propNamesBuffer.append(",");
		}
		propNamesBuffer.deleteCharAt(propNamesBuffer.lastIndexOf(","));
		String propNames = propNamesBuffer.toString();
		for (String string : last_firstLine) {
			oldNamesBuffer.append(string);
			oldNamesBuffer.append(",");
		}
		oldNamesBuffer.deleteCharAt(oldNamesBuffer.lastIndexOf(","));
		String oldNames = oldNamesBuffer.toString();
		if (oldNames.equals(propNames)) {
			provider.setParameter(CSVInstanceReader.PARAM_SKIP_FIRST_LINE, Value.of("True"));
		}
		else {
			provider.setParameter(CSVInstanceReader.PARAM_SKIP_FIRST_LINE, Value.of("False"));
		}
		provider.setParameter(CSVSchemaReader.PARAM_PROPERTY, Value.of(propNames));

		for (ComboViewer combo : comboFields) {
			comboViewerBuffer.append(((PropertyTypeFactory) ((IStructuredSelection) combo
					.getSelection()).getFirstElement()).getIdentifier());
			comboViewerBuffer.append(",");
		}
		comboViewerBuffer.deleteCharAt(comboViewerBuffer.lastIndexOf(","));
		String combViewNames = comboViewerBuffer.toString();
		provider.setParameter(CSVSchemaReader.PARAM_PROPERTYTYPE, Value.of(combViewNames));

		return true;

	}

	/**
	 * @see HaleWizardPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {

		LocatableInputSupplier<? extends InputStream> source = getWizard().getProvider()
				.getSource();

		int indexStart = 0;
		int indexEnd = source.getLocation().getPath().length() - 1;

		if (source.getLocation().getPath() != null) {
			indexStart = source.getLocation().getPath().lastIndexOf("/") + 1;
			if (source.getLocation().getPath().lastIndexOf(".") >= 0) {
				indexEnd = source.getLocation().getPath().lastIndexOf(".");
			}

			defaultString = source.getLocation().getPath().substring(indexStart, indexEnd);
			sfe.setStringValue(defaultString);
			setPageComplete(sfe.isValid());
		}

		try {
			CSVReader reader = CSVUtil.readFirst(getWizard().getProvider());

			String[] firstLine = reader.readNext();
			final String[] nextLine = reader.readNext();

			int length = 0;
			if (firstLine.length != 0) {
				length = firstLine.length;
			}

			// disposes all property names if the read configuration has changed
			if (last_firstLine != null && !Arrays.equals(firstLine, last_firstLine)) {
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

			if (!Arrays.equals(firstLine, last_firstLine)) {
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
							setPageComplete(isValid && valid);

						}
					});
					propField.setStringValue(firstLine[i]);
					cv = new ComboViewer(group);
					comboFields.add(cv);
					cv.addSelectionChangedListener(new ISelectionChangedListener() {

						@Override
						public void selectionChanged(SelectionChangedEvent event) {

							int i = comboFields.indexOf(event.getSource());
							PropertyTypeFactory actualSelection = ((PropertyTypeFactory) ((IStructuredSelection) cv
									.getSelection()).getFirstElement());

							try {
								actualSelection.createExtensionObject().convertFromField(
										nextLine[i]);
								validSel.set(i, true);

							} catch (Exception e) {
								log.warn("Selection invalid!", e);
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

			last_firstLine = firstLine;

		} catch (IOException e) {
			setErrorMessage("File could not be read");
			setPageComplete(false);
			e.printStackTrace();
		}

		group.layout();
		sc.layout();

		super.onShowPage(firstShow);
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

		sc.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 200)
				.create());

		Composite page = new Composite(sc, SWT.NONE);
		page.setLayout(new GridLayout(2, false));

		sfe = new TypeNameField("typename", "Typename", page);
		sfe.setEmptyStringAllowed(false);
		sfe.setErrorMessage("Please enter a valid Type Name");
		sfe.setPropertyChangeListener(new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(StringFieldEditor.IS_VALID)) {
					setPageComplete((Boolean) event.getNewValue());
				}
			}
		});

		sfe.setStringValue(defaultString);
		sfe.setPage(this);

		group = new Group(page, SWT.NONE);
		group.setText("Properties");
		group.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(2, 1).create());
		group.setLayout(GridLayoutFactory.swtDefaults().numColumns(3).equalWidth(false)
				.margins(5, 5).create());

		sc.setContent(page);
		sc.layout();

		setPageComplete(sfe.isValid());
	}

}
