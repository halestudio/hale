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

package eu.esdihumboldt.cst.extension.hooks;

import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;

/**
 * Transformation tree hook, called in preparation of property transformation.
 * @author Simon Templer
 */
public interface TransformationTreeHook {
	
	/**
	 * Classification of transformation tree states.
	 */
	public enum TreeState {
		
		/**
		 * Minimal tree derived from the alignment.
		 */
		MINIMAL,
		/**
		 * Tree populated with source values (including duplication).
		 */
		SOURCE_POPULATED,
		/**
		 * Full tree with source and target populated.
		 */
		FULL;

	}
	
	/**
	 * Process the given transformation tree, called before property
	 * transformation of an instance. Hooks should either be stateless or 
	 * thread safe.
	 * @param tree the transformation tree
	 * @param state the tree state
	 * @param target the target instance
	 */
	public void processTransformationTree(TransformationTree tree, TreeState state,
			MutableInstance target); //XXX expose target instance for this method? or only metadata?!

}
