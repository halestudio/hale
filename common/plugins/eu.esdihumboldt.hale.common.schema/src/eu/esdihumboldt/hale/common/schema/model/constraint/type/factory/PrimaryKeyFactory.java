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
import java.util.List;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.ValueList;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.ClassResolver;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.TypeReferenceBuilder;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.TypeResolver;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.ValueConstraintFactory;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.PrimaryKey;

/**
 * Converts a {@link PrimaryKey} constraint to a {@link Value} or vice versa.
 * 
 * @author Simon Templer
 */
public class PrimaryKeyFactory implements ValueConstraintFactory<PrimaryKey> {

	@Override
	public Value store(PrimaryKey constraint, TypeReferenceBuilder typeIndex) throws Exception {
		if (!constraint.hasPrimaryKey()) {
			return null;
		}

		ValueList list = new ValueList();

		for (QName name : constraint.getPrimaryKeyPath()) {
			list.add(Value.complex(name));
		}

		return list.toValue();
	}

	@Override
	public PrimaryKey restore(Value value, Definition<?> definition, TypeResolver typeIndex,
			ClassResolver resolver) throws Exception {
		ValueList list = value.as(ValueList.class);

		List<QName> names = new ArrayList<>();
		for (Value val : list) {
			QName name = val.as(QName.class);
			if (name != null) {
				names.add(name);
			}
			else {
				throw new IllegalStateException("Failed to read name for primary key path");
			}
		}

		return new PrimaryKey(names);
	}

}
