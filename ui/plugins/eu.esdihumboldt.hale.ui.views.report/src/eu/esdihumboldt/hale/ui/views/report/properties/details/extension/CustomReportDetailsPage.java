/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
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
	 * Dispose resources.
	 */
	public void dispose();

	/**
	 * Enum constants indicating the type of {@link Message}.
	 * 
	 * @author Kai Schwierczek
	 */
	public static enum MessageType {
		/**
		 * For information messages.
		 */
		Information, //
		/**
		 * For warning messages
		 */
		Warning, //
		/**
		 * For error messages
		 */
		Error
	}

	/**
	 * Set how many more message there are that are not displayed.
	 * 
	 * @param more the number of messages
	 */
	public void setMore(int more);
}
