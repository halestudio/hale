/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.cst.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.transformation.function.ExecutionContext;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;

/**
 * Execution context for the transformation.
 * 
 * @author Simon Templer
 */
public class TransformationContext {

	/**
	 * Function contexts, mapped by function identifier.
	 */
	private final FunctionContexts functionContexts = new FunctionContexts();

	/**
	 * Overall transformation context.
	 */
	private final Map<Object, Object> context = Collections
			.synchronizedMap(new HashMap<Object, Object>());

	private final Map<Cell, ExecutionContext> cachedContexts = new IdentityHashMap<Cell, ExecutionContext>();

	private final ServiceProvider serviceProvider;

	private final Alignment alignment;

	/**
	 * Create a transformation context.
	 * 
	 * @param serviceProvider the provider for services needed by transformation
	 *            functions
	 * @param alignment the transformation alignment
	 */
	public TransformationContext(ServiceProvider serviceProvider, Alignment alignment) {
		super();
		this.serviceProvider = serviceProvider;
		this.alignment = alignment;
	}

	/**
	 * @return the serviceProvider
	 */
	public ServiceProvider getServiceProvider() {
		return serviceProvider;
	}

	/**
	 * Get the execution context for the given cell.
	 * 
	 * @param cell the cell
	 * @return the execution context
	 */
	public ExecutionContext getCellContext(final Cell cell) {
		ExecutionContext context;
		synchronized (cachedContexts) {
			context = cachedContexts.get(cell);
			if (context == null) {
				context = new ExecutionContextImpl(serviceProvider, alignment, functionContexts,
						TransformationContext.this.context, cell);
			}
			cachedContexts.put(cell, context);
		}
		return context;
	}

}
