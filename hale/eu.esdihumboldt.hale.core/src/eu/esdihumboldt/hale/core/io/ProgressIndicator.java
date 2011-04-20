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

package eu.esdihumboldt.hale.core.io;

/**
 * Progress indicator
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2 
 */
public interface ProgressIndicator {
	
	/**
	 * Start the progress tracking
	 * 
	 * @param taskName the main task name
	 * @param undetermined if the progress is undetermined (i.e. 
	 *   {@link #setProgress(float)} will not be called)
	 */
	public void begin(String taskName, boolean undetermined);
	
	/**
	 * Sets the current task name
	 * 
	 * @param taskName the task name
	 */
	public void setCurrentTask(String taskName);
	
	/**
	 * Set the current progress
	 * 
	 * @param percent the progress in percent [0..100]
	 */
	public void setProgress(float percent);
	
	/**
	 * States if the execution was canceled
	 * 
	 * @return if the execution was canceled
	 */
	public boolean isCanceled();
	
	/**
	 * Stop the progress tracking, signaling that the task is done
	 */
	public void end();

}
