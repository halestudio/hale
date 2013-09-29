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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.BootstrapPagingNavigator.Position;
import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.ajax.BootstrapAjaxPagingNavigator;
import eu.esdihumboldt.hale.server.db.orient.DatabaseHelper;
import eu.esdihumboldt.hale.server.model.Template;
import eu.esdihumboldt.hale.server.templates.war.pages.UploadPage;
import eu.esdihumboldt.util.blueprints.entities.NonUniqueResultException;

/**
 * Templates list.
 * 
 * @author Simon Templer
 */
public class TemplateList extends Panel {

	private static final long serialVersionUID = -6939011129125355533L;

//	private static final ALogger log = ALoggerFactory.getLogger(ProjectList.class);

	private String searchText;

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * Constructor
	 * 
	 * @param id the panel id
	 */
	public TemplateList(String id) {
		super(id);

		setOutputMarkupId(true);

		// templates list
		final IModel<? extends List<ORID>> templatesModel = new LoadableDetachableModel<List<ORID>>() {

			private static final long serialVersionUID = 7277175702043541004L;

			@Override
			protected List<ORID> load() {
				OrientGraph graph = DatabaseHelper.getGraph();
				try {
					String searchText = getSearchText();
					OCommandSQL sql;
					if (searchText == null || searchText.isEmpty()) {
						sql = new OCommandSQL(
								"SELECT @rid,name FROM template WHERE valid = true ORDER BY name");
					}
					else {
						searchText = "%" + searchText.toLowerCase() + "%";
						sql = new OCommandSQL(
								"SELECT @rid,name FROM template WHERE valid = true"
										+ " AND (name.toLowerCase() like $searchtext OR author.toLowerCase() like $searchtext)"
										+ " ORDER BY name");
						sql.getContext().setVariable("searchtext", searchText);
					}

					Iterable<ODocument> docs = graph.command(sql).execute();

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

		PageableListView<ORID> templateList = new PageableListView<ORID>("templates",
				templatesModel, 10) {

			private static final long serialVersionUID = -6740090246572869212L;

			@Override
			protected void populateItem(ListItem<ORID> item) {
				final ORID id = item.getModelObject();

				OrientGraph graph = DatabaseHelper.getGraph();
				try {
					Template template = Template.getById(graph, id);

					// id
//					item.add(new Label("id", id));

					// name
					item.add(new Label("name", template.getName()));

					// author
					item.add(new Label("author", template.getAuthor()));

					// last update
					item.add(new Label("update", dateFormat.format(template.getLastUpdate())));

					// popularity
					WebMarkupContainer popularity = new WebMarkupContainer("popularity");
					item.add(popularity);
					int pop = template.getHits() + template.getDownloads();
					Label popValue = new Label("value", String.valueOf(pop));
					popValue.setVisible(pop > 0);
					popularity.add(popValue);
				} catch (NonUniqueResultException e) {
					// ignore
				} finally {
					graph.shutdown();
				}
			}

		};
		add(templateList);

		BootstrapAjaxPagingNavigator pager = new BootstrapAjaxPagingNavigator("pager", templateList) {

			private static final long serialVersionUID = -9058994579222245191L;

			@Override
			public boolean isVisible() {
				return !templatesModel.getObject().isEmpty();
			}

		};
		pager.setPosition(Position.Centered);
		add(pager);

		// caption
		WebMarkupContainer caption = new WebMarkupContainer("caption") {

			private static final long serialVersionUID = 3631062343612621123L;

			@Override
			public boolean isVisible() {
				return !templatesModel.getObject().isEmpty();
			}

		};
		add(caption);

		add(new WebMarkupContainer("notemplates") {

			private static final long serialVersionUID = 8802435323301967389L;

			@Override
			public boolean isVisible() {
				return templatesModel.getObject().isEmpty();
			}

		});

		// search form
		Form<TemplateList> searchForm = new Form<TemplateList>("search",
				new CompoundPropertyModel<>(this));
		add(searchForm);

		TextField<String> searchText = new TextField<String>("searchText");
		searchForm.add(searchText);

		// new template link
		add(new BookmarkablePageLink<Void>("upload", UploadPage.class));
	}

	/**
	 * @return the searchText
	 */
	public String getSearchText() {
		return searchText;
	}

	/**
	 * @param searchText the searchText to set
	 */
	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

}
