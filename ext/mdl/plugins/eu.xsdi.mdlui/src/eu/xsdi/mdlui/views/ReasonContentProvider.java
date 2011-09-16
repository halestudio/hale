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

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import eu.xsdi.mdl.model.Reason;
import eu.xsdi.mdl.model.reason.ReasonSet;

/**
 * Provides Content based on a {@link Reason} model for a {@link TreeViewer}.
 * 
 * @author Thorsten Reitz, thor@xsdi.eu
 * @version $Id$ 
 * @since 0.1.0
 */
public class ReasonContentProvider implements ITreeContentProvider, IStructuredContentProvider {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement != null) {
			if (inputElement instanceof List) {
				return ((List) inputElement).toArray();
			}
		}
		return new Object[]{};
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		Object[] result;
		if (parentElement instanceof Reason) {
			Reason r = (Reason) parentElement;
			result = new Object[2];
			if (r.getReasonRule() != null) {
				result[0] = r.getReasonRule().getSet1();
				result[1] = r.getReasonRule().getSet2();
			}
			else {
				result = new Object[0];
			}
		}
		else if (parentElement instanceof ReasonSet) {
			ReasonSet rs = (ReasonSet) parentElement;
			if (rs.getSubSet() != null) {
				result = new Object[]{rs.getSubSet()};
			}
			else {
				result = new Object[0];
			}
		}
		else {
			result = new Object[0];
		}
		return result;
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof Reason) {
			return true;
		}
		else if (element instanceof ReasonSet) {
			ReasonSet rs = (ReasonSet) element;
			if (rs.getSubSet() != null) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}

}
