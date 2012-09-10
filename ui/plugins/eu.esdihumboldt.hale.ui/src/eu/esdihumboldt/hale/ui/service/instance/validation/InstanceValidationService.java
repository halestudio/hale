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

package eu.esdihumboldt.hale.ui.service.instance.validation;

/**
 * Service that listens to the instance service and validates instances.
 * 
 * @author Kai Schwierczek
 */
public interface InstanceValidationService {

	/**
	 * Adds a listener.
	 * 
	 * @param listener the listener to add
	 */
	public void addListener(InstanceValidationListener listener);

	/**
	 * Removes a listener.
	 * 
	 * @param listener the listener to remove
	 */
	public void removeListener(InstanceValidationListener listener);

	/**
	 * Returns whether the automatic instance validation after each
	 * transformation is enabled.
	 * 
	 * @return whether the automatic instance validation after each
	 *         transformation is enabled
	 */
	public boolean isValidationEnabled();

	/**
	 * Set whether the automatic instance validation after each transformation
	 * is enabled.
	 * 
	 * @param enable whether the automatic instance validation after each
	 *            transformation is enabled
	 */
	public void setValidationEnabled(boolean enable);
}
