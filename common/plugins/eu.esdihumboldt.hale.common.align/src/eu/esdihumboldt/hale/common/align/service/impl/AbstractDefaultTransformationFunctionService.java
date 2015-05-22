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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import de.fhg.igd.eclipse.util.extension.AbstractObjectFactory;
import eu.esdihumboldt.hale.common.align.extension.function.custom.CustomPropertyFunction;
import eu.esdihumboldt.hale.common.align.extension.transformation.PropertyTransformationFactory;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyTransformation;

/**
 * Transformation function service that includes dynamic content in addition to
 * the statically defined functions.
 * 
 * @author Simon Templer
 */
public abstract class AbstractDefaultTransformationFunctionService extends
		StaticTransformationFunctionService {

	private static class CustomPropertyFunctionFactory extends
			AbstractObjectFactory<PropertyTransformation<?>> implements
			PropertyTransformationFactory {

		private final CustomPropertyFunction customFunction;

		public CustomPropertyFunctionFactory(CustomPropertyFunction customFunction) {
			this.customFunction = customFunction;
		}

		@Override
		public String getEngineId() {
			return null;
		}

		@Override
		public String getFunctionId() {
			return customFunction.getDescriptor().getId();
		}

		@Override
		public Map<String, String> getExecutionParameters() {
			return Collections.emptyMap();
		}

		@Override
		public PropertyTransformation<?> createExtensionObject() throws Exception {
			return customFunction.createTransformationFunction();
		}

		@Override
		public void dispose(PropertyTransformation<?> instance) {
			// nothing to do
		}

		@Override
		public String getIdentifier() {
			return getFunctionId();
		}

		@Override
		public String getDisplayName() {
			return getIdentifier();
		}

		@Override
		public String getTypeName() {
			return getIdentifier();
		}

	}

	/**
	 * @return the current alignment
	 */
	protected abstract Alignment getCurrentAlignment();

	@Override
	public List<PropertyTransformationFactory> getPropertyTransformations(String functionId) {
		List<PropertyTransformationFactory> functions = super
				.getPropertyTransformations(functionId);

		Alignment al = getCurrentAlignment();
		if (al != null) {
			List<PropertyTransformationFactory> cfs = new ArrayList<>();
			for (CustomPropertyFunction cf : al.getCustomPropertyFunctions().values()) {
				cfs.add(new CustomPropertyFunctionFactory(cf));
			}
			cfs.addAll(functions);

			functions = cfs;
		}

		return functions;
	}

}
