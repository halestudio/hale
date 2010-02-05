/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 *
 * Componet     : hale
 * 	 
 * Classname    : eu.esdihumboldt.hale.rcp.wizards.functions.generic.FunctionTypeContentProvider.java 
 * 
 * Author       : Josef Bezdek
 * 
 * Created on   : Jan, 2010
 *
 */

package eu.esdihumboldt.hale.rcp.wizards.functions.generic;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import eu.esdihumboldt.hale.rcp.wizards.functions.generic.model.FunctionType;
import eu.esdihumboldt.hale.rcp.wizards.functions.generic.model.Model;


public class FunctionTypeContentProvider implements ITreeContentProvider {
	private static Object[] EMPTY_ARRAY = new Object[0];
	protected TreeViewer viewer;
	
	/*
	 * @see IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		
	}

	/*
	 * @see IContentProvider#inputChanged(Viewer, Object, Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
/*		this.viewer = (TreeViewer)viewer;
		if(oldInput != null) {
		}
		if(newInput != null) {
		}*/
	}
	

	/*
	 * @see ITreeContentProvider#getChildren(Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof FunctionType) {
			FunctionType box = (FunctionType)parentElement;
			return concat(box.getBoxes().toArray(), 
				box.getCoreFunctions().toArray(),
				box.getInspireFunctions().toArray(),
				box.getOthersFunctions().toArray());
				
		}
		return EMPTY_ARRAY;
	}
	
	/**
	 * The method that returns all treeviewer items in one array 
	 * @param object first item
	 * @param more	second item
	 * @param more2	third item
	 * @param more3
	 * @param more4
	 * @param more5
	 * @param more6
	 * @param more7
	 * @param more8
	 * @return	united array of all items
	 */
	protected Object[] concat(Object[] object, Object[] more, Object[] more2, Object[] more3){ 
		Object[] both = new Object[object.length + more.length + more2.length + more3.length];
		int length = 0;
		System.arraycopy(object, 0, both, length, object.length);
		length = length + object.length;
		System.arraycopy(more, 0, both, length, more.length);
		length = length + more.length;
		System.arraycopy(more2, 0, both, length, more2.length);		
		length = length + more2.length;
		System.arraycopy(more3, 0, both, length, more3.length);		
		length = length + more3.length;
		return both;
	}

	/*
	 * @see ITreeContentProvider#getParent(Object)
	 */
	@Override
	public Object getParent(Object element) {
		if(element instanceof Model) {
			return ((Model)element).getParent();
		}
		return null;
	}

	/*
	 * @see ITreeContentProvider#hasChildren(Object)
	 */
	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	/*
	 * @see IStructuredContentProvider#getElements(Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}



}
