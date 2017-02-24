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
 * Default implementation of HelperContext interface.
 * 
 * @author Simon Templer
 */
public class DefaultHelperContext implements HelperContext {

	private final ServiceProvider serviceProvider;
	private final ExecutionContext executionContext;
	private final Cell contextCell;
	private final Cell typeCell;

	/**
	 * Create a new helper context.
	 * 
	 * @param serviceProvider the service provider if available
	 * @param executionContext the transformation execution context if available
	 * @param contextCell the context cell if available
	 * @param typeCell the related type cell if available
	 */
	public DefaultHelperContext(@Nullable ServiceProvider serviceProvider,
			@Nullable ExecutionContext executionContext, @Nullable Cell contextCell,
			@Nullable Cell typeCell) {
		super();
		this.serviceProvider = serviceProvider;
		this.executionContext = executionContext;
		this.contextCell = contextCell;
		this.typeCell = typeCell;
	}

	@Override
	public ServiceProvider getServiceProvider() {
		return serviceProvider;
	}

	@Override
	public ExecutionContext getExecutionContext() {
		return executionContext;
	}

	@Override
	public Cell getContextCell() {
		return contextCell;
	}

	@Override
	public Cell getTypeCell() {
		return typeCell;
	}

}
