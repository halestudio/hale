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
 * The {@link HaleServiceListener} allows views to be notified when a
 * service's data has been updated, such as by loading a new schema.
 * It has to be implemented by Views wanting to be notified.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public interface HaleServiceListener {
	
	/**
	 * This method is called by the service when it's internal state 
	 * changes.
	 */
	public void update(UpdateMessage message);

}
