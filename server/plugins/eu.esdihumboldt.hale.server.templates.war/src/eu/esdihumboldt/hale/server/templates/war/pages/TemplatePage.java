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

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.server.db.orient.DatabaseHelper;
import eu.esdihumboldt.hale.server.model.Template;
import eu.esdihumboldt.hale.server.model.User;
import eu.esdihumboldt.hale.server.templates.TemplateScavenger;
import eu.esdihumboldt.hale.server.templates.war.TemplateLocations;
import eu.esdihumboldt.hale.server.templates.war.components.DeleteTemplateLink;
import eu.esdihumboldt.hale.server.templates.war.components.ResourcesPanel;
import eu.esdihumboldt.hale.server.webapp.util.UserUtil;
import eu.esdihumboldt.util.blueprints.entities.NonUniqueResultException;

/**
 * Page displaying a single template.
 * 
 * @author Simon Templer
 */
public class TemplatePage extends TemplatesBasePage {

	private static final long serialVersionUID = 596965328223399419L;

	private static final ALogger log = ALoggerFactory.getLogger(TemplatePage.class);

	@SpringBean
	private TemplateScavenger scavenger;

	/**
	 * Default constructor.
	 */
	public TemplatePage() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param parameters the page parameters
	 */
	public TemplatePage(PageParameters parameters) {
		super(parameters);
	}

	@Override
	protected void addControls(boolean loggedIn) {
		super.addControls(loggedIn);

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
					// name
					Label name = new Label("name", template.getName());
					add(name);

					// download
					String href = TemplateLocations.getTemplateDownloadUrl(templateId);
					ExternalLink download = new ExternalLink("download", href);
					add(download);

					// project location
					WebMarkupContainer project = new WebMarkupContainer("project");
					project.add(AttributeModifier.replace("value",
							TemplateLocations.getTemplateProjectUrl(scavenger, templateId)));
					add(project);

					// author
					Label author = new Label("author", template.getAuthor());
					add(author);

					// edit-buttons container
					WebMarkupContainer editButtons = new WebMarkupContainer("edit-buttons");
					editButtons.setVisible(false);
					add(editButtons);

					// deleteDialog container
					WebMarkupContainer deleteDialog = new WebMarkupContainer("deleteDialog");
					deleteDialog.setVisible(false);
					add(deleteDialog);

					// user
					String userName;
					Vertex v = template.getV();
					Iterator<Vertex> owners = v.getVertices(Direction.OUT, "owner").iterator();
					if (owners.hasNext()) {
						User user = new User(owners.next(), graph);
						userName = UserUtil.getDisplayName(user);

						// edit buttons
						if (loggedIn && UserUtil.getLogin().equals(user.getLogin())) {
							editButtons.setVisible(true);
							deleteDialog.setVisible(true);

							// edit
							editButtons
									.add(new BookmarkablePageLink<>("edit", EditTemplatePage.class,
											new PageParameters().set(0, templateId)));

							// update
							editButtons.add(new BookmarkablePageLink<>("update",
									UpdateTemplatePage.class, new PageParameters().set(0,
											templateId)));

							// delete
							deleteDialog.add(new DeleteTemplateLink("delete", templateId));
						}
					}
					else {
						userName = "Unregistered user";
					}
					Label user = new Label("user", userName);
					add(user);

					// description
					String descr = template.getDescription();
					if (descr == null || descr.isEmpty()) {
						descr = "No description for the project template available.";
					}
					Label description = new Label("description", descr);
					add(description);

					// invalid
					WebMarkupContainer statusInvalid = new WebMarkupContainer("invalid");
					statusInvalid.setVisible(!template.isValid());
					add(statusInvalid);

					// resources
					ResourcesPanel resources = new ResourcesPanel("resources", templateId);
					add(resources);
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
