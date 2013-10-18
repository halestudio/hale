/*
 * Copyright (c) 2012 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.server.projects.war.components;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.server.projects.ProjectScavenger;
import eu.esdihumboldt.hale.server.projects.ProjectScavenger.Status;

/**
 * Project list.
 * 
 * @author Simon Templer
 */
public class ProjectList extends Panel {

	private static final long serialVersionUID = -6939011129125355533L;

//	private static final ALogger log = ALoggerFactory.getLogger(ProjectList.class);

	@SpringBean
	private ProjectScavenger projects;

	/**
	 * Constructor
	 * 
	 * @param id the panel id
	 * @param showCaption if the caption shall be shown
	 */
	public ProjectList(String id, boolean showCaption) {
		super(id);

		// projects list
		final IModel<? extends List<String>> projectsModel = new LoadableDetachableModel<List<String>>() {

			private static final long serialVersionUID = 7277175702043541004L;

			@Override
			protected List<String> load() {
				return new ArrayList<String>(projects.getResources());
			}

		};

		final ListView<String> projectList = new ListView<String>("projects", projectsModel) {

			private static final long serialVersionUID = -6740090246572869212L;

			/**
			 * @see ListView#populateItem(ListItem)
			 */
			@Override
			protected void populateItem(ListItem<String> item) {
				final boolean odd = item.getIndex() % 2 != 0;
				if (odd) {
					item.add(AttributeModifier.replace("class", "odd"));
				}

				final String id = item.getModelObject();

				// identifier
				item.add(new Label("identifier", id));

				// status
				Status status = projects.getStatus(id);
				String statusImagePath;
				String statusTitle;
				switch (status) {
				case ACTIVE:
					statusImagePath = "images/ok.png";
					statusTitle = "Active";
					break;
				case INACTIVE:
					statusImagePath = "images/sleeping.gif";
					statusTitle = "Inactive";
					break;
				case BROKEN:
					statusImagePath = "images/error.gif";
					statusTitle = "Project cannot be loaded";
					break;
				case NOT_AVAILABLE:
				default:
					statusImagePath = "images/unknown.gif";
					statusTitle = "Project file missing or not set";
				}
				WebComponent statusImage = new WebComponent("status");
				statusImage.add(AttributeModifier.replace("src", statusImagePath));
				statusImage.add(AttributeModifier.replace("title", statusTitle));
				item.add(statusImage);

				// action
				String actionImagePath;
				String actionTitle;
				boolean showAction;
				Link<?> actionLink;
				switch (status) {
				case ACTIVE:
					actionTitle = "Stop";
					actionImagePath = "images/stop.gif";
					showAction = true;
					actionLink = new Link<Void>("action") {

						private static final long serialVersionUID = 393941411843332519L;

						@Override
						public void onClick() {
							projects.deactivate(id);
						}

					};
					break;
				case BROKEN:
				case NOT_AVAILABLE:
					actionTitle = "Rescan";
					actionImagePath = "images/refresh.gif";
					showAction = true;
					actionLink = new Link<Void>("action") {

						private static final long serialVersionUID = -4403828305588875839L;

						@Override
						public void onClick() {
							projects.triggerScan();
						}

					};
					break;
				case INACTIVE:
				default:
					actionTitle = "Start";
					actionImagePath = "images/start.gif";
					showAction = status.equals(Status.INACTIVE);
					actionLink = new Link<Void>("action") {

						private static final long serialVersionUID = 393941411843332519L;

						@Override
						public void onClick() {
							projects.activate(id);
						}

					};
					break;
				}
				WebComponent actionImage = new WebComponent("image");
				actionImage.add(AttributeModifier.replace("src", actionImagePath));
				actionImage.add(AttributeModifier.replace("title", actionTitle));
				actionLink.add(actionImage);
				actionLink.setVisible(showAction);
				item.add(actionLink);

				// name
				String projectName = "";
				ProjectInfo info = projects.getInfo(id);
				if (info != null) {
					projectName = info.getName();
				}
				item.add(new Label("name", projectName));

				// download log
				File logFile = projects.getLoadReports(id);
				DownloadLink log = new DownloadLink("log", logFile, id + ".log");
				log.setVisible(logFile != null && logFile.exists());
				WebComponent logImage = new WebComponent("image");
				if (status == Status.BROKEN) {
					logImage.add(AttributeModifier.replace("src", "images/error_log.gif"));
				}
				log.add(logImage);
				item.add(log);
			}

		};
		add(projectList);

		boolean noProjects = projectsModel.getObject().isEmpty();

		// caption
		WebMarkupContainer caption = new WebMarkupContainer("caption");
		caption.setVisible(showCaption && !noProjects);
		add(caption);

		add(new WebMarkupContainer("noprojects").setVisible(noProjects));
	}

}
