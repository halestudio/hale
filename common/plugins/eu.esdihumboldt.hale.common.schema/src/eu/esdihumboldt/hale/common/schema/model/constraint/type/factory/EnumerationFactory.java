/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.schema.model.constraint.type.factory;

import java.util.ArrayList;
import java.util.Collection;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.ValueList;
import eu.esdihumboldt.hale.common.core.io.ValueProperties;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.ClassResolver;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.TypeReferenceBuilder;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.TypeResolver;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.ValueConstraintFactory;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Enumeration;

/**
 * Converts {@link Enumeration} constraints to {@link Value} objects and vice
 * versa.
 * 
 * @author Simon Templer
 */
public class EnumerationFactory implements ValueConstraintFactory<Enumeration<?>> {

	/**
	 * Name of the property specifying the list of allowed values.
	 */
	private static final String P_VALUES = "values";

	/**
	 * Name of the property specifying if other values are allowed in addition.
	 */
	private static final String P_ALLOW_OTHERS = "allowOthers";

	@Override
	public Value store(Enumeration<?> constraint, TypeReferenceBuilder typeIndex) {
		ValueProperties props = new ValueProperties(2);

		// values
		if (constraint.getValues() != null) {
			ValueList list = new ValueList();
			for (Object value : constraint.getValues()) {
				list.add(Value.simple(value));
			}
			props.put(P_VALUES, Value.complex(list));

			// XXX also store the value type for reconstruction?
		}

		// allow others?
		props.put(P_ALLOW_OTHERS, Value.of(constraint.isAllowOthers()));

		return Value.complex(props);
	}

	@Override
	public Enumeration<?> restore(Value value, Definition<?> definition, TypeResolver typeIndex,
			ClassResolver resolver) throws Exception {
		ValueProperties props = value.as(ValueProperties.class);

		boolean allowOthers = props.get(P_ALLOW_OTHERS).as(Boolean.class, true);
		Collection<Object> values = null;
		if (props.containsKey(P_VALUES)) {
			values = new ArrayList<>();
			ValueList list = props.get(P_VALUES).as(ValueList.class);
			for (Value val : list) {
				// XXX determine value type?
				// XXX for now just use string
				String str = val.as(String.class);
				if (str != null) {
					values.add(str);
				}
				else {
					// TODO warn?
				}
			}
		}

		return new Enumeration<Object>(values, allowOthers);
	}

}
