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

package eu.esdihumboldt.hale.align.transformation.model;

import com.google.common.collect.Multimap;

import eu.esdihumboldt.hale.align.transformation.report.TransformationReporter;

/**
 * Common interface for all transformation functions
 * @author Simon Templer
 */
public interface TransformationFunction {
	
	/**
	 * Sets the parameters for the transformation.
	 * @param parameters the transformation parameters
	 */
	public void setParameters(Multimap<String, String> parameters);
	
	/**
	 * Execute the function as configured.
	 * @param report the transformation report to add any information about the
	 *   execution of the transformation to
	 */
	public void execute(TransformationReporter report);
	
	//TODO reset method? or something like it
	
}
