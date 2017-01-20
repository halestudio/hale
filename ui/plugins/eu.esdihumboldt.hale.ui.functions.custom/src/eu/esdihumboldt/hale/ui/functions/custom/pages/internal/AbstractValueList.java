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

package eu.esdihumboldt.hale.ui.functions.custom.pages.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import javax.annotation.Nullable;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import eu.esdihumboldt.hale.ui.common.CommonSharedImages;
import eu.esdihumboldt.hale.ui.common.Editor;

/**
 * Represents a list of editable parameters.
 * 
 * @param <T> the type of object to edit
 * @param <C> the editor control type for an object
 * @author Simon Templer
 */
public abstract class AbstractValueList<T, C extends Editor<T>> extends Observable {

	private class EditorWrapper<X> {

		private final C editor;
		private final Control mainControl;

		public EditorWrapper(C editor, Control mainControl) {
			super();
			this.editor = editor;
			this.mainControl = mainControl;
		}

		public C getEditor() {
			return editor;
		}

		public Control getMainControl() {
			return mainControl;
		}

	}

	private final List<C> editors = new ArrayList<C>();
	private final Map<C, EditorWrapper<C>> wrappers = new IdentityHashMap<>();
	private final Composite editorContainer;

	private final boolean valid = false;

	private final IPropertyChangeListener propertyChangeListener;
	private Control addControl;

