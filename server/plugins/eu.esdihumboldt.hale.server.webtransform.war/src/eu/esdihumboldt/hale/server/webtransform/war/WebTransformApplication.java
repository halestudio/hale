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

package eu.esdihumboldt.hale.server.webtransform.war;

import org.apache.wicket.Page;

import eu.esdihumboldt.hale.server.webapp.BaseWebApplication;
import eu.esdihumboldt.hale.server.webtransform.war.pages.StatusPage;
import eu.esdihumboldt.hale.server.webtransform.war.pages.TransformationsPage;
import eu.esdihumboldt.hale.server.webtransform.war.pages.UploadPage;

/**
 * Web transformation webapp.
 * 
 * @author Simon Templer
 */
public class WebTransformApplication extends BaseWebApplication {

	/**
	 * @see eu.esdihumboldt.hale.server.webapp.BaseWebApplication#init()
	 */
	@Override
	public void init() {
		super.init();

		mountPage("/upload/${" + UploadPage.PARAMETER_PROJECT + "}", UploadPage.class);

		mountPage("/status/${" + StatusPage.PARAMETER_WORKSPACE + "}", StatusPage.class);
	}

	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends Page> getHomePage() {
		return TransformationsPage.class;
	}

}
