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
