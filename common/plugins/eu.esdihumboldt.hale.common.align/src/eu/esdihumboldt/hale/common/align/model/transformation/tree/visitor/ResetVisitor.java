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

package eu.esdihumboldt.hale.common.align.model.transformation.tree.visitor;

import eu.esdihumboldt.hale.common.align.model.transformation.tree.CellNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.SourceNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TargetNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationNodeVisitor;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree;

/**
 * Resets a nodes in a transformation tree.
 * @author Simon Templer
 */
public class ResetVisitor implements TransformationNodeVisitor {

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
	 * @see TransformationNodeVisitor#isFromTargetToSource()
	 */
	@Override
	public boolean isFromTargetToSource() {
		return true;
	}

	/**
	 * @see TransformationNodeVisitor#includeAnnotatedNodes()
	 */
	@Override
	public boolean includeAnnotatedNodes() {
		// annotated nodes are removed on reset and thus don't have to be reset themselves
		return false;
	}

}
