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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.cst.internal;

import java.util.List;

import com.google.common.base.Predicate;

import eu.esdihumboldt.hale.common.align.compatibility.CompatibilityMode;
import eu.esdihumboldt.hale.common.align.compatibility.CompatibilityModeUtil;
import eu.esdihumboldt.hale.common.align.extension.transformation.PropertyTransformationExtension;
import eu.esdihumboldt.hale.common.align.extension.transformation.PropertyTransformationFactory;
import eu.esdihumboldt.hale.common.align.extension.transformation.TypeTransformationExtension;
import eu.esdihumboldt.hale.common.align.extension.transformation.TypeTransformationFactory;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.filter.AbstractGeotoolsFilter;
import eu.esdihumboldt.hale.common.instance.model.Filter;

/**
 * Control class for checkups of transformation functions compatibility with the
 * HALE/CST mode
 * 
 * @author Sebastian Reinhardt
 */
public class CSTCompatibilityMode implements CompatibilityMode {

	/**
	 * The identifier of the mode in the extension.
	 */
	public static final String ID = "eu.esdihumboldt.cst.compatibility";

	/**
	 * @see eu.esdihumboldt.hale.common.align.compatibility.CompatibilityMode#supportsFunction(java.lang.String)
	 */
	@Override
	public boolean supportsFunction(String id) {
		TypeTransformationExtension typesTransformations = TypeTransformationExtension
				.getInstance();
		PropertyTransformationExtension propertyTransformations = PropertyTransformationExtension
				.getInstance();

		List<TypeTransformationFactory> transformations = typesTransformations
				.getTransformations(id);

		List<PropertyTransformationFactory> ptransformations = propertyTransformations
				.getTransformations(id);

		if ((transformations == null || transformations.isEmpty())
				&& (ptransformations == null || ptransformations.isEmpty())) {
			return false;
		}
		else
			return true;
	}

	@Override
	public boolean supportsCell(Cell cell) {
		// only accept cells with supported filters
		if (!CompatibilityModeUtil.checkFilters(cell, new Predicate<Filter>() {

			@Override
			public boolean apply(Filter filter) {
				/*
				 * XXX not nice to check it like this, but will do for now
				 */
				return filter instanceof AbstractGeotoolsFilter;
			}
		})) {
			return false;
		}

		// true for now on CST
		return true;
	}

}
