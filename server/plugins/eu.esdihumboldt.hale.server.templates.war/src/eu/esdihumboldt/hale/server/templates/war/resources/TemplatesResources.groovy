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

package eu.esdihumboldt.hale.server.templates.war.resources

import java.nio.file.Files
import java.nio.file.Path

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

import eu.esdihumboldt.hale.server.templates.TemplateScavenger
import groovy.transform.CompileStatic


/**
 * Template resources provided to the user.
 * 
 * @author Simon Templer
 */
@CompileStatic
@Controller
class TemplatesResources {

	private final Path baseDir

	@Autowired
	public TemplatesResources(TemplateScavenger scavenger) {
		this.baseDir = scavenger.huntingGrounds.toPath();
	}

	@RequestMapping(value = '/files/{id}/**', method = RequestMethod.GET)
	void createProject(@PathVariable('id') String id,
			HttpServletRequest request, HttpServletResponse response) {
		// try to resolve the servlet path against the base path
		String path = request.pathInfo
		int index = path.indexOf('/', 1)
		path = path.substring(index + 1);
		Path requested = baseDir.resolve(path).normalize()
		if (requested.startsWith(baseDir)) {
			// path lies in templates directory
			if (Files.exists(requested)) {
				response.outputStream.withStream { OutputStream out ->
					Files.copy(requested, out)
				}
				return
			}
		}

		// send 404
		response.sendError HttpServletResponse.SC_NOT_FOUND
	}
}