	/**
	 * Create a parameter list.
	 * 
	 * @param caption the list caption
	 * @param description the list description
	 * @param parent the parent composite
	 * @param params the existing parameter values
	 */
	public AbstractValueList(@Nullable String caption, @Nullable String description,
			final Composite parent, List<T> params) {
		super();

		ControlDecoration descriptionDecoration = null;

		// caption and description
		if (caption != null) {
			Label name = new Label(parent, SWT.NONE);
			name.setText(caption);
			name.setLayoutData(GridDataFactory.swtDefaults().create());

			if (description != null) {
				// add decoration
				descriptionDecoration = new ControlDecoration(name, SWT.RIGHT, parent);
			}
		}

		editorContainer = new Composite(parent, SWT.NONE);
		editorContainer.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		// left margin 6 pixels for ControlDecorations to have place within this
		// component
		// so they're not drawn outside of the ScrolledComposite in case it's
		// present.
		editorContainer
				.setLayout(GridLayoutFactory.fillDefaults().extendedMargins(6, 0, 0, 0).create());

		propertyChangeListener = new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				// TODO check event type?

				int changedIndex = editors.indexOf(event.getSource());
				@SuppressWarnings("unused")
				C changedEditor = editors.get(changedIndex);

//				// add/remove selector
//				// check whether all selectors are valid (so must the changed
//				// one be)
//				if (countValidEntities() == editors.size()) {
//					// maybe last invalid entity was set, check whether to add
//					// another one
//					if (AbstractParameterList.this.definition.getMaxOccurrence() != editors.size()) {
//						S newSelector = createEditor(AbstractParameterList.this.ssid,
//								AbstractParameterList.this.definition, editorContainer);
//						newSelector.getControl().setLayoutData(
//								GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER)
//										.grab(true, false).create());
//						addSelector(newSelector);
//
//						// layout new selector in scrolled pane
//						editorContainer.getParent().getParent().layout();
//					}
//				}
//				else {
//					// check whether a field was set to None and remove the
//					// field if it isn't the last one and minOccurrence is still
//					// met
//					if (event.getSelection().isEmpty()
//							&& changedIndex != editors.size() - 1
//							&& AbstractParameterList.this.definition.getMinOccurrence() < editors
//									.size()) {
//						// check whether first selector will be removed and it
//						// had the fields description
//						boolean createDescriptionDecoration = changedIndex == 0
//								&& AbstractParameterList.this.definition.getDisplayName().isEmpty()
//								&& !AbstractParameterList.this.definition.getDescription()
//										.isEmpty();
//						removeSelector(changedEditor);
//
//						// add new description decoration if necessary
//						if (createDescriptionDecoration) {
//							ControlDecoration descriptionDecoration = new ControlDecoration(editors
//									.get(0).getControl(), SWT.RIGHT | SWT.TOP, parent);
//							descriptionDecoration
//									.setDescriptionText(AbstractParameterList.this.definition
//											.getDescription());
//							FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault()
//									.getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION);
//							descriptionDecoration.setImage(fieldDecoration.getImage());
//							descriptionDecoration.setMarginWidth(2);
//						}
//
//						// necessary layout call for control decoration to
//						// appear at the correct place
//						editorContainer.getParent().getParent().layout();
//
//						// add mandatory decoration to next selector if needed
//						if (changedIndex < AbstractParameterList.this.definition.getMinOccurrence()) {
//							S newMandatorySelector = editors
//									.get(AbstractParameterList.this.definition.getMinOccurrence() - 1);
//
//							ControlDecoration mandatory = new ControlDecoration(
//									newMandatorySelector.getControl(), SWT.LEFT | SWT.TOP, parent);
//							FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault()
//									.getFieldDecoration(FieldDecorationRegistry.DEC_REQUIRED);
//							mandatory.setImage(CommonSharedImages.getImageRegistry().get(
//									CommonSharedImages.IMG_DECORATION_MANDATORY));
//							mandatory.setDescriptionText(fieldDecoration.getDescription());
//						}
//					}
//				}

				// update state
				updateState();
			}
		};

		// add initial fields
		if (params != null) {
			for (T param : params) {
				// create editor
				EditorWrapper<C> wrapper = createEditorWrapper(editorContainer, param);

				// add the editor now
				addEditor(wrapper);
			}
		}

		createAddControl();

		// setup description decoration
		if (descriptionDecoration != null) {
			descriptionDecoration.setDescriptionText(description);
			FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault()
					.getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION);
			descriptionDecoration.setImage(fieldDecoration.getImage());
			descriptionDecoration.setMarginWidth(2);
		}

		updateLayout();

		updateState();
	}

	@SuppressWarnings("javadoc")
	protected void updateLayout() {
		// layout new editor in scrolled pane
		// FIXME this is currently very specific!
		editorContainer.getParent().getParent().layout();
	}

	private EditorWrapper<C> createEditorWrapper(Composite parent, T value) {
		if (addControl != null) {
			addControl.dispose();
			addControl = null;
		}

		// create control that encompasses editor and remove button
//		Composite comp = new Composite(parent, SWT.NONE);
		Group comp = new Group(parent, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(comp);

		// editor
		final C editor = createEditor(comp);
		editor.setValue(value);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(editor.getControl());

		// remove button
		Button remove = new Button(comp, SWT.PUSH);
		remove.setToolTipText("Remove");
		remove.setImage(CommonSharedImages.getImageRegistry().get(CommonSharedImages.IMG_REMOVE));
		GridDataFactory.swtDefaults().align(SWT.END, SWT.BEGINNING).applyTo(remove);
		remove.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				removeEditor(editor);
			}
		});

		// readd add control after editor
		createAddControl();

		// create wrapper
		comp.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER)
				.grab(true, false).create());
		return new EditorWrapper<>(editor, comp);
	}

	private void createAddControl() {
		if (addControl != null) {
			return;
		}

		GridDataFactory addData = GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.BEGINNING);

		Composite addComp = new Composite(editorContainer, SWT.NONE);
		addData.applyTo(addComp);
		GridLayoutFactory.swtDefaults().numColumns(1).applyTo(addComp);
		Button add = new Button(addComp, SWT.PUSH);
		addData.applyTo(add);
		add.setToolTipText("Add");
		add.setImage(CommonSharedImages.getImageRegistry().get(CommonSharedImages.IMG_ADD));
		add.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// create editor
				EditorWrapper<C> wrapper = createEditorWrapper(editorContainer, null);

				// add the editor now
				addEditor(wrapper);

				updateLayout();
			}
		});

		addControl = addComp;
	}

	/**
	 * Create an editor.
	 * 
	 * @param parent the parent composite
	 * @return the editor
	 */
	protected abstract C createEditor(Composite parent);

	/**
	 * Get the editors.
	 * 
	 * @return the list of editors.
	 */
	protected List<C> getEditors() {
		return Collections.unmodifiableList(editors);
	}

	/**
	 * Add an editor.
	 * 
	 * @param wrapper the editor wrapper of the editor to add
	 */
	protected void addEditor(EditorWrapper<C> wrapper) {
		editors.add(wrapper.getEditor());
		wrappers.put(wrapper.getEditor(), wrapper);
		wrapper.getEditor().setPropertyChangeListener(propertyChangeListener);
	}

	/**
	 * Remove an editor.
	 * 
	 * @param editor the editor to remove
	 */
	protected void removeEditor(C editor) {
		editor.setPropertyChangeListener(null);
		editors.remove(editor);
		EditorWrapper<C> wrapper = wrappers.remove(editor);
		if (wrapper != null) {
			wrapper.getMainControl().dispose();
		}
		else {
			editor.getControl().dispose();
		}
	}

//	/**
//	 * Counts valid entities.
//	 * 
//	 * @return number of valid entities
//	 */
//	private int countValidEntities() {
//		int validCount = 0;
//		for (EntitySelector<F> selector : editors) {
//			if (!selector.getSelection().isEmpty()) // TODO improve condition
//				validCount++;
//		}
//		return validCount;
//	}

	/**
	 * Updates the valid state
	 */
	private void updateState() {
//		boolean newValid = countValidEntities() >= definition.getMinOccurrence();
//		boolean change = newValid != valid;
//		valid = newValid;
//		if (change) {
//			setChanged();
//			notifyObservers();
//		}
	}

	/**
	 * Determines if the field is valid in its current configuration
	 * 
	 * @return if the field is valid
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * @return the values contained in the list editor
	 */
	public List<T> getValues() {
		List<T> result = new ArrayList<>();
		for (C editor : editors) {
			result.add(editor.getValue());
		}
		return result;
	}
}
