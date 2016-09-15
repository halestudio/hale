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

package eu.esdihumboldt.hale.common.align.service.impl;

import java.util.List;

import eu.esdihumboldt.hale.common.align.extension.transformation.PropertyTransformationFactory;
import eu.esdihumboldt.hale.common.align.extension.transformation.TypeTransformationFactory;
import eu.esdihumboldt.hale.common.align.extension.transformation.internal.PropertyTransformationExtension;
import eu.esdihumboldt.hale.common.align.extension.transformation.internal.TypeTransformationExtension;
import eu.esdihumboldt.hale.common.align.service.TransformationFunctionService;

/**
 * Transformation function service implementation using only statically defined
 * transformation functions.
 * 
 * @author Simon Templer
 */
public class StaticTransformationFunctionService implements TransformationFunctionService {

	@Override
	public List<PropertyTransformationFactory> getPropertyTransformations(String functionId) {
		return PropertyTransformationExtension.getInstance().getTransformations(functionId);
	}

	@Override
	public List<TypeTransformationFactory> getTypeTransformations(String functionId) {
		return TypeTransformationExtension.getInstance().getTransformations(functionId);
	}

}
