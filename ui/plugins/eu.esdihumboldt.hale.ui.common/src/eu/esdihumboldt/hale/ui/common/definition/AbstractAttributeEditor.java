/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.common.definition;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import eu.esdihumboldt.hale.ui.common.Editor;


/**
 * Abstract base class for editors for events.
 * 
 * @author Kai Schwierczek
 * @param <T> the attribute value type/binding
 */
public abstract class AbstractAttributeEditor<T> implements Editor<T> {
	private IPropertyChangeListener propertyChangeListener;

	/**
	 * @see eu.esdihumboldt.hale.ui.common.Editor#setPropertyChangeListener(org.eclipse.jface.util.IPropertyChangeListener)
	 */
	@Override
	public void setPropertyChangeListener(IPropertyChangeListener listener) {
		propertyChangeListener = listener;
	}

	/**
     * Informs this editor's listener, if it has one, about a change to
     * one of this editor's boolean-valued properties. Does nothing
     * if the old and new values are the same.
     *
     * @param property the editor property name, 
     *   such as <code>VALUE</code> or <code>IS_VALID</code>
     * @param oldValue the old value
     * @param newValue the new value
     */
    protected void fireStateChanged(String property, boolean oldValue,
            boolean newValue) {
        if (oldValue == newValue) {
			return;
		}
        fireValueChanged(property, oldValue ? Boolean.TRUE : Boolean.FALSE, newValue ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Informs this editor's listener, if it has one, about a change to
     * one of this editor's properties.
     *
     * @param property the editor property name, 
     *   such as <code>VALUE</code> or <code>IS_VALID</code>
     * @param oldValue the old value object, or <code>null</code>
     * @param newValue the new value, or <code>null</code>
     */
    protected void fireValueChanged(String property, Object oldValue,
            Object newValue) {
        if (propertyChangeListener == null) {
			return;
		}
        propertyChangeListener.propertyChange(new PropertyChangeEvent(this,
                property, oldValue, newValue));
    }
}
