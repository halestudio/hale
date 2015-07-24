/*
 * Copyright (c) 2015 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.common.function.viewer;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.google.common.collect.Iterables;

import eu.esdihumboldt.cst.functions.groovy.helper.Category;
import eu.esdihumboldt.cst.functions.groovy.helper.HelperFunctionOrCategory;
import eu.esdihumboldt.cst.functions.groovy.helper.HelperFunctionsService;

/**
 * TODO Type description
 * 
 * @author sameer sheikh
 */
public class HelperFunctionContentProvider implements ITreeContentProvider {

	private final HelperFunctionsService helperFunctionService;

	/**
	 * @param functions
	 */
	public HelperFunctionContentProvider(HelperFunctionsService functions) {
		this.helperFunctionService = functions;
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang.Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {

		if (inputElement instanceof Category) {
			Iterable<HelperFunctionOrCategory> x = helperFunctionService
					.getChildren((Category) inputElement);
			return Iterables.toArray(x, HelperFunctionOrCategory.class);

		}

		return null;
	}

	/**
	 * @see ITreeContentProvider#getChildren(Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {

		return getElements(parentElement);
	}

	/**
	 * @see ITreeContentProvider#getParent(Object)
	 */
	@Override
	public Object getParent(Object element) {
		if (element instanceof Category) {
			return ((Category) element).getParent();
		}

		return null;
	}

	/**
	 * @see ITreeContentProvider#hasChildren(Object)
	 */
	@Override
	public boolean hasChildren(Object element) {
		Object[] children = getChildren(element);
		return children != null && children.length > 0;
	}
}
