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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.server.status.war.components;

import org.apache.wicket.markup.html.panel.Panel;

import eu.esdihumboldt.hale.server.webapp.components.JobPanel;

/**
 * Wraps a job panel.
 * 
 * @author Simon Templer
 */
public class JobPanelWrapper extends Panel {

	private static final long serialVersionUID = -8131466999665720071L;

	/**
	 * Create a job panel wrapper.
	 * 
	 * @param id the component ID
	 */
	public JobPanelWrapper(String id) {
		super(id);

		add(new JobPanel("jobs", null, false));
	}

}
