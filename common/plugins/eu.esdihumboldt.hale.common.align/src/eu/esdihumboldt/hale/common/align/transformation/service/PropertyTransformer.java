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

package eu.esdihumboldt.hale.common.align.transformation.service;

import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.instance.model.FamilyInstance;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;

/**
 * Executes property transformations on source/target instance pairs.
 * @author Simon Templer
 */
public interface PropertyTransformer {
	
	/**
	 * Publish a source/target instance pair for property transformation.
	 * 
	 * @param source the source instances
	 * @param target the target instance
	 * @param typeLog the type transformation log 
	 */
	public void publish(FamilyInstance source, MutableInstance target,
			TransformationLog typeLog);

	/**
	 * Join with the property transformer and wait for its completion, e.g. 
	 * if the property transformer executes tasks in worker threads.
	 * @param cancel if still pending transformations should be canceled
	 */
	void join(boolean cancel);

}
