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

package eu.esdihumboldt.hale.rcp.views.report;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import eu.esdihumboldt.hale.Messages;

/**
 * The ContentProvider for {@link ReportView#view}.
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class ReportContentProvider implements ITreeContentProvider {

	/**
	 * Contains the current {@link ReportModel}.
	 */
	private ReportModel model;
	
	/**
	 * Contains the childs for Warning or Error.
	 */
	private List<TransformationResultItem> item = new ArrayList<TransformationResultItem>();
	
	@Override
	public Object[] getElements(Object inputElement) {
		Object[] ret = new Object[2];
		ret[0] = new String(MessageFormat.format(Messages.ReportContentProvider_2, model.getWarnings().size())); //$NON-NLS-1$ //$NON-NLS-2$
		ret[1] = new String(MessageFormat.format(Messages.ReportContentProvider_3, model.getErrors().size())); //$NON-NLS-1$ //$NON-NLS-2$
		
		return ret;
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof ReportModel) {
			this.model = (ReportModel) newInput;
		}
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement.toString().startsWith("Error")) { //$NON-NLS-1$
			// get all error messages
			this.item = this.model.getErrors();
			return this.item.toArray();
		} else if (parentElement.toString().startsWith(Messages.ReportContentProvider_0)) { //$NON-NLS-1$
			// get all warning messages
			this.item = this.model.getWarnings();
			return this.item.toArray();
		}
		else if (parentElement instanceof TransformationResultItem){
			// get all lines
			return ((TransformationResultItem)parentElement).getLines().toArray();
		}
		
		return null;
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		boolean hasChildren = false;
		
		if (element.toString().contains(Messages.ReportContentProvider_0)) { //$NON-NLS-1$
			if (this.model.getWarnings().size() > 0) {
				hasChildren = true;
			}
		}
		else if (element.toString().contains(Messages.ReportContentProvider_1)) { //$NON-NLS-1$
			if (this.model.getErrors().size() > 0) {
				hasChildren = true;
			}
		}
		
		if (this.item.size() > 0 && element instanceof TransformationResultItem) {
			TransformationResultItem item = (TransformationResultItem)element;

			if (item.getLines().size() > 0) {
				hasChildren = true;
			} else{
				hasChildren = false;
			}
		}
		
		return hasChildren;
	}

}
