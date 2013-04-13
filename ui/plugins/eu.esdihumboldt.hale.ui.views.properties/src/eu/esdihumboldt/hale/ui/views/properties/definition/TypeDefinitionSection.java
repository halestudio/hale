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

package eu.esdihumboldt.hale.ui.views.properties.definition;

import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Basic type definition section that also supports extracting the property type
 * from a porperty definition.
 * 
 * @author Simon Templer
 */
public abstract class TypeDefinitionSection extends DefaultDefinitionSection<TypeDefinition> {

	@Override
	protected TypeDefinition extract(Object input) {
		if (input instanceof PropertyDefinition) {
			return ((PropertyDefinition) input).getPropertyType();
		}

		return super.extract(input);
	}

}
