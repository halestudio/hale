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

package eu.esdihumboldt.hale.common.core.service.internal;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.ExtensionUtil;
import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension.Identifiable;
import eu.esdihumboldt.hale.common.core.service.ServiceFactory;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;

/**
 * Definition of a service factory from the extension point.
 * 
 * @author Simon Templer
 */
public class ServiceFactoryDefinition implements Identifiable, ServiceFactory {

	private final String id;

	private final String scope;

	private final ServiceFactory serviceFactory;

	private final Set<Class<?>> serviceInterfaces = new HashSet<Class<?>>();

	/**
	 * Create a service factory definition from the given configuration element.
	 * 
	 * @param id the service factory identifier
	 * @param conf the configuration element
	 * @throws CoreException if instantiating the service factory fails
	 */
	public ServiceFactoryDefinition(String id, IConfigurationElement conf) throws CoreException {
		super();
		this.id = id;

		serviceFactory = (ServiceFactory) conf.createExecutableExtension("factory");

		scope = conf.getAttribute("scope");

		for (IConfigurationElement service : conf.getChildren("service")) {
			serviceInterfaces.add(ExtensionUtil.loadClass(service, "interface"));
		}
	}

	@Override
	public <T> T createService(Class<T> serviceInterface, ServiceProvider serviceLocator) {
		return serviceFactory.createService(serviceInterface, serviceLocator);
	}

	@Override
	public String getId() {
		return id;
	}

	/**
	 * Get the service factory service scope.
	 * 
	 * @return the scope the service scope
	 */
	public String getScope() {
		return scope;
	}

	/**
	 * Get the supported service interfaces of the factory.
	 * 
	 * @return the supported service interfaces
	 */
	public Set<Class<?>> getServiceInterfaces() {
		return serviceInterfaces;
	}

}
