/*
 * LICENSE: This program is being made available under the LGPL 3.0 license.
 * For more information on the license, please read the following:
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * 
 * For additional information on the Model behind Mismatches, please refer to
 * the following publication(s):
 * Thorsten Reitz (2010): A Mismatch Description Language for Conceptual Schema 
 * Mapping and Its Cartographic Representation, Geographic Information Science,
 * http://www.springerlink.com/content/um2082120r51232u/
 */
package eu.xsdi.mdlui.views;

import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import eu.xsdi.mdl.model.Consequence;

/**
 * Provides Content based on a {@link Consequence} model for a {@link TreeViewer}.
 * 
 * @author Thorsten Reitz, thor@xsdi.eu
 * @version $Id$ 
 * @since 0.1.0
 */
public class ConsequenceContentProvider 
	implements ITreeContentProvider, IStructuredContentProvider {

	@SuppressWarnings("unchecked")
	@Override
	public Object[] getChildren(Object parentElement) {
		Object[] result;
		if (parentElement instanceof Set) {
			result = ((Set)parentElement).toArray();
		}
		else if (parentElement instanceof Consequence) {
			Consequence c = (Consequence)parentElement;
			result = new Object[]{c.getContext(), c.getImpact()};
		}
		else if (parentElement instanceof List) {
			result = ((List)parentElement).toArray();
		}
		else {
			result = new Object[]{};
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof Set) {
			return true;
		}
		if (element instanceof Consequence) {
			return true;
		}
		if (element instanceof List) {
			return true;
		}
		else {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		Object[] result;
		if (inputElement instanceof Set) {
			result = ((Set)inputElement).toArray();
		}
		else {
			result = new Object[]{};
		}
		return result;
	}
	
	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		
	}

}
