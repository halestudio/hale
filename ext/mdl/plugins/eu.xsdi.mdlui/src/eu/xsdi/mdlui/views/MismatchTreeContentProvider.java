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

import java.util.Set;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import eu.xsdi.mdl.model.Consequence;
import eu.xsdi.mdl.model.Mismatch;
import eu.xsdi.mdl.model.MismatchCell;

/**
 * Provides Content to a {@link TreeViewer} with {@link Mismatch} input objects.
 * 
 * @author Thorsten Reitz
 * @version $Id$ 
 * @since 0.1.0
 */
public class MismatchTreeContentProvider implements ITreeContentProvider, IStructuredContentProvider {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object[] getChildren(Object parentElement) {
		Object[] result;
		if (parentElement instanceof Mismatch) {
			Mismatch m = (Mismatch) parentElement;
			result = new Object[2];
			result[0] = m.getReason();
			result[1] = m.getConsequences();
		}
		else if (parentElement instanceof Set) {
			Set s = (Set) parentElement;
			result = s.toArray();
		}
		else if (parentElement instanceof Consequence) {
			result = new Object[0];
		}
		else {
			result = new Object[0];
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof Mismatch) {
			return true;
		}
		else if (element instanceof Set) {
			return true;
		}
		else if (element instanceof Consequence) {
			return false;
		}
		else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement != null) {
			if (inputElement instanceof MismatchCell) {
				return ((MismatchCell) inputElement).getMismatches().toArray();
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		// ignore.
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// ignore. Maybe FIXME later.
	}

}
