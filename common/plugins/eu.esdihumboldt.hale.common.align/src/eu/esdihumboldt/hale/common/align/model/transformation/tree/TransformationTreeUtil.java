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

package eu.esdihumboldt.hale.common.align.model.transformation.tree;

import eu.esdihumboldt.util.IdentityWrapper;

/**
 * Transformation tree utilities.
 * @author Simon Templer
 */
public abstract class TransformationTreeUtil {
	
	/**
	 * Extract the definition or cell contained in a transformation node.
	 * @param node the node or other kind of object
	 * @return the contained definition, cell or the node/object itself
	 */
	public static Object extractObject(Object node) {
		if (node instanceof IdentityWrapper<?>) {
			node = ((IdentityWrapper<?>) node).getValue();
		}
		
		if (node instanceof TransformationTree) {
			return ((TransformationTree) node).getType();
		}
		if (node instanceof TargetNode) {
			return ((TargetNode) node).getEntityDefinition();
		}
		if (node instanceof CellNode) {
			return ((CellNode) node).getCell();
		}
		if (node instanceof SourceNode) {
			return ((SourceNode) node).getEntityDefinition();
		}
		
		return node;
	}

}
