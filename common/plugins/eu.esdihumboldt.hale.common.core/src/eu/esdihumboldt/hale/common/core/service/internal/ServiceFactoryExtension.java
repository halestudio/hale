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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;

/**
 * Extension for service factories.
 * 
 * @author Simon Templer
 */
public class ServiceFactoryExtension extends IdentifiableExtension<ServiceFactoryDefinition> {

	private static final ALogger log = ALoggerFactory.getLogger(ServiceFactoryExtension.class);

	/**
	 * The extension point identifier.
	 */
	public static final String EXTENSION_ID = "eu.esdihumboldt.hale.service";

	private static ServiceFactoryExtension instance;

	/**
	 * Get the service factory extension instance.
	 * 
	 * @return the extension instance
	 */
	public static ServiceFactoryExtension getInstance() {
		synchronized (ServiceFactoryExtension.class) {
			if (instance == null)
				instance = new ServiceFactoryExtension();
		}
		return instance;
	}

	/**
	 * Default constructor.
	 */
	public ServiceFactoryExtension() {
		super(EXTENSION_ID, true, false);
	}

	@Override
	protected String getIdAttributeName() {
		return "id";
	}

	@Override
	protected ServiceFactoryDefinition create(String elementId, IConfigurationElement element) {
		try {
			return new ServiceFactoryDefinition(elementId, element);
		} catch (CoreException e) {
			log.error("Could not load service factory with ID " + elementId);
			return null;
		}
	}

}
