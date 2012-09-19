/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.views.properties.definition.typedefinition;

import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.views.properties.definition.DefaultDefinitionFilter;

/**
 * Filter that only accepts type definitions with childrens.
 * 
 * @author Simon Templer
 */
public class TypeDefinitionStructureFilter extends DefaultDefinitionFilter {

	/**
	 * @see DefaultDefinitionFilter#isFiltered(Definition)
	 */
	@Override
	public boolean isFiltered(Definition<?> input) {
		if (input instanceof TypeDefinition) {
			TypeDefinition type = ((TypeDefinition) input);
			return type.getChildren().isEmpty();
		}
		return true;
	}

}
