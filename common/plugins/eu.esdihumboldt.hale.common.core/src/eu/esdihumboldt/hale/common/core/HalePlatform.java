/*
 * Copyright (c) 2016 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.core;

import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.annotation.Nullable;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.osgi.framework.Version;

import de.fhg.igd.osgi.util.OsgiUtils;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.internal.CoreBundle;
import eu.esdihumboldt.hale.common.core.service.ServiceConstants;
import eu.esdihumboldt.hale.common.core.service.ServiceManager;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.util.nonosgi.NonOsgiPlatform;

/**
 * Helper methods for the Hale platform.
 * 
 * @author Simon Templer
 */
public class HalePlatform {

	private static final ALogger log = ALoggerFactory.getLogger(HalePlatform.class);

	private static class PlatformServiceProvider implements ServiceProvider {

		private static final ServiceManager globalScope = new ServiceManager(
				ServiceConstants.SCOPE_GLOBAL);

		@Override
		public <T> T getService(Class<T> serviceInterface) {
			T service = globalScope.getService(serviceInterface);
			// TODO service manager with SPI support?

			// try OSGi as fall-back
			if (service == null && CoreBundle.isOsgi()) {
				service = OsgiUtils.getService(serviceInterface);
			}

			return service;
		}

	}

	private static final ServiceProvider serviceProvider = new PlatformServiceProvider();

	/**
	 * Get the content type manager. Uses the default Eclipse content type
	 * manager if running in OSGi, otherwise an adapted version.
	 * 
	 * @return the content type manager
	 */
	public static IContentTypeManager getContentTypeManager() {
		if (CoreBundle.isOsgi()) {
			return Platform.getContentTypeManager();
		}
		else {
			return NonOsgiPlatform.getContentTypeManager();
		}
	}

	/**
	 * Get a service based on it's interface type. The service provided here
	 * have global scope, services that are UI-specific are not included, use
	 * HaleUI instead to access those services.
	 * 
	 * @param serviceClass the service class
	 * @return the service instance or <code>null</code>
	 */
	@Nullable
	public static <S> S getService(Class<S> serviceClass) {
		return getServiceProvider().getService(serviceClass);
	}

	/**
	 * Get the platform global scoped service provider.
	 * 
	 * @return the service provider
	 */
	public static ServiceProvider getServiceProvider() {
		return serviceProvider;
	}

	private static volatile Version coreVersion;

	/**
	 * Determine the version of the currently used HALE core bundle/library.
	 * 
	 * @return the version of the HALE core bundle/library
	 */
	public static Version getCoreVersion() {
		if (coreVersion == null) {
			try {
				if (CoreBundle.isOsgi() && CoreBundle.getInstance() != null) {
					coreVersion = CoreBundle.getInstance().getContext().getBundle().getVersion();
				}
				else {
					String classFile = HalePlatform.class.getSimpleName() + ".class";
					String classPath = HalePlatform.class.getResource(classFile).toString();
					String manifestPath = classPath.replace(
							HalePlatform.class.getCanonicalName().replaceAll("\\.", "/") + ".class",
							"META-INF/MANIFEST.MF");
					Manifest manifest = new Manifest(new URL(manifestPath).openStream());
					Attributes attr = manifest.getMainAttributes();
					String versionString = attr.getValue("Bundle-Version");
					coreVersion = Version.parseVersion(versionString);
				}
			} catch (Exception e) {
				log.error("Failure determining hale core version", e);
				coreVersion = Version.emptyVersion;
			}
		}

		return coreVersion;
	}

	/**
	 * Determine if the HALE core bundle/library is a SNAPSHOT version.
	 * 
	 * @return <code>true</code> if the HALE core bundle/library used is
	 *         verified to be a SNAPSHOT version, <code>false</code> otherwise
	 */
	public static boolean isSnapshotVersion() {
		String qualifier = getCoreVersion().getQualifier();
		return qualifier != null && (qualifier.equals("SNAPSHOT") || qualifier.equals("qualifier"));
	}

}
