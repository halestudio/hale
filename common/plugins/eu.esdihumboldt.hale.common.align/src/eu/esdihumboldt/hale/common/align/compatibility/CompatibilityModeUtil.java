/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.align.compatibility;

import com.google.common.base.Predicate;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Condition;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.instance.model.Filter;

/**
 * Utilities for compatibility mode checks.
 * 
 * @author Simon Templer
 */
public abstract class CompatibilityModeUtil {

	/**
	 * Check all filters referenced in a cell for compatibility.
	 * 
	 * @param cell the cell to check
	 * @param predicate the predicate to test the filters with
	 * @return if all filters were accepted
	 */
	public static boolean checkFilters(Cell cell, Predicate<Filter> predicate) {
		if (!checkEntities(cell.getSource(), predicate)) {
			return false;
		}

		// not necessary as there may be no filters in target entities
//		if (!checkEntities(cell.getTarget(), predicate)) {
//			return false;
//		}

		return true;
	}

	private static boolean checkEntities(ListMultimap<String, ? extends Entity> entities,
			Predicate<Filter> predicate) {
		if (entities == null) {
			return true;
		}

		for (Entity entity : entities.values()) {
			Filter typeFilter = entity.getDefinition().getFilter();
			if (typeFilter != null && !predicate.apply(typeFilter)) {
				return false;
			}

			for (ChildContext context : entity.getDefinition().getPropertyPath()) {
				Condition cond = context.getCondition();
				if (cond != null && cond.getFilter() != null) {
					if (!predicate.apply(cond.getFilter())) {
						return false;
					}
				}
			}
		}
		return true;
	}

}
