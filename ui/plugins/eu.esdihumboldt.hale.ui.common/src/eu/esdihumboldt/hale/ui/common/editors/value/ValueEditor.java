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

package eu.esdihumboldt.hale.ui.common.editors.value;

import java.util.Collection;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Control;

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.util.VariableReplacer;
import eu.esdihumboldt.hale.ui.common.AttributeEditor;

/**
 * Wraps an existing editor as a {@link Value} editor.
 * 
 * @param <T> the object type of the internal editor
 * @author Simon Templer
 */
public abstract class ValueEditor<T> implements AttributeEditor<Value> {

	private final AttributeEditor<T> editor;
	private volatile IPropertyChangeListener listener;
	private final IPropertyChangeListener listenerDelegate = new IPropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent event) {
			IPropertyChangeListener l = listener;
			if (l != null) {
				if (VALUE.equals(event.getProperty())) {
					@SuppressWarnings("unchecked")
					PropertyChangeEvent convEvent = new PropertyChangeEvent(ValueEditor.this,
							event.getProperty(), toValue((T) event.getOldValue()),
							toValue((T) event.getNewValue()));
					l.propertyChange(convEvent);
				}
				else {
					l.propertyChange(event);
				}
			}
		}
	};

	/**
	 * Create an editor wrapping the given editor.
	 * 
	 * @param editor the editor to wrap
	 */
	public ValueEditor(AttributeEditor<T> editor) {
		super();
		this.editor = editor;

		editor.setPropertyChangeListener(listenerDelegate);
	}

	@Override
	public Control getControl() {
		return editor.getControl();
	}

	@Override
	public void setValue(Value value) {
		editor.setValue(fromValue(value));
	}

	/**
	 * Convert a value to the editor supported value.
	 * 
	 * @param value the value to convert
	 * @return the editor supported value
	 */
	protected abstract T fromValue(Value value);

	@Override
	public Value getValue() {
		return toValue(editor.getValue());
	}

	/**
	 * Convert an editor supported value to a value.
	 * 
	 * @param value the value to convert
	 * @return the {@link Value}
	 */
	protected abstract Value toValue(T value);

	@Override
	public void setAsText(String text) {
		editor.setAsText(text);
	}

	@Override
	public String getAsText() {
		return editor.getAsText();
	}

	@Override
	public boolean isValid() {
		return editor.isValid();
	}

	@Override
	public void setPropertyChangeListener(IPropertyChangeListener listener) {
		this.listener = listener;
	}

	@Override
	public void setVariables(Collection<PropertyEntityDefinition> properties) {
		editor.setVariables(properties);
	}

	@Override
	public String getValueType() {
		return editor.getValueType();
	}

	@Override
	public void setVariableReplacer(VariableReplacer variableReplacer) {
		editor.setVariableReplacer(variableReplacer);
	}

}
