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

import java.io.Closeable;
import java.util.Map;

import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;

/**
 * {@link Instance} processor
 * 
 * @author Florian Esser
 */
public interface InstanceProcessor extends Closeable {

	/**
	 * Set the context service provider.
	 * 
	 * @param services the service provider
	 */
	void setServiceProvider(ServiceProvider services);

	/**
	 * Process an instance together with its reference
	 * 
	 * @param instance the instance to process
	 * @param reference a reference to the instance, may be null
	 * 
	 */
	void process(Instance instance, InstanceReference reference);

	/**
	 * Process instances and their references
	 * 
	 * @param instancesAndReferences Map of instances and their respective
	 *            references. The reference may be null.
	 */
	void processAll(Map<Instance, InstanceReference> instancesAndReferences);
}
