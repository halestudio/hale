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

package eu.esdihumboldt.hale.ui.views.report.properties.details.tree;

import javax.annotation.Nullable;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.dialogs.FilteredTree;

import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.ui.views.report.ReportListLabelProvider;
import eu.esdihumboldt.hale.ui.views.report.properties.details.ReportDetailsPage;
import eu.esdihumboldt.hale.ui.views.report.properties.details.extension.CustomReportDetailsPage.MessageType;

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
	 * Get the type of the given message.
	 * 
	 * @param message the message
	 * @return the message type or <code>null</code>
	 */
	@Nullable
	public MessageType getMessageType(Message message) {
		return null;
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof Message) {
			// get the right image
			Message message = (Message) element;

			String img = "icons/warning.gif";
			if (message.getStackTrace() != null && !message.getStackTrace().equals("")) {
				img = "icons/error_log.gif";
			}
			else {
				MessageType type = getMessageType(message);
				if (type != null) {
					switch (type) {
					case Error:
						img = "icons/error.gif";
						break;
					case Information:
						img = "icons/info.gif";
						break;
					case Warning: // keep default
						break;
					}
				}
			}

			return getImage(img);
		}
		else
			return super.getImage(element);
	}
}
