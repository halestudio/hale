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

package eu.esdihumboldt.hale.common.align.extension.function;

import java.util.List;

import eu.esdihumboldt.hale.common.align.model.condition.TypeCondition;

/**
 * Type entity parameter definition.
 * 
 * @author Simon Templer
 */
public interface TypeParameterDefinition extends ParameterDefinition {

	/**
	 * Get the property conditions. All conditions have to match.
	 * 
	 * @return the property conditions
	 */
	public abstract List<TypeCondition> getConditions();

}