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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.server.webapp.components;

import java.util.Vector;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import eu.esdihumboldt.hale.server.webapp.util.PageDescription;

/**
 * A panel which displays simple breadcrumbs
 * 
 * @author Michel Kraemer
 */
public class SimpleBreadcrumbPanel extends Panel {

	/**
	 * The serial version UID
	 */
	private static final long serialVersionUID = 27264261659073154L;

	/**
	 * Constructs a new breadcrumb panel. If a link to a root website should be
	 * part of the breadcrumbs, then <code>rootLinkName</code> and
	 * <code>rootLinkTarget</code> must both be set.
	 * 
	 * @param id the component's id
	 * @param currentPage the class of the current page
	 * @param rootLinkName the title of the link to the root website (can be
	 *            null)
	 * @param rootLinkTarget the href target of the link to the root website
	 *            (can be null)
	 */
	public SimpleBreadcrumbPanel(String id, Class<? extends Page> currentPage,
			final String rootLinkName, final String rootLinkTarget) {
		super(id);

		String title = "";
		PageDescription anno = currentPage.getAnnotation(PageDescription.class);
		if (anno != null) {
			if (anno.title() == null) {
				throw new RuntimeException(currentPage + " has no annotated title");
			}
			title = anno.title();
		}

		add(new Label("breadcrumb-current", title));

		// add bread crumbs
		Vector<Class<? extends Page>> links = new Vector<Class<? extends Page>>();
		while (anno != null && anno.parent() != WebPage.class) {
			Class<? extends WebPage> par = anno.parent();
			if (!par.isAnnotationPresent(PageDescription.class)) {
				break;
			}
			links.insertElementAt(anno.parent(), 0);
			anno = anno.parent().getAnnotation(PageDescription.class);
		}

		// add a dummy element for the root web page (but don't do this if the
		// current page *is* the root page)
		if (rootLinkName != null && rootLinkTarget != null) {
			boolean root = false;
			if (currentPage.isAnnotationPresent(PageDescription.class)) {
				root = currentPage.getAnnotation(PageDescription.class).root();
			}
			if (!root) {
				links.insertElementAt(null, 0);
			}
		}

		// fill list view with bread crumbs
		add(new ListView<Class<? extends Page>>("breadcrumb-panel", links) {

			private static final long serialVersionUID = 1221964671030364825L;

			@Override
			public void populateItem(final ListItem<Class<? extends Page>> item) {
				Class<? extends Page> p = item.getModelObject();
				WebMarkupContainer link;
				if (p != null) {
					link = new BookmarkablePageLink<Void>("breadcrumb-link", p);
					PageDescription anno = p.getAnnotation(PageDescription.class);
					link.add(new Label("breadcrumb-link-text", anno.title()));
				}
				else {
					link = new ExternalLink("breadcrumb-link", rootLinkTarget);
					link.add(new Label("breadcrumb-link-text", rootLinkName));
				}
				item.add(link);
			}
		});
	}
}
