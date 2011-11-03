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

import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;

import eu.esdihumboldt.hale.common.core.io.report.IOMessage;

/**
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ReportIODetails extends ReportDetails {

	/**
	 * @see AbstractPropertySection#refresh()
	 */
	@Override
	public void refresh() {
		int warnCount = this.report.getWarnings().size();
		int errorCount = this.report.getErrors().size();
		warnings.setText(""+warnCount);
		errors.setText(""+errorCount);
		
		if (warnCount > 0) {
			for (Object o : this.report.getWarnings()) {
				IOMessage message = (IOMessage) o;
				
				this.warningList.add("["+message.getLineNumber()+"] "+message.getMessage());
			}
		}
		
		if (errorCount > 0) {
			for (Object o : this.report.getErrors()) {
				IOMessage message = (IOMessage) o;
				
				this.errorList.add("["+message.getLineNumber()+"] "+message.getMessage());

			}
		}
	}
}
