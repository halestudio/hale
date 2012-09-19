/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.cst.extension.hooks;

import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;

/**
 * Transformation tree hook, called in preparation of property transformation.
 * 
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
	 * transformation of an instance. Hooks should either be stateless or thread
	 * safe.
	 * 
	 * @param tree the transformation tree
	 * @param state the tree state
	 * @param target the target instance
	 */
	public void processTransformationTree(TransformationTree tree, TreeState state,
			MutableInstance target); // XXX expose target instance for this
										// method? or only metadata?!

}
