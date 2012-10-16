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

package eu.esdihumboldt.hale.server.webapp.pages;

import java.util.Calendar;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.protocol.http.WebApplication;

import eu.esdihumboldt.hale.server.webapp.BaseWebApplication;
import eu.esdihumboldt.hale.server.webapp.components.SimpleBreadcrumbPanel;
import eu.esdihumboldt.hale.server.webapp.util.PageDescription;

/**
 * The base page for all web applications. It contains definitions for all
 * pages' header and footer.
 * 
 * @author Michel Kraemer
 * @author Simon Templer
 */
public abstract class BasePage extends WebPage {

	/**
	 * Default constructor
	 */
	public BasePage() {
		// add base css to page
		HeaderContributor css = CSSPackageResource.getHeaderContribution(new ResourceReference(
				BasePage.class, BasePage.class.getSimpleName() + ".css"));
		add(css);

		// set link to home page
		WebApplication app = (WebApplication) this.getApplication();

		// add current year for copyright
		add(new Label("base-year", String.valueOf(Calendar.getInstance().get(Calendar.YEAR))));

		// set application title
		String applicationTitle = BaseWebApplication.DEFAULT_TITLE;
		if (app instanceof BaseWebApplication) {
			applicationTitle = ((BaseWebApplication) app).getMainTitle();
		}
		String pageTitle = applicationTitle.replace("-", "&raquo;");
		Label applicatonTitleLabel = new Label("base-application-title", applicationTitle);
		applicatonTitleLabel.setEscapeModelStrings(false);
		add(applicatonTitleLabel);

		// get specific page title
		PageDescription anno = getClass().getAnnotation(PageDescription.class);
		if (anno != null && anno.title() != null) {
			pageTitle = pageTitle + " &raquo " + anno.title();
		}
		Label pageTitleLabel = new Label("base-page-title", pageTitle);
		pageTitleLabel.setEscapeModelStrings(false);
		add(pageTitleLabel);

		add(new SimpleBreadcrumbPanel("breadcrumb", this.getClass(), "Home", "/"));
	}
}
