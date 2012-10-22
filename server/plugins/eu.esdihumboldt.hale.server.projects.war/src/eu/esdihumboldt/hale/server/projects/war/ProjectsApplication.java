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

package eu.esdihumboldt.hale.server.projects.war;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.Page;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadWebRequest;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;

import eu.esdihumboldt.hale.server.projects.war.pages.ProjectsPage;
import eu.esdihumboldt.hale.server.projects.war.pages.UploadPage;
import eu.esdihumboldt.hale.server.webapp.BaseWebApplication;

/**
 * Application for uploading projects.
 * 
 * @author Simon Templer
 */
public class ProjectsApplication extends BaseWebApplication {

	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends Page> getHomePage() {
		return ProjectsPage.class;
	}

	/**
	 * @see WebApplication#newWebRequest(HttpServletRequest)
	 */
	@Override
	protected WebRequest newWebRequest(HttpServletRequest servletRequest) {
		// support for upload progress
		return new UploadWebRequest(servletRequest);
	}

	/**
	 * @see BaseWebApplication#init()
	 */
	@Override
	public void init() {
		super.init();

		mountBookmarkablePage("/upload", UploadPage.class);
	}

}
