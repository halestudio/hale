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

import java.util.Collection;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.widgets.Control;

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;

/**
 * Attribute editor interface
 * 
 * @param <T> the attribute value type/binding
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
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
	 * Set the editor value as text
	 * 
	 * @param text the value to set as text
	 */
	public void setAsText(String text);

	/**
	 * Get the editor value as text
	 * 
	 * @return the text representation of the editor value
	 */
	public String getAsText();

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

	/**
	 * Sets available variables. Editors may ignore this.
	 * 
	 * @param properties the property variables
	 */
	public void setVariables(Collection<PropertyEntityDefinition> properties);
}
