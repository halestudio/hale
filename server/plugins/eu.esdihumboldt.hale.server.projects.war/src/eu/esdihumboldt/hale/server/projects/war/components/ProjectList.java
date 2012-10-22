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

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import eu.esdihumboldt.hale.server.projects.ProjectScavenger;

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

		// caption
		WebMarkupContainer caption = new WebMarkupContainer("caption");
		caption.setVisible(showCaption);
		add(caption);

		// attachment list
		final IModel<? extends List<String>> projectsModel = new LoadableDetachableModel<List<String>>() {

			private static final long serialVersionUID = 7277175702043541004L;

			@Override
			protected List<String> load() {
				return new ArrayList<String>(projects.getProjects());
			}

		};

		final ListView<String> files = new ListView<String>("projects", projectsModel) {

			private static final long serialVersionUID = -6740090246572869212L;

			/**
			 * @see ListView#populateItem(ListItem)
			 */
			@Override
			protected void populateItem(ListItem<String> item) {
				final String id = item.getModelObject();

				item.add(new Label("identifier", id));
				item.add(new Label("status", projects.getStatus(id).toString()));
			}

		};
		add(files);

		add(new WebComponent("noprojects") {

			private static final long serialVersionUID = 3116030626059724802L;

			@Override
			public boolean isVisible() {
				return projectsModel.getObject().isEmpty();
			}

		});
	}

}
