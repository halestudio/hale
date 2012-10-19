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

import org.apache.wicket.PageParameters;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.protocol.http.WebApplication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import eu.esdihumboldt.hale.server.security.UserConstants;
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
		super();

		init();
	}

	/**
	 * @see WebPage#WebPage(PageParameters)
	 */
	public BasePage(PageParameters parameters) {
		super(parameters);

		init();
	}

	/**
	 * Add the page controls
	 */
	protected final void init() {
		// check if logged in
		SecurityContext securityContext = SecurityContextHolder.getContext();
		if (securityContext != null) {
			Authentication authentication = securityContext.getAuthentication();
			if (authentication != null && authentication.isAuthenticated()) {
				boolean user = false;
				for (GrantedAuthority authority : authentication.getAuthorities()) {
					if (authority.getAuthority().equals(UserConstants.ROLE_USER)
							|| authority.getAuthority().equals(UserConstants.ROLE_ADMIN)) {

						user = true;
						break;
					}
				}
				// logged in
				if (user) {
					addControls(true);

					return;
				}
			}
		}

		// not logged in or login forbidden
		addControls(false);
	}

	/**
	 * Add page controls
	 * 
	 * @param loggedIn if a user is logged in
	 */
	protected void addControls(boolean loggedIn) {
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

		WebMarkupContainer loginLogoutPanel = new WebMarkupContainer("loginLogoutPanel");
		// loginLogoutPanel.setVisible(...); XXX visibility can be toggled
		if (!loggedIn) {
			// login link
			BookmarkablePageLink<Void> link;
			loginLogoutPanel.add(link = new BookmarkablePageLink<Void>("loginLogout",
					LoginPage.class));
			link.add(new Label("label", "Login"));
		}
		else {
			// logout link
			ExternalLink link;
			loginLogoutPanel.add(link = new ExternalLink("loginLogout",
					((WebApplication) getApplication()).getServletContext().getContextPath()
							+ "/j_spring_security_logout"));

			// determine user name
			String userName = SecurityContextHolder.getContext().getAuthentication().getName();
			link.add(new Label("label", "Logout (" + userName + ")"));
		}
		add(loginLogoutPanel);

		add(new SimpleBreadcrumbPanel("breadcrumb", this.getClass(), "Home", "/"));
	}

}
