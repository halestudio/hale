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
import org.apache.wicket.protocol.http.WebApplication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import eu.esdihumboldt.hale.server.security.UserConstants;
import eu.esdihumboldt.hale.server.webapp.pages.LoginPage;
import eu.esdihumboldt.hale.server.webapp.pages.SecuredPage;
import eu.esdihumboldt.hale.server.webapp.util.DynamicSpringComponentInjector;

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
	 * @return the main title of this application
	 */
	public abstract String getMainTitle();

	@Override
	public void init() {
		super.init();

		// enforce mounts so security interceptors based on URLs can't be fooled
		getSecuritySettings().setEnforceMounts(true);

		getSecuritySettings().setAuthorizationStrategy(
				new SimplePageAuthorizationStrategy(SecuredPage.class, LoginPage.class) {

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

		addComponentInstantiationListener(new DynamicSpringComponentInjector());

		// add login page to every application based on this one
		// XXX make configurable?

		// login page
		mountBookmarkablePage("/login", LoginPage.class);
	}

}
