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

package eu.esdihumboldt.hale.server.webapp;

import org.apache.wicket.authorization.strategies.page.SimplePageAuthorizationStrategy;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.core.request.handler.RenderPageRequestHandler;
import org.apache.wicket.markup.html.IPackageResourceGuard;
import org.apache.wicket.markup.html.SecurePackageResourceGuard;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.core.settings.BootstrapSettings;
import de.agilecoders.wicket.core.settings.ThemeProvider;
import de.agilecoders.wicket.less.BootstrapLess;
import de.agilecoders.wicket.themes.markup.html.bootstrap3.Bootstrap3Theme;
import de.agilecoders.wicket.themes.markup.html.google.GoogleTheme;
import de.agilecoders.wicket.themes.markup.html.metro.MetroTheme;
import de.agilecoders.wicket.themes.markup.html.wicket.WicketTheme;
import de.agilecoders.wicket.themes.settings.BootswatchThemeProvider;
import eu.esdihumboldt.hale.server.security.UserConstants;
import eu.esdihumboldt.hale.server.webapp.pages.BasePage;
import eu.esdihumboldt.hale.server.webapp.pages.ExceptionPage;
import eu.esdihumboldt.hale.server.webapp.pages.LoginPage;
import eu.esdihumboldt.hale.server.webapp.pages.NewUserPage;
import eu.esdihumboldt.hale.server.webapp.pages.OpenIdLoginPage;
import eu.esdihumboldt.hale.server.webapp.pages.SecuredPage;
import eu.esdihumboldt.hale.server.webapp.pages.UserSettingsPage;

/**
 * A basic class for web applications
 * 
 * @author Michel Kraemer
 * @author Simon Templer
 */
public abstract class BaseWebApplication extends WebApplication {

	/**
	 * The default title of a web application
	 */
	public static final String DEFAULT_TITLE = "HALE Web";

	/**
	 * Name of the system property that allows to specify a custom main title.
	 */
	public static final String SYSTEM_PROPERTY_MAIN_TITLE = "hale.webapp.maintitle";

	/**
	 * Name of the system property that allows enabling/disabling the login
	 * page.
	 */
	public static final String SYSTEM_PROPERTY_LOGIN_PAGE = "hale.webapp.loginpage";

	/**
	 * Get the default application title. Is either the value of the system
	 * property {@value #SYSTEM_PROPERTY_MAIN_TITLE} or {@link #DEFAULT_TITLE}.
	 * 
	 * @return the default title
	 */
	public static String getDefaulTitle() {
		return System.getProperty(SYSTEM_PROPERTY_MAIN_TITLE, DEFAULT_TITLE);
	}

	/**
	 * @return the main title of this application
	 */
	public String getMainTitle() {
		return getDefaulTitle();
	}

	/**
	 * Determines the login page type for this application. The default
	 * implementation looks at the {@value #SYSTEM_PROPERTY_LOGIN_PAGE} system
	 * property for this, if not specified the default is no login page.
	 * 
	 * @return a page class or <code>null</code>
	 */
	public Class<? extends BasePage> getLoginPageClass() {
		String loginPage = System.getProperty(SYSTEM_PROPERTY_LOGIN_PAGE, "false");

		switch (loginPage.toLowerCase()) {
		case "true": // fall through
		case "form":
			return LoginPage.class;
		case "openid":
			return OpenIdLoginPage.class;
		default:
			return null;
		}
	}

