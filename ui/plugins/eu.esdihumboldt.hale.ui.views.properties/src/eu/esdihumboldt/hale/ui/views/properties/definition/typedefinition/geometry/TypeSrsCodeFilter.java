/*
 * Copyright (c) 2018 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.ui.views.properties.definition.typedefinition.geometry;

import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.GeometryMetadata;
import eu.esdihumboldt.hale.ui.views.properties.definition.DefaultDefinitionFilter;

/**
 * Filter for showing SRS code section.
 * 
 * @author Simon Templer
 */
public class TypeSrsCodeFilter extends DefaultDefinitionFilter {

	@Override
	public boolean isFiltered(Definition<?> input) {
		boolean accept = false;

		TypeDefinition def;
		if (input instanceof PropertyDefinition) {
			def = ((PropertyDefinition) input).getPropertyType();
		}
		else {
			def = (TypeDefinition) input;
		}
		if (def != null) {
			GeometryMetadata gm = def.getConstraint(GeometryMetadata.class);
			if (gm.getAuthName() != null || gm.getSrs() != null) {
				accept = true;
			}
		}

		return !accept;
	}

}
