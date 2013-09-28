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

package eu.esdihumboldt.hale.server.templates.war.pages;

import org.apache.wicket.markup.html.link.BookmarkablePageLink;

import eu.esdihumboldt.hale.server.templates.war.components.TemplateList;
import eu.esdihumboldt.hale.server.webapp.pages.BasePage;
import eu.esdihumboldt.hale.server.webapp.util.PageDescription;

/**
 * Page for managin projects.
 * 
 * @author Simon Templer
 */
@PageDescription(title = "Templates")
public class TemplatesPage extends BasePage {

	private static final long serialVersionUID = 221216335635652135L;

	/**
	 * @see BasePage#addControls(boolean)
	 */
	@Override
	protected void addControls(boolean loggedIn) {
		super.addControls(loggedIn);

		add(new BookmarkablePageLink<Void>("upload", UploadPage.class));
		add(new TemplateList("templates", true));
	}

}
