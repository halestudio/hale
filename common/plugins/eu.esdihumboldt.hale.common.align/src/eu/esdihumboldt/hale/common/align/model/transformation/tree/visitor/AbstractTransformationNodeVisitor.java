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
 * TODO Type description
 * 
 * @author Simon Templer
 */
public abstract class AbstractTransformationNodeVisitor implements TransformationNodeVisitor {

	/**
	 * @see TransformationNodeVisitor#visit(TransformationTree)
	 */
	@Override
	public boolean visit(TransformationTree root) {
		return true;
	}

	/**
	 * @see TransformationNodeVisitor#visit(TargetNode)
	 */
	@Override
	public boolean visit(TargetNode target) {
		return true;
	}

	/**
	 * @see TransformationNodeVisitor#visit(CellNode)
	 */
	@Override
	public boolean visit(CellNode cell) {
		return true;
	}

	/**
	 * @see TransformationNodeVisitor#visit(SourceNode)
	 */
	@Override
	public boolean visit(SourceNode source) {
		return true;
	}

	/**
	 * @see TransformationNodeVisitor#leave(TransformationTree)
	 */
	@Override
	public void leave(TransformationTree root) {
		// override me
	}

	/**
	 * @see TransformationNodeVisitor#leave(TargetNode)
	 */
	@Override
	public void leave(TargetNode target) {
		// override me
	}

	/**
	 * @see TransformationNodeVisitor#leave(CellNode)
	 */
	@Override
	public void leave(CellNode cell) {
		// override me
	}

	/**
	 * @see TransformationNodeVisitor#leave(SourceNode)
	 */
	@Override
	public void leave(SourceNode source) {
		// override me
	}

}
