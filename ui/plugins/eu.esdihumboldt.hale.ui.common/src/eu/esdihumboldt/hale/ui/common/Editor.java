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

package eu.esdihumboldt.hale.ui.common;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.widgets.Control;

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
}
