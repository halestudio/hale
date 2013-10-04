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

package eu.esdihumboldt.hale.ui.functions.core;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameter;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.functions.ClassificationMappingFunction;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.lookup.LookupService;
import eu.esdihumboldt.hale.common.lookup.LookupTable;
import eu.esdihumboldt.hale.common.lookup.impl.LookupTableImpl;
import eu.esdihumboldt.hale.common.lookup.internal.LookupLoadAdvisor;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.common.CommonSharedImages;
import eu.esdihumboldt.hale.ui.common.Editor;
import eu.esdihumboldt.hale.ui.common.definition.AttributeInputDialog;
import eu.esdihumboldt.hale.ui.function.generic.AbstractGenericFunctionWizard;
import eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage;
import eu.esdihumboldt.hale.ui.io.action.IOWizardAction;
import eu.esdihumboldt.hale.ui.lookup.LookupTableImportWizard;
import eu.esdihumboldt.hale.ui.lookup.LookupTableLoadWizard;
import eu.esdihumboldt.hale.ui.util.wizard.HaleWizardDialog;

/**
 * Parameter page for classification mapping function.
 * 
 * @author Kai Schwierczek, Dominik Reuter
 */
public class ClassificationMappingParameterPage extends
		HaleWizardPage<AbstractGenericFunctionWizard<?, ?>> implements ParameterPage,
		ClassificationMappingFunction {

	private final Map<Value, Value> lookupTable = new LinkedHashMap<>();

	private TabFolder tabs;
	private TabItem manualItem;
	private TabItem fromFileItem;

	// not classified action stuff
	private Composite notClassifiedActionComposite;
	private ComboViewer notClassifiedActionViewer;
	private String notClassifiedAction;
	private Text fixedValueText;
	private Button fixedValueInputButton;
	private static final List<String> notClassifiedActionOptions = new ArrayList<String>(3);
	static {
		notClassifiedActionOptions.add("assign null");
		notClassifiedActionOptions.add("assign the source value");
		notClassifiedActionOptions.add("assign a fixed value");
	}

	// manual configuration tab fields
// ------------------- fields used by the old UI -------------------
//	private final Map<String, Set<String>> classifications = new TreeMap<String, Set<String>>();
//	// value selection stuff
//	private ComboViewer classes;
//	private ListViewer values;

	// from file tab fields
	private ComboViewer lookupTableComboViewer;
	private Label description;
	private String selectedLookupTableID = null;

	private PropertyDefinition sourceProperty;
	private PropertyDefinition targetProperty;
	private EntityDefinition sourceEntity;
	private EntityDefinition targetEntity;

	// TODO allowedValues bei enum source
	// TODO fixedClassifications bei enum target

	/**
	 * Default constructor.
	 */
	public ClassificationMappingParameterPage() {
		super("classificationmapping", "Classification", null);
		setPageComplete(false);
	}

	@Override
	protected void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);
		Cell unfinishedCell = getWizard().getUnfinishedCell();
		sourceEntity = unfinishedCell.getSource().values().iterator().next().getDefinition();
		sourceProperty = (PropertyDefinition) sourceEntity.getDefinition();
		targetEntity = unfinishedCell.getTarget().values().iterator().next().getDefinition();
		targetProperty = (PropertyDefinition) targetEntity.getDefinition();
		if (fixedValueText == null || fixedValueText.getText() != null)
			setPageComplete(true);
	}

	@Override
	public void setParameter(Set<FunctionParameter> params,
			ListMultimap<String, ParameterValue> initialValues) {
		// this page is only for parameter classificationMapping, ignore params
		if (initialValues == null)
			return;

		// Load the lookupTableConfiguration
		List<ParameterValue> lookupTableId = initialValues.get(PARAMETER_LOOKUPTABLE_ID);
		if (!lookupTableId.isEmpty()) {
			selectedLookupTableID = lookupTableId.get(0).as(String.class);
		}

		// Load the complex value configuration
		List<ParameterValue> lookupTableComplex = initialValues.get(PARAMETER_LOOKUPTABLE);
		if (!lookupTableComplex.isEmpty()) {
			LookupTable table = (LookupTable) lookupTableComplex.get(0).getValue();
			lookupTable.putAll(table.asMap());
//			ListMultimap<Value, Value> tableReverse = table.reverse();
//			for (Value key : tableReverse.keySet()) {
//				TreeSet<String> valueSet = new TreeSet<String>();
//				for (Value value : tableReverse.get(key)) {
//					valueSet.add(value.as(String.class));
//				}
//				classifications.put(key.as(String.class), valueSet);
//			}
		}

		// For reason of compatibility we need the following code
		List<ParameterValue> mappings = initialValues.get(PARAMETER_CLASSIFICATIONS);
		for (ParameterValue value : mappings) {
			String s = value.as(String.class);
			String[] splitted = s.split(" ");
			try {
				for (int i = 0; i < splitted.length; i++)
					splitted[i] = URLDecoder.decode(splitted[i], "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// UTF-8 should be everywhere
			}
			Value targetValue = Value.of(splitted[0]);
			for (int i = 1; i < splitted.length; i++)
				lookupTable.put(Value.of(splitted[i]), targetValue);
//			if (!classifications.containsKey(splitted[0]))
//				classifications.put(splitted[0], new TreeSet<String>());
//			for (int i = 1; i < splitted.length; i++)
//				classifications.get(splitted[0]).add(splitted[i]);
		}

		List<ParameterValue> notClassifiedActionParams = initialValues
				.get(PARAMETER_NOT_CLASSIFIED_ACTION);
		if (!notClassifiedActionParams.isEmpty())
			notClassifiedAction = notClassifiedActionParams.get(0).as(String.class);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage#getConfiguration()
	 */
	@Override
	public ListMultimap<String, ParameterValue> getConfiguration() {
		ListMultimap<String, ParameterValue> configuration = ArrayListMultimap.create();

		for (TabItem tabItem : tabs.getSelection()) {
			if (tabItem.equals(fromFileItem)) {
				// Set the selected lookupTable
				IStructuredSelection selection = (IStructuredSelection) lookupTableComboViewer
						.getSelection();
				configuration.put(PARAMETER_LOOKUPTABLE_ID, new ParameterValue(selection
						.getFirstElement().toString()));
			}
			else {
				if (tabItem.equals(manualItem)) {
//					Map<Value, Value> values = new HashMap<Value, Value>();
//					for (Map.Entry<String, Set<String>> mapping : classifications.entrySet()) {
//						for (String s : mapping.getValue()) {
//							values.put(Value.of(s), Value.of(mapping.getKey()));
//						}
//					}
					LookupTable realLookupTable = new LookupTableImpl(lookupTable);
					configuration.put(PARAMETER_LOOKUPTABLE,
							new ParameterValue(Value.complex(realLookupTable)));
				}

//-------------- This is the code of an older version to store the lookup table ------------
//				for (Map.Entry<String, Set<String>> mapping : classifications.entrySet()) {
//					StringBuffer buffer = new StringBuffer();
//					try {
//						buffer.append(URLEncoder.encode(mapping.getKey(), "UTF-8"));
//						for (String s : mapping.getValue())
//							buffer.append(' ').append(URLEncoder.encode(s, "UTF-8"));
//					} catch (UnsupportedEncodingException e) {
//						// UTF-8 should be everywhere
//					}
//					configuration.put(PARAMETER_CLASSIFICATIONS,
//							new ParameterValue(buffer.toString()));
//				}
			}
		}
		switch (notClassifiedActionOptions
				.indexOf(((IStructuredSelection) notClassifiedActionViewer.getSelection())
						.getFirstElement())) {
		case 1:
			notClassifiedAction = "source";
			break;
		case 2:
			notClassifiedAction = "fixed:" + fixedValueText.getText();
			break;
		case 0:
		case -1:
		default:
			notClassifiedAction = "null";
		}
		configuration.put(PARAMETER_NOT_CLASSIFIED_ACTION, new ParameterValue(notClassifiedAction));
		return configuration;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		page.setLayout(GridLayoutFactory.swtDefaults().numColumns(4).create());
		// not classified action
		notClassifiedActionComposite = new Composite(page, SWT.NONE);
		notClassifiedActionComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true,
				false, 4, 1));
		notClassifiedActionComposite.setLayout(GridLayoutFactory.swtDefaults().numColumns(4)
				.margins(0, 0).create());

		Label notClassifiedActionLabel = new Label(notClassifiedActionComposite, SWT.NONE);
		notClassifiedActionLabel.setText("For unmapped source values");
		notClassifiedActionLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));

		notClassifiedActionViewer = new ComboViewer(notClassifiedActionComposite, SWT.DROP_DOWN
				| SWT.READ_ONLY);
		notClassifiedActionViewer.getControl().setLayoutData(
				new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		notClassifiedActionViewer.setContentProvider(ArrayContentProvider.getInstance());

		notClassifiedActionViewer.setInput(notClassifiedActionOptions);
		notClassifiedActionViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (notClassifiedActionOptions.indexOf(((IStructuredSelection) event.getSelection())
						.getFirstElement()) == 2)
					createFixedValueInputButton(null);
				else
					removeFixedValueInputButton();
			}
		});

		notClassifiedActionViewer.setSelection(new StructuredSelection(notClassifiedActionOptions
				.get(0)));
		if (notClassifiedAction != null) {
			if (notClassifiedAction.equals("source"))
				notClassifiedActionViewer.setSelection(new StructuredSelection(
						notClassifiedActionOptions.get(1)));
			else if (notClassifiedAction.startsWith("fixed:")) {
				notClassifiedActionViewer.setSelection(new StructuredSelection(
						notClassifiedActionOptions.get(2)));
				createFixedValueInputButton(notClassifiedAction.substring(6));
			}
		}

		// Tabs
		tabs = new TabFolder(page, SWT.NONE);
		tabs.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// The manualTab for the manual way to specify something like a
		// lookupTable
		manualItem = new TabItem(tabs, SWT.NONE);
		manualItem.setText("Manual");
		manualItem.setControl(createManualTabControl(tabs));

		// FromFileTab to load lookupTable from file
		fromFileItem = new TabItem(tabs, SWT.NONE);
		fromFileItem.setText("From File");
		fromFileItem.setControl(createFromFileTabControl(tabs));

		if (selectedLookupTableID != null) {
			tabs.setSelection(fromFileItem);
		}
	}

	private Control createManualTabControl(Composite tabParent) {

		// TODO load occurring value sources
		// TODO handle null target values (warn on finish?; do not insert in
		// lookuptable?)
		Composite tabContent = new Composite(tabParent, SWT.NONE);
		tabContent.setLayout(new GridLayout(4, true));

		Composite tableContainer = new Composite(tabContent, SWT.NONE);
		tableContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
		TableColumnLayout layout = new TableColumnLayout();
		tableContainer.setLayout(layout);

		final TableViewer tableViewer = new TableViewer(tableContainer, SWT.SINGLE
				| SWT.FULL_SELECTION | SWT.BORDER);
		tableViewer.getTable().setLinesVisible(true);
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());

		TableViewerColumn sourceColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		sourceColumn.getColumn().setText("Source value");
		layout.setColumnData(sourceColumn.getColumn(), new ColumnWeightData(1));
		sourceColumn.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				@SuppressWarnings("unchecked")
				Entry<Value, Value> entry = (Entry<Value, Value>) element;
				return entry.getKey().getStringRepresentation();
			}
		});

		TableViewerColumn targetColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		targetColumn.getColumn().setText("Target value");
		layout.setColumnData(targetColumn.getColumn(), new ColumnWeightData(1));
		targetColumn.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				@SuppressWarnings("unchecked")
				Entry<Value, Value> entry = (Entry<Value, Value>) element;
				if (entry.getValue() == null)
					return "";
				else
					return entry.getValue().getStringRepresentation();
			}
		});

		tableViewer.setInput(lookupTable.entrySet());

		tableViewer.getTable().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				ViewerCell cell = tableViewer.getCell(new Point(e.x, e.y));
				if (cell != null) {
					@SuppressWarnings("unchecked")
					Entry<Value, Value> entry = (Entry<Value, Value>) cell.getElement();
					Value oldValue;
					Value newValue;
					if (cell.getColumnIndex() == 0) {
						oldValue = entry.getKey();
						newValue = selectValue(sourceProperty, sourceEntity, "Edit source value",
								"Enter a new source value", oldValue.getStringRepresentation());
					}
					else {
						oldValue = entry.getValue();
						String initialValue = oldValue == null ? null : oldValue
								.getStringRepresentation();
						newValue = selectValue(targetProperty, targetEntity, "Edit target value",
								"Enter a target value", initialValue);
					}
					if (newValue == null)
						return;
					if (cell.getColumnIndex() == 0) {
						if (!newValue.equals(oldValue) && lookupTable.containsKey(newValue)) {
							showDuplicateSourceWarning(newValue.getStringRepresentation());
						}
						else {
							lookupTable.put(newValue, entry.getValue());
							lookupTable.remove(oldValue);
							tableViewer.refresh();
						}
					}
					else {
						entry.setValue(newValue);
						tableViewer.update(entry, null);
					}
				}
			}
		});

		final Button valueAdd = new Button(tabContent, SWT.PUSH);
		final Button loadButton = new Button(tabContent, SWT.PUSH);
		final Button valueRemove = new Button(tabContent, SWT.PUSH);
		final Button removeAllButton = new Button(tabContent, SWT.PUSH);

		valueAdd.setImage(CommonSharedImages.getImageRegistry().get(CommonSharedImages.IMG_ADD));
		valueAdd.setToolTipText("Add source value");
		valueAdd.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		valueAdd.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Value newSource = selectValue(sourceProperty, sourceEntity, "Add source value",
						"Enter a new source value", null);
				if (newSource != null) {
					if (lookupTable.containsKey(newSource))
						showDuplicateSourceWarning(newSource.getStringRepresentation());
					else {
						lookupTable.put(newSource, null);
						removeAllButton.setEnabled(true);
						tableViewer.refresh();
					}
				}
			}
		});

		loadButton.setImage(CommonSharedImages.getImageRegistry().get(CommonSharedImages.IMG_OPEN));
		loadButton.setToolTipText("Load classification from file");
		loadButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		loadButton.addSelectionListener(new SelectionAdapter() {

			@SuppressWarnings("restriction")
			@Override
			public void widgetSelected(SelectionEvent e) {
				LookupTableImportWizard wizard = new LookupTableLoadWizard();
				LookupLoadAdvisor advisor = new LookupLoadAdvisor();
				wizard.setAdvisor(advisor, null);
				Shell shell = Display.getCurrent().getActiveShell();
				HaleWizardDialog dialog = new HaleWizardDialog(shell, wizard);
				dialog.open();

				if (advisor.getLookupTable() != null) {
					lookupTable.putAll(advisor.getLookupTable().getTable().asMap());
					tableViewer.refresh();
					removeAllButton.setEnabled(!lookupTable.isEmpty());
				}
			}
		});

		valueRemove.setImage(CommonSharedImages.getImageRegistry().get(
				CommonSharedImages.IMG_REMOVE));
		valueRemove.setToolTipText("Remove classification entry");
		valueRemove.setEnabled(false);
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				valueRemove.setEnabled(!event.getSelection().isEmpty());
			}
		});
		valueRemove.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		valueRemove.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (tableViewer.getSelection().isEmpty())
					return;

				Object element = ((IStructuredSelection) tableViewer.getSelection())
						.getFirstElement();
				@SuppressWarnings("unchecked")
				Entry<Value, Value> entry = (Entry<Value, Value>) element;
				lookupTable.remove(entry.getKey());
				tableViewer.refresh();
				removeAllButton.setEnabled(!lookupTable.isEmpty());
			}
		});

		removeAllButton.setImage(CommonSharedImages.getImageRegistry().get(
				CommonSharedImages.IMG_TRASH));
		removeAllButton.setEnabled(false);
		removeAllButton.setToolTipText("Remove complete classification");
		removeAllButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		removeAllButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				lookupTable.clear();
				tableViewer.refresh();
				removeAllButton.setEnabled(false);
			}
		});

