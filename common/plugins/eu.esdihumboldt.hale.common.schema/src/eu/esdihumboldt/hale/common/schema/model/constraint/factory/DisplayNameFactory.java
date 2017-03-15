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

package eu.esdihumboldt.hale.common.schema.model.constraint.factory;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.constraint.DisplayName;

/**
 * Value factory for {@link DisplayName} constraints.
 * 
 * @author Simon Templer
 */
public class DisplayNameFactory implements ValueConstraintFactory<DisplayName> {

	@Override
	public Value store(DisplayName constraint, TypeReferenceBuilder typeIndex) {
		String name = constraint.getCustomName();
		if (name == null) {
			// default
			return null;
		}
		return Value.of(name);
	}

	@Override
	public DisplayName restore(Value value, Definition<?> definition, TypeResolver typeIndex,
			ClassResolver resolver) throws Exception {
		return new DisplayName(value.as(String.class));
	}

}
