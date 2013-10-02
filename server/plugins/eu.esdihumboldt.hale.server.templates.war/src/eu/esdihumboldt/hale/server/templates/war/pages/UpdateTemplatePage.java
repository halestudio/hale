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

package eu.esdihumboldt.hale.server.templates.war.pages;

import java.util.Iterator;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.server.db.orient.DatabaseHelper;
import eu.esdihumboldt.hale.server.model.Template;
import eu.esdihumboldt.hale.server.model.User;
import eu.esdihumboldt.hale.server.templates.war.components.TemplateUploadForm;
import eu.esdihumboldt.hale.server.webapp.util.UserUtil;
import eu.esdihumboldt.util.blueprints.entities.NonUniqueResultException;

/**
 * Page for updating a template.
 * 
 * @author Simon Templer
 */
public class UpdateTemplatePage extends TemplatesSecuredPage {

	private static final long serialVersionUID = 5042784175750862684L;

	private static final ALogger log = ALoggerFactory.getLogger(UpdateTemplatePage.class);

	/**
	 * @see TemplatesSecuredPage#TemplatesSecuredPage(PageParameters)
	 */
	public UpdateTemplatePage(PageParameters parameters) {
		super(parameters);
	}

	@Override
	protected void addControls() {
		StringValue idParam = getPageParameters().get(0);
		if (!idParam.isNull() && !idParam.isEmpty()) {
			String templateId = idParam.toString();

			OrientGraph graph = DatabaseHelper.getGraph();
			try {
				Template template = null;
				try {
					template = Template.getByTemplateId(graph, templateId);
				} catch (NonUniqueResultException e) {
					log.error("Duplicate template representation: " + templateId, e);
				}
				if (template != null) {
					// get associated user
					Vertex v = template.getV();
					Iterator<Vertex> owners = v.getVertices(Direction.OUT, "owner").iterator();
					if (owners.hasNext()) {
						User user = new User(owners.next(), graph);

						// check if user is owner
						if (UserUtil.getLogin().equals(user.getLogin())) {
							add(new Label("name", template.getName()));

							add(new TemplateUploadForm("upload-form", templateId));
						}
						else {
							throw new AbortWithHttpErrorCodeException(
									HttpServletResponse.SC_FORBIDDEN);
						}
					}
					else {
						throw new AbortWithHttpErrorCodeException(
								HttpServletResponse.SC_BAD_REQUEST,
								"Template doesn't have an owner.");
					}
				}
				else {
					throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND,
							"Template not found.");
				}
			} finally {
				graph.shutdown();
			}
		}
		else
			throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_BAD_REQUEST,
					"Template identifier must be specified.");
	}
}
