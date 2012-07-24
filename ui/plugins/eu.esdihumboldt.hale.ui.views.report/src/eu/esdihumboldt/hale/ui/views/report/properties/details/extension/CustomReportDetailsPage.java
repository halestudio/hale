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

package eu.esdihumboldt.hale.ui.views.report.properties.details.extension;

import java.util.Collection;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.common.core.report.Report;

/**
 * Interface for custom report details pages for specific {@link Report}s.
 *
 * @author Kai Schwierczek
 */
public interface CustomReportDetailsPage {
	/**
	 * Create the custom controls for the details page.
	 *
	 * @param parent the parent composite for the page
	 * @return the created control
	 */
	public Control createControls(Composite parent);

	/**
	 * Set the input messages for the details page to messages.
	 *
	 * @param messages the messages to show
	 * @param type the type of messages
	 */
	public void setInput(Collection<? extends Message> messages, MessageType type);

	/**
	 * Enum constants indicating the type of {@link Message}.
	 *
	 * @author Kai Schwierczek
	 */
	public static enum MessageType {
		/**
		 * For information messages.
		 */
		Information,
		/**
		 * For warning messages
		 */
		Warning,
		/**
		 * For error messages
		 */
		Error
	}
}
