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
import java.util.zip.ZipOutputStream

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

import com.tinkerpop.blueprints.impls.orient.OrientGraph

import de.fhg.igd.slf4jplus.ALogger
import de.fhg.igd.slf4jplus.ALoggerFactory
import eu.esdihumboldt.hale.common.headless.scavenger.ProjectReference
import eu.esdihumboldt.hale.server.db.orient.DatabaseHelper
import eu.esdihumboldt.hale.server.model.Template
import eu.esdihumboldt.hale.server.templates.TemplateScavenger
import eu.esdihumboldt.util.io.IOUtils
import groovy.transform.CompileStatic


/**
 * Template resources provided to the user.
 * 
 * @author Simon Templer
 */
@CompileStatic
@Controller
class TemplatesResources {

	private static final ALogger log = ALoggerFactory.getLogger(TemplatesResources)

	private final Path baseDir

	private final TemplateScavenger scavenger

	@Autowired
	public TemplatesResources(TemplateScavenger scavenger) {
		this.scavenger = scavenger
		this.baseDir = scavenger.huntingGrounds.toPath()
	}

	/**
	 * Retrieve mapping files. 
	 */
	@RequestMapping(value = '/files/{id}/**', method = RequestMethod.GET)
	void getFile(@PathVariable('id') String id,
			HttpServletRequest request, HttpServletResponse response) {
		ProjectReference<?> ref = (ProjectReference) scavenger.getReference(id);
		if (ref == null) {
			response.sendError HttpServletResponse.SC_NOT_FOUND, "Template with ID $id does not exist."
			return
		}

		// try to resolve the servlet path against the base path
		String path = request.pathInfo
		int index = path.indexOf('/', 1)
		path = path.substring(index + 1);
		Path requested = baseDir.resolve(path).normalize()
		if (requested.startsWith(baseDir)) {
			// path lies in templates directory
			if (Files.exists(requested)) {
				response.status = HttpServletResponse.SC_OK
				response.outputStream.withStream { OutputStream out ->
					Files.copy(requested, out)
				}

				// check if accessed file was the project file and count hit
				File projectFile = ref.getProjectFile();
				if (projectFile != null && projectFile.toPath() == requested) {
					OrientGraph graph = DatabaseHelper.getGraph()
					try {
						Template template = Template.getByTemplateId(graph, id)
						if (template != null) {
							template.setHits(template.getHits() + 1)
						}
					} catch (Exception e) {
						log.error("Error counting hit on template $id", e);
					} finally {
						graph.shutdown()
					}
				}

				return
			}
		}

		// send 404
		response.sendError HttpServletResponse.SC_NOT_FOUND
	}

	/**
	 * Retrieve ZIP archive of template.
	 */
	@RequestMapping(value = '/archive/{id}', method = RequestMethod.GET, produces = 'application/zip')
	void getArchive(@PathVariable('id') String id,
			HttpServletRequest request, HttpServletResponse response) {
		ProjectReference<?> ref = (ProjectReference) scavenger.getReference(id);
		if (ref == null) {
			response.sendError HttpServletResponse.SC_NOT_FOUND, "Template with ID $id does not exist."
			return
		}

		Path templatePath = scavenger.huntingGrounds.toPath().resolve(id);
		if (Files.exists(templatePath)) {
			response.status = HttpServletResponse.SC_OK
			response.contentType = 'application/zip'
			response.addHeader('Content-Disposition', "attachment; filename=\"template-${id}.zip\"")

			new ZipOutputStream(response.outputStream).withStream{ OutputStream zos ->
				IOUtils.zipDirectory(templatePath.toFile(), (ZipOutputStream) zos)
			}

			// count download
			OrientGraph graph = DatabaseHelper.getGraph()
			try {
				Template template = Template.getByTemplateId(graph, id)
				if (template != null) {
					template.setDownloads(template.getDownloads() + 1)
				}
			} catch (Exception e) {
				log.error("Error counting donwload on template $id", e);
			} finally {
				graph.shutdown()
			}

			return
		}

		// send 404
		response.sendError HttpServletResponse.SC_NOT_FOUND
	}
}
