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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.common.definition.editors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.scripting.ScriptExtension;
import eu.esdihumboldt.hale.common.scripting.ScriptFactory;
import eu.esdihumboldt.hale.ui.common.AttributeEditor;
import eu.esdihumboldt.hale.ui.common.editors.AbstractAttributeEditor;
import eu.esdihumboldt.hale.ui.common.editors.BooleanEditor;
import eu.esdihumboldt.hale.ui.scripting.ScriptUI;
import eu.esdihumboldt.hale.ui.scripting.ScriptUIExtension;

/**
 * Editor that provides a drop down to select from a list of available editors.
 * 
 * @author Kai Schwierczek
 * @param <T> the attribute value type/binding
 */
public abstract class EditorChooserEditor<T> extends AbstractAttributeEditor<T> implements
		IPropertyChangeListener {

	private final Composite composite;
	private final ComboViewer comboViewer;
	private AttributeEditor<T> currentEditor;
	private String currentScriptId;
	private final Class<? extends T> binding;
	private final Entry defaultEntry;
	private final Collection<Entry> availableEditors;
	private Collection<PropertyEntityDefinition> properties = Collections.emptySet();

	/**
	 * Constructs the editor chooser.
	 * 
	 * @param parent the parent composite
	 * @param binding the binding
	 */
	public EditorChooserEditor(Composite parent, Class<? extends T> binding) {
		this.binding = binding;

		composite = new Composite(parent, SWT.NONE);
		composite.setLayout(GridLayoutFactory.fillDefaults().create());

		comboViewer = new ComboViewer(composite, SWT.BORDER | SWT.READ_ONLY);
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new LabelProvider() {

			/**
			 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				return ((Entry) element).displayName;
			}
		});
		comboViewer.addPostSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Entry newSelection = (Entry) ((IStructuredSelection) event.getSelection())
						.getFirstElement();
				editorSelected(newSelection);
			}
		});

		availableEditors = new ArrayList<Entry>();
		defaultEntry = new Entry("default", "Plain value", null);
		availableEditors.add(defaultEntry);

		for (ScriptFactory script : ScriptExtension.getInstance().getScripts(binding)) {
			ScriptUI scriptUI = ScriptUIExtension.getInstance().getScriptUI(script.getIdentifier());
			if (scriptUI != null)
				availableEditors.add(new Entry(script.getIdentifier(), script.getDisplayName(),
						scriptUI));
		}

		comboViewer.setInput(availableEditors);
	}

	/**
	 * Preselects the default editor.
	 */
	public void selectDefaultEditor() {
		comboViewer.setSelection(new StructuredSelection(defaultEntry));
	}

	/**
	 * Handles a selection change.
	 * 
	 * @param newEditor the new selection
	 */
	@SuppressWarnings("unchecked")
	private void editorSelected(Entry newEditor) {
		if (newEditor.scriptId.equals(currentScriptId))
			return;

		String currentValue = "";
		boolean oldValid = false;
		if (currentEditor != null) {
			currentValue = currentEditor.getAsText();
			oldValid = currentEditor.isValid();
			currentEditor.setPropertyChangeListener(null);
			currentEditor.getControl().dispose();
		}

		currentScriptId = newEditor.scriptId;
		if ("default".equals(currentScriptId)) {
			if (Boolean.class.equals(binding))
				currentEditor = (AttributeEditor<T>) new BooleanEditor(composite);
			else
				currentEditor = createDefaultEditor(composite);
		}
		else
			currentEditor = (AttributeEditor<T>) newEditor.scriptUI.createEditor(composite, binding);

		currentEditor.getControl().setLayoutData(
				GridDataFactory.fillDefaults().grab(true, true).create());
		if (currentValue != null)
			currentEditor.setAsText(currentValue);
		currentEditor.setVariables(properties);
		currentEditor.setPropertyChangeListener(this);

		if (currentEditor.isValid() != oldValid)
			fireStateChanged(IS_VALID, oldValid, currentEditor.isValid());

		composite.getParent().layout(true, true);
	}

	/**
	 * Create the default editor for this chooser.
	 * 
	 * @param parent the parent composite
	 * @return the created editor
	 */
	protected abstract AttributeEditor<T> createDefaultEditor(Composite parent);

	/**
	 * @see eu.esdihumboldt.hale.ui.common.AttributeEditor#getControl()
	 */
	@Override
	public Control getControl() {
		return composite;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.AttributeEditor#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(T value) {
		currentEditor.setValue(value);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.AttributeEditor#getValue()
	 */
	@Override
	public T getValue() {
		return currentEditor.getValue();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.AttributeEditor#setAsText(java.lang.String)
	 */
	@Override
	public void setAsText(String text) {
		currentEditor.setAsText(text);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.AttributeEditor#getAsText()
	 */
	@Override
	public String getAsText() {
		return currentEditor.getAsText();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.AttributeEditor#isValid()
	 */
	@Override
	public boolean isValid() {
		if (currentEditor == null)
			return false;
		else
			return currentEditor.isValid();
	}

	/**
	 * Returns the used script id which depends on the used editor.
	 * 
	 * @return the used script id
	 */
	public String getUsedScriptId() {
		return currentScriptId;
	}

	/**
	 * Select the specified editor if available.
	 * 
	 * @param scriptId the script id for which to select an editor or
	 *            {@link ParameterValue#DEFAULT_TYPE}
	 */
	public void selectEditor(String scriptId) {
		for (Entry entry : availableEditors)
			if (entry.scriptId.equals(scriptId)) {
				comboViewer.setSelection(new StructuredSelection(entry));
				return;
			}
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.editors.AbstractAttributeEditor#setVariables(java.util.Collection)
	 */
	@Override
	public void setVariables(Collection<PropertyEntityDefinition> properties) {
		this.properties = properties;
		if (currentEditor != null)
			currentEditor.setVariables(properties);
	}

	/**
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		fireValueChanged(event.getProperty(), event.getOldValue(), event.getNewValue());
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.AttributeEditor#getValueType()
	 */
	@Override
	public String getValueType() {
		if (currentEditor == null)
			return ParameterValue.DEFAULT_TYPE;
		else
			return currentEditor.getValueType();
	}

	/**
	 * Model for chooser list.
	 */
	private static class Entry {

		final String scriptId;
		final String displayName;
		final ScriptUI scriptUI;

		/**
		 * @param scriptId the script id this ui is for
		 * @param displayName the display name of the script
		 * @param scriptUI the ScriptUI object
		 */
		protected Entry(String scriptId, String displayName, ScriptUI scriptUI) {
			super();
			this.scriptId = scriptId;
			this.displayName = displayName;
			this.scriptUI = scriptUI;
		}
	}
}
