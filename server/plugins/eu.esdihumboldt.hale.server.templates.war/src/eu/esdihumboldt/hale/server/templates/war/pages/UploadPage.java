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

import org.apache.wicket.markup.html.form.Form;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.server.db.orient.DatabaseHelper;
import eu.esdihumboldt.hale.server.model.Template;
import eu.esdihumboldt.hale.server.model.User;
import eu.esdihumboldt.hale.server.templates.war.components.UploadForm;
import eu.esdihumboldt.hale.server.webapp.util.PageDescription;
import eu.esdihumboldt.hale.server.webapp.util.UserUtil;
import eu.esdihumboldt.util.blueprints.entities.NonUniqueResultException;

/**
 * Page for uploading new project templates.
 * 
 * @author Simon Templer
 */
@PageDescription(title = "Share template")
public class UploadPage extends TemplatesBasePage {

	private static final long serialVersionUID = -8268659882000252602L;

	private static final ALogger log = ALoggerFactory.getLogger(UploadPage.class);

	@Override
	protected void addControls(boolean loggedIn) {
		super.addControls(loggedIn);

		add(new UploadForm("upload") {

			private static final long serialVersionUID = -6705418055336562815L;

			@Override
			protected void onUploadSuccess(Form<?> form, String templateId, ProjectInfo info) {
				OrientGraph graph = DatabaseHelper.getGraph();
				try {
					Template template = Template.getByTemplateId(graph, templateId);
					if (template == null) {
						form.error("Template could not be created");
						return;
					}

					// associate user as owner to template
					String login = UserUtil.getLogin();
					if (login != null) {
						User user = User.getByLogin(graph, login);
						graph.addEdge(null, template.getV(), user.getV(), "owner");
					}

					setResponsePage(new NewTemplatePage(templateId));
				} catch (NonUniqueResultException e) {
					form.error("Internal error");
					log.error("Duplicate template or user");
				} finally {
					graph.shutdown();
				}
			}

		});
	}

}
