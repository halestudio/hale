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

/**
 * Function contexts used by {@link TransformationContext} and
 * {@link ExecutionContextImpl}.
 * 
 * @author Kai
 */
public class FunctionContexts {

	private final Map<String, Map<Object, Object>> functionContexts = new HashMap<String, Map<Object, Object>>();

	/**
	 * Returns a function context for the function of the specified cell.
	 * Convenience method for
	 * <code>getFunctionContext(cell.getTransformationIdentifier())</code>.
	 * 
	 * When invoking multiple operations on the context map, make sure to use
	 * synchronization if appropriate.
	 * 
	 * @param cell the cell for which to acquire a function context
	 * @return the function context associated with the given cell
	 */
	public Map<Object, Object> getFunctionContext(Cell cell) {
		return getFunctionContext(cell.getTransformationIdentifier());
	}

	/**
	 * Returns a function context for the specified function id.
	 * 
	 * When invoking multiple operations on the context map, make sure to use
	 * synchronization if appropriate.
	 * 
	 * @param functionId the function id for which to acquire a function context
	 * @return the function context associated with the id
	 */
	public Map<Object, Object> getFunctionContext(String functionId) {
		Map<Object, Object> context;
		synchronized (functionContexts) {
			context = functionContexts.get(functionId);
			if (context == null) {
				context = Collections.synchronizedMap(new HashMap<Object, Object>());
				functionContexts.put(functionId, context);
			}
		}
		return context;
	}
}
