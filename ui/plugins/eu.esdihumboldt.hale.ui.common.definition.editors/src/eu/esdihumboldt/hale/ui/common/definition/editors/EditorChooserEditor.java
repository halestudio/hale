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

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
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

import eu.esdihumboldt.hale.common.scripting.ScriptExtension;
import eu.esdihumboldt.hale.ui.common.Editor;
import eu.esdihumboldt.hale.ui.common.editors.AbstractEditor;
import eu.esdihumboldt.hale.ui.common.editors.BooleanEditor;
import eu.esdihumboldt.hale.ui.scripting.ScriptUI;
import eu.esdihumboldt.hale.ui.scripting.ScriptUIExtension;

/**
 * Editor that provides a drop down to select from a list of available editors.
 * 
 * @author Kai Schwierczek
 * @param <T> the attribute value type/binding
 */
public abstract class EditorChooserEditor<T> extends AbstractEditor<T> {

	private final Composite composite;
	private final ComboViewer comboViewer;
	private Editor<T> currentEditor;
	private String currentScriptId;
	private final Class<? extends T> binding;
	private final Entry defaultEntry;

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
				return ((Entry) element).scriptId;
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

		Collection<Entry> availableEditors = new ArrayList<Entry>();
		defaultEntry = new Entry("default", null);
		availableEditors.add(defaultEntry);

		for (String scriptId : ScriptExtension.getInstance().getScripts(binding)) {
			ScriptUI scriptUI = ScriptUIExtension.getInstance().getScriptUI(scriptId);
			if (scriptUI != null)
				availableEditors.add(new Entry(scriptId, scriptUI));
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
		if (currentEditor != null) {
			currentValue = currentEditor.getAsText();
			currentEditor.getControl().dispose();
		}

		currentScriptId = newEditor.scriptId;
		if ("default".equals(currentScriptId)) {
			if (Boolean.class.equals(binding))
				currentEditor = (Editor<T>) new BooleanEditor(composite);
			else
				currentEditor = createDefaultEditor(composite);
		}
		else
			currentEditor = (Editor<T>) newEditor.scriptUI.createEditor(composite, binding);

		currentEditor.getControl().setLayoutData(
				GridDataFactory.fillDefaults().grab(true, true).create());
		if (currentValue != null)
			currentEditor.setAsText(currentValue);

		composite.getParent().layout(true, true);
	}

	/**
	 * Create the default editor for this chooser.
	 * 
	 * @param parent the parent composite
	 * @return the created editor
	 */
	protected abstract Editor<T> createDefaultEditor(Composite parent);

	/**
	 * @see eu.esdihumboldt.hale.ui.common.Editor#getControl()
	 */
	@Override
	public Control getControl() {
		return composite;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.Editor#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(T value) {
		currentEditor.setValue(value);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.Editor#getValue()
	 */
	@Override
	public T getValue() {
		return currentEditor.getValue();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.Editor#setAsText(java.lang.String)
	 */
	@Override
	public void setAsText(String text) {
		currentEditor.setAsText(text);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.Editor#getAsText()
	 */
	@Override
	public String getAsText() {
		return currentEditor.getAsText();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.Editor#isValid()
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
	 * Model for chooser list.
	 */
	private static class Entry {

		String scriptId;
		ScriptUI scriptUI;

		/**
		 * @param scriptId the script id this ui is for
		 * @param scriptUI the ScriptUI object
		 */
		protected Entry(String scriptId, ScriptUI scriptUI) {
			super();
			this.scriptId = scriptId;
			this.scriptUI = scriptUI;
		}
	}
}
