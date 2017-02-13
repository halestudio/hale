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

import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;

/**
 * {@link Instance} processor
 * 
 * @author Florian Esser
 */
public interface InstanceProcessor {

	/**
	 * Set the context service provider.
	 * 
	 * @param services the service provider
	 */
	void setServiceProvider(ServiceProvider services);

	/**
	 * Processing before an instance is stored.
	 * 
	 * @param instance the instance
	 */
	void beforeStore(Instance instance);

	/**
	 * Processing after an instance is stored
	 * 
	 * @param instance the stored instance
	 * @param reference a reference to the stored instance, may be null
	 */
	void afterStore(Instance instance, InstanceReference reference);
}
