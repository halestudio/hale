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

package eu.esdihumboldt.hale.ui.views.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import eu.esdihumboldt.hale.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.core.report.Report;

/**
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ReportListContentProvider implements ITreeContentProvider {

	public Map<ProjectInfo, List<Report>> data = new HashMap<ProjectInfo, List<Report>>();
	
	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof ReportItem) {
			ReportItem item = (ReportItem) newInput;
			ProjectInfo project = item.getProject();
			ArrayList<Report> reports;
			int id = project.hashCode();
			
			//System.err.println("ReportListContentProvider.inputChanged() "+project);
			
			// check if there's already a list
			if (data.get(project) == null) {
				reports = new ArrayList<Report>();
			} else {
				reports = (ArrayList<Report>) this.data.get(project);
			}
			
			// add the new report
			reports.add(item.getReport());
			data.put(project, reports);
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang.Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		// display the listing of projects
		return data.keySet().toArray();
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		List<Report> reports = this.data.get(parentElement);
		
		if (reports.size() == 0) {
			return null;
		}
		
		
		Object[] ret = new Object[reports.size()];
		for(int i = 0; i < reports.size(); i++) {
			ret[i] = reports.get(i);
		}
		
		return ret;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	@Override
	public Object getParent(Object element) {
		//System.err.println("ReportListContentProvider.getParent(): Implement me!");
		// TODO Auto-generated method stub
		return "";
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof Report) {
			// assume that Reports do not have any childs!
			return false;
		}
			
		List<Report> list = this.data.get(element);
		if (list != null && list.size() > 0) {
			return true;
		}

		return false;
		
	}

}