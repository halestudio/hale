/*
 * Copyright (c) 2013 Simon Templer
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
 *     Simon Templer - initial version
 */

package eu.esdihumboldt.hale.server.api.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.server.projects.ProjectScavenger;
import eu.esdihumboldt.hale.server.projects.ScavengerException;
import eu.esdihumboldt.util.io.IOUtils;

/**
 * Project management controller.
 * 
 * @author Simon Templer
 */
@Controller
public class Projects {

	private static final ALogger log = ALoggerFactory.getLogger(Projects.class);

	private final ProjectScavenger projects;

	private final Set<String> allowedUploadContentTypes = new HashSet<String>();

	/**
	 * Create a projects controller.
	 * 
	 * @param projects the project service
	 */
	@Autowired
	public Projects(ProjectScavenger projects) {
		super();
		this.projects = projects;

		allowedUploadContentTypes.add("application/zip");
		allowedUploadContentTypes.add("application/x-zip");
		allowedUploadContentTypes.add("application/x-zip-compressed");
	}

	/**
	 * Upload a new project.<br>
	 * <br>
	 * Example call with curl:
	 * <code>curl -F "archive=@test.zip" http://localhost:8080/api/project/test</code>
	 * 
	 * @param id the project identifier
	 * @param archive the project archive
	 * @param response the request response
	 */
	@RequestMapping(value = "/project/{id}", method = RequestMethod.POST, consumes = "multipart/form-data")
	public void createProject(@PathVariable String id, @RequestPart MultipartFile archive,
			HttpServletResponse response) {
		try {
			if (id != null && archive != null) {
				File dir = projects.reserveProjectId(id);

//				String type = archive.getContentType();
//
//				if (allowedUploadContentTypes.contains(type)) {
				// try extracting the archive
				IOUtils.extract(dir, new BufferedInputStream(archive.getInputStream()));

				// trigger scan after upload
				projects.triggerScan();
				log.info("Successfully uploaded new project " + id);

				response.setStatus(HttpServletResponse.SC_CREATED);
//				}
//				else {
//					projects.releaseProjectId(id);
//					throw new HttpMediaTypeNotAcceptableException(
//							"Only ZIP project archives supported for upload.");
//				}
			}
			else {
				// bad request
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			}
		} catch (ScavengerException e) {
			// could not create project
			try {
				response.sendError(HttpServletResponse.SC_CONFLICT);
			} catch (IOException e1) {
				// ignore
			}
		} catch (Exception e) {
			projects.releaseProjectId(id);
			log.error("Error while uploading project file", e);
			try {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			} catch (IOException e1) {
				// ignore
			}
		}
	}

	/**
	 * List the available projects.<br>
	 * <br>
	 * Example call with curl:
	 * <code>curl -G http://localhost:8080/api/projects</code>
	 */
	@RequestMapping(value = "/projects", method = RequestMethod.GET)
	public void listProjects() {
		// TODO
	}

}
