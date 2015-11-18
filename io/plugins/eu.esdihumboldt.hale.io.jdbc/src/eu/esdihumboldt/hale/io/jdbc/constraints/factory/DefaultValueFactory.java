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

import java.util.Map;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.ClassResolver;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.ValueConstraintFactory;
import eu.esdihumboldt.hale.io.jdbc.constraints.DefaultValue;

/**
 * Value conversion for {@link DefaultValue} constraint.
 * 
 * FIXME may fail if there is not possibility to write the default value as a
 * {@link Value}.
 * 
 * @author Simon Templer
 */
public class DefaultValueFactory implements ValueConstraintFactory<DefaultValue> {

	@Override
	public Value store(DefaultValue constraint, Map<TypeDefinition, String> typeIndex)
			throws Exception {
		if (constraint.isSet()) {
			// XXX which may be cases there this causes a problem?
			return Value.of(constraint.getValue());
		}
		else {
			return null;
		}
	}

	@Override
	public DefaultValue restore(Value value, Definition<?> definition,
			Map<String, TypeDefinition> typeIndex, ClassResolver resolver) throws Exception {
		return new DefaultValue(value.getValue());
	}

}
