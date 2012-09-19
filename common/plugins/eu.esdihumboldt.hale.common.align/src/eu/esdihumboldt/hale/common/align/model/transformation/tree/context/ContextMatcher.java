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

package eu.esdihumboldt.hale.common.align.model.transformation.tree.context;

import eu.esdihumboldt.hale.common.align.model.transformation.tree.SourceNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree;

/**
 * Identifies context matches between source and target nodes in a
 * transformation tree.
 * 
 * @author Simon Templer
 */
public interface ContextMatcher {

	/**
	 * Find the context matches in the given transformation tree and applies
	 * {@link TransformationContext}s to the {@link SourceNode}s in the tree (if
	 * possible)
	 * 
	 * @param tree the transformation tree
	 */
	public void findMatches(TransformationTree tree);

}
