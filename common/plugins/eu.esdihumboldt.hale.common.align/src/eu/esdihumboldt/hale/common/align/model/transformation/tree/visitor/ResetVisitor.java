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

package eu.esdihumboldt.hale.common.align.model.transformation.tree.visitor;

import eu.esdihumboldt.hale.common.align.model.transformation.tree.CellNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.SourceNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TargetNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationNodeVisitor;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree;

/**
 * Resets a nodes in a transformation tree.
 * 
 * @author Simon Templer
 */
public class ResetVisitor extends AbstractTargetToSourceVisitor {

	/**
	 * @see TransformationNodeVisitor#visit(TransformationTree)
	 */
	@Override
	public boolean visit(TransformationTree root) {
		root.reset();
		return true;
	}

	/**
	 * @see TransformationNodeVisitor#visit(TargetNode)
	 */
	@Override
	public boolean visit(TargetNode target) {
		target.reset();
		return true;
	}

	/**
	 * @see TransformationNodeVisitor#visit(CellNode)
	 */
	@Override
	public boolean visit(CellNode cell) {
		cell.reset();
		return true;
	}

	/**
	 * @see TransformationNodeVisitor#visit(SourceNode)
	 */
	@Override
	public boolean visit(SourceNode source) {
		source.reset();
		return true;
	}

	/**
	 * @see TransformationNodeVisitor#includeAnnotatedNodes()
	 */
	@Override
	public boolean includeAnnotatedNodes() {
		// annotated nodes are removed on reset and thus don't have to be reset
		// themselves
		return false;
	}

}
