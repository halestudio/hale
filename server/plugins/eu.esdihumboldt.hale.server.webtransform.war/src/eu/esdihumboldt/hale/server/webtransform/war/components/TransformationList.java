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

package eu.esdihumboldt.hale.server.webtransform.war.components;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.common.headless.EnvironmentService;
import eu.esdihumboldt.hale.common.headless.TransformationEnvironment;
import eu.esdihumboldt.hale.server.webtransform.war.pages.UploadPage;

/**
 * Transformations list.
 * 
 * @author Simon Templer
 */
public class TransformationList extends Panel {

	private static final long serialVersionUID = -6939011129125355533L;

//	private static final ALogger log = ALoggerFactory.getLogger(ProjectList.class);

	@SpringBean
	private EnvironmentService transformations;

	/**
	 * Constructor
	 * 
	 * @param id the panel id
	 * @param showCaption if the caption shall be shown
	 */
	public TransformationList(String id, boolean showCaption) {
		super(id);

		// transformations list
		final IModel<? extends List<TransformationEnvironment>> transformationsModel = new LoadableDetachableModel<List<TransformationEnvironment>>() {

			private static final long serialVersionUID = 7277175702043541004L;

			@Override
			protected List<TransformationEnvironment> load() {
				return new ArrayList<TransformationEnvironment>(transformations.getEnvironments());
			}

		};

		final ListView<TransformationEnvironment> transformationList = new ListView<TransformationEnvironment>(
				"transformations", transformationsModel) {

			private static final long serialVersionUID = -6740090246572869212L;

//			private boolean odd = true; // starting with one

			/**
			 * @see ListView#populateItem(ListItem)
			 */
			@Override
			protected void populateItem(ListItem<TransformationEnvironment> item) {
//				if (odd) {
//					item.add(new SimpleAttributeModifier("class", "odd"));
//				}
//				odd = !odd;

				final TransformationEnvironment env = item.getModelObject();

				// identifier
				item.add(new Label("identifier", env.getId()));

				// name
				String projectName = "";
				ProjectInfo info = env.getProjectInfo();
				if (info != null) {
					projectName = info.getName();
				}
				item.add(new Label("name", projectName));

				// upload and transform link
				item.add(new BookmarkablePageLink<Void>("upload", UploadPage.class,
						new PageParameters().add(UploadPage.PARAMETER_PROJECT, env.getId())));
			}

		};
		add(transformationList);

		boolean noTransformations = transformationsModel.getObject().isEmpty();

		// caption
		WebMarkupContainer caption = new WebMarkupContainer("caption");
		caption.setVisible(showCaption && !noTransformations);
		add(caption);

		add(new WebMarkupContainer("notransformations").setVisible(noTransformations));
	}

}
