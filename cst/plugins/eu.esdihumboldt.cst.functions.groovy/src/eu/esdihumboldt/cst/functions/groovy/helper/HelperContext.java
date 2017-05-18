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

package eu.esdihumboldt.cst.functions.groovy.helper;

import javax.annotation.Nullable;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.transformation.function.ExecutionContext;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;

/**
 * Context for helper functions.
 * 
 * @author Simon Templer
 */
public interface HelperContext {

	/**
	 * @return the service provider if available, otherwise <code>null</code>
	 */
	@Nullable
	ServiceProvider getServiceProvider();

	/**
	 * @return the transformation execution context if available, otherwise
	 *         <code>null</code>
	 */
	@Nullable
	ExecutionContext getExecutionContext();

	/**
	 * @return the context cell if available, otherwise <code>null</code>
	 */
	@Nullable
	Cell getContextCell();

	/**
	 * @return the related type cell if available, otherwise <code>null</code>
	 */
	@Nullable
	Cell getTypeCell();

}
