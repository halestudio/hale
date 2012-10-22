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

package eu.esdihumboldt.hale.server.webapp.util;

import org.apache.wicket.Component;
import org.apache.wicket.application.IComponentInstantiationListener;
import org.apache.wicket.injection.ConfigurableInjector;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.ISpringContextLocator;
import org.apache.wicket.spring.injection.annot.AnnotSpringInjector;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 
 * Injects Spring beans into Wicket components just like the
 * SpringComponentInjector does, but saves the Injector itself and not in the
 * static InjectorHolder class.
 * 
 * <p>
 * This solves the problem with multiple wicket applications. The
 * SpringComponentInjector uses the same injector for all applications, but the
 * injector saves the application context of only one application at the same
 * time. Therefore, most beans cannot be injected correctly.
 * </p>
 * 
 * <p>
 * Hence, if you want to use the SpringComponentInjector with multiple web
 * applications, you better use the DynamicSpringComponentInjector instead. In
 * the WebApplication.init() method call <code>
 *      addComponentInstantiationListener(new DynamicSpringComponentInjector());
 *   </code>
 * </p>
 * 
 * @author Michel Kraemer
 */
public class DynamicSpringComponentInjector implements IComponentInstantiationListener {

	/**
	 * The actual component injector
	 */
	private final ConfigurableInjector _injector;

	/**
	 * Construct a new component injector
	 */
	public DynamicSpringComponentInjector() {
		_injector = new AnnotSpringInjector(new ContextLocator());
	}

	/**
	 * @see IComponentInstantiationListener#onInstantiation(Component)
	 */
	@Override
	public void onInstantiation(Component component) {
		_injector.inject(component);
	}

	/**
	 * Retrieves the Spring application context from the current web application
	 */
	private static class ContextLocator implements ISpringContextLocator {

		/**
		 * The serial version UID
		 */
		private static final long serialVersionUID = -5976010333002182774L;

		/**
		 * @see ISpringContextLocator#getSpringContext()
		 */
		@Override
		public ApplicationContext getSpringContext() {
			WebApplication app = WebApplication.get();
			WebApplicationContext ctx = WebApplicationContextUtils
					.getRequiredWebApplicationContext(app.getServletContext());
			return ctx;
		}
	}
}
