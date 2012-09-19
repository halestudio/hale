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

package eu.esdihumboldt.hale.common.align.model.transformation.tree;

/**
 * Interface for transformation tree visitors.
 * 
 * @author Simon Templer
 */
public interface TransformationNodeVisitor {

	/**
	 * Visit a transformation tree root.
	 * 
	 * @param root the transformation tree root
	 * @return if the visitor is to be applied to any further nodes down the
	 *         path
	 */
	public boolean visit(TransformationTree root);

	/**
	 * Called after a transformation tree root has been visited.
	 * 
	 * @param root the transformation tree root
	 */
	public void leave(TransformationTree root);

	/**
	 * Visit a target node.
	 * 
	 * @param target the target node
	 * @return if the visitor is to be applied to any further nodes down the
	 *         path
	 */
	public boolean visit(TargetNode target);

	/**
	 * Called after a target node has been visited.
	 * 
	 * @param target the target node
	 */
	public void leave(TargetNode target);

	/**
	 * Visit a cell node.
	 * 
	 * @param cell the cell node
	 * @return if the visitor is to be applied to any further nodes down the
	 *         path
	 */
	public boolean visit(CellNode cell);

	/**
	 * Called after a cell node has been visited.
	 * 
	 * @param cell the cell node
	 */
	public void leave(CellNode cell);

	/**
	 * Visit a source node.
	 * 
	 * @param source the source node
	 * @return if the visitor is to be applied to any further nodes down the
	 *         path
	 */
	public boolean visit(SourceNode source);

	/**
	 * Called after a source node has been visited.
	 * 
	 * @param source the source node
	 */
	public void leave(SourceNode source);

	/**
	 * Specifies the traversal direction.
	 * 
	 * @return <code>true</code> if the tree is to be traversed from targets to
	 *         sources, <code>false</code> if the other way round
	 */
	public boolean isFromTargetToSource();

	/**
	 * Specifies if annotated nodes should be included in the traversal.
	 * 
	 * @return if nodes that are only present as annotations should be visited
	 */
	public boolean includeAnnotatedNodes();

}
