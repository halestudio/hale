/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.common.instance.processing;

import java.io.IOException;
import java.util.Map;

import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;

/**
 * Convenience base class for {@link InstanceProcessor} implementations
 * 
 * @author Florian Esser
 */
public abstract class AbstractInstanceProcessor implements InstanceProcessor {

	private ServiceProvider serviceProvider;

	/**
	 * @see eu.esdihumboldt.hale.common.instance.processing.InstanceProcessor#processAll(java.util.Map)
	 */
	@Override
	public void processAll(Map<Instance, InstanceReference> instancesAndReferences) {
		instancesAndReferences.forEach((inst, ref) -> this.process(inst, ref));
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.processing.InstanceProcessor#setServiceProvider(eu.esdihumboldt.hale.common.core.service.ServiceProvider)
	 */
	@Override
	public void setServiceProvider(ServiceProvider services) {
		this.serviceProvider = services;
	}

	/**
	 * @return the {@link ServiceProvider} or null
	 */
	protected ServiceProvider getServiceProvider() {
		return this.serviceProvider;
	}

	/**
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {
		// does nothing by default
	}

}
