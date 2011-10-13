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

package eu.esdihumboldt.hale.io.csv.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.springframework.core.convert.ConversionService;

import au.com.bytecode.opencsv.CSVReader;

import com.vividsolutions.jts.geom.Geometry;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import de.fhg.igd.osgi.util.OsgiUtils;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.schema.io.SchemaReader;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.io.csv.reader.internal.CSVInstanceReader;
import eu.esdihumboldt.hale.io.csv.reader.internal.CSVSchemaReader;
import eu.esdihumboldt.hale.io.csv.reader.internal.CSVUtil;
import eu.esdihumboldt.hale.io.csv.reader.internal.PropertyType;
import eu.esdihumboldt.hale.io.csv.reader.internal.PropertyTypeExtension;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.io.IOWizardPage;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;
import eu.esdihumboldt.hale.ui.io.schema.SchemaReaderConfigurationPage;

/**
 * Creates the Page used for the Schema Type
 * 
 * @author Kevin Mais
 */
@SuppressWarnings("restriction")
public class SchemaTypePage extends SchemaReaderConfigurationPage {

	private String defaultString = "";
	private StringFieldEditor sfe;
	private TypeNameField geoField;
	private Group group;
	private Group geom;
	private ComboViewer cvgeo;
	private String[] last_firstLine = null;
	private List<TypeNameField> fields = new ArrayList<TypeNameField>();
	private List<ComboViewer> comboFields = new ArrayList<ComboViewer>();
	private List<TypeNameField> geoNameFields = new ArrayList<TypeNameField>();
	private Map<Integer, TypeNameField> geomap = new HashMap<Integer, TypeNameField>();
	private Map<Integer, ComboViewer> combomap = new HashMap<Integer, ComboViewer>();
	private List<ComboViewer> geoComboFields = new ArrayList<ComboViewer>();
	private List<Boolean> validSel = new ArrayList<Boolean>();
	private Map<Integer, Boolean> map = new HashMap<Integer, Boolean>();
	private Integer index;
	private static final ALogger log = ALoggerFactory
			.getLogger(PropertyTypeExtension.class);

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

