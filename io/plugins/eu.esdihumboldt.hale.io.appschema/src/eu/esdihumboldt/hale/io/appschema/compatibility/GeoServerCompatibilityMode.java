/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.appschema.compatibility;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.compatibility.CompatibilityMode;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Condition;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.io.appschema.writer.internal.PropertyTransformationHandlerFactory;
import eu.esdihumboldt.hale.io.appschema.writer.internal.TypeTransformationHandlerFactory;
import eu.esdihumboldt.hale.io.appschema.writer.internal.UnsupportedTransformationException;

/**
 * Compatibility mode for GeoServer app-schema mappings.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class GeoServerCompatibilityMode implements CompatibilityMode {

	@Override
	public boolean supportsFunction(String id, ServiceProvider serviceProvider) {
		return (checkTypeFunction(id) || checkPropertyFunction(id));
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.compatibility.CompatibilityMode#supportsCell(eu.esdihumboldt.hale.common.align.model.Cell)
	 */
	@Override
	public boolean supportsCell(Cell cell) {
		boolean noFilters = checkNoFilters(cell);

		return noFilters;
	}

	private boolean checkTypeFunction(String id) {
		try {
			TypeTransformationHandlerFactory.getInstance().createTypeTransformationHandler(id);
		} catch (UnsupportedTransformationException e) {
			return false;
		}

		return true;
	}

	private boolean checkPropertyFunction(String id) {
		try {
			PropertyTransformationHandlerFactory.getInstance().createPropertyTransformationHandler(
					id);
		} catch (UnsupportedTransformationException e) {
			return false;
		}

		return true;
	}

	private boolean checkNoFilters(Cell cell) {
		// filters are not (yet) supported
		ListMultimap<String, ? extends Entity> entities = cell.getSource();
		if (entities == null) {
			return true;
		}

		for (Entity entity : entities.values()) {
			Filter typeFilter = entity.getDefinition().getFilter();
			if (typeFilter != null) {
				return false;
			}

			for (ChildContext context : entity.getDefinition().getPropertyPath()) {
				Condition cond = context.getCondition();
				if (cond != null && cond.getFilter() != null) {
					return false;
				}
			}
		}

		return true;
	}
}
