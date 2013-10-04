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

import eu.esdihumboldt.hale.server.templates.war.components.TemplateUploadForm;
import eu.esdihumboldt.hale.server.webapp.pages.BasePage;
import eu.esdihumboldt.hale.server.webapp.util.PageDescription;

/**
 * Page for uploading new project templates.
 * 
 * @author Simon Templer
 */
@PageDescription(title = "Share template")
public class UploadTemplatePage extends BasePage {

	private static final long serialVersionUID = -8268659882000252602L;

	@Override
	protected void addControls(boolean loggedIn) {
		super.addControls(loggedIn);

		add(new TemplateUploadForm("upload", null));
	}

}
