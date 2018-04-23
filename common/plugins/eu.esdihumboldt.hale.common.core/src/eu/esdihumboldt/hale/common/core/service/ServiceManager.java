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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.service.internal.ServiceFactoryDefinition;
import eu.esdihumboldt.hale.common.core.service.internal.ServiceFactoryExtension;

/**
 * Manages HALE service instances.
 * 
 * @author Simon Templer
 */
public class ServiceManager implements ServiceProvider, ServiceConstants {

	private static final ALogger log = ALoggerFactory.getLogger(ServiceManager.class);

	private final String serviceScope;

	/**
	 * Service factories.
	 */
	private final ListMultimap<Class<?>, ServiceFactory> factories = ArrayListMultimap.create();

	/**
	 * Instantiated services.
	 */
	private final ListMultimap<Class<?>, Object> services = ArrayListMultimap.create();

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

	@Override
	public <T> T getService(Class<T> serviceInterface) {
		Collection<T> services = getServices(serviceInterface);
		if (!services.isEmpty()) {
			return services.iterator().next();
		}
		else {
			return null;
		}
	}

	/**
	 * Get service instances providing the given interface.
	 * 
	 * @param serviceInterface the service interface
	 * @return the service instances available
	 */
	@SuppressWarnings("unchecked")
	public <T> Collection<T> getServices(Class<T> serviceInterface) {
		synchronized (services) {
			List<T> instances = (List<T>) services.get(serviceInterface);
			if (instances.isEmpty()) {
				List<ServiceFactory> serviceFactories = factories.get(serviceInterface);
				for (ServiceFactory factory : serviceFactories) {
					try {
						T service = factory.createService(serviceInterface, getServiceLocator());
						if (service != null) {
							services.put(serviceInterface, service);
						}
					} catch (Exception e) {
						log.error("Error creating " + getServiceScope()
								+ " service instance for interface " + serviceInterface.getName(),
								e);
					}
				}
				if (instances.isEmpty()) {
					// add null to mark that we tried to create the services
					// already
					instances.add(null);
				}
			}
			if (instances.size() == 1 && instances.get(0) == null) {
				// no services available
				return Collections.emptyList();
			}
			else {
				return ImmutableList.copyOf(instances);
			}
		}
	}

	/**
	 * @return the service locator to provide when creating services
	 */
	protected ServiceProvider getServiceLocator() {
		return this;
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
