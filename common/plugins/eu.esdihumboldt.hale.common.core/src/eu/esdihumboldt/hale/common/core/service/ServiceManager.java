/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.core.service;

import java.util.HashMap;
import java.util.Map;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.service.internal.ServiceFactoryDefinition;
import eu.esdihumboldt.hale.common.core.service.internal.ServiceFactoryExtension;

/**
 * Manages HALE service instances.
 * 
 * @author Simon Templer
 */
public class ServiceManager implements ServiceProvider, ServiceConstants {

	/*
	 * An alternative to the ServiceManager would be something like a
	 * ServicePublisher, e.g. for publishing service as OSGi service. But for
	 * this either all services would have to be created an provided, or (better
	 * option) a proxy object should be published.
	 * 
	 * TODO Create a ServicePublisher based on proxies and create and install it
	 * in the core bundle activator for the 'global' scope, so these services
	 * always are available through OSGi.
	 */

	private static final ALogger log = ALoggerFactory.getLogger(ServiceManager.class);

	private final String serviceScope;

	/**
	 * Service factories.
	 */
	private final Map<Class<?>, ServiceFactory> factories = new HashMap<Class<?>, ServiceFactory>();

	/**
	 * Instantiated services.
	 */
	private final Map<Class<?>, Object> services = new HashMap<Class<?>, Object>();

	/**
	 * Create a service manager for the given scope.
	 * 
	 * @param serviceScope the service scope, <code>null</code> for all service
	 *            scopes
	 */
	public ServiceManager(String serviceScope) {
		super();
		this.serviceScope = serviceScope;

		// get all relevant services of the given scope
		for (ServiceFactoryDefinition sf : ServiceFactoryExtension.getInstance().getElements()) {
			if (serviceScope == null || serviceScope.equals(sf.getScope())) {
				// only use service factories of the correct scope
				for (Class<?> iface : sf.getServiceInterfaces()) {
					factories.put(iface, sf);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getService(Class<T> serviceInterface) {
		synchronized (services) {
			Object service = services.get(serviceInterface);
			if (service == null) {
				ServiceFactory factory = factories.get(serviceInterface);
				if (factory != null) {
					try {
						service = factory.createService(serviceInterface, this);
						services.put(serviceInterface, service);
					} catch (Exception e) {
						log.error("Error creating " + getServiceScope()
								+ " service instance for interface " + serviceInterface.getName(),
								e);
					}
				}
			}

			return (T) service;
		}
	}

	/**
	 * Get the scope of services provided by the manager.
	 * 
	 * @return the service scope name or <code>null</code> representing all
	 *         scopes
	 */
	public String getServiceScope() {
		return serviceScope;
	}

	/**
	 * Remove all service instances.
	 */
	public void clear() {
		synchronized (services) {
			services.clear();
		}
	}

}
