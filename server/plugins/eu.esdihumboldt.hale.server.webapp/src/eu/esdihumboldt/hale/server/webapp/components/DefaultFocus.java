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

package eu.esdihumboldt.hale.server.webapp.components;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.html.IHeaderResponse;

/**
 * Add this behavior to a component for it to have the focus.
 * 
 * @author Simon Templer
 */
public class DefaultFocus extends AbstractBehavior {

	private static final long serialVersionUID = -6001239595605543034L;

	private Component component;

	/**
	 * @see AbstractBehavior#bind(Component)
	 */
	@Override
	public void bind(Component component) {
		this.component = component;
		component.setOutputMarkupId(true);
	}

	/**
	 * @see AbstractBehavior#renderHead(IHeaderResponse)
	 */
	@Override
	public void renderHead(IHeaderResponse iHeaderResponse) {
		super.renderHead(iHeaderResponse);
		iHeaderResponse.renderOnLoadJavascript("document.getElementById('"
				+ component.getMarkupId() + "').focus();");
	}
}
