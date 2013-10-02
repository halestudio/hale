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

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import eu.esdihumboldt.hale.common.core.io.IOAction;
import eu.esdihumboldt.hale.common.core.io.extension.IOActionExtension;
import eu.esdihumboldt.hale.common.core.io.project.model.Resource;
import eu.esdihumboldt.hale.server.templates.TemplateProject;
import eu.esdihumboldt.hale.server.templates.TemplateScavenger;
import eu.esdihumboldt.hale.server.templates.war.TemplateLocations;

/**
 * Panel listing a templates' resources.
 * 
 * @author Simon Templer
 */
public class ResourcesPanel extends Panel {

	private static final long serialVersionUID = -2736270787771363436L;

	@SpringBean
	private TemplateScavenger templates;

	/**
	 * Constructor.
	 * 
	 * @param id the component ID
	 * @param templateId the template identifier
	 */
	public ResourcesPanel(String id, final String templateId) {
		super(id);

		@SuppressWarnings("serial")
		IModel<? extends List<? extends IOAction>> actionResources = new LoadableDetachableModel<List<? extends IOAction>>() {

			@Override
			protected List<? extends IOAction> load() {
				TemplateProject ref = templates.getReference(templateId);
				List<IOAction> result = new ArrayList<>();
				if (ref != null) {
					for (String id : ref.getResources().keySet()) {
						IOAction action = IOActionExtension.getInstance().get(id);
						if (action != null) {
							result.add(action);
						}
					}
				}
				return result;
			}
		};

		@SuppressWarnings("serial")
		ListView<IOAction> actions = new ListView<IOAction>("resources", actionResources) {

			@Override
			protected void populateItem(ListItem<IOAction> item) {
				IOAction action = item.getModelObject();
				final String actionId = action.getId();

				// resource category
				String category = action.getResourceCategoryName();
				if (category == null || category.isEmpty()) {
					category = action.getName();
				}
				if (category == null || category.isEmpty()) {
					category = action.getId();
				}
				item.add(new Label("category", category));

				@SuppressWarnings("serial")
				IModel<? extends List<? extends Resource>> resourcesModel = new LoadableDetachableModel<List<? extends Resource>>() {

					@Override
					protected List<? extends Resource> load() {
						TemplateProject ref = templates.getReference(templateId);
						List<Resource> result = new ArrayList<>();
						if (ref != null) {
							result.addAll(ref.getResources().get(actionId));
						}
						return result;
					}
				};

				// resources
				@SuppressWarnings("serial")
				ListView<Resource> resources = new ListView<Resource>("resource", resourcesModel) {

					@Override
					protected void populateItem(ListItem<Resource> item) {
						Resource res = item.getModelObject();

						String href = null;
						String name = "Unknown resource";
						if (res.getSource() != null) {
							if ("file".equals(res.getSource().getScheme())) {
								Path resPath = Paths.get(res.getSource()).normalize();
								Path basePath = new File(templates.getHuntingGrounds(), templateId)
										.toPath();

								if (resPath.startsWith(basePath)) {
									String templateRelativePath = basePath.relativize(resPath)
											.toString();
									name = resPath.getFileName().toString();
									href = TemplateLocations.getTemplateFileUrl(templates,
											templateId, templateRelativePath);
								}
								else {
									// invalid file to reference
									name = resPath.toString();
								}
							}
							else if ("resource".equals(res.getSource().getScheme())) {
								name = res.getSource().toASCIIString();
							}
							else {
								href = res.getSource().toASCIIString();
								name = href;
							}
						}

						WebMarkupContainer link;
						if (href != null) {
							link = new ExternalLink("link", href);
						}
						else {
							link = new WebMarkupContainer("link");
						}
						item.add(link);

						link.add(new Label("name", name));
					}
				};
				item.add(resources);
			}
		};
		add(actions);
	}
}
