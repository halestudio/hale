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

import eu.esdihumboldt.hale.common.align.compatibility.CompatibilityMode;
import eu.esdihumboldt.hale.common.align.extension.transformation.PropertyTransformationExtension;
import eu.esdihumboldt.hale.common.align.extension.transformation.PropertyTransformationFactory;
import eu.esdihumboldt.hale.common.align.extension.transformation.TypeTransformationExtension;
import eu.esdihumboldt.hale.common.align.extension.transformation.TypeTransformationFactory;
import eu.esdihumboldt.hale.common.align.model.Cell;

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

	/**
	 * @see eu.esdihumboldt.hale.common.align.compatibility.CompatibilityMode#supportsCell(eu.esdihumboldt.hale.common.align.model.Cell)
	 */
	@Override
	public boolean supportsCell(Cell cell) {
		// true for now on CST
		return true;
	}

}
