/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.rcp.wizards.functions.core.classification;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.dialogs.ListSelectionDialog;

import eu.esdihumboldt.hale.rcp.utils.definition.AttributeInputDialog;
import eu.esdihumboldt.hale.rcp.utils.definition.DefinitionLabelFactory;
import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleCellWizardPage;
import eu.esdihumboldt.hale.rcp.wizards.functions.core.CoreFunctionWizardsPlugin;
import eu.esdihumboldt.hale.rcp.wizards.functions.core.Messages;
import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;

/**
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 *
 */
public class ClassificationMappingPage extends AbstractSingleCellWizardPage {
	
	private final Map<String, Set<String>> classifications = new TreeMap<String, Set<String>>();
	
	private final Image addImage = CoreFunctionWizardsPlugin.getImageDescriptor("icons/add.gif").createImage(); //$NON-NLS-1$
	
	private final Image removeImage = CoreFunctionWizardsPlugin.getImageDescriptor("icons/remove.gif").createImage(); //$NON-NLS-1$
	
	private ComboViewer classes;
	
	private ListViewer values;
	
	private boolean fixedClassifications = false;

	private Set<String> allowedValues = null;

	/**
	 * @see AbstractSingleCellWizardPage#AbstractSingleCellWizardPage(String, String, ImageDescriptor)
	 */
	public ClassificationMappingPage(String pageName, String title,
			ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		super.initializeDialogUnits(parent);
		
		DefinitionLabelFactory dlf = (DefinitionLabelFactory) PlatformUI.getWorkbench().getService(DefinitionLabelFactory.class);
		
		Composite page = new Composite(parent, SWT.NONE);
		page.setLayout(new GridLayout(4, false));
		
		// target label
		final String targetFt = getParent().getTargetItem().getParent().getName().getLocalPart();
		final String targetProperty = getParent().getTargetItem().getName().getLocalPart();
		
		Control targetLabel = dlf.createLabel(page, getParent().getTargetItem().getDefinition(), true);
		targetLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		
		// target value selection
		Combo combo = new Combo(page, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		classes = new ComboViewer(combo);
		classes.setContentProvider(new ArrayContentProvider());
		classes.setInput(classifications.keySet());
		
		// add target value
		Button addButton = new Button(page, SWT.PUSH);
		addButton.setImage(addImage);
		addButton.setToolTipText(Messages.ClassificationMappingPage_2);
		addButton.setEnabled(!fixedClassifications);
		addButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				final Display display = Display.getCurrent();
				AttributeInputDialog dialog = new AttributeInputDialog(
						(AttributeDefinition) getParent().getTargetItem().getDefinition(),
						display.getActiveShell(),
						Messages.ClassificationMappingPage_3,
						MessageFormat.format(Messages.ClassificationMappingPage_0, targetFt, targetProperty));
				
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
		removeButton.setImage(removeImage);
		removeButton.setEnabled(false);
		removeButton.setToolTipText(Messages.ClassificationMappingPage_6);
		removeButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				final Display display = Display.getCurrent();
				
				String selectedClass = ((IStructuredSelection) classes.getSelection()).getFirstElement().toString();
				
				if (MessageDialog.openQuestion(
						display.getActiveShell(), 
						Messages.ClassificationMappingPage_7, 
						MessageFormat.format(Messages.ClassificationMappingPage_1,selectedClass))) {
					removeClassification(selectedClass);
				}
			}
			
		});
		
		// source label
		final String sourceFt = getParent().getSourceItem().getParent().getName().getLocalPart();
		final String sourceProperty = getParent().getSourceItem().getName().getLocalPart();
		
		Control sourceLabel = dlf.createLabel(page, getParent().getSourceItem().getDefinition(), true);
		sourceLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false, 1, 2));
		
		// list
		List list = new List(page, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		values = new ListViewer(list);
		values.setContentProvider(new ArrayContentProvider());
		
		// value list controls
		Composite listControls = new Composite(page, SWT.NONE);
		listControls.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
		listControls.setLayout(new GridLayout(2, true));
		
		final Button valueAdd = new Button(listControls, SWT.PUSH);
		valueAdd.setImage(addImage);
		valueAdd.setText(Messages.ClassificationMappingPage_10);
		valueAdd.setEnabled(false);
		valueAdd.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		valueAdd.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				final Display display = Display.getCurrent();
				
				String selectedClass = ((IStructuredSelection) classes.getSelection()).getFirstElement().toString();
				
				if (allowedValues == null) {
					// no restriction
					AttributeInputDialog dialog = new AttributeInputDialog(
							(AttributeDefinition) getParent().getSourceItem().getDefinition(),
							display.getActiveShell(), 
							Messages.ClassificationMappingPage_11, 
							MessageFormat.format(Messages.ClassificationMappingPage_4, sourceFt, sourceProperty, selectedClass, targetFt, targetProperty));
					
					if (dialog.open() == AttributeInputDialog.OK) {
						String newValue = dialog.getValueAsText();
						if (newValue != null) {
							addValue(newValue);
						}
					}
				}
				else {
					// restricted to the allowed values
					ListSelectionDialog dialog = new ListSelectionDialog(
							display.getActiveShell(), 
							allowedValues, 
							ArrayContentProvider.getInstance(), 
							new LabelProvider(), 
							MessageFormat.format(Messages.ClassificationMappingPage_5, selectedClass));
					dialog.setTitle(Messages.ClassificationMappingPage_19);
					
					if (dialog.open() == ListDialog.OK) {
						Object[] result = dialog.getResult();
						if (result != null) {
							for (Object newValue : result) {
								if (newValue != null) {
									addValue(newValue.toString());
								}
							}
						}
					}
				}
			}
			
		});
		
		final Button valueRemove = new Button(listControls, SWT.PUSH);
		valueRemove.setImage(removeImage);
		valueRemove.setText(Messages.ClassificationMappingPage_20);
		valueRemove.setEnabled(false);
		valueRemove.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		valueRemove.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				final Display display = Display.getCurrent();
				
				String selectedValue = ((IStructuredSelection) values.getSelection()).getFirstElement().toString();
				
				if (MessageDialog.openQuestion(
						display.getActiveShell(), 
						Messages.ClassificationMappingPage_21, 
						MessageFormat.format(Messages.ClassificationMappingPage_8, selectedValue))) {
					removeValue(selectedValue);
				}
			}
			
		});
		
		// set the control
		setControl(page);
		
		// combo selection change
		classes.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				boolean empty = event.getSelection().isEmpty();
				
				removeButton.setEnabled(!fixedClassifications && !empty);
				valueAdd.setEnabled(!empty);
				
				if (!empty) {
					String className = ((IStructuredSelection) event.getSelection()).getFirstElement().toString();
					
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
		
		// initialize selection
		if (!classifications.keySet().isEmpty()) {
			classes.setSelection(new StructuredSelection(classifications.keySet().iterator().next()));
		}
		
		updateMessage();
	}
	
	/**
	 * @return the classifications
	 */
	public Map<String, Set<String>> getClassifications() {
		return classifications;
	}

	/**
	 * Get the classification for the given value
	 * 
	 * @param value the value
	 * 
	 * @return the classification or <code>null</code> if the value is
	 *   not classified
	 */
	private String getClassification(String value) {
		for (Entry<String, Set<String>> entry : classifications.entrySet()) {
			if (entry.getValue().contains(value)) {
				return entry.getKey();
			}
		}
		
		return null;
	}

	/**
	 * Remove the given value from the current classification
	 * 
	 * @param selectedValue the value to remove
	 */
	protected void removeValue(String selectedValue) {
		String selectedClass = ((IStructuredSelection) classes.getSelection()).getFirstElement().toString();
		
		Set<String> valueSet = classifications.get(selectedClass);
		if (valueSet != null && valueSet.contains(selectedValue)) {
			valueSet.remove(selectedValue);
			values.refresh();
			
			updateMessage();
		}
	}

	/**
	 * Add a new value to the current classification
	 * 
	 * @param newValue the value to add
	 */
	protected void addValue(String newValue) {
		final Display display = Display.getCurrent();
		
		String selectedClass = ((IStructuredSelection) classes.getSelection()).getFirstElement().toString();
		
		// check for value in other classification
		final String oldClass = getClassification(newValue);
		if (oldClass == null ||
			MessageDialog.openConfirm(display.getActiveShell(),
					Messages.ClassificationMappingPage_24, 
					MessageFormat.format(Messages.ClassificationMappingPage_9, oldClass))) {
		
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
			
			updateMessage();
		}
	}

	/**
	 * Remove the given classification
	 * 
	 * @param selectedClass the classification to remove
	 */
	protected void removeClassification(String selectedClass) {
		if (classifications.containsKey(selectedClass)) {
			classifications.remove(selectedClass);
			classes.refresh();
			
			updateMessage();
		}
	}

	/**
	 * Add a new classification if it doesn't already exist
	 * 
	 * @param newClass the new classification
	 */
	protected void addClassification(String newClass) {
		if (!classifications.containsKey(newClass)) {
			classifications.put(newClass, new TreeSet<String>());
			classes.refresh();
			classes.setSelection(new StructuredSelection(newClass));
			
			updateMessage();
		}
	}

	/**
	 * Update the message
	 */
	private void updateMessage() {
		// check if there are any values that are not mapped
		if (allowedValues != null) {
			Set<String> notMapped = new HashSet<String>();
			for (String value : allowedValues) {
				boolean found = false;
				Iterator<Set<String>> classes = classifications.values().iterator();
				while (!found && classes.hasNext()) {
					found = classes.next().contains(value);
				}
				
				if (!found) {
					notMapped.add(value);
				}
			}
			
			if (notMapped.isEmpty()) {
				setMessage(Messages.ClassificationMappingPage_27, INFORMATION);
			}
			else {
				setMessage(Messages.ClassificationMappingPage_28 + 
						Arrays.toString(notMapped.toArray()), WARNING);
			}
		}
		else {
			setMessage(null);
		}
	}

	/**
	 * @see DialogPage#dispose()
	 */
	@Override
	public void dispose() {
		addImage.dispose();
		removeImage.dispose();
		
		super.dispose();
	}

	/**
	 * Add the given classifications
	 * 
	 * @param classifications the classifications
	 */
	public void addClassifications(Map<String, Set<String>> classifications) {
		this.classifications.putAll(classifications);
	}

	/**
	 * @param fixedClassifications the fixedClassifications to set
	 */
	public void setFixedClassifications(boolean fixedClassifications) {
		this.fixedClassifications = fixedClassifications;
	}

	/**
	 * @param allowedValues the allowed values to set
	 */
	public void setAllowedValues(Set<String> allowedValues) {
		this.allowedValues = allowedValues;
	}

}
