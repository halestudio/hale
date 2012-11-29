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

package eu.esdihumboldt.hale.server.security.extender;

import java.io.IOException;

import org.eclipse.gemini.blueprint.io.OsgiBundleResourcePatternResolver;
import org.eclipse.gemini.blueprint.util.BundleDelegatingClassLoader;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.Resource;
import org.springframework.osgi.web.context.support.OsgiBundleXmlWebApplicationContext;

import de.fhg.igd.osgi.util.ChainedClassLoader;
import eu.esdihumboldt.hale.server.security.extender.internal.Activator;

/**
 * A web application context which can load context configuration resources from
 * security bundles. These security bundles are found through an extender
 * pattern. They are registered by this bundle's activator.
 * 
 * @author Michel Kraemer
 */
public class SecuredBundleXmlWebApplicationContext extends OsgiBundleXmlWebApplicationContext
		implements SecuredApplicationContext {

	/**
	 * The META-INF directory
	 */
	private static final String META_INF = "/META-INF/";

	/**
	 * The cached class loader returned by {@link #getClassLoader()}
	 */
	private transient ClassLoader _classLoader;

	/**
	 * True if security management is enabled, false if everyone is allowed to
	 * do everything. Will be set to false when the default configuration file
	 * is used (because this file allows everything), will be set to true when
	 * any other file is used. The default value is true, because we want to be
	 * restrictive in the beginning.
	 */
	private boolean _securityEnabled = true;

	/**
	 * @see OsgiBundleXmlWebApplicationContext#loadBeanDefinitions(XmlBeanDefinitionReader)
	 */
	@Override
	protected void loadBeanDefinitions(XmlBeanDefinitionReader reader) throws BeansException,
			IOException {
		// load bean definitions of the war bundle
		super.loadBeanDefinitions(reader);

		// load security bean definitions...
		BundleContext ctx = Activator.getContext();
		Bundle securitybnd = ctx.getBundle();
		OsgiBundleResourcePatternResolver resolver = new OsgiBundleResourcePatternResolver(
				securitybnd);

		// try to load security bean definitions from a fragment
		String loc = META_INF + "applicationContext-security.xml";
		Resource[] resources = resolver.getResources(loc);
		int n = 0;
		if (resources != null) {
			// check if the resources exist
			for (Resource r : resources) {
				if (r.exists()) {
					++n;
				}
			}
			if (n > 0) {
				n = reader.loadBeanDefinitions(resources);
			}
		}

		if (n == 0) {
			// load default security bean definitions from this bundle
			loc = META_INF + "applicationContext-security-default.xml";
			resources = resolver.getResources(loc);
			if (resources != null) {
				n = reader.loadBeanDefinitions(resources);
			}
			if (n == 0) {
				throw new RuntimeException("Could not load default security " + "bean definitions");
			}

			// the default definition file allows everything, so set
			// security management to disabled
			_securityEnabled = false;
		}
	}

	/**
	 * @see OsgiBundleXmlWebApplicationContext#getClassLoader()
	 */
	@Override
	public ClassLoader getClassLoader() {
		ClassLoader cl = super.getClassLoader();
		if (cl == null) {
			// the class loader has not been set yet
			return null;
		}

		if (_classLoader == null) {
			BundleContext ctx = Activator.getContext();
			Bundle securitybnd = ctx.getBundle();
			ClassLoader bndcl = BundleDelegatingClassLoader.createBundleClassLoaderFor(securitybnd);
			_classLoader = new ChainedClassLoader(cl, bndcl);
		}
		return _classLoader;
	}

	/**
	 * @see OsgiBundleXmlWebApplicationContext#setClassLoader(ClassLoader)
	 */
	@Override
	public void setClassLoader(ClassLoader classLoader) {
		_classLoader = null;
		super.setClassLoader(classLoader);
	}

	@Override
	public boolean isSecurityEnabled() {
		return _securityEnabled;
	}
}
