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

package eu.esdihumboldt.hale.ui.common.editors;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import eu.esdihumboldt.hale.ui.common.Editor;

/**
 * Abstract base class for editors for events.
 * 
 * @author Kai Schwierczek
 * @param <T> the attribute value type/binding
 */
public abstract class AbstractEditor<T> implements Editor<T> {

	private IPropertyChangeListener propertyChangeListener;

	/**
	 * @see eu.esdihumboldt.hale.ui.common.AttributeEditor#setPropertyChangeListener(org.eclipse.jface.util.IPropertyChangeListener)
	 */
	@Override
	public void setPropertyChangeListener(IPropertyChangeListener listener) {
		propertyChangeListener = listener;
	}

	/**
	 * Informs this editor's listener, if it has one, about a change to one of
	 * this editor's boolean-valued properties. Does nothing if the old and new
	 * values are the same.
	 * 
	 * @param property the editor property name, such as <code>VALUE</code> or
	 *            <code>IS_VALID</code>
	 * @param oldValue the old value
	 * @param newValue the new value
	 */
	protected void fireStateChanged(String property, boolean oldValue, boolean newValue) {
		if (oldValue == newValue) {
			return;
		}
		fireValueChanged(property, oldValue ? Boolean.TRUE : Boolean.FALSE, newValue ? Boolean.TRUE
				: Boolean.FALSE);
	}

	/**
	 * Informs this editor's listener, if it has one, about a change to one of
	 * this editor's properties.
	 * 
	 * @param property the editor property name, such as <code>VALUE</code> or
	 *            <code>IS_VALID</code>
	 * @param oldValue the old value object, or <code>null</code>
	 * @param newValue the new value, or <code>null</code>
	 */
	protected void fireValueChanged(String property, Object oldValue, Object newValue) {
		if (propertyChangeListener == null) {
			return;
		}
		propertyChangeListener.propertyChange(new PropertyChangeEvent(this, property, oldValue,
				newValue));
	}

}
