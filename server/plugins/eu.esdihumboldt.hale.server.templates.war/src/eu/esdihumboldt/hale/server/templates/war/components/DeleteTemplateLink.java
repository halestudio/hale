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

package eu.esdihumboldt.hale.server.templates.war.components;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.server.db.orient.DatabaseHelper;
import eu.esdihumboldt.hale.server.model.Template;
import eu.esdihumboldt.hale.server.templates.TemplateScavenger;
import eu.esdihumboldt.hale.server.templates.war.pages.TemplatesPage;
import eu.esdihumboldt.util.blueprints.entities.NonUniqueResultException;

/**
 * Link that deletes a template and forwards to the templates page.
 * 
 * @author Simon Templer
 */
public class DeleteTemplateLink extends Link<Object> {

	private static final long serialVersionUID = 2329770405574728793L;

	private static final ALogger log = ALoggerFactory.getLogger(DeleteTemplateLink.class);

	private final String templateId;

	@SpringBean
	private TemplateScavenger scavenger;

	/**
	 * Constructor.
	 * 
	 * @param id the component ID
	 * @param templateId the identifier of the template to delete
	 */
	public DeleteTemplateLink(String id, String templateId) {
		super(id);
		this.templateId = templateId;
	}

	@Override
	public void onClick() {
		// delete from file system
		scavenger.deleteResource(templateId);

		// afterwards trigger the database deletion
		OrientGraph graph = DatabaseHelper.getGraph();
		try {
			Template template = Template.getByTemplateId(graph, templateId);
			if (template == null) {
				error("Template not found");
				return;
			}

			// delete from database
			template.delete();
		} catch (NonUniqueResultException e) {
			error("Internal error");
			log.error("Duplicate template");
		} finally {
			graph.shutdown();
		}

		setResponsePage(TemplatesPage.class);
	}
}