// ------------------- the old UI -------------------
//		item1Content.setLayout(GridLayoutFactory.swtDefaults().numColumns(6).create());
//
//		// target label
//		Label targetLabel = new Label(item1Content, SWT.NONE);
//		targetLabel.setText("Target value: ");
//		targetLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
//
//		// target value selection
//		Combo combo = new Combo(item1Content, SWT.DROP_DOWN | SWT.READ_ONLY);
//		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
//		classes = new ComboViewer(combo);
//		classes.setContentProvider(new ArrayContentProvider());
//		classes.setInput(classifications.keySet());
//
//		// add target value
//		Button addButton = new Button(item1Content, SWT.PUSH);
//		addButton.setImage(CommonSharedImages.getImageRegistry().get(CommonSharedImages.IMG_ADD));
//		addButton.setToolTipText("Add classification value");
//		addButton.setEnabled(true);
//		addButton.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				AttributeInputDialog dialog = new AttributeInputDialog(targetProperty,
//						targetEntity, Display.getCurrent().getActiveShell(), "Add classification",
//						"Enter new classification value");
//				if (dialog.open() == AttributeInputDialog.OK) {
//					String newClass = dialog.getValueAsText();
//					if (newClass != null) {
//						addClassification(newClass);
//					}
//				}
//			}
//		});
//
//		// remove target value
//		final Button removeButton = new Button(item1Content, SWT.PUSH);
//		removeButton.setImage(CommonSharedImages.getImageRegistry().get(
//				CommonSharedImages.IMG_REMOVE));
//		removeButton.setEnabled(false);
//		removeButton.setToolTipText("Remove currently selected classification");
//		removeButton.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				String selectedClass = ((IStructuredSelection) classes.getSelection())
//						.getFirstElement().toString();
//
//				if (MessageDialog.openQuestion(Display.getCurrent().getActiveShell(),
//						"Remove classification", MessageFormat.format(
//								"Do you really want to remove the classification for \"{0}\"?",
//								selectedClass))) {
//					removeClassification(selectedClass);
//				}
//			}
//		});
//
//		final Button loadButton = new Button(item1Content, SWT.PUSH);
//		loadButton.setImage(CommonSharedImages.getImageRegistry().get(CommonSharedImages.IMG_OPEN));
//		loadButton.setToolTipText("Load classification from file");
//		final Button removeAllButton = new Button(item1Content, SWT.PUSH);
//
//		loadButton.addSelectionListener(new SelectionAdapter() {
//
//			@SuppressWarnings("restriction")
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				LookupTableImportWizard wizard = new LookupTableLoadWizard();
//				LookupLoadAdvisor advisor = new LookupLoadAdvisor();
//				wizard.setAdvisor(advisor, null);
//				Shell shell = Display.getCurrent().getActiveShell();
//				HaleWizardDialog dialog = new HaleWizardDialog(shell, wizard);
//				dialog.open();
//
//				if (advisor.getLookupTable() != null) {
//					ListMultimap<Value, Value> tableReverse = advisor.getLookupTable().getTable()
//							.reverse();
//					for (Value key : tableReverse.keySet()) {
//						TreeSet<String> valueSet = new TreeSet<String>();
//						for (Value value : tableReverse.get(key)) {
//							valueSet.add(value.as(String.class));
//						}
//						classifications.put(key.as(String.class), valueSet);
//					}
//					classes.refresh();
//					classes.setSelection(new StructuredSelection(classes.getElementAt(0)));
//					removeAllButton.setEnabled(!classes.getSelection().isEmpty());
//				}
//			}
//		});
//
//		removeAllButton.setImage(CommonSharedImages.getImageRegistry().get(
//				CommonSharedImages.IMG_TRASH));
//		removeAllButton.setEnabled(false);
//		removeAllButton.setToolTipText("Remove complete classification");
//		removeAllButton.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				classifications.clear();
//				classes.refresh();
//				values.setInput(null);
//				removeAllButton.setEnabled(false);
//			}
//		});
//
//		// source label
//		Label sourceLabel = new Label(item1Content, SWT.NONE);
//		sourceLabel.setText("Source values: ");
//		sourceLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false, 1, 2));
//
//		// list
//		org.eclipse.swt.widgets.List list = new org.eclipse.swt.widgets.List(item1Content,
//				SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
//		list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 5, 1));
//		values = new ListViewer(list);
//		values.setContentProvider(new ArrayContentProvider());
//
//		// value list controls
//		Composite listControls = new Composite(item1Content, SWT.NONE);
//		listControls.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 5, 1));
//		listControls.setLayout(new GridLayout(2, true));
//
//		final Button valueAdd = new Button(listControls, SWT.PUSH);
//		valueAdd.setImage(CommonSharedImages.getImageRegistry().get(CommonSharedImages.IMG_ADD));
//		valueAdd.setText("Add value");
//		valueAdd.setEnabled(false);
//		valueAdd.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
//		valueAdd.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				String selectedClass = ((IStructuredSelection) classes.getSelection())
//						.getFirstElement().toString();
//
//				AttributeInputDialog dialog = new AttributeInputDialog(sourceProperty,
//						sourceEntity, Display.getCurrent().getActiveShell(), "Add value",
//						MessageFormat.format("Enter a new value that is classified as \"{0}\"",
//								selectedClass));
//
//				if (dialog.open() == Dialog.OK) {
//					String newValue = dialog.getValueAsText();
//					if (newValue != null)
//						addValue(newValue);
//				}
//			}
//		});
//
//		final Button valueRemove = new Button(listControls, SWT.PUSH);
//		valueRemove.setImage(CommonSharedImages.getImageRegistry().get(
//				CommonSharedImages.IMG_REMOVE));
//		valueRemove.setText("Remove value");
//		valueRemove.setEnabled(false);
//		valueRemove.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
//		valueRemove.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				removeValue(((IStructuredSelection) values.getSelection()).getFirstElement()
//						.toString());
//			}
//		});
//
//		// combo selection change
//		classes.addSelectionChangedListener(new ISelectionChangedListener() {
//
//			@Override
//			public void selectionChanged(SelectionChangedEvent event) {
//				boolean empty = event.getSelection().isEmpty();
//				removeButton.setEnabled(!empty); // !fixedClassifications &&
//				valueAdd.setEnabled(!empty);
//				removeAllButton.setEnabled(!empty);
//				if (!empty) {
//					String className = ((IStructuredSelection) event.getSelection())
//							.getFirstElement().toString();
//					Set<String> valueSet = classifications.get(className);
//					values.setInput(valueSet);
//					values.setSelection(StructuredSelection.EMPTY);
//				}
//			}
//		});
//
//		// list selection change
//		values.addSelectionChangedListener(new ISelectionChangedListener() {
//
//			@Override
//			public void selectionChanged(SelectionChangedEvent event) {
//				boolean empty = event.getSelection().isEmpty();
//				valueRemove.setEnabled(!empty);
//			}
//		});

		return tabContent;
	}

	private void showDuplicateSourceWarning(String sourceValue) {
		MessageDialog.openWarning(Display.getCurrent().getActiveShell(), "Duplicate source value",
				"The value " + sourceValue + " already exists.");
	}

	private Value selectValue(PropertyDefinition property, EntityDefinition entity, String title,
			String message, String initialValue) {
		AttributeInputDialog dialog = new AttributeInputDialog(property, entity, Display
				.getCurrent().getActiveShell(), title, message);
		dialog.create();
		if (initialValue != null)
			dialog.getEditor().setAsText(initialValue);

		if (dialog.open() == Dialog.OK)
			return Value.of(dialog.getValueAsText());
		else
			return null;
	}

	private Control createFromFileTabControl(Composite tabParent) {
		// Parent composite for fromFileTab
		Composite item2Content = new Composite(tabParent, SWT.NONE);
		item2Content.setLayout(new GridLayout());

		// Label to descripe what the user should do
		Label l = new Label(item2Content, SWT.NONE);
		l.setText("Select the LookupTable you want to use");

		// Get the Lookuptable Service
		final LookupService lookupService = HaleUI.getServiceProvider().getService(
				LookupService.class);

		// Composite for comboViewerComposite and Button
		Composite parent = new Composite(item2Content, SWT.NONE);
		parent.setLayout(new GridLayout(2, false));
		parent.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

		// Description Label
		description = new Label(item2Content, SWT.WRAP);
		description.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		description.setText("");
		description.setVisible(false);

		// label with warning message
		Composite warnComp = new Composite(item2Content, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(warnComp);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(warnComp);

		Label warnImage = new Label(warnComp, SWT.NONE);
		warnImage.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_OBJS_WARN_TSK));
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.BEGINNING).applyTo(warnImage);

		Label warn = new Label(warnComp, SWT.WRAP);
		warn.setText("Classifications from a file resource will not function in another project where the alignment with the classification is used as a base alignment.");
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false)
				.hint(300, SWT.DEFAULT).applyTo(warn);

		// Composite for ComboViewer
		Composite viewerComposite = new Composite(parent, SWT.NONE);
		viewerComposite.setLayout(new FillLayout());
		GridData layoutData = new GridData(SWT.FILL, SWT.NONE, true, false);
		viewerComposite.setLayoutData(GridDataFactory.copyData(layoutData));

		// ComboViewer
		lookupTableComboViewer = new ComboViewer(viewerComposite, SWT.READ_ONLY);
		lookupTableComboViewer.setContentProvider(ArrayContentProvider.getInstance());
		lookupTableComboViewer.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof String) {
					return lookupService.getTable((String) element).getName();
				}
				return null;
			}
		});
		lookupTableComboViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				// Show the description for the selected lookupTable
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				String desc = lookupService.getTable(selection.getFirstElement().toString())
						.getDescription();
				if (desc != null) {
					description.setText("Description: " + desc);
				}
				else {
					description.setText("");
				}

				if (!description.isVisible()) {
					description.setVisible(true);
				}
			}
		});
		lookupTableComboViewer.setInput(lookupService.getTableIDs());
		if (selectedLookupTableID != null) {
			lookupTableComboViewer.setSelection(new StructuredSelection(selectedLookupTableID),
					true);
		}

		// Button to load a lookupTable if no one is loaded
		final Button browseButton = new Button(parent, SWT.PUSH);
		browseButton.setText("Browse ...");
		browseButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IOWizardAction action = new IOWizardAction("eu.esdihumboldt.hale.lookup.import");
				action.run();
				action.dispose();
				// Refresh the viewer
				lookupTableComboViewer.setInput(lookupService.getTableIDs());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing to do here
			}
		});

		return item2Content;
	}

	/**
	 * Creates an button to open an editor for setting the fixed value.
	 * 
	 * @param initialValue the initial value or null
	 */
	private void createFixedValueInputButton(final String initialValue) {
		if (fixedValueInputButton != null) {
			fixedValueInputButton.dispose();
			if (fixedValueText != null) {
				fixedValueText.dispose();
				fixedValueText = null;
			}
		}

		setPageComplete(false);
		fixedValueInputButton = new Button(notClassifiedActionComposite, SWT.PUSH);
		fixedValueInputButton.setText("Select");
		fixedValueInputButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

		// Set the text for the label
		if (fixedValueText == null && initialValue != null) {
			fixedValueText = new Text(notClassifiedActionComposite, SWT.READ_ONLY | SWT.BORDER);
			fixedValueText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			notClassifiedActionComposite.layout();
			fixedValueText.setText(initialValue);
		}

		fixedValueInputButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				AttributeInputDialog dialog = new AttributeInputDialog(targetProperty,
						targetEntity, Display.getCurrent().getActiveShell(), "Set default value",
						"This value will be assigned to targets when the source value is not mapped");
				if (initialValue != null) {
					Editor<?> editor = dialog.getEditor();
					if (editor != null) {
						editor.setAsText(initialValue);
					}
				}

				if (dialog.open() == Dialog.OK) {
					if (fixedValueText == null) {
						fixedValueText = new Text(notClassifiedActionComposite, SWT.READ_ONLY
								| SWT.BORDER);
						fixedValueText
								.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
						notClassifiedActionComposite.layout();
					}
					fixedValueText.setText(dialog.getValueAsText());
					setPageComplete(true);
				}
			}
		});

		notClassifiedActionComposite.layout();
		notClassifiedActionComposite.getParent().layout();
	}

	/**
	 * Removes the button for opening the fixed value editor in case it is
	 * present.
	 */
	private void removeFixedValueInputButton() {
		if (fixedValueInputButton != null) {
			fixedValueInputButton.dispose();
			fixedValueInputButton = null;
			if (fixedValueText != null) {
				fixedValueText.dispose();
			}
			fixedValueText = null;
			notClassifiedActionComposite.layout();
			setPageComplete(true);
		}
	}

