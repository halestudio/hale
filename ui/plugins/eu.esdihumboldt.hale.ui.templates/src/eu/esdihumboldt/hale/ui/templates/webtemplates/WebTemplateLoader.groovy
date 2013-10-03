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

package eu.esdihumboldt.hale.ui.templates.webtemplates

import eu.esdihumboldt.hale.ui.templates.internal.TemplatesUIPlugin
import eu.esdihumboldt.hale.ui.templates.preferences.WebTemplatesPreferences
import groovy.transform.CompileStatic
import groovyx.net.http.RESTClient


/**
 * Loads web templates.
 * @author Simon Templer
 */
class WebTemplateLoader {

	@CompileStatic
	static List<WebTemplate> load() throws Exception {
		load(TemplatesUIPlugin.getDefault().preferenceStore.getString(WebTemplatesPreferences.PREF_WEB_TEMPLATES_URL))
	}

	static List<WebTemplate> load(String templatesUrl) throws Exception {
		List<WebTemplate> result = []

		def client = new RESTClient(templatesUrl)

		def resp = client.get(path: 'api/all')
		assert resp.status == 200

		resp.data.templates.each { result << new WebTemplate(id: it.id, name: it.name, project: URI.create(it.project), site: URI.create(it.site)) }

		result
	}
}
