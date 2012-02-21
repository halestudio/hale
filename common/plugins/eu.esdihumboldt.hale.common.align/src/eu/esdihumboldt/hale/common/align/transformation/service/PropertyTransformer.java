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

import java.util.Collection;

import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;

/**
 * Executes property transformations on source/target instance pairs.
 * @author Simon Templer
 */
public interface PropertyTransformer {
	
	/**
	 * Publish a source/target instance pair for property transformation.
	 * @param sourceTypes the source types associated with the source instance
	 *   (needed for determining the property transformations that apply)
	 * @param source the source instance
	 * @param target the target instance
	 */
	public void publish(Collection<? extends Type> sourceTypes, 
			Instance source, MutableInstance target);

	/**
	 * Join with the property transformer and wait for its completion, e.g. 
	 * if the property transformer executes tasks in worker threads.
	 */
	void join();

}
