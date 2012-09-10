/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.ui.util.tree;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jface.viewers.TreeNode;

/**
 * Tree node that associates children with a key, children are sorted by the key
 * 
 * @param <T> the key type
 * @param <N> the node type
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class SortedMapTreeNode<T extends Comparable<?>, N extends TreeNode> extends
		MapTreeNode<T, N> {

	/**
	 * @see MapTreeNode#MapTreeNode(Object...)
	 */
	public SortedMapTreeNode(Object... values) {
		super(values);
	}

	/**
	 * @see MapTreeNode#createMap()
	 */
	@Override
	protected Map<T, N> createMap() {
		return new TreeMap<T, N>();
	}

}
