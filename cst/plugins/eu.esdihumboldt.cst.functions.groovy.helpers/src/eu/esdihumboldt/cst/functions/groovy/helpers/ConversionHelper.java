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

package eu.esdihumboldt.cst.functions.groovy.helpers;

import java.util.Map;

import org.springframework.core.convert.ConversionService;

import com.google.common.base.Preconditions;

import eu.esdihumboldt.cst.functions.groovy.helper.HelperFunction;
import eu.esdihumboldt.cst.functions.groovy.helper.spec.Specification;
import eu.esdihumboldt.cst.functions.groovy.helper.spec.impl.HelperFunctionArgument;
import eu.esdihumboldt.cst.functions.groovy.helper.spec.impl.HelperFunctionSpecification;
import eu.esdihumboldt.hale.common.core.HalePlatform;

/**
 * Helper using the {@link ConversionService} for value conversions.
 * 
 * @author Simon Templer
 */
public class ConversionHelper implements HelperFunction<Object> {

	private static final String ARG_VALUE = "value";
	private static final String ARG_TO = "to";

	private static final ThreadLocal<ConversionService> conversionService = new ThreadLocal<ConversionService>() {

		@Override
		protected ConversionService initialValue() {
			return HalePlatform.getService(ConversionService.class);
		}
	};

	@Override
	public Object call(Object arg) throws Exception {
		Preconditions.checkArgument(arg instanceof Map, "Named parameters expected");
		@SuppressWarnings("unchecked")
		Map<String, Object> args = (Map<String, Object>) arg;

		Preconditions.checkArgument(args.containsKey(ARG_VALUE), "Value to convert is not defined");
		Object value = args.get(ARG_VALUE);
		if (value == null) {
			return null;
		}

		Preconditions.checkArgument(args.get(ARG_TO) instanceof Class,
				"Argument \"to\" must be a class");
		Class<?> target = (Class<?>) args.get(ARG_TO);

		ConversionService cs = conversionService.get();
		if (cs != null) {
			return cs.convert(value, target);
		}
		else {
			throw new IllegalStateException("Conversion service is not available");
		}
	}

	@Override
	public Specification getSpec(String functionName) throws Exception {
		return new HelperFunctionSpecification(
				"Converts a given value to a target class using the ConversionService.", // description
				"the converted value or null if the conversion is not possible", // result
				new HelperFunctionArgument(ARG_VALUE, "the value to convert"),
				new HelperFunctionArgument(ARG_TO, "the class to convert to"));
	}
}
