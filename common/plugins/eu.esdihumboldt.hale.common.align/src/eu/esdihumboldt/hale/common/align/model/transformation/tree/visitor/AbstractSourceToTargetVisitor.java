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
 * Transformation tree visitor for source to target traversal.
 * @author Simon Templer
 */
public abstract class AbstractSourceToTargetVisitor implements TransformationNodeVisitor {

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
	 * @see TransformationNodeVisitor#isFromTargetToSource()
	 */
	@Override
	public final boolean isFromTargetToSource() {
		return false;
	}

}
