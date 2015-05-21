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

package eu.esdihumboldt.hale.ui.common;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.widgets.Control;

/**
 * Attribute editor interface
 * 
 * @param <T> the attribute value type/binding
 * 
 * @author Simon Templer
 */
public interface Editor<T> {

	/**
	 * Property name constant (value <code>"editor_is_valid"</code>) to signal a
	 * change in the validity of the value of this field editor.
	 */
	public static final String IS_VALID = "editor_is_valid"; //$NON-NLS-1$

	/**
	 * Property name constant (value <code>"editor_value"</code>) to signal a
	 * change in the value of this field editor.
	 */
	public static final String VALUE = "editor_value"; //$NON-NLS-1$

	/**
	 * Get the editor control
	 * 
	 * @return the editor control
	 */
	public Control getControl();

	/**
	 * Set the editor value
	 * 
	 * @param value the value
	 */
	public void setValue(T value);

	/**
	 * Get the editor value
	 * 
	 * @return the editor value
	 */
	public T getValue();

	/**
	 * Determines if the user has entered a valid value
	 * 
	 * @return if the value is valid
	 */
	public boolean isValid();

	/**
	 * Sets or removes the property change listener for this editor.
	 * <p>
	 * Note that editors can support only a single listener.
	 * </p>
	 * 
	 * @param listener a property change listener, or <code>null</code> to
	 *            remove
	 */
	public void setPropertyChangeListener(IPropertyChangeListener listener);

}
