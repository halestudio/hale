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

package eu.esdihumboldt.hale.ui.cst.debug.metadata;

import eu.esdihumboldt.cst.extension.hooks.TransformationTreeHook.TreeState;

/**
 * Transformation tree metadata constants.
 * @author Simon Templer
 */
public interface TransformationTreeMetadata {
	
	/**
	 * Metadata key for the populated transformation tree of an instance.
	 * @see TreeState#SOURCE_POPULATED
	 */
	public static final String KEY_POPULATED_TREE = "cst.ttree.populated";

}
