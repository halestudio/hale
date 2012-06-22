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
package eu.esdihumboldt.hale.ui.style.editors;

import java.net.MalformedURLException;

import org.eclipse.swt.widgets.Control;


/**
 * Editor interface
 * @param <T> the type to edit
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface Editor<T> {
	
	/**
	 * Get the value
	 * 
	 * @return the value
	 * @throws Exception 
	 */
	public abstract T getValue() throws Exception;
	
	/**
	 * Set the value
	 * 
	 * @param value the value to set
	 */
	public abstract void setValue(T value);
	
	/**
	 * Get the editor control
	 * 
	 * @return the editor control
	 */
	public Control getControl();
	
	/**
	 * States if the value has been changed
	 * 
	 * @return if the value has been changed
	 */
	public boolean isChanged();

}
