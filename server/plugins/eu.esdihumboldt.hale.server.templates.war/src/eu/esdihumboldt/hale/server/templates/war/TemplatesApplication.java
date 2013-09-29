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

package eu.esdihumboldt.hale.server.templates.war;

import org.apache.wicket.Page;

import eu.esdihumboldt.hale.server.templates.war.pages.TemplatePage;
import eu.esdihumboldt.hale.server.templates.war.pages.TemplatesPage;
import eu.esdihumboldt.hale.server.templates.war.pages.UploadPage;
import eu.esdihumboldt.hale.server.webapp.BaseWebApplication;

/**
 * Application for managing and accessing project templates.
 * 
 * @author Simon Templer
 */
public class TemplatesApplication extends BaseWebApplication {

	@Override
	public Class<? extends Page> getHomePage() {
		return TemplatesPage.class;
	}

	@Override
	public void init() {
		super.init();

		mountPage("/upload", UploadPage.class);

		mountPage("/show", TemplatePage.class);
	}

	@Override
	public String getMainTitle() {
		return super.getMainTitle() + " Templates";
	}

}