	@Override
	public void init() {
		super.init();

		BootstrapSettings settings = new BootstrapSettings();
		final ThemeProvider themeProvider = new BootswatchThemeProvider() {

			{
				add(new MetroTheme());
				add(new GoogleTheme());
				add(new WicketTheme());
				add(new Bootstrap3Theme());
//				defaultTheme("bootstrap-responsive");
				// XXX CSS for bootstrap responsive has some issues
				defaultTheme("bootstrap");
			}
		};
		settings.setThemeProvider(themeProvider);

		Bootstrap.install(this, settings);
		BootstrapLess.install(this);
		configureResourceBundles();

		IPackageResourceGuard packageResourceGuard = getResourceSettings()
				.getPackageResourceGuard();
		if (packageResourceGuard instanceof SecurePackageResourceGuard) {
			SecurePackageResourceGuard guard = (SecurePackageResourceGuard) packageResourceGuard;
			guard.addPattern("+org/apache/wicket/resource/jquery/*.map");
		}

		// enforce mounts so security interceptors based on URLs can't be fooled
		getSecuritySettings().setEnforceMounts(true);

		getSecuritySettings().setAuthorizationStrategy(
				new SimplePageAuthorizationStrategy(SecuredPage.class, getLoginPageClass()) {

					@Override
					protected boolean isAuthorized() {
						SecurityContext securityContext = SecurityContextHolder.getContext();
						if (securityContext != null) {
							Authentication authentication = securityContext.getAuthentication();
							if (authentication != null && authentication.isAuthenticated()) {
								for (GrantedAuthority authority : authentication.getAuthorities()) {
									if (authority.getAuthority().equals(UserConstants.ROLE_USER)
											|| authority.getAuthority().equals(
													UserConstants.ROLE_ADMIN)) {

										// allow access only for users/admins
										return true;
									}
								}
							}
						}

						return false;
					}

				});

		getComponentInstantiationListeners().add(new SpringComponentInjector(this));

		getRequestCycleListeners().add(new AbstractRequestCycleListener() {

			@Override
			public IRequestHandler onException(RequestCycle cycle, Exception ex) {
				return new RenderPageRequestHandler(new PageProvider(new ExceptionPage(ex)));
			}
		});

		// add login page to every application based on this one (if enabled)
		Class<? extends BasePage> loginClass = getLoginPageClass();
		if (loginClass != null) {
			// login page
			mountPage("/login", loginClass);

			// user settings
			mountPage("/settings", UserSettingsPage.class);

			if (OpenIdLoginPage.class.equals(loginClass)) {
				// for OpenID auth also add page for new users
				mountPage("/new", NewUserPage.class);
			}
		}
	}

	/**
	 * Configure all resource bundles (CSS and JS)
	 */
	private void configureResourceBundles() {
		/*
		 * XXX Somehow wrecks JQuery needed in OpenID login page, also, some
		 * resources of the given are not found.
		 */
//		getResourceBundles().addJavaScriptBundle(
//				BaseWebApplication.class,
//				"core.js",
//				(JavaScriptResourceReference) getJavaScriptLibrarySettings().getJQueryReference(),
//				(JavaScriptResourceReference) getJavaScriptLibrarySettings()
//						.getWicketEventReference(),
//				(JavaScriptResourceReference) getJavaScriptLibrarySettings()
//						.getWicketAjaxReference(),
//				(JavaScriptResourceReference) ModernizrJavaScriptReference.INSTANCE);
//
//		getResourceBundles().addJavaScriptBundle(BaseWebApplication.class, "bootstrap.js",
//				(JavaScriptResourceReference) Bootstrap.getSettings().getJsResourceReference(),
//				(JavaScriptResourceReference) BootstrapPrettifyJavaScriptReference.INSTANCE);
//
//		getResourceBundles().addJavaScriptBundle(BaseWebApplication.class,
//				"bootstrap-extensions.js", JQueryUIJavaScriptReference.instance(),
//				Html5PlayerJavaScriptReference.instance());
//
//		getResourceBundles().addCssBundle(BaseWebApplication.class, "bootstrap-extensions.css",
//				Html5PlayerCssReference.instance(), OpenWebIconsCssReference.instance());
//
//		getResourceBundles().addCssBundle(BaseWebApplication.class, "application.css",
//				(CssResourceReference) BootstrapPrettifyCssReference.INSTANCE);
	}
}
