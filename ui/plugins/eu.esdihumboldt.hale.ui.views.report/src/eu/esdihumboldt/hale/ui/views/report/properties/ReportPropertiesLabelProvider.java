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

package eu.esdihumboldt.hale.ui.views.report.properties;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.graphics.Image;

import eu.esdihumboldt.hale.ui.views.report.ReportListLabelProvider;

/**
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ReportPropertiesLabelProvider extends ReportListLabelProvider {
	/**
	 * @see ReportListLabelProvider#getImage(Object)
	 */
	@Override
	public Image getImage(Object element) {
		if (element instanceof IStructuredSelection) {
			// overwrite element with first element from selection
			element = ((IStructuredSelection) element).getFirstElement();
		}
		return super.getImage(element);
	}
	
	/**
	 * @see ReportListLabelProvider#getText(Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof IStructuredSelection) {
			// overwrite element with first element from selection
			element = ((IStructuredSelection) element).getFirstElement();
		}

		return super.getText(element);
	}
}
