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

import static org.apache.commons.lang.StringUtils.chomp;

import org.apache.wicket.Component;
import org.apache.wicket.core.util.string.ComponentRenderer;
import org.apache.wicket.model.IModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.components.PopoverBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.PopoverConfig;

/**
 * Popover with HTML content.
 */
public abstract class HTMLPopoverBehavior extends PopoverBehavior {

	private static final long serialVersionUID = 7241028278227133513L;

	/**
	 * Constructor
	 * 
	 * @param label popover title
	 * @param config popover configuration
	 */
	public HTMLPopoverBehavior(final IModel<String> label, final PopoverConfig config) {
		super(label, null, config);

		config.withHtml(true);
	}

	@Override
	protected final String newContent() {
		@SuppressWarnings("deprecation")
		final String content = String.valueOf(
				ComponentRenderer.renderComponent(newBodyComponent(ComponentRenderer.COMP_ID)));

		// XXX how to correctly escape?
		return chomp(content);
	}

	/**
	 * Creates a new popover body component.
	 * 
	 * @param markupId the markup id that the body component must be use
	 * @return new body component
	 */
	public abstract Component newBodyComponent(final String markupId);
}
