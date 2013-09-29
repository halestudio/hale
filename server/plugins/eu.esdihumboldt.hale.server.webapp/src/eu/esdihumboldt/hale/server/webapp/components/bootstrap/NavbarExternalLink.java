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

import org.apache.wicket.Component;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.IMarkupSourcingStrategy;
import org.apache.wicket.markup.html.panel.PanelMarkupSourcingStrategy;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import de.agilecoders.wicket.core.markup.html.bootstrap.common.Invertible;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.Icon;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.Navbar;

/**
 * External link for use in the Navbar.
 * 
 * @author Simon Templer
 */
public class NavbarExternalLink extends ExternalLink implements Invertible<NavbarExternalLink> {

	private static final long serialVersionUID = -8203106286146037750L;

	private final Icon icon;
	private final Label label;
	private final Component splitter;

	/**
	 * Construct. Navbar ajax link with default component id for navbar
	 * components ("component") and an empty label.
	 * 
	 * @param href the link address
	 * @param label Label of link
	 */
	public NavbarExternalLink(String href, String label) {
		this(Model.of(href), Model.of(label));
	}

	/**
	 * Construct. Navbar ajax link with default component id for navbar
	 * components ("component") and a specific label.
	 * 
	 * @param href the model containing the link address
	 * @param label Label of link
	 */
	public NavbarExternalLink(IModel<String> href, final IModel<String> label) {
		this(Navbar.componentId(), href, label);
	}

	/**
	 * Construct.
	 * 
	 * @param markupId component markup id
	 * @param href the model containing the link address
	 * @param label label of link
	 */
	public NavbarExternalLink(final String markupId, IModel<String> href, IModel<String> label) {
		super(markupId, href);

		this.icon = new Icon("icon", IconType.NULL);

		this.label = new Label("label", label);
		this.label.setRenderBodyOnly(true);

		add(this.icon, this.label, this.splitter = new WebMarkupContainer("splitter"));
	}

	/**
	 * setter for icon type
	 * 
	 * @param iconType icon type
	 * @return this instance
	 */
	public NavbarExternalLink setIconType(final IconType iconType) {
		this.icon.setType(iconType);

		return this;
	}

	@Override
	public NavbarExternalLink setInverted(boolean inverted) {
		icon.setInverted(inverted);
		return this;
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();

		splitter.setVisible(!IconType.NULL.equals(icon.getType()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IMarkupSourcingStrategy newMarkupSourcingStrategy() {
		return new PanelMarkupSourcingStrategy(true);
	}

	@Override
	protected void onComponentTag(ComponentTag tag) {
		if (!"a".equalsIgnoreCase(tag.getName()) && !"button".equalsIgnoreCase(tag.getName())) {
			tag.setName("a");
		}

		super.onComponentTag(tag);
	}

}
