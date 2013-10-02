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

package eu.esdihumboldt.hale.server.templates.war.pages;

import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.Navbar.ComponentPosition;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarComponents;
import eu.esdihumboldt.hale.server.webapp.pages.BasePage;
import eu.esdihumboldt.hale.server.webapp.pages.SecuredPage;

/**
 * Base page for templates application.
 * 
 * @author Simon Templer
 */
public class TemplatesSecuredPage extends SecuredPage {

	private static final long serialVersionUID = 3282978244574600398L;

	/**
	 * @see BasePage#BasePage()
	 */
	public TemplatesSecuredPage() {
		super();
	}

	/**
	 * @see BasePage#BasePage(PageParameters)
	 */
	public TemplatesSecuredPage(PageParameters parameters) {
		super(parameters);
	}

	@Override
	protected void addControls() {
		super.addControls();

		NavbarButton<Void> share = new NavbarButton<>(UploadTemplatePage.class, Model.of("Share"));

		NavbarButton<Void> my = new NavbarButton<>(MyTemplatesPage.class, Model.of("My Templates"));

		getNavbar().addComponents(NavbarComponents.transform(ComponentPosition.LEFT, my, share));
	}

}
