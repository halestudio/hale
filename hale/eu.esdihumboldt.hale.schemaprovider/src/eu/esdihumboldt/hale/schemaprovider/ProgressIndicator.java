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

package eu.esdihumboldt.hale.schemaprovider;

/**
 * Progress indicator
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public interface ProgressIndicator {
	
	/**
	 * Sets the current task name
	 * 
	 * @param taskName the task name
	 */
	public void setCurrentTask(String taskName);
	
	/**
	 * Set the current progress
	 * 
	 * @param percent the progress in percent
	 */
	public void setProgress(int percent);

}
