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

package eu.esdihumboldt.hale.core.report;

/**
 * Report message
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2 
 */
public interface Message {

	/**
	 * Get the message string
	 * 
	 * @return the message string
	 */
	public String getMessage();
	
	/**
	 * Get the associated throwable
	 * 
	 * @return the associated throwable, may be <code>null</code>
	 */
	public Throwable getThrowable();
	
}
