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

package eu.esdihumboldt.hale.ui.util.viewer;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Content provider that uses the input as single element.
 * 
 * @author Simon Templer
 */
public class ObjectContentProvider implements IStructuredContentProvider {

	private static ObjectContentProvider instance;

	/**
	 * Get the content provider instance
	 * 
	 * @return the content provider instance
	 */
	public static ObjectContentProvider getInstance() {
		if (instance == null) {
			instance = new ObjectContentProvider();
		}
		return instance;
	}

	/**
	 * Default constructor
	 */
	protected ObjectContentProvider() {
		super();
	}

	/**
	 * @see IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		// do nothing
	}

	/**
	 * @see IContentProvider#inputChanged(Viewer, Object, Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// do nothing
	}

	/**
	 * @see IStructuredContentProvider#getElements(Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement == null) {
			return new Object[0];
		}
		else {
			return new Object[] { inputElement };
		}
	}

}
