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

package eu.esdihumboldt.hale.ui.views.report.properties.tree;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.dialogs.FilteredTree;

import eu.esdihumboldt.hale.common.core.report.Report;
import eu.esdihumboldt.hale.ui.views.report.properties.ReportDetails;

/**
 * ContentProvider for {@link FilteredTree} in {@link ReportDetails}.
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ReportTreeContentProvider implements ITreeContentProvider {

	private Report<?> report;
	
	private ReportGroupInfo info;
	private ReportGroupWarning warning;
	private ReportGroupError error;
	
	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		// do nothing
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof Report<?>) {
			report = (Report<?>) newInput;
			
			info = new ReportGroupInfo();
			info.addAll(report.getInfos());
			
			warning = new ReportGroupWarning();
			warning.addAll(report.getWarnings());
			
			error = new ReportGroupError();
			error.addAll(report.getErrors());
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang.Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		Object[] array = { info, warning, error};
		return array;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof AbstractList) {
			return ((AbstractList)parentElement).toArray();
		}
		
		return new Object[0];
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	@Override
	public Object getParent(Object element) {
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof Report<?>) {
			return true;
		}
		
		if (element instanceof AbstractList && ((AbstractList)element).size() > 0) {
			return true;
		} 
		
		return false;
	}
}