// ------------------- methods used by the old UI -------------------
//	/**
//	 * Get the classification for the given value.
//	 * 
//	 * @param value the value
//	 * @return the classification or <code>null</code> if the value is not
//	 *         classified
//	 */
//	private String getClassification(String value) {
//		for (Entry<String, Set<String>> entry : classifications.entrySet())
//			if (entry.getValue().contains(value))
//				return entry.getKey();
//
//		return null;
//	}
//
//	/**
//	 * Remove the given value from the current classification.
//	 * 
//	 * @param selectedValue the value to remove
//	 */
//	private void removeValue(String selectedValue) {
//		String selectedClass = ((IStructuredSelection) classes.getSelection()).getFirstElement()
//				.toString();
//
//		Set<String> valueSet = classifications.get(selectedClass);
//		if (valueSet != null && valueSet.contains(selectedValue)) {
//			valueSet.remove(selectedValue);
//			values.refresh();
//		}
//	}
//
//	/**
//	 * Add a new value to the current classification.
//	 * 
//	 * @param newValue the value to add
//	 */
//	private void addValue(String newValue) {
//		String selectedClass = ((IStructuredSelection) classes.getSelection()).getFirstElement()
//				.toString();
//
//		// check for value in other classification
//		final String oldClass = getClassification(newValue);
//		if (oldClass == null
//				|| MessageDialog
//						.openConfirm(
//								Display.getCurrent().getActiveShell(),
//								"Duplicate value",
//								MessageFormat
//										.format("The value was already classified as \"{0}\", the old classification will be replaced.",
//												oldClass))) {
//
//			// add value
//			Set<String> valueSet = classifications.get(selectedClass);
//			if (valueSet != null && !valueSet.contains(newValue)) {
//				valueSet.add(newValue);
//				values.refresh();
//			}
//
//			// remove old classification
//			if (oldClass != null) {
//				valueSet = classifications.get(oldClass);
//				valueSet.remove(newValue);
//			}
//		}
//	}
//
//	/**
//	 * Remove the given classification.
//	 * 
//	 * @param selectedClass the classification to remove
//	 */
//	private void removeClassification(String selectedClass) {
//		if (classifications.containsKey(selectedClass)) {
//			classifications.remove(selectedClass);
//			classes.refresh();
//			values.setInput(null);
//			values.refresh();
//		}
//	}
//
//	/**
//	 * Add a new classification if it doesn't already exist
//	 * 
//	 * @param newClass the new classification
//	 */
//	private void addClassification(String newClass) {
//		if (!classifications.containsKey(newClass)) {
//			classifications.put(newClass, new TreeSet<String>());
//			classes.refresh();
//			classes.setSelection(new StructuredSelection(newClass));
//		}
//	}
}
