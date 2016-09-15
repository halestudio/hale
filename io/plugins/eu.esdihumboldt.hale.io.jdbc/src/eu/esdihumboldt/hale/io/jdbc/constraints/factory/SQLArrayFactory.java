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

package eu.esdihumboldt.hale.io.jdbc.constraints.factory;

import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.ValueList;
import eu.esdihumboldt.hale.common.core.io.ValueProperties;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.ClassResolver;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.ValueConstraintFactory;
import eu.esdihumboldt.hale.io.jdbc.constraints.SQLArray;

/**
 * Value conversion for {@link SQLArray} constraint.
 * 
 * @author Simon Templer
 */
public class SQLArrayFactory implements ValueConstraintFactory<SQLArray> {

	private static final String NAME_DIMENSION = "dimension";
	private static final String NAME_ELEMENT_CLASS = "elementClass";
	private static final String NAME_ELEMENT_TYPE_NAME = "elementTypeName";
	private static final String NAME_SIZES = "sizes";

	@Override
	public Value store(SQLArray constraint, Map<TypeDefinition, String> typeIndex) throws Exception {
		ValueProperties props = new ValueProperties();

		if (constraint.isArray()) {
			// store element class
			Class<?> elementType = constraint.getElementType();
			if (elementType != null) {
				props.put(NAME_ELEMENT_CLASS, Value.of(elementType.getName()));
			}

			// store element database type name
			String typeName = constraint.getElementTypeName();
			if (typeName != null) {
				props.put(NAME_ELEMENT_TYPE_NAME, Value.of(typeName));
			}

			// store dimension
			props.put(NAME_DIMENSION, Value.of(constraint.getDimension()));

			// store array dimension sizes
			List<Integer> sizes = constraint.getSizes();
			if (sizes != null && !sizes.isEmpty()) {
				ValueList sizeList = new ValueList(Collections2.transform(sizes,
						new Function<Integer, Value>() {

							@Override
							public Value apply(Integer input) {
								return Value.of(input);
							}
						}));
				props.put(NAME_SIZES, Value.complex(sizeList));
			}

			return props.toValue();
		}
		else {
			return null;
		}
	}

	@Override
	public SQLArray restore(Value value, Definition<?> definition,
			Map<String, TypeDefinition> typeIndex, ClassResolver resolver) throws Exception {
		ValueProperties props = value.as(ValueProperties.class);
		if (props != null) {
			// read element class
			Class<?> elementType = null;
			String className = props.getSafe(NAME_ELEMENT_CLASS).as(String.class);
			if (className != null) {
				elementType = resolver.loadClass(className);
			}

			// read element database type name
			String elementTypeName = props.getSafe(NAME_ELEMENT_TYPE_NAME).as(String.class);

			// read dimension
			int dimension = props.getSafe(NAME_DIMENSION).as(Integer.class, 0);

			// read array dimension sizes
			int[] sizes = null;
			ValueList sizeList = props.getSafe(NAME_SIZES).as(ValueList.class);
			if (sizeList != null) {
				sizes = new int[sizeList.size()];
				int index = 0;
				for (Value size : sizeList) {
					sizes[index] = size.as(Integer.class, 0);
					index++;
				}
			}

			return new SQLArray(elementType, elementTypeName, dimension, sizes);
		}
		return SQLArray.NO_ARRAY;
	}

}
