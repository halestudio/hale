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

package eu.esdihumboldt.hale.ui.common.definition.viewer;

import org.eclipse.jface.viewers.TreeViewer;

/**
 * Content provider that only displays types and not their children
 * 
 * @author Simon Templer
 */
public class TypesContentProvider extends TypeIndexContentProvider {

	private static final Object[] EMPTY = new Object[] {};

	/**
	 * @see TypeIndexContentProvider#TypeIndexContentProvider(TreeViewer)
	 */
	public TypesContentProvider(TreeViewer tree) {
		super(tree);
	}

	/**
	 * @see TypeIndexContentProvider#getChildren(Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		return EMPTY;
	}

	/**
	 * @see TypeIndexContentProvider#hasChildren(Object)
	 */
	@Override
	public boolean hasChildren(Object parentElement) {
		return false;
	}

}
