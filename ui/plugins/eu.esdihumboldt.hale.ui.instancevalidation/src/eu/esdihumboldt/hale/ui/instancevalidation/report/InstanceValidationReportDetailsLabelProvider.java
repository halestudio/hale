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

package eu.esdihumboldt.hale.ui.instancevalidation.report;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.jface.viewers.LabelProvider;

import eu.esdihumboldt.hale.common.instancevalidator.report.InstanceValidationMessage;

/**
 * Label provider for the instance validation report details page.
 *
 * @author Kai Schwierczek
 */
public class InstanceValidationReportDetailsLabelProvider extends LabelProvider {
	/**
	 * @see LabelProvider#getText(Object)
	 */
	@Override
	public String getText(Object element) {
		List<?> list = (List<?>) element;
		InstanceValidationMessage firstMessage = (InstanceValidationMessage) list.get(0);

		return MessageFormat.format("({0} times) {1}", list.size(), firstMessage.getFormattedMessage());
	}
}
