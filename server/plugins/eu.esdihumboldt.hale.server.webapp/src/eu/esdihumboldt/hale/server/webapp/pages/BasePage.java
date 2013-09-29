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

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.util.string.StringValue;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.BootstrapBaseBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.dropdown.DropDownButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.dropdown.MenuBookmarkablePageLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.dropdown.MenuDivider;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.dropdown.MenuHeader;
import de.agilecoders.wicket.core.markup.html.bootstrap.html.ChromeFrameMetaTag;
import de.agilecoders.wicket.core.markup.html.bootstrap.html.HtmlTag;
import de.agilecoders.wicket.core.markup.html.bootstrap.html.OptimizedMobileViewportMetaTag;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.Navbar;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.Navbar.ComponentPosition;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.Navbar.Position;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarComponents;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarDropDownButton;
import de.agilecoders.wicket.core.settings.IBootstrapSettings;
import de.agilecoders.wicket.core.settings.ITheme;
import de.agilecoders.wicket.less.LessResourceReference;
import eu.esdihumboldt.hale.server.security.UserConstants;
import eu.esdihumboldt.hale.server.webapp.BaseWebApplication;
import eu.esdihumboldt.hale.server.webapp.components.bootstrap.NavbarExternalLink;
import eu.esdihumboldt.hale.server.webapp.util.PageDescription;
import eu.esdihumboldt.hale.server.webapp.util.UserUtil;

/**
 * The base page for all web applications. It contains definitions for all
 * pages' header and footer.
 * 
 * @author Michel Kraemer
 * @author Simon Templer
 */
public abstract class BasePage extends WebPage {

	private static final long serialVersionUID = 8363436886319254849L;
	private Navbar navbar;

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
	 * sets the theme for the current user.
	 * 
	 * @param pageParameters current page parameters
	 */
	private void configureTheme(PageParameters pageParameters) {
		StringValue theme = pageParameters.get("theme");

		if (!theme.isEmpty()) {
			IBootstrapSettings settings = Bootstrap.getSettings(getApplication());
			settings.getActiveThemeProvider().setActiveTheme(theme.toString(""));
		}
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();

		configureTheme(getPageParameters());
	}

	/**
	 * @see org.apache.wicket.Component#renderHead(IHeaderResponse)
	 */
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);

		Bootstrap.renderHead(response);

		// add base css to page
		response.render(CssReferenceHeaderItem.forReference(new CssResourceReference(
				BasePage.class, BasePage.class.getSimpleName() + ".css")));

		response.render(CssHeaderItem.forReference(new LessResourceReference(BasePage.class,
				BasePage.class.getSimpleName() + ".less")));
	}

	/**
	 * Add page controls
	 * 
	 * @param loggedIn if a user is logged in
	 */
	protected void addControls(boolean loggedIn) {
		add(new HtmlTag("html"));

		add(new OptimizedMobileViewportMetaTag("viewport"));
		add(new ChromeFrameMetaTag("chrome-frame"));

		// enable theme switching
		add(new BootstrapBaseBehavior());

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
//		Label applicatonTitleLabel = new Label("base-application-title", applicationTitle);
//		applicatonTitleLabel.setEscapeModelStrings(false);
//		add(applicatonTitleLabel);

		// get specific page title
		PageDescription anno = getClass().getAnnotation(PageDescription.class);
		if (anno != null && anno.title() != null) {
			pageTitle = pageTitle + " &raquo " + anno.title();
		}
		Label pageTitleLabel = new Label("base-page-title", pageTitle);
		pageTitleLabel.setEscapeModelStrings(false);
		add(pageTitleLabel);

		// create navigation bar
		this.navbar = new Navbar("navbar");
		add(navbar);

		navbar.setPosition(Position.TOP);
//		navbar.setBrandImage(new PackageResourceReference(BasePage.class, "graphics/logo.png"),
//				Model.<String> of());
		navbar.brandName(Model.of(applicationTitle));
		navbar.setInverted(true);

		if (loginEnabled) {
			if (!loggedIn) {
				// login link
				NavbarButton<Void> loginButton = new NavbarButton<>(
						((BaseWebApplication) app).getLoginPageClass(), Model.of("Login"));
				navbar.addComponents(NavbarComponents.transform(ComponentPosition.RIGHT,
						loginButton));
			}
			else {
				// logout link
				String logoutUrl = ((WebApplication) getApplication()).getServletContext()
						.getContextPath() + "/j_spring_security_logout";
				NavbarExternalLink logoutLink = new NavbarExternalLink(logoutUrl, "Logout");
				logoutLink.setIconType(IconType.off);
				logoutLink.setInverted(true);

				// user settings
				NavbarButton<Void> userButton = new NavbarButton<Void>(UserSettingsPage.class,
						Model.of(UserUtil.getUserName(null)));
				userButton.setIconType(IconType.user);
				/*
				 * XXX instead of getting the user name each time from DB, store
				 * it somewhere?
				 */

				navbar.addComponents(NavbarComponents.transform(ComponentPosition.RIGHT,
						userButton, logoutLink));
			}
		}

		// Theme selector drop-down
//		DropDownButton dropdown = createThemeDropdownButton();
//		dropdown.add(new DropDownAutoOpen());
//
//		navbar.addComponents(NavbarComponents.transform(ComponentPosition.RIGHT, dropdown));

		// XXX
//		add(new SimpleBreadcrumbPanel("breadcrumb", this.getClass(), "Home", "/"));
	}

	/**
	 * @return the navbar
	 */
	public Navbar getNavbar() {
		return navbar;
	}

	/**
	 * Create a dropdown button for selecting the theme.
	 * 
	 * @return the drop down button to add to a navbar
	 */
	protected DropDownButton createThemeDropdownButton() {
		return new NavbarDropDownButton(Model.of("Themes")) {

			private static final long serialVersionUID = -7119419621661580297L;

			@Override
			public boolean isActive(Component item) {
				return false;
			}

			@Override
			protected List<AbstractLink> newSubMenuButtons(final String buttonMarkupId) {
				final List<AbstractLink> subMenu = new ArrayList<AbstractLink>();
				subMenu.add(new MenuHeader(Model.of("all available themes:")));
				subMenu.add(new MenuDivider());

				final IBootstrapSettings settings = Bootstrap.getSettings(getApplication());
				final List<ITheme> themes = settings.getThemeProvider().available();

				for (final ITheme theme : themes) {
					PageParameters params = new PageParameters();
					params.set("theme", theme.name());

					subMenu.add(new MenuBookmarkablePageLink<Page>(getPageClass(), params, Model
							.of(theme.name())));
				}

				return subMenu;
			}
		}.setIconType(IconType.book);
	}
}
