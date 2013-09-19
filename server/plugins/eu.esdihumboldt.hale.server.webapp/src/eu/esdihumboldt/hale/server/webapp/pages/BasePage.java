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

import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
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

	private static final long serialVersionUID = 8363436886319254849L;

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
	 * @see org.apache.wicket.Component#renderHead(IHeaderResponse)
	 */
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);

		// add base css to page
		response.render(CssReferenceHeaderItem.forReference(new CssResourceReference(
				BasePage.class, BasePage.class.getSimpleName() + ".css")));
	}

	/**
	 * Add page controls
	 * 
	 * @param loggedIn if a user is logged in
	 */
	protected void addControls(boolean loggedIn) {
		// set link to home page
		WebApplication app = (WebApplication) this.getApplication();

		// set application title & determine if login page is enabled
		String applicationTitle = BaseWebApplication.DEFAULT_TITLE;
		boolean loginEnabled = false;
		if (app instanceof BaseWebApplication) {
			BaseWebApplication bwa = (BaseWebApplication) app;
			applicationTitle = bwa.getMainTitle();
			loginEnabled = bwa.getLoginPageClass() != null;
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
		loginLogoutPanel.setVisible(loginEnabled);
		if (!loggedIn) {
			// login link
			BookmarkablePageLink<Void> link;
			loginLogoutPanel.add(link = new BookmarkablePageLink<Void>("loginLogout",
					((BaseWebApplication) app).getLoginPageClass()));
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
