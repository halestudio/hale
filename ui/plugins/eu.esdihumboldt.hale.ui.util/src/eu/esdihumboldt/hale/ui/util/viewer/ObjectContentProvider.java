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
