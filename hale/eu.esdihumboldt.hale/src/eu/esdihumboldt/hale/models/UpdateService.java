/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.models;

/**
 * This type should be used as a base interface by all Services in HALE
 * whose content has to be synchronized in different views.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public interface UpdateService {
	
	/**
	 * This operation can be used to notify this service of a ServiceListener 
	 * that wants to be informed when the model is updated.
	 * @param sl the {@link HaleServiceListener} that wants to be informed.
	 * @return
	 */
	public boolean addListener(HaleServiceListener sl);
	
	/**
	 * Remove a listener
	 * 
	 * @param listener the listener to remove
	 */
	public void removeListener(HaleServiceListener listener);

}
