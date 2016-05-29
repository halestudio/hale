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

package eu.esdihumboldt.hale.common.align.extension.transformation;

import java.util.List;

import javax.annotation.Nullable;

import eu.esdihumboldt.hale.common.align.extension.transformation.internal.PropertyTransformationExtension;
import eu.esdihumboldt.hale.common.align.extension.transformation.internal.TypeTransformationExtension;
import eu.esdihumboldt.hale.common.align.service.TransformationFunctionService;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;

/**
 * Function utility methods
 * 
 * @author Simon Templer
 */
@SuppressWarnings("javadoc")
public abstract class TransformationFunctionUtil {

	public static List<PropertyTransformationFactory> getPropertyTransformations(
			final String functionId, @Nullable ServiceProvider serviceProvider) {
		if (serviceProvider != null) {
			TransformationFunctionService fs = serviceProvider
					.getService(TransformationFunctionService.class);
			if (fs != null) {
				return fs.getPropertyTransformations(functionId);
			}
		}

		return PropertyTransformationExtension.getInstance().getTransformations(functionId);
	}

	public static List<TypeTransformationFactory> getTypeTransformations(final String functionId,
			@Nullable ServiceProvider serviceProvider) {
		if (serviceProvider != null) {
			TransformationFunctionService fs = serviceProvider
					.getService(TransformationFunctionService.class);
			if (fs != null) {
				return fs.getTypeTransformations(functionId);
			}
		}

		return TypeTransformationExtension.getInstance().getTransformations(functionId);
	}

}
