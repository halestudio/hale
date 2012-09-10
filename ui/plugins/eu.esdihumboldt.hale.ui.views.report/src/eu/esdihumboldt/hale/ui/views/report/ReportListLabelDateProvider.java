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

import java.text.SimpleDateFormat;

import org.eclipse.jface.viewers.LabelProvider;

import eu.esdihumboldt.hale.common.core.report.Report;

/**
 * LabelProvider for {@link ReportList}, which provides date and time
 * information.
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ReportListLabelDateProvider extends LabelProvider {

	private SimpleDateFormat df = new SimpleDateFormat("HH:mm.ss");

	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof Report<?> && ((Report<?>) element).getStartTime() != null)
			return df.format(((Report<?>) element).getStartTime());

		return "";
	}
}
