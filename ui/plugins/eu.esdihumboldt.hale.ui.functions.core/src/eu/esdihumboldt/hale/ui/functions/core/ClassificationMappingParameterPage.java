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
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameter;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.common.CommonSharedImages;
import eu.esdihumboldt.hale.ui.common.Editor;
import eu.esdihumboldt.hale.ui.common.definition.AttributeInputDialog;
import eu.esdihumboldt.hale.ui.function.generic.AbstractGenericFunctionWizard;
import eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage;

/**
 * Parameter page for classification mapping function.
 * 
 * @author Kai Schwierczek
 */
public class ClassificationMappingParameterPage extends
		HaleWizardPage<AbstractGenericFunctionWizard<?, ?>> implements ParameterPage {

	private final Map<String, Set<String>> classifications = new TreeMap<String, Set<String>>();

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

	private ComboViewer classes;
	private ListViewer values;
	private PropertyDefinition sourceProperty;
	private PropertyDefinition targetProperty;

	// TODO allowedValues bei enum source
	// TODO fixedClassifications bei enum target

	private static final String PARAMETER_CLASSIFICATIONS = "classificationMapping";
	private static final String PARAMETER_NOT_CLASSIFIED_ACTION = "notClassifiedAction";

	/**
	 * Default constructor.
	 */
	public ClassificationMappingParameterPage() {
		super("classificationmapping", "Classification", null);
		setPageComplete(false);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);
		Cell unfinishedCell = getWizard().getUnfinishedCell();
		sourceProperty = (PropertyDefinition) unfinishedCell.getSource().values().iterator().next()
				.getDefinition().getDefinition();
		targetProperty = (PropertyDefinition) unfinishedCell.getTarget().values().iterator().next()
				.getDefinition().getDefinition();
		if (fixedValueText == null || fixedValueText.getText() != null)
			setPageComplete(true);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage#setParameter(java.util.Set,
	 *      com.google.common.collect.ListMultimap)
	 */
	@Override
	public void setParameter(Set<FunctionParameter> params,
			ListMultimap<String, ParameterValue> initialValues) {
		// this page is only for parameter classificationMapping, ignore params
		if (initialValues == null)
			return;

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
			if (!classifications.containsKey(splitted[0]))
				classifications.put(splitted[0], new TreeSet<String>());
			for (int i = 1; i < splitted.length; i++)
				classifications.get(splitted[0]).add(splitted[i]);
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
		for (Map.Entry<String, Set<String>> mapping : classifications.entrySet()) {
			StringBuffer buffer = new StringBuffer();
			try {
				buffer.append(URLEncoder.encode(mapping.getKey(), "UTF-8"));
				for (String s : mapping.getValue())
					buffer.append(' ').append(URLEncoder.encode(s, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// UTF-8 should be everywhere
			}
			configuration.put(PARAMETER_CLASSIFICATIONS, new ParameterValue(buffer.toString()));
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

		// target label
		Label targetLabel = new Label(page, SWT.NONE);
		targetLabel.setText("Target value: ");
		targetLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));

		// target value selection
		Combo combo = new Combo(page, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		classes = new ComboViewer(combo);
		classes.setContentProvider(new ArrayContentProvider());
		classes.setInput(classifications.keySet());

		// add target value
		Button addButton = new Button(page, SWT.PUSH);
		addButton.setImage(CommonSharedImages.getImageRegistry().get(CommonSharedImages.IMG_ADD));
		addButton.setToolTipText("Add classification value");
		addButton.setEnabled(true);
		addButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				AttributeInputDialog dialog = new AttributeInputDialog(targetProperty, Display
						.getCurrent().getActiveShell(), "Add classification",
						"Enter new classification value");
				if (dialog.open() == AttributeInputDialog.OK) {
					String newClass = dialog.getValueAsText();
					if (newClass != null) {
						addClassification(newClass);
					}
				}
			}
		});

		// remove target value
		final Button removeButton = new Button(page, SWT.PUSH);
		removeButton.setImage(CommonSharedImages.getImageRegistry().get(
				CommonSharedImages.IMG_REMOVE));
		removeButton.setEnabled(false);
		removeButton.setToolTipText("Remove currently selected classification");
		removeButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				String selectedClass = ((IStructuredSelection) classes.getSelection())
						.getFirstElement().toString();

				if (MessageDialog.openQuestion(Display.getCurrent().getActiveShell(),
						"Remove classification", MessageFormat.format(
								"Do you really want to remove the classification for \"{0}\"?",
								selectedClass))) {
					removeClassification(selectedClass);
				}
			}
		});

		// source label
		Label sourceLabel = new Label(page, SWT.NONE);
		sourceLabel.setText("Source values: ");
		sourceLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false, 1, 2));

		// list
		org.eclipse.swt.widgets.List list = new org.eclipse.swt.widgets.List(page, SWT.MULTI
				| SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		values = new ListViewer(list);
		values.setContentProvider(new ArrayContentProvider());

		// value list controls
		Composite listControls = new Composite(page, SWT.NONE);
		listControls.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
		listControls.setLayout(new GridLayout(2, true));

		final Button valueAdd = new Button(listControls, SWT.PUSH);
		valueAdd.setImage(CommonSharedImages.getImageRegistry().get(CommonSharedImages.IMG_ADD));
		valueAdd.setText("Add value");
		valueAdd.setEnabled(false);
		valueAdd.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		valueAdd.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				String selectedClass = ((IStructuredSelection) classes.getSelection())
						.getFirstElement().toString();

				AttributeInputDialog dialog = new AttributeInputDialog(sourceProperty, Display
						.getCurrent().getActiveShell(), "Add value", MessageFormat.format(
						"Enter a new value that is classified as \"{0}\"", selectedClass));

				if (dialog.open() == Dialog.OK) {
					String newValue = dialog.getValueAsText();
					if (newValue != null)
						addValue(newValue);
				}
			}
		});

		final Button valueRemove = new Button(listControls, SWT.PUSH);
		valueRemove.setImage(CommonSharedImages.getImageRegistry().get(
				CommonSharedImages.IMG_REMOVE));
		valueRemove.setText("Remove value");
		valueRemove.setEnabled(false);
		valueRemove.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		valueRemove.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				removeValue(((IStructuredSelection) values.getSelection()).getFirstElement()
						.toString());
			}
		});

		// combo selection change
		classes.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				boolean empty = event.getSelection().isEmpty();
				removeButton.setEnabled(!empty); // !fixedClassifications &&
				valueAdd.setEnabled(!empty);
				if (!empty) {
					String className = ((IStructuredSelection) event.getSelection())
							.getFirstElement().toString();
					Set<String> valueSet = classifications.get(className);
					values.setInput(valueSet);
					values.setSelection(StructuredSelection.EMPTY);
				}
			}
		});

		// list selection change
		values.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				boolean empty = event.getSelection().isEmpty();
				valueRemove.setEnabled(!empty);
			}
		});
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
		fixedValueInputButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				AttributeInputDialog dialog = new AttributeInputDialog(targetProperty, Display
						.getCurrent().getActiveShell(), "Set default value",
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

	/**
	 * Get the classification for the given value.
	 * 
	 * @param value the value
	 * @return the classification or <code>null</code> if the value is not
	 *         classified
	 */
	private String getClassification(String value) {
		for (Entry<String, Set<String>> entry : classifications.entrySet())
			if (entry.getValue().contains(value))
				return entry.getKey();

		return null;
	}

	/**
	 * Remove the given value from the current classification.
	 * 
	 * @param selectedValue the value to remove
	 */
	private void removeValue(String selectedValue) {
		String selectedClass = ((IStructuredSelection) classes.getSelection()).getFirstElement()
				.toString();

		Set<String> valueSet = classifications.get(selectedClass);
		if (valueSet != null && valueSet.contains(selectedValue)) {
			valueSet.remove(selectedValue);
			values.refresh();
		}
	}

	/**
	 * Add a new value to the current classification.
	 * 
	 * @param newValue the value to add
	 */
	private void addValue(String newValue) {
		String selectedClass = ((IStructuredSelection) classes.getSelection()).getFirstElement()
				.toString();

		// check for value in other classification
		final String oldClass = getClassification(newValue);
		if (oldClass == null
				|| MessageDialog
						.openConfirm(
								Display.getCurrent().getActiveShell(),
								"Duplicate value",
								MessageFormat
										.format("The value was already classified as \"{0}\", the old classification will be replaced.",
												oldClass))) {

			// add value
			Set<String> valueSet = classifications.get(selectedClass);
			if (valueSet != null && !valueSet.contains(newValue)) {
				valueSet.add(newValue);
				values.refresh();
			}

			// remove old classification
			if (oldClass != null) {
				valueSet = classifications.get(oldClass);
				valueSet.remove(newValue);
			}
		}
	}

	/**
	 * Remove the given classification.
	 * 
	 * @param selectedClass the classification to remove
	 */
	private void removeClassification(String selectedClass) {
		if (classifications.containsKey(selectedClass)) {
			classifications.remove(selectedClass);
			classes.refresh();
			values.setInput(null);
			values.refresh();
		}
	}

	/**
	 * Add a new classification if it doesn't already exist
	 * 
	 * @param newClass the new classification
	 */
	private void addClassification(String newClass) {
		if (!classifications.containsKey(newClass)) {
			classifications.put(newClass, new TreeSet<String>());
			classes.refresh();
			classes.setSelection(new StructuredSelection(newClass));
		}
	}
}
