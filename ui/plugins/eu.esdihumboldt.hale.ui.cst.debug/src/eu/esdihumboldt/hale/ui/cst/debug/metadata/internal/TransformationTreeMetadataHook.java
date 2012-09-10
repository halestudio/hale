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

package eu.esdihumboldt.hale.ui.cst.debug.metadata.internal;

import eu.esdihumboldt.cst.extension.hooks.TransformationTreeHook;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.ui.cst.debug.metadata.TransformationTreeMetadata;

/**
 * Transformation tree hook storing the transformation tree in the instance
 * metadata.
 * 
 * @author Simon Templer
 */
public class TransformationTreeMetadataHook implements TransformationTreeHook,
		TransformationTreeMetadata {

	/**
	 * @see TransformationTreeHook#processTransformationTree(TransformationTree,
	 *      TreeState, MutableInstance)
	 */
	@Override
	public void processTransformationTree(TransformationTree tree, TreeState state,
			MutableInstance target) {
		if (state == TreeState.SOURCE_POPULATED) { // TODO key per state - for
													// now only support this
													// state
			// TODO get "serializable tree"
			// suggestion: write dot export based on a transformation tree
			// visitor

			// TODO store tree as metadata
//			target.setMetaData(KEY_POPULATED_TREE, treeVal);
			// XXX dummy
			target.setMetaData(KEY_POPULATED_TREE, "TODO");
		}
	}

}
