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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import eu.esdihumboldt.hale.common.core.report.Report;

/**
 * ContentProvider for {@link ReportList}.
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ReportListContentProvider implements ITreeContentProvider {

	/**
	 * Contains all projects with related data.
	 */
	@SuppressWarnings("rawtypes")
	public static Map<Long, List<Report>> data = new LinkedHashMap<Long, List<Report>>();
	
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
	@SuppressWarnings("rawtypes")
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof ReportItem) {
			ReportItem item = (ReportItem) newInput;
			long project = item.getIdentifier();
			ArrayList<Report> reports;
			
			// check if there's already a list
			if (data.get(project) == null) {
				reports = new ArrayList<Report>();
			} else {
				reports = (ArrayList<Report>) data.get(project);
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
	@SuppressWarnings("rawtypes")
	@Override
	public Object[] getChildren(Object parentElement) {
		List<Report> reports = data.get(parentElement);
		
		// no reports?
		if (reports.size() == 0) {
			return null;
		}
		
		return reports.toArray();
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
	@SuppressWarnings("rawtypes")
	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof Report) {
			// assume that Reports do not have any children!
			return false;
		}
			
		List<Report> list = data.get(element);
		if (list != null && list.size() > 0) {
			return true;
		}

		return false;
		
	}

}