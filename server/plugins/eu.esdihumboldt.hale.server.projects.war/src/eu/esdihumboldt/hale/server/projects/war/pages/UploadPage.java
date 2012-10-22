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
import eu.esdihumboldt.hale.server.webapp.pages.SecuredPage;
import eu.esdihumboldt.hale.server.webapp.util.PageDescription;

/**
 * Page for new project upload.
 * 
 * @author Simon Templer
 */
@PageDescription(title = "Upload", parent = ProjectsPage.class)
public class UploadPage extends SecuredPage {

	/**
	 * @see SecuredPage#addControls()
	 */
	@Override
	protected void addControls() {
		super.addControls();

		add(new UploadForm("upload"));
	}

}
