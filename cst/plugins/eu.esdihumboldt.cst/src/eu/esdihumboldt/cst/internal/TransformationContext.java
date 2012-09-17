/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.cst.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.transformation.function.ExecutionContext;

/**
 * Execution context for the transformation.
 * 
 * @author Simon Templer
 */
public class TransformationContext {

	/**
	 * Function contexts, mapped by function identifier.
	 * 
	 * XXX would implementation class be better?
	 */
	private final Map<String, Map<Object, Object>> functionContexts = new HashMap<String, Map<Object, Object>>();

	/**
	 * Overall transformation context.
	 */
	private final Map<Object, Object> context = Collections
			.synchronizedMap(new HashMap<Object, Object>());

	private final Map<Cell, ExecutionContext> cachedContexts = new IdentityHashMap<Cell, ExecutionContext>();

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
				context = new ExecutionContext() {

					private final Map<Object, Object> cellContext = Collections
							.synchronizedMap(new HashMap<Object, Object>());

					@Override
					public Map<Object, Object> getTransformationContext() {
						return TransformationContext.this.context;
					}

					@Override
					public Map<Object, Object> getFunctionContext() {
						String functionId = cell.getTransformationIdentifier();
						Map<Object, Object> context;
						synchronized (functionContexts) {
							context = functionContexts.get(functionId);
							if (context == null) {
								context = Collections
										.synchronizedMap(new HashMap<Object, Object>());
								functionContexts.put(functionId, context);
							}
						}
						return context;
					}

					@Override
					public Map<Object, Object> getCellContext() {
						return cellContext;
					}
				};
			}
			cachedContexts.put(cell, context);
		}
		return context;
	}

}
