/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.server.webapp.components.bootstrap;

import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

/**
 * Uses bootstrap CSS classes for messages.
 * 
 * @author Simon Templer
 */
public class BootstrapFeedbackPanel extends FeedbackPanel {

	private static final long serialVersionUID = -7809671724990837932L;

	/**
	 * @see FeedbackPanel#FeedbackPanel(String, IFeedbackMessageFilter)
	 */
	public BootstrapFeedbackPanel(String id, IFeedbackMessageFilter filter) {
		super(id, filter);
	}

	/**
	 * @see FeedbackPanel#FeedbackPanel(String)
	 */
	public BootstrapFeedbackPanel(String id) {
		super(id);
	}

	@Override
	protected String getCSSClass(FeedbackMessage message) {
		return "text-" + message.getLevelAsString().toLowerCase();
	}

}