		provider.setParameter(CSVSchemaReader.PARAM_TYPENAME,
				sfe.getStringValue());

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
			provider.setParameter(CSVInstanceReader.PARAM_SKIP_FIRST_LINE,
					"True");
		} else {
			provider.setParameter(CSVInstanceReader.PARAM_SKIP_FIRST_LINE,
					"False");
		}
		provider.setParameter(CSVSchemaReader.PARAM_PROPERTY, propNames);

		for (ComboViewer combo : comboFields) {
			comboViewerBuffer
					.append(((PropertyType) ((IStructuredSelection) combo
							.getSelection()).getFirstElement()).getId());
			comboViewerBuffer.append(",");
		}
		comboViewerBuffer.deleteCharAt(comboViewerBuffer.lastIndexOf(","));
		String combViewNames = comboViewerBuffer.toString();
		provider.setParameter(CSVSchemaReader.PARAM_PROPERTYTYPE, combViewNames);

		return true;

	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#onShowPage()
	 */
	@Override
	protected void onShowPage() {

		LocatableInputSupplier<? extends InputStream> source = getWizard()
				.getProvider().getSource();

		int indexStart = 0;
		int indexEnd = source.getLocation().getPath().length() - 1;

		if (source.getLocation().getPath() != null) {
			indexStart = source.getLocation().getPath().lastIndexOf("/") + 1;
			if (source.getLocation().getPath().lastIndexOf(".") >= 0) {
				indexEnd = source.getLocation().getPath().lastIndexOf(".");
			}

			defaultString = source.getLocation().getPath()
					.substring(indexStart, indexEnd);
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
			if (last_firstLine != null
					&& !Arrays.equals(firstLine, last_firstLine)) {
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

					propField = new TypeNameField("properties",
							Integer.toString(i + 1), group);
					propField.setEmptyStringAllowed(false);
					propField
							.setErrorMessage("Please enter a valid Property Name");
					propField
							.setPropertyChangeListener(new IPropertyChangeListener() {

								@Override
								public void propertyChange(
										PropertyChangeEvent event) {
									if (event.getProperty().equals(
											StringFieldEditor.IS_VALID)) {
										setPageComplete((Boolean) event
												.getNewValue());
									}
									if (event.getProperty().equals(TypeNameField.TXT_CHNGD)) {
										if (geoNameFields.indexOf(geoField) != -1) {
											geoNameFields.get(geoNameFields.indexOf(geoField)).setStringValue((String)event.getNewValue());
										}
									}
								}
							});
					propField.setStringValue(firstLine[i]);
					cv = new ComboViewer(group);
					comboFields.add(cv);
					cv.addSelectionChangedListener(new ISelectionChangedListener() {

						@Override
						public void selectionChanged(SelectionChangedEvent event) {
							ConversionService conversionService = OsgiUtils
									.getService(ConversionService.class);

							int i = comboFields.indexOf(event.getSource());
							PropertyType actualSelection = ((PropertyType) ((IStructuredSelection) cv
									.getSelection()).getFirstElement());

							try {
								conversionService.convert(nextLine[i],
										actualSelection.getTypeDefinition()
												.getConstraint(Binding.class)
												.getBinding());
								validSel.set(i, true);

							} catch (Exception e) {
								log.warn("Selection invalid!");
								validSel.set(i, false);
							}
							if (validSel.contains(false)) {
								int j = validSel.indexOf(false) + 1;
								setMessage("Your selection in field # " + j
										+ " is not valid!", WARNING);
							} else {
								setMessage(null);
							}

//							int index;
							// if actualSelection is from Type Geometry
							// (geometry,point, ...)
							if (Geometry.class.isAssignableFrom(actualSelection
									.getTypeDefinition()
									.getConstraint(Binding.class).getBinding())) {
								// if map is empty ( = default/first call) or
								// map
								// has an element at index i and this element is
								// assigned to "false" ( = this field was no
								// geometry before SelectionChanged) or the
								// object in the map assigned to number i is
								// null
								if (map.isEmpty()
										|| (map.get(i) != null && map.get(i) == false)
										|| map.get(i) == null) {
									map.put(i, true);

									geoField = new TypeNameField("geometries",
											Integer.toString(i + 1), geom);
									geoNameFields.add(geoField);
									geomap.put(i, geoField);

									geoField.setStringValue(propField
											.getStringValue());
									geoField.getTextControl(geom).setEnabled(
											false);

									cvgeo = new ComboViewer(geom);
									geoComboFields.add(cvgeo);
									combomap.put(i, cvgeo);
									cvgeo.setContentProvider(ArrayContentProvider
											.getInstance());
									cvgeo.setLabelProvider(new LabelProvider());
									cvgeo.addSelectionChangedListener(new ISelectionChangedListener() {

										@Override
										public void selectionChanged(
												SelectionChangedEvent event) {
											String coordsys = cvgeo.getCombo()
													.getText();

											if (coordsys.isEmpty()) {
												setErrorMessage("You have not selected a valid coordinate system!");
											} else {
												setErrorMessage(null);
											}

										}
									});
									// cvgeo.setInput(); TODO coordinate systems
								}

								geom.setLayout(new GridLayout(3, false));
								geom.layout();
								geom.getParent().layout(true, true);
							} else if (map.get(i) != null && map.get(i) == true) {
								index = geoNameFields.indexOf(geomap.get(i));

								geoNameFields.get(index).dispose();
								geoNameFields.get(index).getTextControl(geom)
										.dispose();
								geoNameFields.get(index).getLabelControl(geom)
										.dispose();

								geoComboFields.get(index).getCombo().dispose();

								geoNameFields.set(index, null);
								geoComboFields.set(index, null);
								map.put(i, false);
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
							if (element instanceof PropertyType) {
								return ((PropertyType) element).getName();
							}
							return super.getText(element);
						}
					});
					Collection<PropertyType> elements = PropertyTypeExtension
							.getInstance().getElements();
					cv.setInput(elements);
					if (!elements.isEmpty()) {
						cv.setSelection(new StructuredSelection(elements
								.iterator().next()));
					}

					fields.add(propField);
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
		group.getParent().layout(true, true);

		super.onShowPage();
	}

	/**
	 * @see HaleWizardPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite page) {
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
		group.setLayoutData(GridDataFactory.fillDefaults().grab(true, false)
				.span(2, 1).create());
		group.setLayout(GridLayoutFactory.swtDefaults().numColumns(3)
				.equalWidth(false).margins(5, 5).create());

		geom = new Group(page, SWT.NONE);
		geom.setText("Geometry Settings");
		geom.setLayoutData(GridDataFactory.fillDefaults().grab(true, false)
				.span(2, 1).create());
		geom.setLayout(GridLayoutFactory.swtDefaults().numColumns(3)
				.equalWidth(false).margins(5, 5).create());

		// ScrolledComposite sc = new ScrolledComposite(page, SWT.V_SCROLL);
		// sc.setExpandVertical(true);
		// sc.setAlwaysShowScrollBars(true);

		setPageComplete(sfe.isValid());
	}

}
