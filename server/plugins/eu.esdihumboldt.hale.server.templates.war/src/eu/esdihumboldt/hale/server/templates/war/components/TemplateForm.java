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

import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapForm;
import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.server.db.orient.DatabaseHelper;
import eu.esdihumboldt.hale.server.model.Template;
import eu.esdihumboldt.hale.server.templates.war.pages.TemplatePage;
import eu.esdihumboldt.hale.server.webapp.components.bootstrap.BootstrapFeedbackPanel;
import eu.esdihumboldt.util.blueprints.entities.NonUniqueResultException;

/**
 * Form for filling in template information.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("serial")
public class TemplateForm extends Panel {

	private static final ALogger log = ALoggerFactory.getLogger(TemplateForm.class);

//	private final boolean newTemplate;

	private String name;

	private String author;

	private String description;

	private String templateId;

	/**
	 * Create a template form.
	 * 
	 * @param componentId the component ID in the markup
	 * @param newTemplate if the panel is displayed for a newly created template
	 * @param templateId the identifier of the created template
	 */
	public TemplateForm(String componentId, boolean newTemplate, String templateId) {
		super(componentId);
//		this.newTemplate = newTemplate;
		this.templateId = templateId;

		OrientGraph graph = DatabaseHelper.getGraph();
		try {
			Template template = Template.getByTemplateId(graph, templateId);
			setName(template.getName());
			setAuthor(template.getAuthor());
			setDescription(template.getDescription());
		} catch (NonUniqueResultException e) {
			error("Internal error");
			log.error("Duplicate template");
		} finally {
			graph.shutdown();
		}

		BootstrapForm<TemplateForm> form = new BootstrapForm<TemplateForm>("template",
				new CompoundPropertyModel<>(this)) {

			@Override
			protected void onSubmit() {
				OrientGraph graph = DatabaseHelper.getGraph();
				try {
					Template template = Template.getByTemplateId(graph,
							TemplateForm.this.templateId);
					if (template == null) {
						error("Template not found");
						return;
					}

					template.setName(getName());
					template.setAuthor(getAuthor());
					template.setDescription(getDescription());
				} catch (NonUniqueResultException e) {
					error("Internal error");
					log.error("Duplicate template");
				} finally {
					graph.shutdown();
				}

				success("The template information was successfully updated.");

//				if (TemplateForm.this.newTemplate) {
				// forward to template page
				setResponsePage(TemplatePage.class,
						new PageParameters().set(0, TemplateForm.this.templateId));
//				}
			}

		};
		add(form);

		// fields
		form.add(new TextField<>("name"));
		form.add(new TextField<>("author"));
		form.add(new TextArea<>("description"));

		// delete link
		Link<?> deleteLink = new DeleteTemplateLink("delete", templateId);
		deleteLink.setVisible(newTemplate);
		form.add(deleteLink);

		// feedback
		form.add(new BootstrapFeedbackPanel("feedback"));
	}

	@SuppressWarnings("javadoc")
	public String getName() {
		return name;
	}

	@SuppressWarnings("javadoc")
	public void setName(String name) {
		this.name = name;
	}

	@SuppressWarnings("javadoc")
	public String getAuthor() {
		return author;
	}

	@SuppressWarnings("javadoc")
	public void setAuthor(String author) {
		this.author = author;
	}

	@SuppressWarnings("javadoc")
	public String getDescription() {
		return description;
	}

	@SuppressWarnings("javadoc")
	public void setDescription(String description) {
		this.description = description;
	}

}
