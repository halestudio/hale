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
import eu.esdihumboldt.hale.io.jdbc.constraints.SQLType;

/**
 * Value conversion for {@link SQLType} constraint.
 * 
 * @author Simon Templer
 */
public class SQLTypeFactory implements ValueConstraintFactory<SQLType> {

	@Override
	public Value store(SQLType constraint, Map<TypeDefinition, String> typeIndex) throws Exception {
		if (constraint.isSet()) {
			return Value.of(constraint.getType());
		}
		else {
			return null;
		}
	}

	@Override
	public SQLType restore(Value value, Definition<?> definition,
			Map<String, TypeDefinition> typeIndex, ClassResolver resolver) throws Exception {
		return SQLType.get(value.as(Integer.class, Integer.MIN_VALUE));
	}

}
