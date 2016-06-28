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

package eu.esdihumboldt.hale.common.align.transformation.function;

import java.util.Map;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.DefaultTransformationVariables;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfoService;
import eu.esdihumboldt.hale.common.core.io.project.ProjectVariables;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;

/**
 * Execution context of a transformation process. The information put into the
 * execution context is held for the whole transformation process. It can be
 * used by transformation functions to share information. For implementations
 * please be aware that if the transformation may be executed in multiple
 * threads, the context getters defined here as well as the returned maps have
 * to use mechanisms to ensure their consistency (e.g. locking/synchronization).
 * 
 * @author Simon Templer
 */
public interface ExecutionContext extends ServiceProvider {

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
	 * throughout the whole transformation.
	 * 
	 * When invoking multiple operations on the context map, make sure to use
	 * synchronization if appropriate.
	 * 
	 * @return the function context map
	 */
	public Map<Object, Object> getTransformationContext();

	/**
	 * Get the (immutable) transformation alignment.
	 * 
	 * @return the transformation alignment
	 */
	public Alignment getAlignment();

	/**
	 * Get the transformation variables.
	 * 
	 * @return the transformation variables
	 */
	default public TransformationVariables getVariables() {
		return new DefaultTransformationVariables(
				new ProjectVariables(this.getService(ProjectInfoService.class)));
	}

}
