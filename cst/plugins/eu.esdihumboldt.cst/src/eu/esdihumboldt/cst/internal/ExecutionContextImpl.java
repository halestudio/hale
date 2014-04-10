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

package eu.esdihumboldt.cst.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.transformation.function.ExecutionContext;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;

/**
 * Implementation of {@link ExecutionContext} used by
 * {@link TransformationContext}.
 * 
 * @author Kai Schwierczek
 */
public class ExecutionContextImpl implements ExecutionContext {

	private final ServiceProvider serviceProvider;
	private final FunctionContexts functionContexts;
	private final Map<Object, Object> transformationContext;
	private final Map<Object, Object> cellContext;
	private final Cell cell;

	/**
	 * Creates a execution context.
	 * 
	 * @param serviceProvider the service provider to use
	 * @param functionContexts used to acquire a function context
	 * @param transformationContext the transformation context to use, should
	 *            already be synchronized
	 * @param cell the cell this context is for
	 */
	public ExecutionContextImpl(ServiceProvider serviceProvider, FunctionContexts functionContexts,
			Map<Object, Object> transformationContext, Cell cell) {
		this.serviceProvider = serviceProvider;
		this.functionContexts = functionContexts;
		this.transformationContext = transformationContext;
		this.cell = cell;
		cellContext = Collections.synchronizedMap(new HashMap<>());
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
		return functionContexts.getFunctionContext(cell);
	}

	@Override
	public Map<Object, Object> getTransformationContext() {
		return transformationContext;
	}

}
