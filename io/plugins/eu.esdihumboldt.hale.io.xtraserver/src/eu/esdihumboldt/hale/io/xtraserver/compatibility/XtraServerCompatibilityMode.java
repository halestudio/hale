/*
 * Copyright (c) 2017 interactive instruments GmbH
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
 *     interactive instruments GmbH <http://www.interactive-instruments.de>
 */

package eu.esdihumboldt.hale.io.xtraserver.compatibility;

import com.google.common.collect.ListMultimap;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.compatibility.CompatibilityMode;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Condition;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.io.xtraserver.writer.handler.PropertyTransformationHandler;
import eu.esdihumboldt.hale.io.xtraserver.writer.handler.TypeTransformationHandler;

/**
 * Compatibility mode for XtraServer configuration mapping files
 * 
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class XtraServerCompatibilityMode implements CompatibilityMode {

	private static final ALogger logger = ALoggerFactory
			.getLogger(XtraServerCompatibilityMode.class);

	/**
	 * Check all supported functions by delegating to the Type and Property
	 * Transformation Handlers
	 * 
	 * @see eu.esdihumboldt.hale.common.align.compatibility.CompatibilityMode#supportsFunction(java.lang.String,
	 *      eu.esdihumboldt.hale.common.core.service.ServiceProvider)
	 */
	@Override
	public boolean supportsFunction(final String functionId, ServiceProvider serviceProvider) {
		boolean supported = TypeTransformationHandler.isTransformationSupported(functionId)
				|| PropertyTransformationHandler.isTransformationSupported(functionId);
		if (!supported) {
			logger.debug("Function {} is not supported by the XtraServer plugin", supported);
		}
		return supported;
	}

	/**
	 * Rejects filters
	 * 
	 * @see eu.esdihumboldt.hale.common.align.compatibility.CompatibilityMode#supportsCell(eu.esdihumboldt.hale.common.align.model.Cell)
	 */
	@Override
	public boolean supportsCell(Cell cell) {
		final ListMultimap<String, ? extends Entity> entities = cell.getSource();
		if (entities == null) {
			return true;
		}
		if (hasFilters(entities)) {
			logger.warn("Filters are not supported");
			return false;
		}

		return true;
	}

	/**
	 * Returns true if Filters are found in the entity definitions or the
	 * property paths
	 * 
	 * @param entities entities to check
	 * @return true if filters are found, false otherwise
	 */
	public static boolean hasFilters(final ListMultimap<String, ? extends Entity> entities) {
		for (Entity entity : entities.values()) {
			/*
			 * final Filter typeFilter = entity.getDefinition().getFilter(); if
			 * (typeFilter != null) { return true; }
			 */
			for (ChildContext context : entity.getDefinition().getPropertyPath()) {
				final Condition cond = context.getCondition();
				if (cond != null && cond.getFilter() != null) {
					return true;
				}
			}
		}
		return false;
	}

}
