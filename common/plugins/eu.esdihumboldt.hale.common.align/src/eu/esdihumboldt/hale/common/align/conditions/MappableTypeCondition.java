/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.align.conditions;

import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.model.condition.TypeCondition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappableFlag;

/**
 * Accepts mappable types.
 * 
 * @author Simon Templer
 */
public class MappableTypeCondition implements TypeCondition {

	@Override
	public boolean accept(Type entity) {
		TypeDefinition type = entity.getDefinition().getDefinition();

		if (!type.getConstraint(MappableFlag.class).isEnabled()) {
			return false;
		}

//		if (type.getConstraint(HasValueFlag.class).isEnabled()) {
//			return false;
//		}
//
//		if (type.getChildren().isEmpty()) {
//			return false;
//		}

		return true;
	}

}
