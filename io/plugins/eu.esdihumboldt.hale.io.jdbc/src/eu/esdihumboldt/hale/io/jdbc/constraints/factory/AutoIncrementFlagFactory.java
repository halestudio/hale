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

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.FlagConstraintFactory;
import eu.esdihumboldt.hale.io.jdbc.constraints.AutoIncrementFlag;

/**
 * Converts a {@link AutoIncrementFlag} constraint to a {@link Value} and vice
 * versa.
 * 
 * @author Simon Templer
 */
public class AutoIncrementFlagFactory extends FlagConstraintFactory<AutoIncrementFlag> {

	@Override
	protected AutoIncrementFlag restore(boolean enabled) {
		if (enabled)
			return AutoIncrementFlag.ENABLED;
		else
			return AutoIncrementFlag.DISABLED;
	}

}
