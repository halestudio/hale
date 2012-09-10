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

package eu.esdihumboldt.hale.ui.views.report.properties.details.tree;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.dialogs.FilteredTree;

import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.ui.views.report.ReportListLabelProvider;
import eu.esdihumboldt.hale.ui.views.report.properties.details.ReportDetailsPage;

/**
 * LabelProvider for {@link FilteredTree} in {@link ReportDetailsPage}.
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ReportTreeLabelProvider extends ReportListLabelProvider {

	@Override
	public String getText(Object element) {
		if (element instanceof Message) {
			return ((Message) element).getFormattedMessage();
		}

		return super.getText(element);
	}

	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object element) {
		if (element instanceof Message) {
			// get the right image
			Message message = (Message) element;

			String img = "icons/warning.gif";
			if (message.getStackTrace() != null && !message.getStackTrace().equals("")) {
				img = "icons/error_log.gif";
			}

			return getImage(img);
		}
		else
			return super.getImage(element);
	}
}
