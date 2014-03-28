/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.functions.groovy;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import eu.esdihumboldt.hale.common.align.transformation.function.ExecutionContext;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;

/**
 * Dummy execution context for validation purposes.
 * 
 * @author Kai Schwierczek
 */
public class DummyExecutionContext implements ExecutionContext {

	private final Map<Object, Object> cellContext = Collections.synchronizedMap(new HashMap<>());
	private final Map<Object, Object> functionContext = Collections
			.synchronizedMap(new HashMap<>());
	private final Map<Object, Object> transformationContext = Collections
			.synchronizedMap(new HashMap<>());
	private final ServiceProvider serviceProvider;

	/**
	 * Constructor which only requires a service provider.
	 * 
	 * @param serviceProvider the service provider to use
	 */
	public DummyExecutionContext(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
	}

	@Override
	public <T> T getService(Class<T> serviceInterface) {
		return serviceProvider.getService(serviceInterface);
	}

	@Override
	public Map<Object, Object> getCellContext() {
		return cellContext;
	}

	@Override
	public Map<Object, Object> getFunctionContext() {
		return functionContext;
	}

	@Override
	public Map<Object, Object> getTransformationContext() {
		return transformationContext;
	}
}
