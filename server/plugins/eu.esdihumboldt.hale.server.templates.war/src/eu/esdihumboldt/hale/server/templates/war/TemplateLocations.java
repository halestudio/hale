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

package eu.esdihumboldt.hale.server.templates.war;

import eu.esdihumboldt.hale.server.api.base.APIUtil;
import eu.esdihumboldt.hale.server.templates.war.resources.TemplatesResources;

/**
 * Helper methods to determine locations of specific resources.
 * 
 * @author Simon Templer
 */
public class TemplateLocations {

	/**
	 * Get the server base URL.
	 * 
	 * @return the base URL w/o trailing slash
	 */
	public static String getServerBaseUrl() {
		return System.getProperty(APIUtil.SYSTEM_PROPERTY_SERVER_URL, "http://localhost:8080");
	}

	private static String getContextPath() {
		return "/templates";
	}

	/**
	 * Get the URL to download a template archive.
	 * 
	 * @param templateId the template ID
	 * @return the URL to the archive
	 * @see TemplatesResources
	 */
	public static String getTemplateDownloadUrl(String templateId) {
		return getServerBaseUrl() + getContextPath() + "/resources/archive/" + templateId;
	}
}
