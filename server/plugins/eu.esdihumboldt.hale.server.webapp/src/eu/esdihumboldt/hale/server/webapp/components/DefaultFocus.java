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
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnLoadHeaderItem;

/**
 * Add this behavior to a component for it to have the focus.
 * 
 * @author Simon Templer
 */
public class DefaultFocus extends Behavior {

	private static final long serialVersionUID = -6001239595605543034L;

	/**
	 * @see Behavior#bind(Component)
	 */
	@Override
	public void bind(Component component) {
		component.setOutputMarkupId(true);
	}

	/**
	 * @see Behavior#renderHead(Component, IHeaderResponse)
	 */
	@Override
	public void renderHead(Component component, IHeaderResponse iHeaderResponse) {
		super.renderHead(component, iHeaderResponse);
		iHeaderResponse.render(OnLoadHeaderItem.forScript("document.getElementById('"
				+ component.getMarkupId() + "').focus();"));
	}
}
