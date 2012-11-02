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

package eu.esdihumboldt.hale.server.projects.war.pages;

import eu.esdihumboldt.hale.server.projects.war.components.UploadForm;
import eu.esdihumboldt.hale.server.webapp.pages.BasePage;
import eu.esdihumboldt.hale.server.webapp.util.PageDescription;

/**
 * Page for new project upload.
 * 
 * @author Simon Templer
 */
@PageDescription(title = "Upload", parent = ProjectsPage.class)
public class UploadPage extends BasePage {

	private static final long serialVersionUID = 6946535530058858323L;

	/**
	 * @see BasePage#addControls(boolean)
	 */
	@Override
	protected void addControls(boolean loggedIn) {
		super.addControls(loggedIn);

		add(new UploadForm("upload"));
	}

}
