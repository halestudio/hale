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

package eu.esdihumboldt.hale.common.align.transformation.function;

import java.util.Map;

/**
 * Execution context of a transformation process. The information put into the
 * execution context is held for the whole transformation process. It can be
 * used by transformation functions to share information. For implementations
 * please be aware that if the transformation may be executed in multiple
 * threads, the getters defined here as well as the returned maps have to use
 * mechanisms to ensure their consistency (e.g. locking/synchronization).
 * 
 * @author Simon Templer
 */
public interface ExecutionContext {

	/**
	 * Get the execution context for the cell associated to the executed
	 * function. This context is shared only between function executions
	 * handling this cell.
	 * 
	 * When invoking multiple operations on the context map, make sure to use
	 * synchronization if appropriate.
	 * 
	 * @return the cell context map
	 */
	public Map<Object, Object> getCellContext();

	/**
	 * Get the execution context for the executed function. This context is
	 * shared between functions of the same type.
	 * 
	 * When invoking multiple operations on the context map, make sure to use
	 * synchronization if appropriate.
	 * 
	 * @return the function context map
	 */
	public Map<Object, Object> getFunctionContext();

	/**
	 * Get the overall transformation execution context. This context is shared
	 * between all functions of the same type.
	 * 
	 * When invoking multiple operations on the context map, make sure to use
	 * synchronization if appropriate.
	 * 
	 * @return the function context map
	 */
	public Map<Object, Object> getTransformationContext();

}
