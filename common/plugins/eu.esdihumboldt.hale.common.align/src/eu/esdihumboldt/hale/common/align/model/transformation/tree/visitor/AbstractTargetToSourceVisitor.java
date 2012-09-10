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

import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationNodeVisitor;

/**
 * Transformation tree visitor for target to source traversal.
 * 
 * @author Simon Templer
 */
public abstract class AbstractTargetToSourceVisitor extends AbstractTransformationNodeVisitor {

	/**
	 * @see TransformationNodeVisitor#isFromTargetToSource()
	 */
	@Override
	public final boolean isFromTargetToSource() {
		return true;
	}

}
