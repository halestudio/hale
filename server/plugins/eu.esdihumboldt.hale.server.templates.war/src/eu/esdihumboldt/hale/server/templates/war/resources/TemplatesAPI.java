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

package eu.esdihumboldt.hale.server.templates.war.resources;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.server.db.orient.DatabaseHelper;
import eu.esdihumboldt.hale.server.model.Template;
import eu.esdihumboldt.hale.server.templates.TemplateScavenger;
import eu.esdihumboldt.hale.server.templates.war.TemplateLocations;

/**
 * Template resources provided to the user.
 * 
 * @author Simon Templer
 */
@Controller
public class TemplatesAPI {

	private static final ALogger log = ALoggerFactory.getLogger(TemplatesAPI.class);

	private final TemplateScavenger scavenger;

	/**
	 * Constructor.
	 * 
	 * @param scavenger the template scavenger
	 */
	@Autowired
	public TemplatesAPI(TemplateScavenger scavenger) {
		this.scavenger = scavenger;
	}

	/**
	 * Retrieve all templates.
	 * 
	 * @param request the request
	 * @param response the response
	 */
	@RequestMapping(value = "/all", method = RequestMethod.GET, produces = "application/json")
	public void getFile(HttpServletRequest request, HttpServletResponse response) {
		OrientGraph graph = DatabaseHelper.getGraph();
		try {
			Iterable<Template> allTemplates = Template.findAll(graph);

			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");

			JsonFactory jsonFactory = new JsonFactory();
			try (Writer writer = response.getWriter();
					JsonGenerator gen = jsonFactory.createGenerator(writer)) {
				gen.writeStartObject();
				gen.writeArrayFieldStart("templates");

				for (Template template : allTemplates) {
					if (template.isValid()) {
						// only valid templates should be returned

						gen.writeStartObject();

						String id = template.getTemplateId();
						gen.writeStringField("id", id);
						gen.writeStringField("name", template.getName());
						gen.writeStringField("project",
								TemplateLocations.getTemplateProjectUrl(scavenger, id));
						gen.writeStringField("site", TemplateLocations.getTemplatePageUrl(id));

						gen.writeEndObject();
					}
				}

				gen.writeEndArray();
				gen.writeEndObject();
			} catch (IOException e) {
				log.error("Error writing JSON response", e);
			}
		} finally {
			graph.shutdown();
		}
	}
}
