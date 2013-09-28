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

package eu.esdihumboldt.hale.server.templates.war.components;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import eu.esdihumboldt.hale.server.db.orient.DatabaseHelper;
import eu.esdihumboldt.hale.server.model.Template;
import eu.esdihumboldt.util.blueprints.entities.NonUniqueResultException;

/**
 * Templates list.
 * 
 * @author Simon Templer
 */
public class TemplateList extends Panel {

	private static final long serialVersionUID = -6939011129125355533L;

//	private static final ALogger log = ALoggerFactory.getLogger(ProjectList.class);

	/**
	 * Constructor
	 * 
	 * @param id the panel id
	 * @param showCaption if the caption shall be shown
	 */
	public TemplateList(String id, boolean showCaption) {
		super(id);

		// templates list
		final IModel<? extends List<ORID>> templatesModel = new LoadableDetachableModel<List<ORID>>() {

			private static final long serialVersionUID = 7277175702043541004L;

			@Override
			protected List<ORID> load() {
				OrientGraph graph = DatabaseHelper.getGraph();
				try {
					Iterable<ODocument> docs = graph.command(
							new OCommandSQL("select @rid from template where valid = true"))
							.execute();

					List<ORID> result = new ArrayList<>();
					for (ODocument doc : docs) {
						result.add(((ODocument) doc.field("rid")).getIdentity());
					}
					return result;
				} finally {
					graph.shutdown();
				}
			}

		};

		final ListView<ORID> templateList = new ListView<ORID>("templates", templatesModel) {

			private static final long serialVersionUID = -6740090246572869212L;

			@Override
			protected void populateItem(ListItem<ORID> item) {
				final ORID id = item.getModelObject();

				OrientGraph graph = DatabaseHelper.getGraph();
				try {
					Template template = Template.getById(graph, id);

					// id
					item.add(new Label("id", id));

					// name
					item.add(new Label("name", template.getName()));

					// author
					item.add(new Label("author", template.getAuthor()));
				} catch (NonUniqueResultException e) {
					// ignore
				} finally {
					graph.shutdown();
				}
			}

		};
		add(templateList);

		boolean noTemplates = templatesModel.getObject().isEmpty();

		// caption
		WebMarkupContainer caption = new WebMarkupContainer("caption");
		caption.setVisible(showCaption && !noTemplates);
		add(caption);

		add(new WebMarkupContainer("notemplates").setVisible(noTemplates));
	}

}
